package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class SewersHeroScene implements MenuScene {
    private static final Color STONE = new Color(0.43f, 0.52f, 0.48f, 1);
    private static final Color FLOOR = new Color(0.31f, 0.40f, 0.37f, 1);
    private static final Color FAR = new Color(0.16f, 0.25f, 0.24f, 1);
    private static final Color EDGE = Color.valueOf("83977C"), SHADOW = Color.valueOf("0B1619");
    private static final Color LAMP = Color.valueOf("C3B67C");
    private static final Rectangle PASSAGE = new Rectangle(192, 832, 1152, 224);
    private static final Sequence[] EVENTS = {
            new Sequence(new Track(Kind.RAT, true, at(0, 112, 832), at(2.8f, 704, 832),
                    look(3.5f, 704, 832, 1), at(6.3f, 1360, 832))),
            new Sequence(new Track(Kind.GNOLL, true, at(0, 1360, 832), at(2.1f, 952, 832),
                    look(3.8f, 952, 832, -1), at(5.9f, 1360, 832))),
            new Sequence(new Track(Kind.CRAB, true, at(0, 112, 832), at(2.2f, 512, 832),
                    at(3.5f, 512, 832), at(5.7f, 112, 832)))
    };
    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.SEWERS);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS,
            new Color(0.62f, 0.72f, 0.62f, 1), new Color(0.44f, 0.56f, 0.49f, 1));
    private final Rectangle clip = new Rectangle();

    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }

    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()
                    && paint.clip(batch, camera, PASSAGE, clip)) {
                try {
                    paint.wall(batch, PASSAGE.x, PASSAGE.y, PASSAGE.width, PASSAGE.height, 64, FAR);
                    actors.draw(batch, true);
                } finally { paint.endClip(batch); }
            }
            paint.pier(batch, 128, 800, 64, 256, STONE, EDGE);
            paint.pier(batch, 1344, 800, 64, 256, STONE, EDGE);
            paint.ledge(batch, 112, 1088, 1312, 128, STONE);
            paint.ledge(batch, 176, 832, 1184, 96, STONE);
            paint.lamp(batch, 160, 944, LAMP, 88);
            paint.lamp(batch, 1376, 944, LAMP, 88);

            paint.pier(batch, 1504, 288, 48, 592, STONE, EDGE);
            paint.pier(batch, 2352, 288, 48, 592, STONE, EDGE);
            paint.ledge(batch, 1488, 920, 928, 128, STONE);
            paint.lamp(batch, 2384, 736, LAMP, 88);


            paint.rect(batch, 104, 360, 1384, 272, SHADOW, 0.45f);
            paint.wall(batch, 120, 416, 1344, 208, 128, FLOOR);
            paint.ledge(batch, 112, 624, 1360, 128, STONE);
            paint.ledge(batch, 112, 448, 1360, 128, STONE);
            for (int i = 0; i < 6; i++) {
                float center = MenuHeroRoster.FIRST_X + i * MenuHeroRoster.SPACING;
                paint.rect(batch, center - 56, 580, 112, 4, EDGE, 0.38f);
                paint.rect(batch, center + MenuHeroRoster.STEP_X - 56, 468, 112, 4, EDGE, 0.25f);
            }
        } finally { batch.setPackedColor(packed); }
    }
}
