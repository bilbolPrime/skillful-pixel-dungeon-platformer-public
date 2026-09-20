package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class CityHeroScene implements MenuScene {
    private static final Color STONE = Color.valueOf("918A7A"), FLOOR = Color.valueOf("60665D");
    private static final Color FAR = Color.valueOf("3B4547"), EDGE = Color.valueOf("A29A79"), LAMP = Color.valueOf("D9AD63");
    private static final Rectangle ARCADE = new Rectangle(176, 816, 1216, 256);
    private static final Sequence[] EVENTS = {
            new Sequence(new Track(Kind.GOLEM, true, at(0, 96, 832), at(2.5f, 576, 832),
                    look(3.6f, 576, 832, 1), at(7.1f, 1472, 832))),
            new Sequence(new Track(Kind.MONK, true, at(0, 1472, 832), at(4.1f, 96, 832))),
            new Sequence(new Track(Kind.WARLOCK, true, at(0, 1472, 832), at(2.3f, 1008, 832),
                    look(3.8f, 1008, 832, -1), at(6.1f, 1472, 832)))
    };
    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.CITY);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS, Color.WHITE, Color.valueOf("91998A"));
    private final Rectangle clip = new Rectangle();

    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }
    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled() && paint.clip(batch, camera, ARCADE, clip)) {
                try {
                    paint.wall(batch, 176, 816, 1216, 256, 64, FAR);
                    paint.ledge(batch, 176, 832, 1216, 64, STONE);
                    paint.ledge(batch, 176, 1040, 1216, 64, FAR);
                    actors.draw(batch, true);
                } finally { paint.endClip(batch); }
            }
            for (int i = 0; i < 4; i++) {
                float x = 128 + i * 416;
                paint.pier(batch, x, 800, 72, 248, STONE, EDGE);
                paint.ledge(batch, x - 24, 1088, 120, 128, STONE);
                paint.ledge(batch, x - 16, 840, 104, 128, STONE);
            }
            paint.ledge(batch, 96, 1120, 1408, 128, STONE);
            paint.ledge(batch, 96, 816, 1408, 128, STONE);
            paint.lamp(batch, 168, 956, LAMP, 88); paint.lamp(batch, 1416, 956, LAMP, 88);
            paint.heroLanding(batch, STONE, FLOOR, EDGE);
            paint.ledge(batch, 88, 416, 1408, 128, STONE);
            paint.heroInfoFrame(batch, STONE, EDGE, LAMP);
            paint.ledge(batch, 1472, 944, 960, 128, STONE);
        } finally { batch.setPackedColor(packed); }
    }
}
