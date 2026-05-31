package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.TimeUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.library.LibraryEntry;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class MobInfoWindow extends Window {
    private static final long OVERLAY_FRAME_DURATION_MILLIS = 140L;
    private static final float MIN_WINDOW_WIDTH = 1650f;
    private static final float TEXT_LEFT = 260f;
    private static final float TEXT_RIGHT_PADDING = 260f;
    private static final float TEXT_LINE_HEIGHT = 55f;
    private static final float TEXT_BOTTOM_PADDING = 130f;

    private final LibraryEntry entry;
    private GameFilm preview;
    private String description;
    private float descriptionFontSize = 3f;
    private float titleFontSize = 4f;
    private long openedAtMillis;

    public MobInfoWindow(LibraryEntry entry, float width, float height) {
        super(width, height);
        this.entry = entry;
    }

    @Override
    public Window build() {
        width = Math.max(width, MIN_WINDOW_WIDTH);
        float textWidth = width - TEXT_LEFT - TEXT_RIGHT_PADDING;
        float maxOverlayHeight = ConstantsHelper.SCREEN_HEIGHT * 0.9f;
        FontHelper.FittedTextBlock fittedDescription = FontHelper.getSingleton().fitOverlayText(
            getClass().getName() + ":description",
            resolveDescriptionSource(),
            resolveDescription(),
            3f,
            textWidth,
            Math.max(60f, maxOverlayHeight - 240f - TEXT_BOTTOM_PADDING));
        description = fittedDescription.text;
        descriptionFontSize = fittedDescription.size;
        titleFontSize = FontHelper.getSingleton().fitSize(
            resolveTitle(),
            4f,
            textWidth,
            Float.POSITIVE_INFINITY);
        height = Math.min(maxOverlayHeight, Math.max(height, 240f + fittedDescription.height + TEXT_BOTTOM_PADDING));
        super.build();
        preview = entry.getLibraryPreview();
        openedAtMillis = TimeUtils.millis();
        if (preview != null) {
            preview.setPosition(x + 120, y + height - 220);
            preview.faceRight(true);
        }
        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        if (preview != null) {
            if (entry instanceof Mob) {
                int[] frames = ((Mob) entry).getLibraryOverlayFrames();
                if (frames != null && frames.length > 0) {
                    long elapsedMillis = TimeUtils.timeSinceMillis(openedAtMillis);
                    preview.tileX = frames[(int) ((elapsedMillis / OVERLAY_FRAME_DURATION_MILLIS) % frames.length)];
                }
            }
            preview.draw(batch);
        }

        FontHelper.getSingleton().writeWhiteRaw(batch, titleFontSize, x + 260, y + height - 120, resolveTitle());
        FontHelper.getSingleton().writeRaw(Color.LIGHT_GRAY, batch, descriptionFontSize, x + 260, y + height - 235, description);
    }

    private String resolveTitleSource() {
        return entry.getLibraryName();
    }

    private String resolveTitle() {
        if (entry instanceof Mob) {
            return ((Mob) entry).getResolvedLibraryName();
        }

        return Messages.maybeTranslate(entry.getLibraryName());
    }

    private String resolveDescription() {
        if (entry instanceof Mob) {
            return ((Mob) entry).getResolvedLibraryDescription();
        }

        return Messages.maybeTranslate(entry.getLibraryDescription());
    }

    private String resolveDescriptionSource() {
        return entry.getLibraryDescription();
    }
}