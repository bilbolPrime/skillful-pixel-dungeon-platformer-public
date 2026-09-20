package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.TitleScreen;

import java.util.ArrayList;

public class LanguageSelectWindow extends Window {

    private static final float WINDOW_WIDTH = 1080f;
    private static final float WINDOW_HEIGHT = 920f;
    private static final float TITLE_HEIGHT = 120f;
    private static final float TITLE_DROP_RATIO = 0.75f;
    private static final float VIEWPORT_PADDING_X = 70f;
    private static final float VIEWPORT_PADDING_BOTTOM = 60f;
    private static final float VIEWPORT_PADDING_TOP = 48f;
    private static final float ROW_HEIGHT = 84f;
    private static final float ROW_GAP = 12f;
    private static final float VIEWPORT_HEIGHT_REDUCTION = ROW_HEIGHT + ROW_GAP;
    private static final float ROW_SIDE_PADDING = 36f;
    private static final float SCROLLBAR_WIDTH = 18f;
    private static final float SCROLLBAR_GAP = 18f;
    private static final float SCROLL_STEP = 92f;
    private static final float CONTENT_RAISE = ROW_HEIGHT;
    private static final float SCROLLBAR_START_RAISE = ROW_HEIGHT * 2f;
    private static final float SCROLLBAR_END_RAISE = ROW_HEIGHT * 0.15f;

    private final ArrayList<Languages> languages = new ArrayList<>();
    private final GlyphLayout titleLayout = new GlyphLayout();
    private final GlyphLayout rightLabelLayout = new GlyphLayout();

    private float viewportX;
    private float viewportY;
    private float viewportWidth;
    private float viewportHeight;
    private float contentHeight;
    private float scrollOffset;
    private boolean panActive;
    private boolean panMoved;
    private boolean scrollBarActive;
    private float scrollThumbGrabOffset;

    private GameSprite rowBackground;
    private GameSprite selectedRowBackground;
    private GameSprite scrollTrack;
    private GameSprite scrollThumb;

    public LanguageSelectWindow() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    @Override
    public Window build() {
        super.build();

        viewportX = x + VIEWPORT_PADDING_X;
        viewportY = y + VIEWPORT_PADDING_BOTTOM;
        viewportWidth = width - VIEWPORT_PADDING_X * 2f;
        viewportHeight = Math.max(ROW_HEIGHT,
            height - TITLE_HEIGHT - VIEWPORT_PADDING_BOTTOM - VIEWPORT_PADDING_TOP - VIEWPORT_HEIGHT_REDUCTION);

        rowBackground = new GameSprite("images/misc/grey.png", 100f, 100f, 0.24f);
        selectedRowBackground = new GameSprite("images/misc/yellow-highlight.png", 100f, 100f, 0.22f);
        scrollTrack = new GameSprite("images/misc/grey.png", SCROLLBAR_WIDTH, 100f, 0.35f);
        scrollThumb = new GameSprite("images/misc/yellow-highlight.png", SCROLLBAR_WIDTH, 100f, 0.28f);

        rebuildLanguages();
        clampScrollOffset();
        return this;
    }

    private void rebuildLanguages() {
        languages.clear();
        for (Languages language : Languages.values()) {
            languages.add(language);
        }

        contentHeight = languages.size() * ROW_HEIGHT + Math.max(0, languages.size() - 1) * ROW_GAP;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);

        String title = Messages.capitalize(Messages.maybeTranslate("Language"));
        titleLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 3.5f, title), title);
        float titleY = y + height - 50f - titleLayout.height * TITLE_DROP_RATIO;
        FontHelper.getSingleton().writeWhite(batch, 3.5f, x + width / 2f - titleLayout.width / 2f, titleY, title);

        float rowWidth = getRowWidth();
        float cursorY = getContentTopY() + scrollOffset;
        Languages currentLanguage = GameSettingsHelper.getInstance().getLanguage();

        for (Languages language : languages) {
            float rowTop = cursorY;
            float rowBottom = rowTop - ROW_HEIGHT;
            if (isVisible(rowTop, rowBottom)) {
                GameSprite background = language == currentLanguage ? selectedRowBackground : rowBackground;
                background.setWidth(Math.round(rowWidth));
                background.setHeight(Math.round(ROW_HEIGHT));
                background.setPosition(viewportX, rowBottom);
                background.draw(batch);

                float textY = rowBottom + 56f;
                Color primaryColor = language == currentLanguage ? Color.GOLD : Color.WHITE;
                Color secondaryColor = language == currentLanguage ? Color.GOLDENROD : Color.LIGHT_GRAY;
                FontHelper.getSingleton().write(primaryColor, batch, 2.6f, viewportX + ROW_SIDE_PADDING, textY, language.nativeName());

                rightLabelLayout.setText(FontHelper.getSingleton().getFont(secondaryColor, 2.15f, language.englishName()), language.englishName());
                FontHelper.getSingleton().write(secondaryColor,
                        batch,
                        2.15f,
                        viewportX + rowWidth - ROW_SIDE_PADDING - rightLabelLayout.width,
                        textY,
                        language.englishName());
            }
            cursorY -= ROW_HEIGHT + ROW_GAP;
        }

        drawScrollBar(batch);
    }

    private void drawScrollBar(Batch batch) {
        float maxScroll = getMaxScrollOffset();
        if (maxScroll <= 0f) {
            return;
        }

        float trackX = getScrollTrackX();
        scrollTrack.setPosition(trackX, getScrollTrackBottomY());
        scrollTrack.setHeight(Math.round(getScrollTrackHeight()));
        scrollTrack.draw(batch);

        float thumbHeight = getScrollThumbHeight();
        float thumbY = getScrollThumbY();
        scrollThumb.setPosition(trackX, thumbY);
        scrollThumb.setHeight(Math.round(thumbHeight));
        scrollThumb.draw(batch);
    }

    @Override
    public boolean pointerDown(float x, float y, int button) {
        scrollBarActive = false;
        panActive = false;
        panMoved = false;

        if (isInsideScrollBar(x, y)) {
            scrollBarActive = true;
            panMoved = true;
            if (isInsideScrollThumb(x, y)) {
                scrollThumbGrabOffset = y - getScrollThumbY();
            } else {
                scrollThumbGrabOffset = getScrollThumbHeight() / 2f;
                setScrollFromThumbY(y - scrollThumbGrabOffset);
            }
            return true;
        }

        panActive = isInsideViewport(x, y);
        return true;
    }

    @Override
    public boolean tap(float x, float y) {
        scrollBarActive = false;
        panActive = false;
        if (!isInsideWindow(x, y)) {
            hide();
            return true;
        }

        if (!panMoved) {
            Languages selectedLanguage = languageAt(x, y);
            if (selectedLanguage != null) {
                selectLanguage(selectedLanguage);
            }
        }

        return true;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (scrollBarActive) {
            setScrollFromThumbY(y - scrollThumbGrabOffset);
            return true;
        }

        if (!panActive) {
            return false;
        }

        if (Math.abs(deltaY) > 0f || Math.abs(deltaX) > 0f) {
            panMoved = true;
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

    private void selectLanguage(Languages selectedLanguage) {
        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        if (settings.getLanguage() == selectedLanguage) {
            hide();
            return;
        }

        settings.setLanguage(selectedLanguage);
        if (settings.isSoundFxEnabled()) {
            SoundHelper.GetSingleton().playUiClick();
        }

        WindowHelper.getInstance().hideAll();
        if (!(SkillfulPixelDungeonPlatformer.getActiveScreen() instanceof TitleScreen)
                || !((TitleScreen) SkillfulPixelDungeonPlatformer.getActiveScreen()).usesDesktopMenuScenes())
            SkillfulPixelDungeonPlatformer.transition(new TitleScreen(), true);
        WindowHelper.getInstance().addWindow(new PauseMenuWindow(false).build());
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

    private float getRowWidth() {
        return viewportWidth - SCROLLBAR_WIDTH - SCROLLBAR_GAP;
    }

    private float getContentBottomY() {
        return viewportY + CONTENT_RAISE;
    }

    private float getContentTopY() {
        return getContentBottomY() + viewportHeight;
    }

    private float getScrollTrackX() {
        return viewportX + getRowWidth() + SCROLLBAR_GAP;
    }

    private float getScrollTrackBottomY() {
        return viewportY + SCROLLBAR_START_RAISE;
    }

    private float getScrollTrackTopY() {
        return viewportY + viewportHeight + SCROLLBAR_END_RAISE;
    }

    private float getScrollTrackHeight() {
        return Math.max(0f, getScrollTrackTopY() - getScrollTrackBottomY());
    }

    private float getScrollThumbHeight() {
        float trackHeight = getScrollTrackHeight();
        if (trackHeight <= 0f) {
            return 0f;
        }

        return Math.max(72f, trackHeight * viewportHeight / contentHeight);
    }

    private float getScrollThumbY() {
        float maxScroll = getMaxScrollOffset();
        float thumbHeight = getScrollThumbHeight();
        if (maxScroll <= 0f) {
            return getScrollTrackTopY() - thumbHeight;
        }

        float scrollRatio = scrollOffset / maxScroll;
        float trackHeight = getScrollTrackHeight();
        return getScrollTrackTopY() - thumbHeight - ((trackHeight - thumbHeight) * scrollRatio);
    }

    private void setScrollFromThumbY(float thumbY) {
        float maxScroll = getMaxScrollOffset();
        if (maxScroll <= 0f) {
            scrollOffset = 0f;
            return;
        }

        float thumbHeight = getScrollThumbHeight();
        float maxThumbTravel = getScrollTrackHeight() - thumbHeight;
        if (maxThumbTravel <= 0f) {
            scrollOffset = 0f;
            return;
        }

        float trackBottomY = getScrollTrackBottomY();
        float clampedThumbY = Math.max(trackBottomY, Math.min(trackBottomY + maxThumbTravel, thumbY));
        float scrollRatio = (trackBottomY + maxThumbTravel - clampedThumbY) / maxThumbTravel;
        scrollOffset = Math.max(0f, Math.min(maxScroll, maxScroll * scrollRatio));
    }

    private Languages languageAt(float px, float py) {
        if (!isInsideViewport(px, py)) {
            return null;
        }

        float cursorY = getContentTopY() + scrollOffset;
        for (Languages language : languages) {
            float rowTop = cursorY;
            float rowBottom = rowTop - ROW_HEIGHT;
            if (px >= viewportX && px <= viewportX + getRowWidth() && py <= rowTop && py >= rowBottom) {
                return language;
            }
            cursorY -= ROW_HEIGHT + ROW_GAP;
        }

        return null;
    }

    private boolean isVisible(float top, float bottom) {
        return bottom <= getContentTopY() && top >= getContentBottomY();
    }

    private boolean isInsideScrollBar(float px, float py) {
        if (getMaxScrollOffset() <= 0f) {
            return false;
        }

        float trackX = getScrollTrackX();
        return px >= trackX && px <= trackX + SCROLLBAR_WIDTH
            && py >= getScrollTrackBottomY() && py <= getScrollTrackTopY();
    }

    private boolean isInsideScrollThumb(float px, float py) {
        if (!isInsideScrollBar(px, py)) {
            return false;
        }

        float thumbY = getScrollThumbY();
        return py >= thumbY && py <= thumbY + getScrollThumbHeight();
    }

    private boolean isInsideViewport(float px, float py) {
        return px >= viewportX && px <= viewportX + viewportWidth
                && py >= getContentBottomY() && py <= getContentTopY();
    }

    private boolean isInsideWindow(float px, float py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
}
