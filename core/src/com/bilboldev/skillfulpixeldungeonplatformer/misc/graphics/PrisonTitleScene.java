package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class PrisonTitleScene implements MenuScene {
    private static final Color FRONT = new Color(0.48f, 0.51f, 0.53f, 1f);
    private static final Color INNER = new Color(0.30f, 0.34f, 0.36f, 1f);
    private static final Color FAR = new Color(0.19f, 0.24f, 0.26f, 1f);
    private static final Color EDGE = Color.valueOf("747D77"), BLACK = Color.valueOf("0A1115");
    private static final Color LAMP = Color.valueOf("D6B06B");
    private static final Rectangle OPENING = new Rectangle(1120, 320, 1184, 704);
    private static final Rectangle REAR = new Rectangle(1504, 752, 672, 256);
    private static final float LOWER = 416f, UPPER = 752f;
    private static final Sequence[] EVENTS = {

            new Sequence(new Track(Kind.BRUTE, false, at(0, 1024, LOWER), at(3, 1760, LOWER),
                    at(3.5f, 1760, LOWER), at(6, 2360, LOWER)),
                    new Track(Kind.BAT, true, at(0.8f, 1424, 888), at(4.8f, 2224, 888))),

            new Sequence(new Track(Kind.SHAMAN, false, at(0, 2360, LOWER), at(2.8f, 1536, LOWER),
                    look(4.2f, 1536, LOWER, -1), at(7, 2360, LOWER))),

            new Sequence(new Track(Kind.BAT, false, at(0, 1024, 672), at(1.5f, 1408, 792),
                    at(2.4f, 1408, 792), at(4.8f, 2360, 832)),
                    new Track(Kind.BAT, true, at(1, 2224, 872), at(4.8f, 1424, 872))),

            new Sequence(new Track(Kind.BRUTE, false, at(0, 2360, LOWER), at(6, 1024, LOWER)),
                    new Track(Kind.SHAMAN, true, at(0, 1424, UPPER), at(2, 1936, UPPER),
                            look(3.8f, 1936, UPPER, -1), at(6, 2224, UPPER))),

            new Sequence(new Track(Kind.BRUTE, false, at(0, 1024, LOWER), at(3, 1776, LOWER), at(5.8f, 2360, LOWER)),
                    new Track(Kind.BRUTE, false, at(0.8f, 2360, LOWER), at(3, 1800, LOWER), at(6.1f, 1024, LOWER))),

            new Sequence(new Track(Kind.SHAMAN, false, at(0, 1024, LOWER), at(1.8f, 1376, LOWER),
                    look(3.8f, 1376, LOWER, 1), at(5.6f, 1024, LOWER)),
                    new Track(Kind.BAT, true, at(2, 1424, 920), at(5.6f, 2224, 848)))
    };

    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.PRISON);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS,
            new Color(0.78f, 0.81f, 0.75f, 1f), new Color(0.52f, 0.59f, 0.58f, 1f));
    private final Rectangle clip = new Rectangle(), rearClip = new Rectangle();

    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }

    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()) drawPassages(batch, camera);

            paint.pier(batch, 1056, 288, 80, 736, FRONT, EDGE);
            paint.pier(batch, 2288, 288, 96, 736, FRONT, EDGE);
            paint.ledge(batch, 1040, 1064, 1360, 128, FRONT);
            paint.lamp(batch, 1088, 872, LAMP, 128);
            paint.lamp(batch, 2352, 872, LAMP, 128);
        } finally { batch.setPackedColor(packed); }
    }

    private void drawPassages(Batch batch, OrthographicCamera camera) {
        if (!paint.clip(batch, camera, OPENING, clip)) return;
        try {
            paint.wall(batch, OPENING.x, OPENING.y, OPENING.width, OPENING.height, 96, INNER);
            paint.rect(batch, 1488, 736, 704, 288, BLACK, 1f);
            if (paint.clip(batch, camera, REAR, rearClip)) {
                try {
                    paint.wall(batch, REAR.x, REAR.y, REAR.width, REAR.height, 64, FAR);
                    paint.doorway(batch, 2048, 752, 64, FAR);
                    actors.draw(batch, true);
                    paint.bars(batch, 1536, 752, 576, 256, 128);
                } finally { paint.endClip(batch); }
            }
            paint.wall(batch, 1472, 736, 64, 288, 96, INNER);
            paint.wall(batch, 2144, 736, 64, 288, 96, INNER);
            paint.ledge(batch, 1472, UPPER, 736, 96, FRONT);
            paint.ledge(batch, 1056, LOWER, 1328, 96, FRONT);
            actors.draw(batch, false);
            paint.bars(batch, 1184, LOWER, 1056, 576, 192);
            paint.pier(batch, 1760, 320, 128, 704, FRONT, EDGE);
        } finally { paint.endClip(batch); }
    }
}
