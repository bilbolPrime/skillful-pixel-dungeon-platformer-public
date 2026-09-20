package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class HallsTitleScene implements MenuScene {
    private static final Color STONE = new Color(0.48f, 0.45f, 0.52f, 1);
    private static final Color INNER = new Color(0.29f, 0.29f, 0.36f, 1);
    private static final Color FAR = new Color(0.17f, 0.22f, 0.26f, 1);
    private static final Color EDGE = Color.valueOf("8C7E90"), BLACK = Color.valueOf("0A1019");
    private static final Color GLOW = Color.valueOf("89AA79");
    private static final Rectangle ARCH = new Rectangle(1120, 352, 1184, 688);
    private static final Rectangle REAR = new Rectangle(1408, 800, 688, 240);
    private static final float BRIDGE = 544, HIGH = 800;
    private static final Sequence[] EVENTS = {

            new Sequence(new Track(Kind.EYE, false, at(0, 1000, 664), at(2, 1456, 736),
                    at(2.8f, 1456, 736), at(4.2f, 1856, 704), at(6, 2400, 816)),
                    new Track(Kind.SUCCUBUS, true, at(1, 1320, HIGH), at(5.8f, 2144, HIGH))),

            new Sequence(new Track(Kind.SCORPIO, true, at(0, 2144, HIGH), at(4.6f, 1320, HIGH)),
                    new Track(Kind.SUCCUBUS, false, at(0.5f, 2400, BRIDGE), at(2, 2016, BRIDGE),
                            look(3.8f, 2016, BRIDGE, -1), at(5.3f, 2400, BRIDGE))),

            new Sequence(new Track(Kind.SUCCUBUS, false, at(0, 1000, BRIDGE), at(1.8f, 1456, BRIDGE),
                    jump(2.5f, 1744, BRIDGE, 88), at(5.3f, 2400, BRIDGE)),
                    new Track(Kind.EYE, true, at(0.4f, 2144, 920), at(5.1f, 1320, 864))),

            new Sequence(new Track(Kind.SCORPIO, false, at(0, 2400, BRIDGE), at(2.1f, 1936, BRIDGE),
                    look(3.1f, 1936, BRIDGE, -1), at(5.2f, 2400, BRIDGE)),
                    new Track(Kind.EYE, true, at(0, 1320, 864), at(4.8f, 2144, 936))),

            new Sequence(new Track(Kind.SCORPIO, false, at(0, 1000, BRIDGE), at(1.8f, 1376, BRIDGE),
                    look(3.6f, 1376, BRIDGE, 1), at(5.4f, 1000, BRIDGE)),
                    new Track(Kind.SUCCUBUS, true, at(0.5f, 2144, HIGH), at(5.6f, 1320, HIGH))),

            new Sequence(new Track(Kind.EYE, false, at(0, 1000, 672), at(2.7f, 1624, 744),
                    at(5.6f, 2400, 664)),
                    new Track(Kind.EYE, false, at(0.8f, 2400, 864), at(3.5f, 1728, 912),
                            at(6.2f, 1000, 848)))
    };

    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.HALL);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS,
            new Color(0.75f, 0.78f, 0.78f, 1), new Color(0.44f, 0.57f, 0.58f, 1));
    private final Rectangle clip = new Rectangle(), rearClip = new Rectangle();

    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }

    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()) drawHall(batch, camera);
            paint.pier(batch, 1072, 288, 128, 744, STONE, EDGE);
            paint.pier(batch, 2288, 288, 112, 744, STONE, EDGE);

            archBlock(batch, 1184, 928, 128, 112);
            archBlock(batch, 1280, 992, 256, 80);
            archBlock(batch, 2192, 944, 112, 96);
            archBlock(batch, 1968, 1008, 256, 64);
            paint.ledge(batch, 1040, 1104, 608, 128, STONE);
            paint.ledge(batch, 1856, 1104, 576, 128, STONE);
            paint.lamp(batch, 1136, 784, GLOW, 152);
            paint.lamp(batch, 2344, 848, GLOW, 152);
        } finally { batch.setPackedColor(packed); }
    }

    private void drawHall(Batch batch, OrthographicCamera camera) {
        if (!paint.clip(batch, camera, ARCH, clip)) return;
        try {
            paint.wall(batch, ARCH.x, ARCH.y, ARCH.width, ARCH.height, 96, INNER);
            paint.rect(batch, 1360, 352, 448, 656, BLACK, 0.75f);
            paint.rect(batch, 1392, 784, 720, 272, BLACK, 1);
            if (paint.clip(batch, camera, REAR, rearClip)) {
                try {
                    paint.wall(batch, REAR.x, REAR.y, REAR.width, REAR.height, 64, FAR);
                    actors.draw(batch, true);
                } finally { paint.endClip(batch); }
            }
            paint.wall(batch, 1376, 784, 64, 272, 96, INNER);
            paint.wall(batch, 2064, 784, 64, 272, 96, INNER);
            paint.ledge(batch, 1376, HIGH, 752, 96, STONE);
            paint.wall(batch, 1088, 352, 424, 168, 96, INNER);
            paint.wall(batch, 1728, 352, 656, 168, 96, INNER);
            paint.ledge(batch, 1088, BRIDGE, 424, 96, STONE);
            paint.ledge(batch, 1728, BRIDGE, 656, 96, STONE);
            actors.draw(batch, false);
            paint.pier(batch, 1824, 352, 80, 280, STONE, EDGE);
        } finally { paint.endClip(batch); }
    }

    private void archBlock(Batch batch, float x, float y, float width, float height) {
        paint.rect(batch, x + 8, y - 8, width, height, BLACK, 0.9f);
        paint.wall(batch, x, y, width, height, 128, STONE);
        paint.ledge(batch, x, y + 24, width, 128, STONE);
    }
}
