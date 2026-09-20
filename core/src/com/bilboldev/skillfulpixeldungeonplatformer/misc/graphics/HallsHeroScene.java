package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class HallsHeroScene implements MenuScene {
    private static final Color STONE = Color.valueOf("7A7385"), FLOOR = Color.valueOf("4B4A5C");
    private static final Color FAR = Color.valueOf("2B3842"), EDGE = Color.valueOf("8C7E90"), GLOW = Color.valueOf("89AA79");
    private static final Color DARK = Color.valueOf("0A1019");
    private static final Rectangle ARCH = new Rectangle(192, 800, 1184, 304);
    private static final Sequence[] EVENTS = {
            new Sequence(new Track(Kind.EYE, true, at(0, 96, 936), at(2.5f, 688, 984),
                    at(3.2f, 688, 984), at(5.8f, 1472, 920))),
            new Sequence(new Track(Kind.SCORPIO, true, at(0, 1472, 832), at(2.2f, 976, 832),
                    look(3.4f, 976, 832, -1), at(5.6f, 1472, 832))),
            new Sequence(new Track(Kind.SUCCUBUS, true, at(0, 96, 832), at(2, 512, 832),
                    look(3.6f, 512, 832, 1), at(5.6f, 96, 832)))
    };
    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.HALL);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS, Color.WHITE, Color.valueOf("829283"));
    private final Rectangle clip = new Rectangle();

    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }
    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled() && paint.clip(batch, camera, ARCH, clip)) {
                try {
                    paint.wall(batch, 192, 800, 1184, 304, 64, FAR);
                    paint.rect(batch, 624, 848, 288, 256, DARK, 0.4f);
                    paint.ledge(batch, 160, 832, 1264, 64, STONE);
                    actors.draw(batch, true);
                } finally { paint.endClip(batch); }
            }
            paint.pier(batch, 112, 784, 96, 240, STONE, EDGE);
            paint.pier(batch, 1344, 784, 96, 240, STONE, EDGE);

            for (int i = 0; i < 3; i++) {
                paint.wall(batch, 144 + i * 128, 1024 + i * 24, 144, 64, 128, STONE);
                paint.ledge(batch, 144 + i * 128, 1040 + i * 24, 144, 128, STONE);
                paint.wall(batch, 1296 - i * 128, 1024 + i * 24, 144, 64, 128, STONE);
                paint.ledge(batch, 1296 - i * 128, 1040 + i * 24, 144, 128, STONE);
            }
            paint.lamp(batch, 160, 928, GLOW, 72); paint.lamp(batch, 1392, 928, GLOW, 72);
            paint.heroLanding(batch, STONE, FLOOR, EDGE);
            paint.ledge(batch, 96, 416, 1392, 128, STONE);
            paint.heroInfoFrame(batch, STONE, EDGE, GLOW);
            paint.pier(batch, 64, 288, 64, 144, STONE, EDGE);
            paint.pier(batch, 1408, 288, 64, 144, STONE, EDGE);
        } finally { batch.setPackedColor(packed); }
    }
}
