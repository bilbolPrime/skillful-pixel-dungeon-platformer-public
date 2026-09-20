package com.bilboldev.skillfulpixeldungeonplatformer.levels;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpritePose;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.TextWindow;

public class Sign {
    private static final float BURN_FADE_SECONDS = 0.65f;

    protected String message;
    protected float x, y;
    protected float visualAlpha = 1f;
    protected boolean burnOnDismiss;
    protected boolean burningOut;
    protected boolean expired;
    private transient String observedRoom;
    private transient float observedX, observedY, observedAlpha;

    public Sign(String message, float x, float y){
        this.message = message;
        this.x = x;
        this.y = y;
    }

    public void rememberDisplayed(String roomIdentifier) {
        if (!GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()) return;
        observedRoom = roomIdentifier;
        observedX = x; observedY = y; observedAlpha = visualAlpha;
    }

    public SpritePose copyObserved(String outgoingRoom, GameSprite sharedSignArt) {
        return !expired && outgoingRoom != null && outgoingRoom.equals(observedRoom) && observedAlpha > 0f
                ? sharedSignArt.copyPoseAt(observedX, observedY, observedAlpha) : null;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public Sign setBurnOnDismiss(boolean burnOnDismiss) {
        this.burnOnDismiss = burnOnDismiss;
        return this;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getVisualAlpha() {
        return visualAlpha;
    }

    public boolean isReadable() {
        return !burningOut && !expired;
    }

    public boolean isExpired() {
        return expired;
    }

    public void act(float delta) {
        if (!burningOut || expired) {
            return;
        }

        visualAlpha = Math.max(0f, visualAlpha - delta / BURN_FADE_SECONDS);
        if (visualAlpha <= 0f) {
            expired = true;
        }
    }

    public void read(){
        if (!isReadable()) {
            return;
        }

        TextWindow window = new TextWindow(1200, 100, message);
        if (burnOnDismiss) {
            window.setOnHide(this::igniteAndFade);
        }
        WindowHelper.getInstance().addWindow(window.build());
    }

    private void igniteAndFade() {
        if (!burnOnDismiss || burningOut || expired) {
            return;
        }

        burningOut = true;
        float burstX = x + ConstantsHelper.TILE / 2f;
        float burstY = y + ConstantsHelper.TILE / 2f;
        EffectsHelper.getInstance().add(new TrapBurst().init(
                burstX,
                burstY,
                "images/misc/green.png",
                11f,
                18,
                32f,
                50f,
                70f,
                0.03f));
        EffectsHelper.getInstance().add(new TrapBurst().init(
                burstX,
                burstY,
                "images/misc/yellow-dot.png",
                9f,
                12,
                26f,
                42f,
                60f,
                0.06f));
    }
}

