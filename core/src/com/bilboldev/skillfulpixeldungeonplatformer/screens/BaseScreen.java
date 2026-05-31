package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;


public abstract class BaseScreen extends ScreenAdapter {

    private static final float FLOATING_JOYSTICK_TOUCH_WIDTH_RATIO = 0.5f;
    private static final float FLOATING_JOYSTICK_DEFAULT_MARGIN_LEFT = 72f;
    private static final float FLOATING_JOYSTICK_DEFAULT_MARGIN_BOTTOM = 64f;
    private static final float FLOATING_JOYSTICK_KNOB_SIZE = 118f;
    private static final float FLOATING_JOYSTICK_MAX_OFFSET = 110f;
    private static final float FLOATING_JOYSTICK_MOVE_THRESHOLD = 0.35f;

    int width = 2500;
    int height = 1200;
    int tile = (int) ConstantsHelper.TILE;

    public float heroSpawnX = 100;

    public boolean isBattle = false;
    public boolean showDamage = false;
    protected float maxRight = 50;
    protected float maxTop = 10;
    protected int platformMinSize = 6;
    public int maxPlants = 0;
    public float decorationDensity = 0;
    public int unitLimit = 20;

    protected SpriteBatch batch;
    protected SpriteBatch batchUI;
    public static OrthographicCamera camera;
    public static OrthographicCamera cameraPar;
    protected OrthographicCamera uiCamera;

    public String template = "kingdom";

    public static Skin skin;

    protected Stage dpadStage;
    protected Stage stageForeground;
    protected Stage stageParalex;
    public Stage stageUnits;
    protected Sprite dpad;
    protected BitmapFont font;
    protected BitmapFont fontSmall;
    private boolean initialized;
    private final Array<Texture> touchpadTextures = new Array<>();



    //protected UX ux;


    public float getMaxRight(){
        return maxRight;
    }

    public float getWallRight() {
        return  getMaxRight();
    }

    public float getMaxTop(){
        return maxTop;
    }

    public int getMinPlatSize() {return platformMinSize;}

    public int getMaxPlants() {return maxPlants;}

    public float getDecorationDensity() {return decorationDensity;}

    @Override
    public final void show() {
        if (!initialized) {
            create();
            init();
            initialized = true;
        }
    }

    @Override
    public final void render(float delta) {
        render();
    }

    public final boolean isInitialized() {
        return initialized;
    }

    public abstract void create();

    public abstract void init();

    public abstract void act(float delta);

    public abstract void render();

    public abstract void dispose();

    public abstract void resize(int width, int height);

    public Stage addTouchPad(final UnitHelper unitHelper, float width, float height){
        disposeTouchPad();
        dpadStage = new Stage(new StretchViewport(width, height, uiCamera));
        TextureRegionDrawable knobDrawable = createRoundedSquareDrawable((int) FLOATING_JOYSTICK_KNOB_SIZE,
            18,
            0,
            new Color(1f, 1f, 1f, 0.22f),
            new Color(1f, 1f, 1f, 0.22f));
        dpadStage.addActor(new FloatingJoystick(unitHelper, knobDrawable, width, height));

        return dpadStage;
    }

    protected void disposeTouchPad() {
        if (dpadStage != null) {
            dpadStage.dispose();
            dpadStage = null;
        }

        for (Texture texture : touchpadTextures) {
            texture.dispose();
        }
        touchpadTextures.clear();
    }

    private TextureRegionDrawable createRoundedSquareDrawable(int size, int cornerRadius, int borderThickness, Color fillColor, Color borderColor) {
        Texture texture = createRoundedSquareTexture(size, cornerRadius, borderThickness, fillColor, borderColor);
        touchpadTextures.add(texture);
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private Texture createRoundedSquareTexture(int size, int cornerRadius, int borderThickness, Color fillColor, Color borderColor) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();

        pixmap.setColor(borderColor);
        fillRoundedRect(pixmap, 0, 0, size, size, cornerRadius);

        int innerInset = Math.max(borderThickness, 0);
        int innerSize = Math.max(1, size - innerInset * 2);
        int innerRadius = Math.max(1, cornerRadius - innerInset);
        pixmap.setColor(fillColor);
        fillRoundedRect(pixmap, innerInset, innerInset, innerSize, innerSize, innerRadius);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private void fillRoundedRect(Pixmap pixmap, int x, int y, int width, int height, int radius) {
        int clampedRadius = Math.min(radius, Math.min(width, height) / 2);
        if (clampedRadius <= 0) {
            pixmap.fillRectangle(x, y, width, height);
            return;
        }

        pixmap.fillRectangle(x + clampedRadius, y, width - clampedRadius * 2, height);
        pixmap.fillRectangle(x, y + clampedRadius, width, height - clampedRadius * 2);
        pixmap.fillCircle(x + clampedRadius, y + clampedRadius, clampedRadius);
        pixmap.fillCircle(x + width - clampedRadius - 1, y + clampedRadius, clampedRadius);
        pixmap.fillCircle(x + clampedRadius, y + height - clampedRadius - 1, clampedRadius);
        pixmap.fillCircle(x + width - clampedRadius - 1, y + height - clampedRadius - 1, clampedRadius);
    }

    private final class FloatingJoystick extends Actor {
        private final UnitHelper unitHelper;
        private final TextureRegionDrawable knobDrawable;
        private final float knobSize;
        private final float maxOffset;
        private final float defaultNeutralX;
        private final float defaultNeutralY;
        private float neutralX;
        private float neutralY;
        private float knobOffsetX;
        private float knobOffsetY;
        private int activePointer = -1;

        private FloatingJoystick(UnitHelper unitHelper, TextureRegionDrawable knobDrawable, float stageWidth, float stageHeight) {
            this.unitHelper = unitHelper;
            this.knobDrawable = knobDrawable;
            this.knobSize = FLOATING_JOYSTICK_KNOB_SIZE;
            this.maxOffset = FLOATING_JOYSTICK_MAX_OFFSET;
            this.defaultNeutralX = FLOATING_JOYSTICK_DEFAULT_MARGIN_LEFT + knobSize * 2.5f;
            this.defaultNeutralY = FLOATING_JOYSTICK_DEFAULT_MARGIN_BOTTOM + knobSize * 2f;
            this.neutralX = defaultNeutralX;
            this.neutralY = defaultNeutralY;
            setBounds(0f, 0f, stageWidth * FLOATING_JOYSTICK_TOUCH_WIDTH_RATIO, stageHeight);

            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (!isAvailable()) {
                        return false;
                    }

                    if (activePointer != -1) {
                        return false;
                    }

                    activate(pointer, x, y);
                    return true;
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    if (!isAvailable()) {
                        reset();
                        return;
                    }

                    if (pointer != activePointer) {
                        return;
                    }

                    updateKnobOffset(x, y);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (!isAvailable()) {
                        reset();
                        return;
                    }

                    if (pointer != activePointer) {
                        return;
                    }

                    reset();
                }
            });
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (!isAvailable()) {
                if (activePointer != -1 || knobOffsetX != 0f || knobOffsetY != 0f) {
                    reset();
                }
                return;
            }

            knobDrawable.draw(batch,
                neutralX + knobOffsetX - knobSize * 0.5f,
                neutralY + knobOffsetY - knobSize * 0.5f,
                knobSize,
                knobSize);
        }

        private boolean isAvailable() {
            return unitHelper.getHero() != null && !unitHelper.getHero().isDead();
        }

        private void activate(int pointer, float x, float y) {
            activePointer = pointer;
            neutralX = clamp(x, knobSize * 0.5f, getWidth() - knobSize * 0.5f);
            neutralY = clamp(y, knobSize * 0.5f, getHeight() - knobSize * 0.5f);
            knobOffsetX = 0f;
            knobOffsetY = 0f;
            applyMovement();
        }

        private void updateKnobOffset(float x, float y) {
            knobOffsetX = clamp(x - neutralX, -maxOffset, maxOffset);
            knobOffsetY = clamp(y - neutralY, -maxOffset, maxOffset);
            applyMovement();
        }

        private void reset() {
            activePointer = -1;
            neutralX = defaultNeutralX;
            neutralY = defaultNeutralY;
            knobOffsetX = 0f;
            knobOffsetY = 0f;
            applyMovement();
        }

        private void applyMovement() {
            if (unitHelper.getHero() == null) {
                return;
            }

            if (unitHelper.getHero().isDead()) {
                unitHelper.getHero().movingLeft = false;
                unitHelper.getHero().movingRight = false;
                return;
            }

            float knobPercentX = maxOffset == 0f ? 0f : knobOffsetX / maxOffset;
            boolean moveLeft = knobPercentX <= -FLOATING_JOYSTICK_MOVE_THRESHOLD;
            boolean moveRight = knobPercentX >= FLOATING_JOYSTICK_MOVE_THRESHOLD;

            unitHelper.getHero().movingLeft = moveLeft;
            unitHelper.getHero().movingRight = moveRight;

            if (moveLeft && !unitHelper.getHero().isAttacking()) {
                unitHelper.getHero().facingRight = false;
                unitHelper.getHero().changeState(UnitState.RUNNING);
            }

            if (moveRight && !unitHelper.getHero().isAttacking()) {
                unitHelper.getHero().facingRight = true;
                unitHelper.getHero().changeState(UnitState.RUNNING);
            }
        }

        private float clamp(float value, float min, float max) {
            return Math.max(min, Math.min(max, value));
        }
    }
}
