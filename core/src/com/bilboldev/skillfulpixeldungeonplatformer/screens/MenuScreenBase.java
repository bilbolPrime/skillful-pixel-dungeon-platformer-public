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
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AmbientMusicHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.IntroHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.InputGestureListener;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Kingdom;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.AmbientSound;

import java.util.ArrayList;

public abstract class MenuScreenBase extends BaseScreen {

    private static final float TITLE_CORNER_BUTTON_SIZE = 100f;
    private static final float TITLE_CORNER_BUTTON_MARGIN = 40f;
    protected final ArrayList<ActionButton> buttons = new ArrayList<>();
    protected AmbientSound ambientSound;
    private GameSprite black;
    private float darknessAlpha;
    private ActionButton pressedMenuButton;

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

        cameraPar = new OrthographicCamera();
        cameraPar.setToOrtho(false, 800, 480);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, width, height);
        GameHelper.GetSingleton().setUICamera(uiCamera);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.input.setCatchKey(Input.Keys.ESCAPE, true);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                    if (WindowHelper.getInstance().windowOpen() && WindowHelper.getInstance().handleKeyDown(keycode)) {
                        return true;
                    }

                    return handleBackAction();
                }

                return false;
            }
        });
        inputMultiplexer.addProcessor(new GestureDetector(WindowHelper.getInstance().inputGestureListener()));
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

    @Override
    public final void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.x = getCameraX();
        GameHelper.GetSingleton().getCamera().position.x = getCameraX();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        IntroHelper.getInstance().draw(batch);
        batch.end();

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

    protected void drawOverlay(Batch batch) {
        black.draw(batch);
    }

    protected void drawButtons(Batch batch) {
        for (ActionButton button : buttons) {
            button.draw(batch);
        }
    }

    protected void addTitleBackButton() {
        buttons.add(new TitleBackButton());
    }

    protected Vector3 projectToUi(float x, float y) {
        return GameHelper.GetSingleton().getUICamera().unproject(new Vector3(x, y, 0f));
    }

    protected boolean handleTouchDown(float x, float y, int pointer, int button) {
        Vector3 projected = projectToUi(x, y);
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

    }

    private void clearPressedMenuButton() {
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
        private Runnable action;
        private boolean playable = true;
        private boolean silhouette;
        private float displayAlpha = 1f;

        public IconButton(String text, float x, float y, float width, float height, String notPressed, String pressed) {
            super(x, y, width, height, notPressed, pressed);
            enableUiPressFeedback();
            this.text = text;
            this.displayText = Messages.maybeTranslate(text);
            textLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 3), displayText);
            this.textOffsetX = width / 2f - textLayout.width / 2f;
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
            Color previousColor = new Color(batch.getColor());
            batch.setColor(previousColor.r, previousColor.g, previousColor.b, previousColor.a * displayAlpha);
            if (silhouette) {
                batch.setColor(0f, 0f, 0f, previousColor.a * SILHOUETTE_ALPHA * displayAlpha);
            }

            super.draw(batch);
            FontHelper.getSingleton().writeWhite(batch, 3, x + textOffsetX, y - 25, displayText);
            batch.setColor(previousColor);
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