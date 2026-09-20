package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;


final class RoomBackdropAmbience {
    private final TextureRegion pixel;
    private TextureRegion glow;
    private final float[] waterX = new float[16], waterStart = new float[16], waterCatch = new float[16], waterPriority = new float[16];
    private final int[] waterSeed = new int[16];
    private final float[] lightX = new float[4], lightY = new float[4], lightPriority = new float[4];
    private final int[] lightSeed = new int[4];
    private final RoomSnapshot.PropKind[] lightKind = new RoomSnapshot.PropKind[4];
    private final boolean[] lightArtwork = new boolean[4];
    private int waterCount, lightCount;

    RoomBackdropAmbience(TextureRegion pixel) { this.pixel = pixel; }

    void draw(Batch batch, RoomSnapshot snapshot, SpritePose[] selectedProps, int propCount, Rectangle view,
              float anchorX, float anchorY, double time, boolean secondary, float depth, boolean reduced, float pixelsPerSourceUnit,
              float red, float green, float blue, float alpha, RoomBackdropBudget budget) {
        waterCount = lightCount = 0;
        if (!Float.isFinite(pixelsPerSourceUnit) || pixelsPerSourceUnit <= 0f) return;
        int waterLimit = budget.availableParticles(), lightsLimit = budget.availableLights();
        for (int i = 0; i < snapshot.props.size(); i++) {
            RoomSnapshot.Prop prop = snapshot.props.get(i);
            if (prop.kind == RoomSnapshot.PropKind.PIPE && selected(prop.pose, selectedProps, propCount))
                water(prop.anchorX, prop.anchorY, prop.waterTargetY, prop.identifier.hashCode(), true,
                        anchorX, anchorY, view, waterLimit);
            else if ((prop.kind == RoomSnapshot.PropKind.LAMP || prop.kind == RoomSnapshot.PropKind.TORCH
                    || prop.kind == RoomSnapshot.PropKind.CITY_LAMP || prop.kind == RoomSnapshot.PropKind.HALLS_GREEN || prop.kind == RoomSnapshot.PropKind.HALLS_RED)
                    && (prop.pose == null || selected(prop.pose, selectedProps, propCount))
                    && visible(view, prop.anchorX - 64f, prop.anchorY - 64f, 128f, 128f))
                light(prop.anchorX, prop.anchorY, prop.identifier.hashCode(), prop.kind, prop.pose != null, anchorX, anchorY, lightsLimit);
        }
        for (int i = 0; i < snapshot.platforms.size(); i++) {
            RoomSnapshot.Platform span = snapshot.platforms.get(i);
            if (!span.wet || span.tileY < ConstantsHelper.MIN_FLOOR) continue;
            float x = span.left() + span.width() * 0.5f, start = span.top() - 2f;
            float target = ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE + 4f;
            for (int j = 0; j < snapshot.platforms.size(); j++) {
                RoomSnapshot.Platform catchSpan = snapshot.platforms.get(j);
                if (catchSpan.top() < start - 4f && x >= catchSpan.left() && x <= catchSpan.left() + catchSpan.width())
                    target = Math.max(target, catchSpan.top());
            }
            water(x, start, target, snapshot.roomIdentifier.hashCode() ^ span.tileX * 97 ^ span.tileY * 61, false,
                    anchorX, anchorY, view, waterLimit);
        }
        float intensity = (reduced ? 0.35f : 1f) * (depth >= 1f ? 0.58f : 1f - 0.42f * depth);
        if (lightCount > 0) {
            if (glow == null) glow = new TextureRegion(TextureHelper.GetSingleton().getSoftLightTexture());
            int drawnLights = 0;
            for (int i = 0; i < lightCount; i++) {
                if (!budget.takeLight() || (!lightArtwork[i] && !budget.takeItemProp())) break;
                drawnLights++;
                float pulse = 1f + (reduced ? 0.035f : 0.08f) * (float) Math.sin(time * 1.35d + Math.floorMod(lightSeed[i], 37));
                if (lightKind[i] == RoomSnapshot.PropKind.HALLS_GREEN)
                    batch.setColor(red * 0.28f, green * 0.60f, blue * 0.34f, alpha * intensity * 0.10f * pulse);
                else if (lightKind[i] == RoomSnapshot.PropKind.HALLS_RED)
                    batch.setColor(red * 0.67f, green * 0.22f, blue * 0.15f, alpha * intensity * 0.10f * pulse);
                else batch.setColor(red * 0.74f, green * 0.52f, blue * 0.27f, alpha * intensity * 0.16f * pulse);
                RoomLighting.drawDetached(batch, snapshot, glow, lightX[i], lightY[i], 80f);
            }
            lightCount = drawnLights;
            for (int i = 0; i < lightCount; i++) {
                if (lightArtwork[i]) continue;

                batch.setColor(red * 0.13f, green * 0.17f, blue * 0.18f, alpha);
                if (lightKind[i] == RoomSnapshot.PropKind.TORCH) batch.draw(pixel, lightX[i] - 8f, lightY[i] - 40f, 16f, 40f);
                else if (lightKind[i] == RoomSnapshot.PropKind.CITY_LAMP) batch.draw(pixel, lightX[i] - 4f, lightY[i] - 28f, 16f, 48f);
                else batch.draw(pixel, lightX[i] - 12f, lightY[i] - 24f, 24f, 48f);
                batch.setColor(red * 0.52f, green * 0.37f, blue * 0.19f, alpha * (depth >= 1f ? 0.60f : 0.80f - 0.20f * depth));
                if (lightKind[i] == RoomSnapshot.PropKind.HALLS_GREEN)
                    batch.setColor(red * 0.44f, green * 0.60f, blue * 0.47f, alpha * (depth >= 1f ? 0.60f : 0.80f - 0.20f * depth));
                else if (lightKind[i] == RoomSnapshot.PropKind.HALLS_RED)
                    batch.setColor(red * 0.60f, green * 0.38f, blue * 0.35f, alpha * (depth >= 1f ? 0.60f : 0.80f - 0.20f * depth));
                if (lightKind[i] == RoomSnapshot.PropKind.TORCH) batch.draw(pixel, lightX[i] - 8f, lightY[i] - 8f, 16f, 24f);
                else if (lightKind[i] == RoomSnapshot.PropKind.CITY_LAMP) batch.draw(pixel, lightX[i] - 4f, lightY[i] - 12f, 8f, 24f);
                else batch.draw(pixel, lightX[i] - 4f, lightY[i] - 16f, 8f, 32f);
            }
        }

        int draws = Math.min(waterLimit, waterCount * 2);
        for (int i = 0; i < draws && budget.takeParticle(); i++) {
            int source = i % waterCount;
            double cycle = 0.8d + Math.sqrt(waterStart[source] - waterCatch[source]) * 0.022d + Math.floorMod(waterSeed[source], 170) / 1000d;
            double value = time / cycle + Math.floorMod(waterSeed[source], 997) / 997d + (i >= waterCount ? 0.5d : 0d);
            float phase = (float) (value - Math.floor(value));
            drawWaterParticle(batch, source, phase, Math.max(3f, 1f / pixelsPerSourceUnit),
                    red, green, blue, alpha * intensity);
        }
    }

    private void drawWaterParticle(Batch batch, int source, float phase, float width, float red, float green, float blue, float alpha) {
        float x = waterX[source], start = waterStart[source], target = waterCatch[source];
        if (phase < 0.78f) {
            float fall = phase / 0.78f, head = start - (start - target) * (0.12f * fall + 0.88f * fall * fall);
            float length = Math.min(start - head, 4f + 8f * fall);
            if (length <= 0f) return;
            batch.setColor(red * 0.28f, green * 0.50f, blue * 0.51f, alpha * 0.52f);
            batch.draw(pixel, x - width / 2f, head, width, length);
            batch.setColor(red * 0.52f, green * 0.70f, blue * 0.68f, alpha * 0.58f);
            batch.draw(pixel, x - width / 2f, head, width, Math.min(2f, length));
        } else {
            float progress = (phase - 0.78f) / 0.22f, radius = 2f + 7f * progress;
            batch.setColor(red * 0.40f, green * 0.60f, blue * 0.59f, alpha * 0.40f * (1f - progress));
            batch.draw(pixel, x - radius - 2f, target, 3f, 1.5f);
            batch.draw(pixel, x + radius - 1f, target, 3f, 1.5f);
        }
    }

    private boolean selected(SpritePose pose, SpritePose[] selected, int count) {
        for (int i = 0; i < count; i++) if (pose != null && selected[i] == pose) return true;
        return false;
    }

    private boolean visible(Rectangle view, float x, float y, float width, float height) {
        return x + width > view.x && x < view.x + view.width && y + height > view.y && y < view.y + view.height;
    }

    private void water(float x, float start, float target, int seed, boolean pipe, float anchorX, float anchorY, Rectangle view, int limit) {
        if (limit == 0 || !Float.isFinite(target) || start <= target || !visible(view, x - 8f, target, 16f, start - target + 2f)) return;
        float priority = Math.abs(x - anchorX) + Math.abs(start - anchorY) + (pipe ? 0f : 4f * ConstantsHelper.TILE);
        int at = 0;
        while (at < waterCount && (waterPriority[at] < priority || waterPriority[at] == priority && waterSeed[at] < seed)) at++;
        if (at >= Math.min(limit, waterX.length)) return;
        for (int i = Math.min(waterCount, Math.min(limit, waterX.length) - 1); i > at; i--) {
            waterX[i] = waterX[i - 1]; waterStart[i] = waterStart[i - 1]; waterCatch[i] = waterCatch[i - 1];
            waterSeed[i] = waterSeed[i - 1]; waterPriority[i] = waterPriority[i - 1];
        }
        waterX[at] = x; waterStart[at] = start; waterCatch[at] = target; waterSeed[at] = seed; waterPriority[at] = priority;
        waterCount = Math.min(waterCount + 1, Math.min(limit, waterX.length));
    }

    private void light(float x, float y, int seed, RoomSnapshot.PropKind kind, boolean artwork, float anchorX, float anchorY, int limit) {
        float priority = Math.abs(x - anchorX) + Math.abs(y - anchorY);
        int at = 0;
        while (at < lightCount && (lightPriority[at] < priority || lightPriority[at] == priority && lightSeed[at] < seed)) at++;
        if (at >= Math.min(limit, lightX.length)) return;
        for (int i = Math.min(lightCount, Math.min(limit, lightX.length) - 1); i > at; i--) {
            lightX[i] = lightX[i - 1]; lightY[i] = lightY[i - 1]; lightSeed[i] = lightSeed[i - 1]; lightPriority[i] = lightPriority[i - 1];
            lightKind[i] = lightKind[i - 1];
            lightArtwork[i] = lightArtwork[i - 1];
        }
        lightX[at] = x; lightY[at] = y; lightSeed[at] = seed; lightPriority[at] = priority;
        lightKind[at] = kind;
        lightArtwork[at] = artwork;
        lightCount = Math.min(lightCount + 1, Math.min(limit, lightX.length));
    }
}
