package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class CavesTitleScene implements MenuScene {
    private static final Color ROCK = new Color(0.48f, 0.53f, 0.51f, 1f);
    private static final Color INNER = new Color(0.26f, 0.33f, 0.33f, 1f);
    private static final Color FAR = new Color(0.16f, 0.23f, 0.25f, 1f);
    private static final Color EDGE = Color.valueOf("78867B"), BLACK = Color.valueOf("0B151B");
    private static final Color WOOD = Color.valueOf("4B4030"), WOOD_EDGE = Color.valueOf("766447");
    private static final Color LAMP = Color.valueOf("C6A965");
    private static final Rectangle OPENING = new Rectangle(1120, 320, 1184, 704);
    private static final Rectangle SHAFT = new Rectangle(1440, 752, 608, 240);
    private static final float LOWER = 448f, UPPER = 752f;
    private static final Sequence[] EVENTS = {

            new Sequence(new Track(Kind.SPINNER, false, at(0, 1024, LOWER), at(1.8f, 1488, LOWER),
                    at(2.1f, 1488, LOWER), jump(2.8f, 1736, LOWER, 88), at(5.3f, 2384, LOWER)),
                    new Track(Kind.ELEMENTAL, true, at(0.7f, 1360, 848), at(5.7f, 2112, 816))),

            new Sequence(new Track(Kind.BRUTE, false, at(0, 1024, LOWER), at(2, 1408, LOWER),
                    look(3.4f, 1408, LOWER, 1), at(5.4f, 1024, LOWER)),
                    new Track(Kind.SPINNER, true, at(0.6f, 2112, UPPER), at(5.8f, 1360, UPPER))),

            new Sequence(new Track(Kind.ELEMENTAL, false, at(0, 2384, 640), at(2, 1840, 704),
                    at(3.4f, 1544, 624), at(5.6f, 1024, 736)),
                    new Track(Kind.BRUTE, true, at(0.2f, 1360, UPPER), at(5.6f, 2112, UPPER))),

            new Sequence(new Track(Kind.SPINNER, false, at(0, 2384, LOWER), at(2.2f, 1752, LOWER),
                    at(2.8f, 1752, LOWER), jump(3.5f, 1440, LOWER, 96), at(5.5f, 1024, LOWER)),
                    new Track(Kind.ELEMENTAL, true, at(0, 1360, 848), at(1.8f, 1704, 848),
                            at(3, 1704, 848), at(5.1f, 2112, 880))),

            new Sequence(new Track(Kind.SPINNER, false, at(0, 1024, LOWER), at(1.8f, 1488, LOWER),
                    jump(2.5f, 1736, LOWER, 72), at(5.1f, 2384, LOWER)),
                    new Track(Kind.SPINNER, false, at(0.8f, 1024, LOWER), at(2.6f, 1488, LOWER),
                            jump(3.3f, 1736, LOWER, 112), at(5.9f, 2384, LOWER))),

            new Sequence(new Track(Kind.BRUTE, false, at(0, 2384, LOWER), at(2.2f, 1832, LOWER),
                    look(4, 1832, LOWER, -1), at(6.2f, 2384, LOWER)),
                    new Track(Kind.SPINNER, true, at(1.2f, 1360, UPPER), at(5.5f, 2112, UPPER)))
    };

    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.CAVES);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS,
            new Color(0.73f, 0.81f, 0.73f, 1f), new Color(0.47f, 0.57f, 0.56f, 1f));
    private final Rectangle clip = new Rectangle(), shaftClip = new Rectangle();

    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }

    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()) drawShaft(batch, camera);
            rockReturn(batch, 1088, 944, 192, 96);
            rockReturn(batch, 1088, 832, 96, 112);
            rockReturn(batch, 1088, 288, 112, 80);
            rockReturn(batch, 2112, 960, 224, 80);
            rockReturn(batch, 2240, 832, 96, 128);
            rockReturn(batch, 2232, 288, 104, 104);
            timber(batch, 1096, 288, 744);
            timber(batch, 2304, 288, 744);
            paint.rect(batch, 1072, 1008, 1296, 40, BLACK, 0.8f);
            paint.rect(batch, 1080, 1024, 1280, 32, WOOD, 1f);
            paint.rect(batch, 1080, 1048, 1280, 8, WOOD_EDGE, 1f);
            paint.lamp(batch, 1136, 760, LAMP, 112);
            paint.lamp(batch, 2344, 880, LAMP, 112);
        } finally { batch.setPackedColor(packed); }
    }

    private void drawShaft(Batch batch, OrthographicCamera camera) {
        if (!paint.clip(batch, camera, OPENING, clip)) return;
        try {
            paint.wall(batch, OPENING.x, OPENING.y, OPENING.width, OPENING.height, 96, INNER);
            paint.rect(batch, 1416, 728, 656, 288, BLACK, 1f);
            if (paint.clip(batch, camera, SHAFT, shaftClip)) {
                try {
                    paint.wall(batch, SHAFT.x, SHAFT.y, SHAFT.width, SHAFT.height, 64, FAR);
                    paint.rect(batch, 1504, 752, 32, 240, BLACK, 0.36f);
                    paint.rect(batch, 1920, 752, 32, 240, BLACK, 0.36f);
                    actors.draw(batch, true);
                } finally { paint.endClip(batch); }
            }
            paint.wall(batch, 1408, 728, 64, 288, 96, INNER);
            paint.wall(batch, 2016, 728, 64, 288, 96, INNER);
            paint.ledge(batch, 1408, UPPER, 672, 96, ROCK);
            paint.rect(batch, 1552, 320, 192, 344, BLACK, 0.75f);
            paint.ledge(batch, 1056, LOWER, 512, 96, ROCK);
            paint.ledge(batch, 1728, LOWER, 656, 96, ROCK);
            actors.draw(batch, false);
            paint.pier(batch, 2136, 320, 80, 304, ROCK, EDGE);
        } finally { paint.endClip(batch); }
    }

    private void timber(Batch batch, float x, float bottom, float height) {
        paint.rect(batch, x + 8, bottom, 48, height, BLACK, 0.8f);
        paint.rect(batch, x, bottom, 40, height, WOOD, 1f);
        paint.rect(batch, x, bottom, 8, height, WOOD_EDGE, 0.7f);
        for (float y = bottom + 112; y < bottom + height; y += 192) {
            paint.rect(batch, x - 8, y, 56, 16, BLACK, 0.9f);
            paint.rect(batch, x - 8, y + 8, 56, 4, EDGE, 0.6f);
        }
    }

    private void rockReturn(Batch batch, float x, float y, float width, float height) {
        paint.rect(batch, x + 8, y - 8, width, height, BLACK, 1f);
        paint.wall(batch, x, y, width, height, 128, ROCK);
        paint.ledge(batch, x, y + 16, width, 128, ROCK);
    }
}
