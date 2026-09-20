package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class CityGalleryScene implements MenuScene {
    private static final Color STONE = Color.valueOf("918A7A"), INNER = Color.valueOf("5C5E57"), FAR = Color.valueOf("3B4547");
    private static final Color EDGE = Color.valueOf("A29A79"), LAMP = Color.valueOf("D9AD63");
    private static final Rectangle ARCADE = new Rectangle(464, 352, 1920, 768);
    private static final Sequence[] EVENTS = {
            new Sequence(new Track(Kind.GOLEM, false, at(0, 384, 416), at(2.2f, 864, 416),
                    look(3.8f, 864, 416, 1), at(6, 384, 416))),
            new Sequence(new Track(Kind.MONK, true, at(0, 2464, 1024), at(4.4f, 384, 1024))),
            new Sequence(new Track(Kind.WARLOCK, false, at(0, 2464, 416), at(2, 2016, 416),
                    look(3.8f, 2016, 416, -1), at(5.8f, 2464, 416))),

            new Sequence(new Track(Kind.GOLEM, false, at(0, 384, 416), at(2.4f, 1104, 416),
                    at(4, 1104, 416), at(7.8f, 2464, 416)),
                    new Track(Kind.MONK, false, at(1.5f, 384, 416), at(5.1f, 2464, 416))),
            new Sequence(new Track(Kind.GOLEM, true, at(0, 384, 1024), at(7, 2464, 1024)),
                    new Track(Kind.MONK, false, at(1, 2464, 416), at(5.2f, 384, 416))),
            new Sequence(new Track(Kind.WARLOCK, false, at(0, 1104, 416), at(1.2f, 1392, 416),
                    look(2.8f, 1392, 416, -1), at(4.2f, 1104, 416)),
                    new Track(Kind.GOLEM, false, at(1.2f, 2464, 416), at(3.2f, 2016, 416),
                            look(4.3f, 2016, 416, -1), at(6.3f, 2464, 416)))
    };
    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.CITY);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS, Color.valueOf("D1CDB5"), Color.valueOf("91998A"));
    private final Rectangle clip = new Rectangle();
    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }
    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled() && paint.clip(batch, camera, ARCADE, clip)) {
                try {
                    paint.wall(batch, 464, 352, 1920, 768, 96, INNER);
                    paint.wall(batch, 464, 1024, 1920, 96, 64, FAR);
                    paint.ledge(batch, 448, 1024, 1952, 64, STONE);
                    actors.draw(batch, true);
                    paint.ledge(batch, 448, 416, 1952, 96, STONE);
                    actors.draw(batch, false);
                    column(batch, 1056, 352, 144, 768);
                    column(batch, 1784, 352, 144, 768);
                } finally { paint.endClip(batch); }
            }
            column(batch, 384, 288, 112, 832); column(batch, 2368, 288, 112, 832);
            paint.ledge(batch, 360, 1176, 2144, 128, STONE);
            paint.ledge(batch, 384, 352, 2096, 128, STONE);
            paint.lamp(batch, 440, 880, LAMP, 104); paint.lamp(batch, 2424, 880, LAMP, 104);
        } finally { batch.setPackedColor(packed); }
    }
    private void column(Batch batch, float x, float y, float width, float height) {
        paint.pier(batch, x, y, width, height, STONE, EDGE);
        paint.ledge(batch, x - 24, y + height + 40, width + 48, 128, STONE);
        paint.ledge(batch, x - 16, y + 40, width + 32, 128, STONE);
    }
}
