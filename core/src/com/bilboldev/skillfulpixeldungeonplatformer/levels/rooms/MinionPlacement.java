package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;


public final class MinionPlacement {
    private static final float[] FLYING_HEIGHTS = {48f, 128f, 0f, -128f};
    private MinionPlacement() { }


    public static boolean canRestoreFlyingAt(Hero hero, float x, float y) {
        Room room = MapHelper.getInstance().getActiveRoom();
        return hero != null && room != null && room.getIdentifier().equals(hero.getRoom())
                && Float.isFinite(x) && Float.isFinite(y) && x >= 0f && y >= 0f
                && x + ConstantsHelper.UNIT_DIMENSIONS <= room.getWidth() * ConstantsHelper.TILE
                && y + ConstantsHelper.UNIT_DIMENSIONS <= room.getHeight() * ConstantsHelper.TILE;
    }


    public static Vector2 findFlying(Hero hero, float width) {
        Room room = MapHelper.getInstance().getActiveRoom();
        if (hero == null || room == null || !room.getIdentifier().equals(hero.getRoom())
                || !Float.isFinite(hero.x) || !Float.isFinite(hero.y) || !Float.isFinite(width) || width <= 0f) return null;
        float origin = hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        for (float height : FLYING_HEIGHTS) for (int step = 1; step <= 4; step++) for (int side = 0; side < 2; side++) {
            float center = origin + ((hero.facingRight == (side == 0)) ? -1f : 1f) * step * ConstantsHelper.TILE;
            float feet = hero.y + height;
            if (center - width / 2f < 4f || center + width / 2f > room.getWidth() * ConstantsHelper.TILE - 4f
                    || feet < ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE
                    || !CorpseTargeting.freeFootprint(null, room, center, feet, width, false)
                    || !PhysicsHelper.getInstance().hasSolidLineOfSight(room, origin, hero.y + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                        center, feet + ConstantsHelper.UNIT_DIMENSIONS / 2f)) continue;
            return new Vector2(center - ConstantsHelper.UNIT_DIMENSIONS / 2f, feet);
        }
        return null;
    }


    public static boolean canRestoreAt(Hero hero, float x, float y, float width) {
        Room room = MapHelper.getInstance().getActiveRoom();
        float center = x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        return hero != null && room != null && room.getIdentifier().equals(hero.getRoom())
                && Float.isFinite(x) && Float.isFinite(y) && Float.isFinite(width) && width > 0f
                && center - width / 2f >= 4f && center + width / 2f <= room.getWidth() * ConstantsHelper.TILE - 4f
                && y >= ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE
                && y + (ConstantsHelper.UNIT_DIMENSIONS + width) / 2f <= room.getHeight() * ConstantsHelper.TILE
                && PhysicsHelper.getInstance().isUnitRestoreSpaceClear(room, new Rectangle(center - width / 2f,
                    y + (ConstantsHelper.UNIT_DIMENSIONS - width) / 2f, width, width));
    }

    public static Vector2 find(Hero hero, float width) {
        Room room = MapHelper.getInstance().getActiveRoom();
        if (hero == null || room == null || !room.getIdentifier().equals(hero.getRoom())
                || !Float.isFinite(hero.x) || !Float.isFinite(hero.y)) return null;


        float center = hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        for (int height = 0; height <= 1; height++) {
            for (int step = 1; step <= 8; step++) {
                for (int side = 0; side < 2; side++) {
                    float direction = (hero.facingRight == (side == 0)) ? -1f : 1f;
                    float candidate = center + direction * step * ConstantsHelper.TILE;
                    float support = room.corpseSupportBelow(candidate, hero.y + height * ConstantsHelper.TILE, width / 2f);
                    float feet = support + CorpseTargeting.FEET_OFFSET;
                    if (!Float.isFinite(support) || hero.y - feet > 2f * ConstantsHelper.TILE
                            || !CorpseTargeting.freeFootprint(null, room, candidate, feet, width, false)
                            || !PhysicsHelper.getInstance().hasSolidLineOfSight(room, center,
                                hero.y + ConstantsHelper.UNIT_DIMENSIONS * .75f, candidate,
                                feet + ConstantsHelper.UNIT_DIMENSIONS * .5f)) continue;
                    return new Vector2(candidate - ConstantsHelper.UNIT_DIMENSIONS / 2f, feet);
                }
            }
        }
        return null;
    }
}
