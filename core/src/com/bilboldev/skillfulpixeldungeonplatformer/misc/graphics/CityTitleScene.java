package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class CityTitleScene implements MenuScene {
    private static final Color STONE = new Color(0.57f, 0.54f, 0.48f, 1f);
    private static final Color INNER = new Color(0.36f, 0.37f, 0.34f, 1f);
    private static final Color FAR = new Color(0.23f, 0.27f, 0.28f, 1f);
    private static final Color EDGE = Color.valueOf("A29A79"), BLACK = Color.valueOf("131819");
    private static final Color LAMP = Color.valueOf("D9AD63");
    private static final Rectangle ARCADE = new Rectangle(1104, 448, 1216, 576);
    private static final Rectangle REAR = new Rectangle(1280, 800, 944, 224);
    private static final float WALK = 512, HIGH = 800;
    private static final Sequence[] EVENTS = {

            new Sequence(new Track(Kind.GOLEM, false, at(0, 1000, WALK), at(2.8f, 1568, WALK),
                    look(3.6f, 1568, WALK, 1), at(7.2f, 2384, WALK)),
                    new Track(Kind.WARLOCK, true, at(1, 2240, HIGH), at(6.8f, 1192, HIGH))),

            new Sequence(new Track(Kind.MONK, false, at(0, 2384, WALK), at(3.6f, 1000, WALK)),
                    new Track(Kind.MONK, true, at(2, 1192, HIGH), at(5.6f, 2240, HIGH))),

            new Sequence(new Track(Kind.WARLOCK, false, at(0, 1000, WALK), at(1.8f, 1440, WALK),
                    look(3.5f, 1440, WALK, 1), at(5.3f, 1000, WALK)),
                    new Track(Kind.GOLEM, true, at(0.6f, 2240, HIGH), at(6.8f, 1192, HIGH))),

            new Sequence(new Track(Kind.GOLEM, false, at(0, 2384, WALK), at(6.6f, 1000, WALK)),
                    new Track(Kind.MONK, true, at(0.8f, 1192, HIGH), at(3.8f, 2240, HIGH))),

            new Sequence(new Track(Kind.GOLEM, false, at(0, 1000, WALK), at(2.6f, 1552, WALK),
                    look(4, 1552, WALK, 1), at(6.6f, 1000, WALK)),
                    new Track(Kind.GOLEM, false, at(0.8f, 2384, WALK), at(3.2f, 1872, WALK),
                            look(4.8f, 1872, WALK, -1), at(7.2f, 2384, WALK))),

            new Sequence(new Track(Kind.WARLOCK, false, at(0, 1712, WALK), at(1.2f, 1976, WALK),
                    look(2.8f, 1976, WALK, -1), at(4.2f, 1712, WALK)),
                    new Track(Kind.MONK, true, at(1.2f, 2240, HIGH), at(4.8f, 1192, HIGH)))
    };

    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.CITY);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS,
            new Color(0.82f, 0.81f, 0.71f, 1), new Color(0.52f, 0.60f, 0.58f, 1));
    private final Rectangle clip = new Rectangle(), rearClip = new Rectangle();

    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }

    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()) drawArcade(batch, camera);

            column(batch, 1056, 96);
            column(batch, 1680, 128);
            column(batch, 2288, 96);
            paint.ledge(batch, 1024, 1088, 1392, 128, STONE);
            paint.ledge(batch, 1040, 1048, 1360, 128, STONE);
            paint.lamp(batch, 1104, 864, LAMP, 112);
            paint.lamp(batch, 1744, 944, LAMP, 136);
            paint.lamp(batch, 2336, 864, LAMP, 112);
        } finally { batch.setPackedColor(packed); }
    }

    private void drawArcade(Batch batch, OrthographicCamera camera) {
        if (!paint.clip(batch, camera, ARCADE, clip)) return;
        try {
            paint.wall(batch, ARCADE.x, ARCADE.y, ARCADE.width, ARCADE.height, 96, INNER);
            paint.rect(batch, 1264, 784, 976, 256, BLACK, 1);
            if (paint.clip(batch, camera, REAR, rearClip)) {
                try {
                    paint.wall(batch, REAR.x, REAR.y, REAR.width, REAR.height, 64, FAR);
                    paint.doorway(batch, 1344, HIGH, 64, FAR);
                    paint.doorway(batch, 2112, HIGH, 64, FAR);
                    actors.draw(batch, true);
                } finally { paint.endClip(batch); }
            }
            paint.wall(batch, 1248, 784, 64, 256, 96, INNER);
            paint.wall(batch, 2192, 784, 64, 256, 96, INNER);
            paint.ledge(batch, 1248, HIGH, 1008, 96, STONE);
            paint.wall(batch, 1088, 448, 1264, 64, 96, INNER);
            paint.ledge(batch, 1088, WALK, 1264, 96, STONE);
            actors.draw(batch, false);

            paint.pier(batch, 1392, 448, 48, 40, STONE, EDGE);
            paint.pier(batch, 2064, 448, 48, 40, STONE, EDGE);
        } finally { paint.endClip(batch); }
    }

    private void column(Batch batch, float x, float width) {
        paint.pier(batch, x, 288, width, 736, STONE, EDGE);
        paint.ledge(batch, x - 24, 352, width + 48, 128, STONE);
        paint.ledge(batch, x - 24, 1008, width + 48, 128, STONE);
    }
}
