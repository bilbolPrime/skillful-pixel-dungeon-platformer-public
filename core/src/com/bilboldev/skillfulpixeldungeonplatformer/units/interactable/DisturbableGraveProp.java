package com.bilboldev.skillfulpixeldungeonplatformer.units.interactable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.ContactShadow;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.other.Wraith;

public abstract class DisturbableGraveProp extends Interactable {
    private static final float COLLAPSE_DURATION_SECONDS = 0.35f;
    private static final float COLLAPSE_RISE = 18f;
    private static final float COLLAPSE_ROTATION = 16f;

    private Item rewardItem;
    private boolean disturbed;
    private boolean collapsing;
    private float collapseProgress;
    private final float collapseDirection;

    protected DisturbableGraveProp(String spritePath, float spriteWidth, float spriteHeight) {
        gs = new GameSprite(spritePath, spriteWidth, spriteHeight);
        showOnly = true;
        hp = mhp = 1000;
        speedY = 0f;
        collapseDirection = (persistentId.hashCode() & 1) == 0 ? -1f : 1f;
    }

    @Override
    public void act(float delta) {
        if (disturbed && !collapsing) {
            removeUnit();
            return;
        }

        if (!collapsing) {
            return;
        }

        collapseProgress = Math.min(1f, collapseProgress + delta / COLLAPSE_DURATION_SECONDS);
        if (collapseProgress >= 1f) {
            collapsing = false;
            removeUnit();
        }
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (gs == null
                || room == null
                || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())
                || (disturbed && !collapsing)) {
            return;
        }

        float previousAlpha = gs.getAlpha();
        float previousRotation = gs.getRotation();
        float previousScaleX = gs.getScaleX();
        float previousScaleY = gs.getScaleY();

        float animationProgress = collapsing ? collapseProgress : 0f;
        float scale = 1f - animationProgress * 0.2f;
        ContactShadow.draw(batch, x + gs.getWidth() / 2f, y + animationProgress * COLLAPSE_RISE,
                gs.getWidth() * 0.85f * scale, alpha * (1f - animationProgress), false);
        gs.setPosition(x, y + animationProgress * COLLAPSE_RISE);
        gs.setRotation(previousRotation + animationProgress * COLLAPSE_ROTATION * collapseDirection);
        gs.setScale(previousScaleX * scale, previousScaleY * scale);
        gs.setAlpha(alpha * (1f - animationProgress));
        gs.draw(batch);
        if (!disturbed) observeBody(gs.copyPose());

        gs.setAlpha(previousAlpha);
        gs.setRotation(previousRotation);
        gs.setScale(previousScaleX, previousScaleY);
    }

    @Override
    public boolean canInteract() {
        return !disturbed && rewardItem != null;
    }

    public Item getRewardItem() {
        return rewardItem;
    }

    public DisturbableGraveProp setRewardItem(Item rewardItem) {
        this.rewardItem = rewardItem;
        return this;
    }

    public boolean isDisturbed() {
        return disturbed;
    }

    public DisturbableGraveProp setDisturbed(boolean disturbed) {
        this.disturbed = disturbed;
        if (disturbed) {
            rewardItem = null;
        }
        return this;
    }

    protected void disturbAndDropReward() {
        Item reward = rewardItem;
        disturbed = true;
        rewardItem = null;
        collapsing = true;
        collapseProgress = 0f;
        if (reward != null) {
            reward.spawnNaturally(x, floorY, floorY, room);
        }
        MapHelper.getInstance().refreshHeroEnvironment();
    }

    protected int spawnWraiths(int count) {
        return Wraith.spawnNear(room, x, floorY, count);
    }

    protected int spawnWraithsAroundHero(int count) {
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null) {
            return 0;
        }

        return Wraith.spawnNear(hero.getRoom(), hero.x, hero.floorY, count);
    }
}
