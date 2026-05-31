package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.JournalCatalogHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class JournalWindow extends Window {

    private static final float WINDOW_WIDTH = 1500f;
    private static final float WINDOW_HEIGHT = 1000f;
    private static final float TITLE_HEIGHT = 130f;
    private static final float VIEWPORT_PADDING_X = 60f;
    private static final float VIEWPORT_PADDING_BOTTOM = 55f;
    private static final float VIEWPORT_PADDING_TOP = 35f;
    private static final float ROW_GAP = 14f;
    private static final float SECTION_GAP = 24f;
    private static final float SECTION_HEADER_HEIGHT = 56f;
    private static final float MIN_ROW_HEIGHT = 118f;
    private static final float ROW_TOP_PADDING = 24f;
    private static final float ICON_SIZE = 72f;
    private static final float TEXT_X = 120f;
    private static final float SCROLLBAR_WIDTH = 18f;
    private static final float SCROLL_STEP = 96f;
    private static final float TITLE_ICON_SIZE = 52f;
    private static final float TITLE_Y_SHIFT = TITLE_ICON_SIZE;
    private static final float SECTION_TITLE_X_OFFSET = 28f;
    private static final float TITLE_ICON_RIGHT_SHIFT = TITLE_ICON_SIZE * 0.1f;
    private static final float TITLE_ICON_DOWN_SHIFT = TITLE_ICON_SIZE * 0.1f;

    private final ArrayList<DisplaySection> sections;

    private float viewportX;
    private float viewportY;
    private float viewportWidth;
    private float viewportHeight;
    private float contentHeight;
    private float scrollOffset;
    private boolean panActive;

    private GameSprite titleIcon;
    private GameSprite rowBackground;
    private GameSprite scrollTrack;
    private GameSprite scrollThumb;

    public JournalWindow() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
        sections = new ArrayList<DisplaySection>();
    }

    @Override
    public Window build() {
        super.build();

        viewportX = x + VIEWPORT_PADDING_X;
        viewportY = y + VIEWPORT_PADDING_BOTTOM;
        viewportWidth = width - VIEWPORT_PADDING_X * 2f;
        viewportHeight = height - TITLE_HEIGHT - VIEWPORT_PADDING_BOTTOM - VIEWPORT_PADDING_TOP;

        titleIcon = new GameSprite("images/buttons/journal.png", TITLE_ICON_SIZE, TITLE_ICON_SIZE);
        titleIcon.setPosition(x + 70f + TITLE_ICON_RIGHT_SHIFT, y + height - 84f - TITLE_Y_SHIFT - TITLE_ICON_DOWN_SHIFT);

        rowBackground = new GameSprite("images/misc/grey.png", 100f, 100f, 0.24f);
        scrollTrack = new GameSprite("images/misc/grey.png", SCROLLBAR_WIDTH, 100f, 0.35f);
        scrollThumb = new GameSprite("images/misc/yellow-highlight.png", SCROLLBAR_WIDTH, 100f, 0.28f);

        rebuildEntries();
        clampScrollOffset();
        return this;
    }

    private void rebuildEntries() {
        sections.clear();
        contentHeight = 0f;

        for (JournalCatalogHelper.Section section : JournalCatalogHelper.buildSections()) {
            DisplaySection displaySection = new DisplaySection(section.title, section.identifiedCount, section.totalCount());
            contentHeight += SECTION_HEADER_HEIGHT;

            for (JournalCatalogHelper.Entry entry : section.entries) {
                DisplayEntry displayEntry = new DisplayEntry(entry, getEntryWidth());
                displaySection.entries.add(displayEntry);
                contentHeight += displayEntry.rowHeight + ROW_GAP;
            }

            sections.add(displaySection);
            contentHeight += SECTION_GAP;
        }

        if (contentHeight > 0f) {
            contentHeight -= SECTION_GAP;
        }
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);

        titleIcon.draw(batch);
        FontHelper.getSingleton().write(Color.GOLD, batch, 3.5f, x + 145f, y + height - 44f - TITLE_Y_SHIFT, Messages.maybeTranslate("Journal"));

        float cursorY = viewportY + viewportHeight + scrollOffset;
        for (DisplaySection section : sections) {
            float headerTop = cursorY;
            float headerBottom = headerTop - SECTION_HEADER_HEIGHT;
            if (isVisible(headerTop, headerBottom)) {
            FontHelper.getSingleton().write(Color.GOLD, batch, 2.5f, viewportX + SECTION_TITLE_X_OFFSET, headerTop - 18f, section.title);
                FontHelper.getSingleton().write(Color.WHITE, batch, 1.85f, viewportX + 260f, headerTop - 18f,
                Messages.get("custom.ui.journal.identified_count", new Object[]{section.identifiedCount, section.totalCount}));
            }
            cursorY = headerBottom;

            for (DisplayEntry entry : section.entries) {
                float rowTop = cursorY;
                float rowBottom = rowTop - entry.rowHeight;
                if (isVisible(rowTop, rowBottom)) {
                    drawEntry(batch, entry, rowBottom);
                }
                cursorY -= entry.rowHeight + ROW_GAP;
            }

            cursorY -= SECTION_GAP;
        }

        drawScrollBar(batch);
    }

    private void drawEntry(Batch batch, DisplayEntry entry, float rowBottom) {
        float rowWidth = getEntryWidth();
        rowBackground.setWidth(Math.round(rowWidth));
        rowBackground.setHeight(Math.round(entry.rowHeight));
        rowBackground.setPosition(viewportX, rowBottom);
        rowBackground.draw(batch);

        entry.icon.setPosition(viewportX + 18f, rowBottom + (entry.rowHeight - ICON_SIZE) / 2f);
        Color previousColor = new Color(batch.getColor());
        if (!entry.identified) {
            batch.setColor(0f, 0f, 0f, previousColor.a);
        }
        entry.icon.draw(batch);
        batch.setColor(previousColor);

        float textX = viewportX + TEXT_X;
        float textTop = rowBottom + entry.rowHeight - ROW_TOP_PADDING;
        FontHelper.getSingleton().write(entry.identified ? Color.GOLD : Color.LIGHT_GRAY,
                batch,
                2.35f,
                textX,
                textTop,
                entry.title);
        FontHelper.getSingleton().writeRaw(entry.identified ? Color.WHITE : Color.GRAY,
                batch,
                1.9f,
                textX,
                textTop - 34f,
                entry.wrappedDescription);
    }

    private void drawScrollBar(Batch batch) {
        float maxScroll = getMaxScrollOffset();
        if (maxScroll <= 0f) {
            return;
        }

        float trackX = viewportX + viewportWidth - SCROLLBAR_WIDTH;
        scrollTrack.setPosition(trackX, viewportY);
        scrollTrack.setHeight(Math.round(viewportHeight));
        scrollTrack.draw(batch);

        float thumbHeight = Math.max(72f, viewportHeight * viewportHeight / contentHeight);
        float scrollRatio = scrollOffset / maxScroll;
        float thumbY = viewportY + viewportHeight - thumbHeight - ((viewportHeight - thumbHeight) * scrollRatio);
        thumbY += thumbHeight;
        scrollThumb.setPosition(trackX, thumbY);
        scrollThumb.setHeight(Math.round(thumbHeight));
        scrollThumb.draw(batch);
    }

    @Override
    public boolean click(float x, float y) {
        return handleTap(x, y);
    }

    @Override
    public boolean pointerDown(float x, float y, int button) {
        panActive = isInsideViewport(x, y);
        return true;
    }

    @Override
    public boolean tap(float x, float y) {
        return handleTap(x, y);
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (!panActive) {
            return false;
        }

        return scrollBy(deltaY);
    }

    @Override
    public boolean scroll(float amountY) {
        return scrollBy(amountY * SCROLL_STEP);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            hide();
            return true;
        }

        return false;
    }

    private boolean handleTap(float x, float y) {
        panActive = false;
        if (!isInsideWindow(x, y)) {
            hide();
        }
        return true;
    }

    private boolean scrollBy(float amount) {
        float maxScroll = getMaxScrollOffset();
        if (maxScroll <= 0f) {
            return false;
        }

        float previousOffset = scrollOffset;
        scrollOffset = Math.max(0f, Math.min(maxScroll, scrollOffset + amount));
        return previousOffset != scrollOffset;
    }

    private void clampScrollOffset() {
        scrollOffset = Math.max(0f, Math.min(getMaxScrollOffset(), scrollOffset));
    }

    private float getMaxScrollOffset() {
        return Math.max(0f, contentHeight - viewportHeight);
    }

    private float getEntryWidth() {
        return viewportWidth - SCROLLBAR_WIDTH - 16f;
    }

    private boolean isVisible(float top, float bottom) {
        return top <= viewportY + viewportHeight
            && top >= viewportY
            && bottom >= viewportY
            && bottom <= viewportY + viewportHeight;
    }

    private boolean isInsideViewport(float px, float py) {
        return px >= viewportX && px <= viewportX + viewportWidth
                && py >= viewportY && py <= viewportY + viewportHeight;
    }

    private boolean isInsideWindow(float px, float py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    private static class DisplaySection {
        private final String title;
        private final int identifiedCount;
        private final int totalCount;
        private final ArrayList<DisplayEntry> entries;

        private DisplaySection(String title, int identifiedCount, int totalCount) {
            this.title = title;
            this.identifiedCount = identifiedCount;
            this.totalCount = totalCount;
            this.entries = new ArrayList<DisplayEntry>();
        }
    }

    private static class DisplayEntry {
        private final String title;
        private final String wrappedDescription;
        private final GameSprite icon;
        private final boolean identified;
        private final float rowHeight;

        private DisplayEntry(JournalCatalogHelper.Entry entry, float rowWidth) {
            this.title = entry.title;
            this.identified = entry.identified;
            this.wrappedDescription = UtilsHelper.multiLine(entry.description, 2, rowWidth - TEXT_X - 40f);
            int lineCount = this.wrappedDescription.isEmpty() ? 1 : this.wrappedDescription.split("\\n").length;
            this.rowHeight = Math.max(MIN_ROW_HEIGHT, 52f + lineCount * 28f);
            this.icon = new GameSprite(entry.spritePath, ICON_SIZE, ICON_SIZE);
        }
    }
}