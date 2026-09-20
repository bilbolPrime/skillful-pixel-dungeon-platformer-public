package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import static com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuScriptActors.*;


final class SewersTitleActors {
    private static final float LOWER = 456f, UPPER = 688f;
    private static final Color NEAR_TINT = new Color(0.77f, 0.84f, 0.73f, 1f);
    private static final Color FAR_TINT = new Color(0.48f, 0.59f, 0.51f, 1f);


    private static final Sequence[] SEQUENCES = {

            new Sequence(
                    new Track(Kind.RAT, false, at(0.4f, 1024, LOWER), at(1.9f, 1416, LOWER),
                            at(2.25f, 1416, LOWER), at(2.7f, 1488, LOWER), jump(3.25f, 1736, LOWER, 96), at(5.6f, 2360, LOWER)),
                    new Track(Kind.GNOLL, true, at(0, 1472, UPPER), at(2.2f, 1784, UPPER),
                            at(2.7f, 1784, UPPER), at(6, 2192, UPPER))),

            new Sequence(
                    new Track(Kind.RAT, false, at(0, 1024, LOWER), at(1.8f, 1488, LOWER),
                            jump(2.35f, 1736, LOWER, 96), at(4.7f, 2360, LOWER)),
                    new Track(Kind.RAT, false, at(0.7f, 1024, LOWER), at(2.5f, 1488, LOWER),
                            jump(3.05f, 1736, LOWER, 112), at(5.4f, 2360, LOWER)),
                    new Track(Kind.GNOLL, true, at(0, 2192, UPPER), at(2.2f, 1864, UPPER),
                            at(3.5f, 1864, UPPER), at(6.2f, 1472, UPPER))),

            new Sequence(
                    new Track(Kind.CRAB, false, at(0, 2320, LOWER), at(2, 1816, LOWER),
                            claws(3.2f, 1816, LOWER), at(5, 2320, LOWER)),
                    new Track(Kind.RAT, true, at(1, 1472, UPPER), at(4, 2192, UPPER))),

            new Sequence(
                    new Track(Kind.RAT, false, at(0, 2320, LOWER), at(2, 1752, LOWER),
                            at(2.4f, 1752, LOWER), jump(3.1f, 1424, LOWER, 112), at(4.6f, 1024, LOWER)),
                    new Track(Kind.CRAB, false, at(1.4f, 2320, LOWER), at(3.5f, 1832, LOWER),
                            claws(4.2f, 1832, LOWER), at(6, 2320, LOWER)),
                    new Track(Kind.GNOLL, true, at(2, 1472, UPPER), at(7, 2192, UPPER))),

            new Sequence(
                    new Track(Kind.RAT, false, at(0, 1024, LOWER), at(0.8f, 1208, LOWER),
                            jump(1.45f, 1296, 600, 48), at(2, 1296, 600), at(2.5f, 1376, 600),
                            look(3, 1376, 600, -1), jump(3.65f, 1160, LOWER, 112), at(4.3f, 1024, LOWER)),
                    new Track(Kind.GNOLL, true, at(0, 2192, UPPER), at(1.6f, 1944, UPPER),
                            look(2.3f, 1944, UPPER, 1), at(4.1f, 2192, UPPER))),

            new Sequence(
                    new Track(Kind.RAT, false, at(0.2f, 2320, LOWER), at(2.4f, 1760, LOWER),
                            jump(3.1f, 1424, LOWER, 104), at(4.9f, 1024, LOWER)),
                    new Track(Kind.GNOLL, true, at(0, 1472, UPPER), at(5.4f, 2192, UPPER)),
                    new Track(Kind.RAT, true, at(1.8f, 2192, UPPER), at(4.4f, 1472, UPPER)))
    };

    private final MenuScriptActors playback = new MenuScriptActors(SEQUENCES, NEAR_TINT, FAR_TINT);

    void act(float delta) { playback.act(delta); }
    void draw(Batch batch, boolean far) { playback.draw(batch, far); }
}
