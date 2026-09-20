package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


public final class SewersGalleryScene implements MenuScene {
    private static final Color STONE = Color.valueOf("6E857A"), INNER = Color.valueOf("354E4C"), FAR = Color.valueOf("263C3C");
    private static final Color EDGE = Color.valueOf("83977C"), LAMP = Color.valueOf("C3B67C");
    private static final Rectangle PASSAGE = new Rectangle(464, 320, 1920, 800);
    private static final Sequence[] EVENTS = {
            new Sequence(new Track(Kind.RAT, false, at(0, 384, 384), at(1.5f, 848, 384),
                    look(2.6f, 848, 384, 1), at(4.5f, 384, 384))),
            new Sequence(new Track(Kind.RAT, false, at(0, 384, 384), at(5.6f, 2464, 384)),
                    new Track(Kind.RAT, false, at(0.9f, 384, 384), at(6.5f, 2464, 384))),
            new Sequence(new Track(Kind.CRAB, false, at(0, 2464, 384), at(2.4f, 1936, 384),
                    look(3.8f, 1936, 384, -1), at(6.2f, 2464, 384))),
            new Sequence(new Track(Kind.GNOLL, true, at(0, 384, 1024), at(2.6f, 1296, 1024),
                    look(3.8f, 1296, 1024, 1), at(6.8f, 2464, 1024))),
            new Sequence(new Track(Kind.RAT, false, at(0, 2464, 384), at(5.4f, 384, 384)),
                    new Track(Kind.GNOLL, true, at(0.6f, 384, 1024), at(6.8f, 2464, 1024))),
            new Sequence(new Track(Kind.RAT, false, at(0, 1520, 384), at(1.4f, 1872, 384),
                    look(2.5f, 1872, 384, -1), at(4.1f, 1520, 384)),
                    new Track(Kind.CRAB, true, at(1, 2464, 1024), at(6.1f, 384, 1024)))
    };
    private final MenuScenePainter paint = new MenuScenePainter(TitleThemeOption.SEWERS);
    private final MenuScriptActors actors = new MenuScriptActors(EVENTS, Color.valueOf("A4B5A5"), Color.valueOf("748F7D"));
    private final Rectangle clip = new Rectangle();
    private final com.badlogic.gdx.graphics.Texture drain = TextureHelper.GetSingleton().getTexture("images/tiles/sewers/decoration.png");
    private static final Color DROP = Color.valueOf("719F9D");
    private float time;

    @Override public void act(float delta) { paint.act(delta); actors.act(delta); time = (time + delta) % 3600f; }
    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        try {
            paint.base(batch, camera);
            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled() && paint.clip(batch, camera, PASSAGE, clip)) {
                try {
                    paint.wall(batch, 464, 320, 1920, 800, 96, INNER);
                    paint.wall(batch, 464, 1024, 1920, 96, 64, FAR);
                    paint.ledge(batch, 448, 1024, 1952, 64, STONE);
                    actors.draw(batch, true);
                    paint.ledge(batch, 448, 384, 1952, 96, STONE);
                    paint.doorway(batch, 1920, 384, 96, INNER);

                    batch.setColor(INNER); batch.draw(drain, 960, 480, 96, 96);
                    int drops = GameSettingsHelper.getInstance().isReducedVisualEffects() ? 2 : 4;
                    for (int i = 0; i < drops; i++) {
                        float phase = (time / 1.05f + i / (float) drops) % 1f;
                        float y = 522 - 138 * phase * phase;
                        paint.rect(batch, 1006, Math.round(y / 2) * 2, 4, Math.min(10, 522 - y), DROP, 0.7f);
                    }
                    actors.draw(batch, false);
                    paint.pier(batch, 1504, 320, 112, 176, STONE, EDGE);
                } finally { paint.endClip(batch); }
            }
            paint.pier(batch, 400, 288, 80, 832, STONE, EDGE);
            paint.pier(batch, 2368, 288, 80, 832, STONE, EDGE);
            paint.ledge(batch, 384, 1152, 2080, 128, STONE);
            paint.lamp(batch, 440, 864, LAMP, 104); paint.lamp(batch, 2416, 864, LAMP, 104);
        } finally { batch.setPackedColor(packed); }
    }
}
