package com.bilboldev.skillfulpixeldungeonplatformer.units.interactable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.TimeUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpritePose;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Interactable extends Unit {
    private static final float DIALOGUE_PORTRAIT_SIZE = 200f;
    private static final long IDLE_DIALOGUE_FRAME_DURATION_MILLIS = 1000L;

    protected GameSprite gs;
    private SpritePose observedBody;
    private String observedRoom;

    @Override protected void drawBodyFilm(Batch batch) {
        super.drawBodyFilm(batch);
        observeBody(gf.copyDrawnFrame());
    }


    protected final void observeBody(SpritePose pose) {
        if (pose == null || pose.alpha <= 0f || !isVisible() || isInvisible() || isDead()
                || !GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()) return;
        OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        if (camera == null || !camera.frustum.boundsInFrustum(pose.x + pose.width / 2f,
                pose.y + pose.height / 2f, 0f, pose.width / 2f, pose.height / 2f, 0f)
                || !UnitHelper.getInstance().canSeeTarget(UnitHelper.getInstance().getHero(), this)) return;
        observedBody = pose;
        observedRoom = room;
    }

    public SpritePose copyObservedBody(String outgoingRoom) {

        if (this instanceof DisturbableGraveProp && ((DisturbableGraveProp)this).isDisturbed()) return null;
        return outgoingRoom != null && outgoingRoom.equals(room) && outgoingRoom.equals(observedRoom)
                ? observedBody : null;
    }

    public void interact(){

    }

    public boolean canInteract() {
        return true;
    }

    public GameSprite getInteractGS(){
        return gs;
    }

    public GameSprite getDialoguePortrait() {
        if (gf == null || idleFrames == null || idleFrames.length == 0 || gf.spriteString == null) {
            return gs == null ? null : gs.clone();
        }

        return new AnimatedDialoguePortrait(
                gf.spriteString,
                DIALOGUE_PORTRAIT_SIZE,
                DIALOGUE_PORTRAIT_SIZE,
                gf.getAlpha(),
                idleFrames,
                gf.clipSizeX,
                gf.clipSizeY,
                gf.yClipOffset,
                gf.tileY,
                facingRight);
    }

    private static final class AnimatedDialoguePortrait extends GameSprite {
        private final int[] frames;
        private final int clipSizeX;
        private final int clipSizeY;
        private final int yClipOffset;
        private final int tileY;
        private final boolean facingRight;

        private AnimatedDialoguePortrait(String spritePath,
                                        float width,
                                        float height,
                                        float alpha,
                                        int[] frames,
                                        int clipSizeX,
                                        int clipSizeY,
                                        int yClipOffset,
                                        int tileY,
                                        boolean facingRight) {
            super(spritePath, width, height, alpha);
            this.frames = frames == null ? new int[0] : frames.clone();
            this.clipSizeX = clipSizeX;
            this.clipSizeY = clipSizeY;
            this.yClipOffset = yClipOffset;
            this.tileY = tileY;
            this.facingRight = facingRight;
        }

        @Override
        public void draw(Batch batch) {
            if (frames.length == 0) {
                super.draw(batch);
                return;
            }

            int frameIndex = (int) ((TimeUtils.millis() / IDLE_DIALOGUE_FRAME_DURATION_MILLIS) % frames.length);
            int tileX = frames[frameIndex];

            sprite.setOrigin(originX, originY);
            sprite.setRotation(rotation);
            Color previousColor = new Color(batch.getColor());
            Color spriteColor = sprite.getColor();
            batch.setColor(previousColor.r * spriteColor.r,
                    previousColor.g * spriteColor.g,
                    previousColor.b * spriteColor.b,
                    previousColor.a * spriteColor.a * alpha);
            batch.draw(sprite.getTexture(), x, y, originX, originY, width, height, scaleX, scaleY, rotation,
                    tileX * clipSizeX, (yClipOffset + tileY) * clipSizeY, clipSizeX, clipSizeY, !facingRight, false);
            batch.setColor(previousColor);
        }
    }
}

