package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class CavesGalleryScene implements MenuScene {
    private static final Color ROCK = Color.valueOf("7A8782"), INNER = Color.valueOf("425454"), FAR = Color.valueOf("293B40");
    private static final Color EDGE = Color.valueOf("78867B"), LAMP = Color.valueOf("C6A965"), DARK = Color.valueOf("0B151B");
    private static final Color WOOD = Color.valueOf("4B4030"), WOOD_EDGE = Color.valueOf("766447");
    private static final Rectangle SHAFT = new Rectangle(464, 320, 1920, 800);
    private static final Sequence[] EVENTS = {
            new Sequence(new Track(Kind.SPINNER, false, at(0, 384, 448), at(2.5f, 1344, 448),
                    at(3, 1344, 448), jump(3.8f, 1680, 448, 96), at(6.1f, 2464, 448))),
            new Sequence(new Track(Kind.BRUTE, false, at(0, 2464, 448), at(2.2f, 1856, 448),
                    look(3.6f, 1856, 448, -1), at(5.8f, 2464, 448))),

            new Sequence(new Track(Kind.ELEMENTAL, true, at(0, 1520, 208), at(2.6f, 1520, 752),
                    at(3.4f, 1520, 752), at(5.8f, 1520, 1168))),
            new Sequence(new Track(Kind.SPINNER, false, at(0, 2464, 448), at(2.4f, 1680, 448),
                    jump(3.2f, 1328, 448, 88), at(5.7f, 384, 448)),
                    new Track(Kind.BRUTE, true, at(0.5f, 2464, 1024), at(2.3f, 1856, 1024),
                            look(3.9f, 1856, 1024, -1), at(5.7f, 2464, 1024))),
            new Sequence(new Track(Kind.SPINNER, false, at(0, 384, 448), at(2.3f, 1344, 448),
                    jump(3, 1680, 448, 72), at(5.1f, 2464, 448)),
                    new Track(Kind.SPINNER, false, at(0.8f, 384, 448), at(2.5f, 1200, 448),
                            at(3.1f, 1200, 448), at(3.6f, 1344, 448), jump(4.3f, 1680, 448, 104), at(6.4f, 2464, 448))),
            new Sequence(new Track(Kind.BRUTE, false, at(0, 384, 448), at(2.6f, 1232, 448),
                    look(4, 1232, 448, 1), at(6.6f, 384, 448)),
                    new Track(Kind.ELEMENTAL, true, at(1, 2464, 1024), at(5.8f, 384, 1000)))
    };
    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.CAVES);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS, Color.valueOf("BACFBA"), Color.valueOf("78918F"));
    private final Rectangle clip = new Rectangle();
    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }
    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled() && paint.clip(batch, camera, SHAFT, clip)) {
                try {
                    paint.wall(batch, 464, 320, 1920, 800, 96, INNER);
                    paint.wall(batch, 464, 992, 1920, 128, 64, FAR);
                    paint.rect(batch, 1456, 320, 208, 800, DARK, 0.78f);
                    paint.ledge(batch, 448, 1024, 1008, 64, ROCK);
                    paint.ledge(batch, 1664, 1024, 736, 64, ROCK);
                    actors.draw(batch, true);
                    paint.ledge(batch, 448, 448, 1008, 96, ROCK);
                    paint.ledge(batch, 1664, 448, 736, 96, ROCK);
                    actors.draw(batch, false);
                    timber(batch, 1056, 320, 272);
                } finally { paint.endClip(batch); }
            }
            paint.pier(batch, 384, 288, 96, 688, ROCK, EDGE);
            paint.pier(batch, 2368, 288, 80, 784, ROCK, EDGE);
            paint.wall(batch, 384, 976, 144, 144, 128, ROCK);
            paint.wall(batch, 2272, 1072, 176, 64, 128, ROCK);
            timber(batch, 432, 288, 840); timber(batch, 2400, 288, 840);
            paint.rect(batch, 408, 1112, 2032, 32, WOOD, 1);
            paint.rect(batch, 408, 1136, 2032, 8, WOOD_EDGE, 0.8f);
            paint.lamp(batch, 448, 856, LAMP, 96); paint.lamp(batch, 2416, 984, LAMP, 96);
        } finally { batch.setPackedColor(packed); }
    }
    private void timber(Batch batch, float x, float y, float height) {
        paint.rect(batch, x + 8, y, 40, height, DARK, 0.8f);
        paint.rect(batch, x, y, 32, height, WOOD, 1);
        paint.rect(batch, x, y, 6, height, WOOD_EDGE, 0.8f);
        for (float at = y + 80; at < y + height; at += 144) paint.rect(batch, x - 4, at, 40, 12, DARK, 0.9f);
    }
}
