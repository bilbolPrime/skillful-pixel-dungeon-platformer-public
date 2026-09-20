package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;


final class MenuScriptActors {
    enum Kind {
        RAT("rat", 16, 15, new int[]{0,1}, new int[]{5,6,7,8,9,10}),
        GNOLL("gnoll", 12, 15, new int[]{0,1}, new int[]{4,5,6,7}),
        CRAB("crab", 16, 15, new int[]{0,1}, new int[]{2,3,4,5,6}),
        BRUTE("brute", 12, 16, new int[]{0,1}, new int[]{4,5,6,7}),
        SHAMAN("shaman", 12, 15, new int[]{0,1}, new int[]{4,5,6,7}),
        BAT("bat", 15, 15, new int[]{0,1}, new int[]{0,1}),
        SPINNER("spinner", 16, 15, new int[]{0,1}, new int[]{2,3}),
        ELEMENTAL("elemental", 12, 15, new int[]{0,1,2,3}, new int[]{0,1,2,3}),
        GOLEM("golem", 16, 16, new int[]{0,1}, new int[]{2,3,4,5}),
        MONK("monk", 15, 14, new int[]{0,1,2}, new int[]{11,12,13,14,15,16}),
        WARLOCK("warlock", 12, 15, new int[]{0,1}, new int[]{0,1,2,3}),
        SCORPIO("scorpio", 18, 17, new int[]{0,1}, new int[]{0,1,2}),
        EYE("eye", 16, 18, new int[]{0,1,2}, new int[]{0,1,2}),
        SUCCUBUS("succubus", 12, 15, new int[]{0,1,2}, new int[]{3,4,5,6,7,8});
        final String path;
        final int width, height;
        final int[] idle, run;
        Kind(String name, int width, int height, int[] idle, int[] run) {
            path = "images/units/" + name + "/" + name + ".png";
            this.width = width; this.height = height; this.idle = idle; this.run = run;
        }
    }

    static final class Point {
        final float at, x, y, arc;
        final int facing;
        final boolean claws;
        Point(float at, float x, float y, float arc, int facing, boolean claws) {
            this.at = at; this.x = x; this.y = y; this.arc = arc; this.facing = facing; this.claws = claws;
        }
    }
    static final class Track {
        final Kind kind;
        final boolean far;
        final Point[] points;
        Track(Kind kind, boolean far, Point... points) {
            if (kind == null || points.length < 2) throw new IllegalArgumentException("Menu track requires art and endpoints");
            this.kind = kind; this.far = far; this.points = points.clone();
            float previous = -1f;
            for (Point point : points) {
                if (point.at <= previous || Float.isNaN(point.at) || Float.isInfinite(point.at))
                    throw new IllegalArgumentException("Menu track times must increase");
                previous = point.at;
            }
        }
    }
    static final class Sequence {
        final Track[] tracks;
        final float duration;
        Sequence(Track... tracks) {
            if (tracks.length < 1 || tracks.length > 3) throw new IllegalArgumentException("Menu event track cap is three");
            this.tracks = tracks.clone();
            float end = 0f;
            for (Track track : tracks) end = Math.max(end, track.points[track.points.length - 1].at);
            duration = end;
        }
    }

    private final Texture[] textures = new Texture[Kind.values().length];
    private final Sequence[] sequences;
    private final MenuEventSchedule schedule;
    private final Color nearTint, farTint;

    MenuScriptActors(Sequence[] sequences, Color nearTint, Color farTint) {
        this(sequences, nearTint, farTint, System.nanoTime());
    }
    MenuScriptActors(Sequence[] sequences, Color nearTint, Color farTint, long seed) {
        this.sequences = sequences.clone();
        this.nearTint = new Color(nearTint); this.farTint = new Color(farTint);
        float[] durations = new float[sequences.length];
        for (int i = 0; i < sequences.length; i++) {
            durations[i] = sequences[i].duration;
            for (Track track : sequences[i].tracks) {
                if (textures[track.kind.ordinal()] == null)
                    textures[track.kind.ordinal()] = TextureHelper.GetSingleton().getTexture(track.kind.path);
            }
        }
        schedule = new MenuEventSchedule(durations, seed);
    }

    void act(float delta) { schedule.act(delta); }

    void draw(Batch batch, boolean far) {
        float packed = batch.getPackedColor();
        float sequenceTime = schedule.time();
        for (Track track : sequences[schedule.index()].tracks) {
            if (track.far != far || sequenceTime < track.points[0].at
                    || sequenceTime >= track.points[track.points.length - 1].at) continue;
            int segment = 1;
            while (sequenceTime >= track.points[segment].at) segment++;
            Point from = track.points[segment - 1], to = track.points[segment];
            float progress = (sequenceTime - from.at) / (to.at - from.at);
            float x = MathUtils.lerp(from.x, to.x, progress);
            float y = MathUtils.lerp(from.y, to.y, progress) + MathUtils.sin(progress * MathUtils.PI) * to.arc;
            int facing = 1;
            for (int i = 1; i <= segment; i++) {
                float dx = track.points[i].x - track.points[i - 1].x;
                if (dx != 0f) facing = dx > 0f ? 1 : -1;
                if (track.points[i].facing != 0) facing = track.points[i].facing;
            }
            boolean moving = from.x != to.x || from.y != to.y;
            int frame;
            if (to.claws && track.kind == Kind.CRAB) frame = 7 + Math.min(2, (int) ((sequenceTime - from.at) * 7f) % 4);
            else if (moving) frame = track.kind.run[(int) (sequenceTime * (track.kind == Kind.GNOLL ? 8f : 12f)) % track.kind.run.length];
            else frame = track.kind.idle[(int) (sequenceTime * 3f) % track.kind.idle.length];
            float scale = far ? (track.kind == Kind.RAT ? 4f : 5f) : 5.5f;
            batch.setColor(far ? farTint : nearTint);
            batch.draw(textures[track.kind.ordinal()], Math.round(x / 2f) * 2f, Math.round(y / 2f) * 2f,
                    track.kind.width * scale, track.kind.height * scale, frame * track.kind.width, 0,
                    track.kind.width, track.kind.height, facing < 0, false);
        }
        batch.setPackedColor(packed);
    }

    static Point at(float time, float x, float y) { return new Point(time, x, y, 0, 0, false); }
    static Point jump(float time, float x, float y, float arc) { return new Point(time, x, y, arc, 0, false); }
    static Point look(float time, float x, float y, int facing) { return new Point(time, x, y, 0, facing, false); }
    static Point claws(float time, float x, float y) { return new Point(time, x, y, 0, 0, true); }
}
