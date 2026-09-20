package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class CavesHeroScene implements MenuScene {
    private static final Color ROCK = Color.valueOf("7A8782"), FLOOR = Color.valueOf("4F6561");
    private static final Color FAR = Color.valueOf("293B40"), EDGE = Color.valueOf("78867B"), LAMP = Color.valueOf("C6A965");
    private static final Color WOOD = Color.valueOf("4B4030"), WOOD_EDGE = Color.valueOf("766447"), DARK = Color.valueOf("0B151B");
    private static final Rectangle SHAFT = new Rectangle(192, 816, 1184, 256);
    private static final Sequence[] EVENTS = {
            new Sequence(new Track(Kind.SPINNER, true, at(0, 96, 832), at(2.2f, 656, 832),
                    at(2.8f, 656, 832), at(5.3f, 1472, 832))),
            new Sequence(new Track(Kind.BRUTE, true, at(0, 1472, 832), at(2.3f, 960, 832),
                    look(3.9f, 960, 832, -1), at(6.2f, 1472, 832))),
            new Sequence(new Track(Kind.ELEMENTAL, true, at(0, 96, 920), at(2.4f, 656, 960),
                    at(3.4f, 816, 928), at(5.7f, 1472, 992)))
    };
    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.CAVES);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS, Color.WHITE, Color.valueOf("78918F"));
    private final Rectangle clip = new Rectangle();

    @Override public void act(float delta) { paint.act(delta); actors.act(delta); }
    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled() && paint.clip(batch, camera, SHAFT, clip)) {
                try {
                    paint.wall(batch, 192, 816, 1184, 256, 64, FAR);
                    paint.rect(batch, 608, 816, 256, 256, DARK, 0.4f);
                    paint.ledge(batch, 176, 832, 1232, 64, ROCK);
                    actors.draw(batch, true);
                } finally { paint.endClip(batch); }
            }
            paint.pier(batch, 112, 784, 96, 224, ROCK, EDGE);
            paint.pier(batch, 1344, 784, 96, 192, ROCK, EDGE);
            paint.wall(batch, 112, 1008, 176, 80, 128, ROCK);
            paint.wall(batch, 1280, 976, 160, 112, 128, ROCK);
            timber(batch, 176, 800, 296); timber(batch, 1360, 800, 296);
            paint.rect(batch, 152, 1080, 1264, 32, WOOD, 1);
            paint.rect(batch, 152, 1104, 1264, 8, WOOD_EDGE, 0.8f);
            paint.lamp(batch, 192, 952, LAMP, 88); paint.lamp(batch, 1376, 952, LAMP, 88);
            paint.heroLanding(batch, ROCK, FLOOR, EDGE);
            paint.wall(batch, 112, 336, 192, 80, 128, ROCK);
            paint.wall(batch, 1264, 336, 208, 80, 128, ROCK);
            paint.heroInfoFrame(batch, ROCK, EDGE, LAMP);
            timber(batch, 1488, 304, 592); timber(batch, 2384, 304, 592);
        } finally { batch.setPackedColor(packed); }
    }

    private void timber(Batch batch, float x, float y, float height) {
        paint.rect(batch, x + 8, y, 40, height, DARK, 0.8f);
        paint.rect(batch, x, y, 32, height, WOOD, 1);
        paint.rect(batch, x, y, 6, height, WOOD_EDGE, 0.8f);
        for (float at = y + 80; at < y + height; at += 144)
            paint.rect(batch, x - 4, at, 40, 12, DARK, 0.9f);
    }
}
