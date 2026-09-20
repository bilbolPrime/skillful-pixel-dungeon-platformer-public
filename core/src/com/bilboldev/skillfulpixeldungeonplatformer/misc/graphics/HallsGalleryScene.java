package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class HallsGalleryScene implements MenuScene {
    private static final Color STONE = Color.valueOf("7A7385"), INNER = Color.valueOf("4A4A5C"), FAR = Color.valueOf("2B3842");
    private static final Color EDGE = Color.valueOf("8C7E90"), GLOW = Color.valueOf("89AA79"), DARK = Color.valueOf("0A1019");
    private static final Rectangle HALL = new Rectangle(464, 320, 1920, 800);
    private static final Sequence[] EVENTS = {
            new Sequence(new Track(Kind.EYE, false, at(0, 384, 512), at(2.6f, 1328, 640),
                    at(3.3f, 1328, 640), at(6.2f, 2464, 544))),
            new Sequence(new Track(Kind.SCORPIO, true, at(0, 2464, 1024), at(6.1f, 384, 1024))),
            new Sequence(new Track(Kind.SUCCUBUS, false, at(0, 384, 448), at(2.2f, 1232, 448),
                    look(3.6f, 1232, 448, 1), at(5.8f, 384, 448))),
            new Sequence(new Track(Kind.SUCCUBUS, false, at(0, 384, 448), at(2.3f, 1248, 448),
                    jump(3.1f, 1600, 448, 104), at(5.7f, 2464, 448)),
                    new Track(Kind.EYE, true, at(0.6f, 2464, 1008), at(5.9f, 384, 984))),
            new Sequence(new Track(Kind.SCORPIO, false, at(0, 2464, 448), at(2.3f, 1904, 448),
                    look(3.7f, 1904, 448, -1), at(6, 2464, 448))),
            new Sequence(new Track(Kind.EYE, false, at(0, 384, 544), at(2.9f, 1472, 616), at(6.1f, 2464, 520)),
                    new Track(Kind.EYE, true, at(1.1f, 2464, 1000), at(6, 384, 976)))
    };
    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.HALL);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS, Color.valueOf("BAC2B7"), Color.valueOf("829283"));
    private final Rectangle clip = new Rectangle();
    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }
    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled() && paint.clip(batch, camera, HALL, clip)) {
                try {
                    paint.wall(batch, 464, 320, 1920, 800, 96, INNER);
                    paint.wall(batch, 464, 1024, 1920, 96, 64, FAR);
                    paint.ledge(batch, 448, 1024, 1952, 64, STONE);
                    actors.draw(batch, true);
                    paint.rect(batch, 1344, 320, 240, 576, DARK, 0.75f);
                    paint.ledge(batch, 448, 448, 896, 96, STONE);
                    paint.ledge(batch, 1584, 448, 816, 96, STONE);
                    actors.draw(batch, false);
                    paint.pier(batch, 1728, 320, 128, 248, STONE, EDGE);
                } finally { paint.endClip(batch); }
            }
            paint.pier(batch, 384, 288, 112, 800, STONE, EDGE);
            paint.pier(batch, 2368, 288, 112, 800, STONE, EDGE);
            for (int i = 0; i < 4; i++) {
                paint.wall(batch, 432 + i * 160, 1088 + i * 16, 176, 64, 128, STONE);
                paint.ledge(batch, 432 + i * 160, 1104 + i * 16, 176, 128, STONE);
                paint.wall(batch, 2272 - i * 160, 1088 + i * 16, 176, 64, 128, STONE);
                paint.ledge(batch, 2272 - i * 160, 1104 + i * 16, 176, 128, STONE);
            }
            paint.lamp(batch, 440, 880, GLOW, 88); paint.lamp(batch, 2424, 880, GLOW, 88);
        } finally { batch.setPackedColor(packed); }
    }
}
