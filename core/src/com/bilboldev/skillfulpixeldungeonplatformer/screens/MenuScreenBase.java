package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AmbientMusicHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.IntroHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.DesktopMenuStyle;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.InputGestureListener;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Kingdom;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.AmbientSound;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.WindowChoiceFocus;

import java.util.ArrayList;

public abstract class MenuScreenBase extends BaseScreen {

    private static final float TITLE_CORNER_BUTTON_SIZE = 100f;
    private static final float TITLE_CORNER_BUTTON_MARGIN = 40f;
    protected final ArrayList<ActionButton> buttons = new ArrayList<>();
    protected AmbientSound ambientSound;
    private GameSprite black;
    private float darknessAlpha;
    private ActionButton pressedMenuButton;
    private Viewport backgroundViewport;
    protected final WindowChoiceFocus menuFocus = new WindowChoiceFocus();

    protected boolean hostedContent;
    Button hostedSelection;
    private String hostedHeading;
    private com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages hostedHeadingLanguage;
    private FontHelper.FittedTextBlock hostedHeadingFit;

    private static final com.badlogic.gdx.utils.IntSet menuKeysHeld = new com.badlogic.gdx.utils.IntSet();

    @Override
    public final void create() {
        buttons.clear();
        WindowHelper.getInstance().hideAll();
        ambientSound = new AmbientSound().setIntro();
        AmbientMusicHelper.getSingleton().playIntro(0.4f);
        black = new GameSprite("images/misc/black.png", 5000, 5000, 1f);
        darknessAlpha = shouldFadeInOnEnter() ? 1f : 0f;
        black.setAlpha(darknessAlpha);
        IntroHelper.getInstance().generateMap(new Kingdom());

        batch = new SpriteBatch();
        batchUI = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        backgroundViewport = new ExtendViewport(width, height, camera);

        cameraPar = new OrthographicCamera();
        cameraPar.setToOrtho(false, 800, 480);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, width, height);
        GameHelper.GetSingleton().setUICamera(uiCamera);
        createUiViewport();
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.input.setCatchKey(Input.Keys.ESCAPE, true);
        for (int keycode = 0; keycode <= Input.Keys.MAX_KEYCODE; keycode++) {
            if (Gdx.input.isKeyPressed(keycode)) menuKeysHeld.add(keycode);
        }

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!ownsMenuInput()) return true;
                if (!menuKeysHeld.add(keycode)) return true;
                clearPressedMenuButton();
                if ((keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) && hasPriorityBackAction())
                    return handleBackAction();
                if (WindowHelper.getInstance().windowOpen()) {
                    WindowHelper.getInstance().handleKeyDown(keycode);
                    return true;
                }
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                    return handleBackAction();
                }

                return !isMenuInputEnabled() || handleMenuKey(keycode) || menuFocus.keyDown(null, keycode, menuChoices());
            }

            @Override
            public boolean keyUp(int keycode) { return menuKeysHeld.remove(keycode); }

            @Override public boolean touchDown(int x, int y, int pointer, int button) { return !ownsMenuInput(); }
            @Override public boolean touchUp(int x, int y, int pointer, int button) { return !ownsMenuInput(); }
            @Override public boolean touchDragged(int x, int y, int pointer) { return !ownsMenuInput(); }

            @Override public boolean scrolled(float amountX, float amountY) {
                if (!ownsMenuInput()) return true;
                if (WindowHelper.getInstance().windowOpen()) {
                    WindowHelper.getInstance().handleScroll(amountY);
                    return true;
                }
                return isMenuInputEnabled() && handleMenuScroll(amountX, amountY);
            }
        });
        inputMultiplexer.addProcessor(new GestureDetector(windowInputGestureListener()));
        inputMultiplexer.addProcessor(new GestureDetector(inputGestureListener()));
        Gdx.input.setInputProcessor(inputMultiplexer);

        createMenuContent();
    }

    protected abstract void createMenuContent();

    @Override
    public void init() {

    }

    @Override
    public void act(float delta) {
        if (darknessAlpha > 0f) {
            darknessAlpha -= 0.25f * Math.min(0.1f, delta);
            if (darknessAlpha < 0f) {
                darknessAlpha = 0f;
            }
            black.setAlpha(darknessAlpha);
        }

        if (ambientSound != null) {
            ambientSound.act(delta);
        }

        actMenu(delta);
    }

    protected void actMenu(float delta) {

    }

    protected boolean shouldFadeInOnEnter() {
        return false;
    }

    protected boolean handleBackAction() {
        return false;
    }

    protected boolean isMenuInputEnabled() { return true; }
    protected boolean ownsMenuInput() { return true; }
    protected boolean hasPriorityBackAction() { return false; }
    protected boolean handleMenuKey(int keycode) { return false; }
    protected boolean handleMenuScroll(float amountX, float amountY) { return false; }

    protected InputGestureListener windowInputGestureListener() {
        return WindowHelper.getInstance().inputGestureListener();
    }

    @Override
    public final void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        backgroundViewport.apply(false);
        camera.position.x = getCameraX();
        GameHelper.GetSingleton().getCamera().position.x = getCameraX();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawMenuBackground(batch);
        batch.end();

        uiViewport.apply(false);
        uiCamera.update();
        batchUI.setProjectionMatrix(uiCamera.combined);
        batchUI.begin();
        drawMenu(batchUI);
        WindowHelper.getInstance().draw(batchUI);
        drawOverlay(batchUI);
        batchUI.end();

        act(Gdx.graphics.getDeltaTime());
    }

    protected float getCameraX() {
        return 1250f;
    }

    protected abstract void drawMenu(Batch batch);

    protected void drawMenuBackground(Batch batch) {
        IntroHelper.getInstance().draw(batch);
    }

    protected void drawOverlay(Batch batch) {
        black.setPosition(uiCamera.position.x - uiCamera.viewportWidth / 2f,
                uiCamera.position.y - uiCamera.viewportHeight / 2f);
        black.setWidth((int) Math.ceil(uiCamera.viewportWidth));
        black.setHeight((int) Math.ceil(uiCamera.viewportHeight));
        black.draw(batch);
    }

    protected void drawButtons(Batch batch) {
        for (ActionButton button : buttons) {
            button.draw(batch);
        }
        drawMenuFocus(batch);
    }

    protected void drawMenuFocus(Batch batch) {
        if (hostedContent) return;
        if (isMenuInputEnabled() && !WindowHelper.getInstance().windowOpen()) {
            Button hovered = hoveredMenuButton();
            if (hovered != null) menuFill(batch, hovered.x + 8f, hovered.y + 6f, hovered.getWidth() - 16f, 4f, Color.GOLDENROD);
            menuFocus.draw(null, batch, menuChoices());
        }
    }

    private ArrayList<ActionButton> menuChoices() {
        ArrayList<ActionButton> choices = new ArrayList<ActionButton>(buttons);
        if (choices.size() > 1 && choices.get(0) instanceof TitleBackButton) choices.add(choices.remove(0));
        return choices;
    }

    private Button hoveredMenuButton() {
        Vector3 point = projectToUi(Gdx.input.getX(), Gdx.input.getY());
        for (Button button : buttons) if (button.isHitProjected(point.x, point.y)) return button;
        return null;
    }

    protected Button selectedMenuButton() {
        if (hostedContent) return hostedSelection;
        if (WindowHelper.getInstance().windowOpen()) return null;
        return menuFocus.isKeyboardActive() ? menuFocus.focused(menuChoices()) : hoveredMenuButton();
    }

    protected static void menuFill(Batch batch, float x, float y, float width, float height, Color color) {
        Color old = new Color(batch.getColor());
        batch.setColor(color);
        batch.draw(TextureHelper.GetSingleton().getSolidPixel(), x, y, width, height);
        batch.setColor(old);
    }

    protected static void drawMenuPanel(Batch batch, float x, float y, float width, float height) {
        menuFill(batch, x + 6f, y - 6f, width, height, new Color(0f, 0f, 0f, 0.5f));
        menuFill(batch, x, y, width, height, new Color(0.025f, 0.045f, 0.055f, 0.92f));
        Color edge = new Color(0.32f, 0.40f, 0.41f, 1f);
        menuFill(batch, x, y, width, 2f, edge);
        menuFill(batch, x, y + height - 2f, width, 2f, edge);
        menuFill(batch, x, y, 2f, height, edge);
        menuFill(batch, x + width - 2f, y, 2f, height, edge);
    }

    protected void drawMenuHeading(Batch batch, String title, float baseline) {
        FontHelper.FittedTextBlock text;
        if (hostedContent) {
            if (!title.equals(hostedHeading) || Messages.lang() != hostedHeadingLanguage) {
                hostedHeading = title; hostedHeadingLanguage = Messages.lang();
                hostedHeadingFit = FontHelper.getSingleton().fitLabelToBounds(Messages.maybeTranslate(title), 3.2f, 1800f, 70f);
            }
            text = hostedHeadingFit;
        } else text = FontHelper.getSingleton().fitOverlayText("menu-heading", title, Messages.maybeTranslate(title), 3.2f, 1800f, 70f);
        float left = (2500f - text.width) / 2f;
        if (hostedContent) {
            DesktopMenuStyle.shade(batch, left - 100, baseline - text.height - 40, text.width + 200, text.height + 100, 40, .7f);
            DesktopMenuStyle.rule(batch, left - 64, baseline - text.height - 20, text.width + 128, DesktopMenuStyle.GOLD);
        } else drawMenuPanel(batch, left - 36f, baseline - text.height - 20f, text.width + 72f, text.height + 40f);
        FontHelper.getSingleton().writeRaw(hostedContent ? DesktopMenuStyle.INK : Color.WHITE,
                batch, text.size, left, baseline, text.text);
    }

    protected void addTitleBackButton() {
        if (!hostedContent && com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer
                .getPlatformProfile().touchControlsEnabled()) buttons.add(new TitleBackButton());
    }

    protected Vector3 projectToUi(float x, float y) {
        return GameHelper.GetSingleton().getUICamera().unproject(new Vector3(x, y, 0f));
    }

    protected boolean handleTouchDown(float x, float y, int pointer, int button) {
        if (!isMenuInputEnabled()) { clearPressedMenuButton(); return true; }
        Vector3 projected = projectToUi(x, y);
        menuFocus.pointerDown(projected.x, projected.y, menuChoices());
        clearPressedMenuButton();
        for (ActionButton menuButton : buttons) {
            if (menuButton.isHitProjected(projected.x, projected.y)) {
                pressedMenuButton = menuButton;
                menuButton.pressDown();
                return true;
            }
        }

        return false;
    }

    protected boolean handleTap(float x, float y) {
        if (!isMenuInputEnabled()) { clearPressedMenuButton(); return true; }
        Vector3 projected = projectToUi(x, y);
        if (pressedMenuButton != null) {
            ActionButton tappedButton = pressedMenuButton;
            pressedMenuButton = null;
            tappedButton.releasePress();
            if (tappedButton.isHitProjected(projected.x, projected.y)) {
                tappedButton.click();
                return true;
            }

            tappedButton.cancelPress();
        }

        for (ActionButton menuButton : buttons) {
            if (menuButton.isHitProjected(projected.x, projected.y)) {
                return true;
            }
        }

        return false;
    }

    protected boolean handleLongPress(float x, float y) {
        if (!isMenuInputEnabled()) { clearPressedMenuButton(); return true; }
        Vector3 projected = projectToUi(x, y);
        clearPressedMenuButton();
        for (ActionButton menuButton : buttons) {
            if (menuButton.isHitProjected(projected.x, projected.y)) {
                menuButton.longClick();
                return true;
            }
        }

        return false;
    }

    protected InputGestureListener inputGestureListener() {
        return new InputGestureListener() {
            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                return handleTouchDown(x, y, pointer, button);
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                return handleTap(x, y);
            }

            @Override
            public boolean longPress(float x, float y) {
                return handleLongPress(x, y);
            }
        };
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (batchUI != null) {
            batchUI.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        clearPressedMenuButton();
        backgroundViewport.update(width, height, false);
        camera.position.y = this.height / 2f;
        camera.update();
        resizeUiViewport(width, height);
    }

    @Override
    public void pause() {
        super.pause();
        menuKeysHeld.clear();
        clearPressedMenuButton();
    }

    @Override
    public void resume() {
        super.resume();
        menuKeysHeld.clear();
        for (int keycode = 0; keycode <= Input.Keys.MAX_KEYCODE; keycode++)
            if (Gdx.input.isKeyPressed(keycode)) menuKeysHeld.add(keycode);
        clearPressedMenuButton();
    }

    protected final void clearPressedMenuButton() {
        if (pressedMenuButton != null) {
            pressedMenuButton.cancelPress();
            pressedMenuButton = null;
        }
    }

    protected class IconButton extends ActionButton {

        private static final float SILHOUETTE_ALPHA = 0.65f;

        private final String text;
        private final GlyphLayout textLayout = new GlyphLayout();
        private final float textOffsetX;
        private final String displayText;
        private GameSprite menuIcon;
        private final FontHelper.FittedTextBlock menuLabel;
        private Runnable action;
        private boolean playable = true;
        private boolean silhouette;
        private float displayAlpha = 1f;
        private boolean panelVisible = true;

        public IconButton(String text, float x, float y, float width, float height, String notPressed, String pressed) {
            this(text, null, x, y, width, height, notPressed, pressed);
        }

        public IconButton(String text, String localizedText, float x, float y, float width, float height, String notPressed, String pressed) {
            super(x, y, width, height, notPressed, pressed);
            enableUiPressFeedback();
            this.text = text;
            this.displayText = localizedText == null ? Messages.maybeTranslate(text) : localizedText;
            textLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 3), displayText);
            this.textOffsetX = width / 2f - textLayout.width / 2f;
            float iconSize = Math.min(width - 40f, height - 90f);
            menuIcon = new GameSprite(notPressed, iconSize, iconSize);
            menuIcon.setPosition(x + (width - iconSize) / 2f, y + height - iconSize - 18f);
            menuLabel = localizedText == null
                    ? FontHelper.getSingleton().fitOverlayText("menu-icon", text, displayText, 2.6f, width - 24f, 52f)
                    : FontHelper.getSingleton().fitLabelToBounds(displayText, 2.6f, width - 24f, 52f);
        }

        protected IconButton setPlayable(boolean playable) {
            this.playable = playable;
            return this;
        }

        protected IconButton setSilhouette(boolean silhouette) {
            this.silhouette = silhouette;
            return this;
        }

        protected IconButton setDisplayAlpha(float displayAlpha) {
            this.displayAlpha = Math.max(0f, Math.min(1f, displayAlpha));
            return this;
        }

        protected IconButton setPanelVisible(boolean visible) {
            panelVisible = visible;
            return this;
        }

        protected IconButton setIcon(GameSprite icon) {
            icon.setPosition(x + (getWidth() - icon.getWidth()) / 2f,
                    menuIcon.getY() + menuIcon.getHeight() - icon.getHeight());
            menuIcon = icon;
            return this;
        }

        protected IconButton setFloorContact(float floorY, float transparentFootPadding) {
            menuIcon.setPosition(menuIcon.getX(), floorY - menuIcon.getHeight() * transparentFootPadding);
            return this;
        }

        protected IconButton setAction(Runnable action) {
            this.action = action;
            return this;
        }

        @Override
        public boolean canClick() {
            return super.canClick() && playable;
        }

        @Override
        public void click() {
            if (action != null) {
                action.run();
            }
        }

        @Override
        public void draw(Batch batch) {
            if (panelVisible) drawMenuPanel(batch, x, y, getWidth(), getHeight());
            Color previousColor = new Color(batch.getColor());
            batch.setColor(previousColor.r, previousColor.g, previousColor.b, previousColor.a * displayAlpha);
            if (silhouette) {
                batch.setColor(0f, 0f, 0f, previousColor.a * SILHOUETTE_ALPHA * displayAlpha);
            }

            menuIcon.setAlpha(isShowingPressFeedback() ? 0.75f : 1f);
            menuIcon.draw(batch);
            batch.setColor(previousColor);
            FontHelper.getSingleton().writeRaw(Color.WHITE, batch, menuLabel.size, x + (getWidth() - menuLabel.width) / 2f,
                    y + 20f + menuLabel.height, menuLabel.text);
        }
    }

    protected class TextButton extends ActionButton {

        private final String text;
        private final GlyphLayout textLayout = new GlyphLayout();
        private final float textOffsetX;
        private final String displayText;

        public TextButton(String text, float x, float y, float width, float height) {
            super(x, y, width, height, "images/misc/grey.png", "images/misc/grey.png");
            enableUiPressFeedback();
            this.text = text;
            this.displayText = Messages.maybeTranslate(text);
            textLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 3), displayText);
            this.textOffsetX = width / 2f - textLayout.width / 2f;
        }

        @Override
        public void draw(Batch batch) {
            super.draw(batch);
            FontHelper.getSingleton().writeWhite(batch, 3, x + textOffsetX, y + height / 2f + 20f, displayText);
        }
    }

    private final class TitleBackButton extends ActionButton {
        private TitleBackButton() {
            super(
                    ConstantsHelper.SCREEN_WIDTH - TITLE_CORNER_BUTTON_MARGIN - TITLE_CORNER_BUTTON_SIZE,
                    ConstantsHelper.SCREEN_HEIGHT - TITLE_CORNER_BUTTON_MARGIN - TITLE_CORNER_BUTTON_SIZE,
                    TITLE_CORNER_BUTTON_SIZE,
                    TITLE_CORNER_BUTTON_SIZE,
                    "images/menu/exit.png",
                    "images/menu/exit.png");
            enableUiPressFeedback();
        }

        @Override
        public void click() {
            handleBackAction();
        }
    }
}
