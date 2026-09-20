package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class PrisonHeroScene implements MenuScene {
    private static final Color STONE = Color.valueOf("7A8287"), FLOOR = Color.valueOf("565F60");
    private static final Color FAR = Color.valueOf("303D42"), EDGE = Color.valueOf("747D77"), LAMP = Color.valueOf("D6B06B");
    private static final Rectangle CELLS = new Rectangle(176, 816, 1216, 240);
    private static final Sequence[] EVENTS = {
            new Sequence(new Track(Kind.BRUTE, true, at(0, 96, 832), at(2.7f, 800, 832),
                    look(3.5f, 800, 832, 1), at(6, 1472, 832))),
            new Sequence(new Track(Kind.SHAMAN, true, at(0, 1472, 832), at(2.3f, 1136, 832),
                    look(3.8f, 1136, 832, -1), at(6, 1472, 832))),
            new Sequence(new Track(Kind.BAT, true, at(0, 96, 960), at(2.3f, 656, 936),
                    at(3.3f, 656, 936), at(5.8f, 1472, 968)))
    };
    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.PRISON);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS, Color.WHITE, Color.valueOf("859794"));
    private final Rectangle clip = new Rectangle();

    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }
    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled() && paint.clip(batch, camera, CELLS, clip)) {
                try {
                    paint.wall(batch, 176, 816, 1216, 240, 64, FAR);
                    for (int i = 0; i < 3; i++) paint.doorway(batch, 384 + i * 400, 832, 64, FAR);
                    actors.draw(batch, true);
                    paint.bars(batch, 208, 832, 1152, 224, 80);
                } finally { paint.endClip(batch); }
            }
            for (int i = 0; i < 4; i++) paint.pier(batch, 128 + i * 416, 800, 64, 256, STONE, EDGE);
            paint.ledge(batch, 112, 1096, 1392, 128, STONE);
            paint.ledge(batch, 160, 832, 1280, 96, STONE);
            paint.lamp(batch, 160, 936, LAMP, 88); paint.lamp(batch, 1408, 936, LAMP, 88);
            paint.heroLanding(batch, STONE, FLOOR, EDGE);
            paint.heroInfoFrame(batch, STONE, EDGE, LAMP);
        } finally { batch.setPackedColor(packed); }
    }
}
