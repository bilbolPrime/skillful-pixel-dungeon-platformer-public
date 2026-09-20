package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class PrisonGalleryScene implements MenuScene {
    private static final Color STONE = Color.valueOf("7A8287"), INNER = Color.valueOf("4D575C"), FAR = Color.valueOf("303D42");
    private static final Color EDGE = Color.valueOf("747D77"), LAMP = Color.valueOf("D6B06B");
    private static final Rectangle WATCH = new Rectangle(464, 352, 1920, 768);
    private static final Sequence[] EVENTS = {

            new Sequence(new Track(Kind.BRUTE, false, at(0, 384, 416), at(2.5f, 912, 416),
                    look(4, 912, 416, 1), at(6.5f, 384, 416)),
                    new Track(Kind.BRUTE, false, at(0.7f, 2464, 416), at(3, 1920, 416),
                            look(4.7f, 1920, 416, -1), at(7, 2464, 416))),
            new Sequence(new Track(Kind.SHAMAN, true, at(0, 2464, 1024), at(6.3f, 384, 1024))),
            new Sequence(new Track(Kind.BAT, true, at(0, 384, 1032), at(2.5f, 1312, 1008),
                    at(3.4f, 1312, 1008), at(5.8f, 2464, 1032))),

            new Sequence(new Track(Kind.BRUTE, false, at(0, 384, 416), at(2, 1072, 416),
                    at(4, 1072, 416), at(7.6f, 2464, 416)),
                    new Track(Kind.SHAMAN, false, at(0.4f, 2464, 416), at(5.4f, 384, 416))),
            new Sequence(new Track(Kind.SHAMAN, false, at(0, 2464, 416), at(2.2f, 1968, 416),
                    look(3.6f, 1968, 416, -1), at(5.8f, 2464, 416)),
                    new Track(Kind.BRUTE, true, at(0, 384, 1024), at(6.5f, 2464, 1024))),
            new Sequence(new Track(Kind.SHAMAN, false, at(0, 1072, 416), at(1.2f, 1328, 416),
                    look(2.8f, 1328, 416, -1), at(4.2f, 1072, 416)),
                    new Track(Kind.BAT, true, at(1, 2464, 1008), at(5.8f, 384, 1032)))
    };
    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.PRISON);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS, Color.valueOf("C7CFBF"), Color.valueOf("859794"));
    private final Rectangle clip = new Rectangle();
    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }
    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled() && paint.clip(batch, camera, WATCH, clip)) {
                try {
                    paint.wall(batch, 464, 352, 1920, 768, 96, INNER);
                    paint.wall(batch, 464, 1024, 1920, 96, 64, FAR);
                    paint.ledge(batch, 448, 1024, 1952, 64, STONE);
                    actors.draw(batch, true);
                    paint.ledge(batch, 448, 416, 1952, 96, STONE);
                    paint.doorway(batch, 2016, 416, 96, INNER);
                    actors.draw(batch, false);
                    paint.bars(batch, 512, 416, 1824, 704, 128);
                    paint.pier(batch, 1056, 352, 128, 768, STONE, EDGE);
                    paint.pier(batch, 1744, 352, 128, 768, STONE, EDGE);
                } finally { paint.endClip(batch); }
            }
            paint.pier(batch, 400, 288, 80, 832, STONE, EDGE);
            paint.pier(batch, 2368, 288, 80, 832, STONE, EDGE);
            paint.ledge(batch, 384, 1152, 2080, 128, STONE);
            paint.lamp(batch, 440, 896, LAMP, 104); paint.lamp(batch, 2416, 896, LAMP, 104);
        } finally { batch.setPackedColor(packed); }
    }
}
