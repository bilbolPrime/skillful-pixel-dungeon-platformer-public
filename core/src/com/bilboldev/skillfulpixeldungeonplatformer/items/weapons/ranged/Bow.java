package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.HuntressArrowProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;

public class Bow extends RangedWeapon {
    private static final int SINGLE_SHOT_ARROWS = 1;
    private static final float PRIMARY_ARROW_Y_OFFSET = 0f;
    private static final float PRIMARY_ARROW_SPEED_Y = 30f;
    private static final float SECONDARY_ARROW_Y_OFFSET = 8f;
    private static final float SECONDARY_ARROW_SPEED_Y = 38f;
    private static final float ARROW_SPEED_X = 1640f;

    {
        gs = new GameSprite("images/misc/extracted items/Bow.png", 45, 45);
        name = "Bow";
        description = "A simple bow tuned for the ranged slot, firing a steady stream of arrows.";
        tier = 2;
        damage = 5f;
        speed = 1.1f;
        goldCost = 35;
        ammo = 1;
    }

    protected ThrownProjectile buildArrowProjectile() {
        return new HuntressArrowProjectile();
    }

    @Override
    public void createProjectile() {
        fireSkillShot(SINGLE_SHOT_ARROWS, 1f, 1f, false, false, false);
    }

    @Override
    public boolean canStackAmmoInInventory() {
        return false;
    }

    public boolean fireSingleArrow(float damageMultiplier, float accuracyMultiplier) {
        return fireSkillShot(SINGLE_SHOT_ARROWS, damageMultiplier, accuracyMultiplier, false, false, false);
    }

    public boolean fireSkillShot(int arrowsToFire,
                                 float damageMultiplier,
                                 float accuracyMultiplier,
                                 boolean kneeShot,
                                 boolean bombvoyage,
                                 boolean ironTip) {
        if (owner == null) {
            return false;
        }

        int arrowsRequired = Math.max(SINGLE_SHOT_ARROWS, arrowsToFire);
        if (getAmmo() < arrowsRequired) {
            EffectsHelper.getInstance().message(owner, "No arrows", Color.RED, 0f);
            return false;
        }

        if (owner.isInvisible()) {
            owner.setInvisible(false);
        }

        spawnArrowProjectile(PRIMARY_ARROW_Y_OFFSET, PRIMARY_ARROW_SPEED_Y, damageMultiplier, accuracyMultiplier, kneeShot, bombvoyage, ironTip);
        if (arrowsRequired > 1) {
            spawnArrowProjectile(SECONDARY_ARROW_Y_OFFSET, SECONDARY_ARROW_SPEED_Y, damageMultiplier, accuracyMultiplier, kneeShot, bombvoyage, ironTip);
        }

        SoundHelper.GetSingleton().play(Sounds.MISS, 0.4f);
        useAmmo(arrowsRequired);
        return true;
    }

    protected void spawnArrowProjectile(float yOffset, float speedY) {
        spawnArrowProjectile(yOffset, speedY, 1f, 1f, false, false, false);
    }

    protected void spawnArrowProjectile(float yOffset,
                                        float speedY,
                                        float damageMultiplier,
                                        float accuracyMultiplier,
                                        boolean kneeShot,
                                        boolean bombvoyage,
                                        boolean ironTip) {
        ThrownProjectile thrownProjectile = buildArrowProjectile().setOwner(owner).setAttackingItem(this);
        thrownProjectile.setAccuracyMultiplier(accuracyMultiplier);
        thrownProjectile.setKneeShot(kneeShot);
        thrownProjectile.setBombvoyageSplash(bombvoyage);
        thrownProjectile.setPiercesTargets(ironTip);
        if (owner instanceof Hero) {
            thrownProjectile.setDamage(getDamage() * ((Hero) owner).getBowDamageMultiplier(this) * damageMultiplier);
        } else {
            thrownProjectile.setDamage(getDamage() * damageMultiplier);
        }
        thrownProjectile.x = owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        thrownProjectile.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 3f + yOffset;
        thrownProjectile.speedY = speedY;
        thrownProjectile.facingRight = owner.facingRight;
        thrownProjectile.setSpeedX(owner.facingRight ? ARROW_SPEED_X : -ARROW_SPEED_X);
        thrownProjectile.isFriendly = owner.isFriendly;
        UnitHelper.getInstance().addUnit(thrownProjectile);
    }

    @Override
    public void useAmmo() {
        useAmmo(SINGLE_SHOT_ARROWS);
    }

    protected void useAmmo(int arrowsUsed) {
        if (owner == null) {
            return;
        }

        if (!InventoryHelper.getInstance().consumeArrows(arrowsUsed)) {
            EffectsHelper.getInstance().message(owner, "No arrows", Color.RED, 0f);
        }
    }

    @Override
    public int getAmmo() {
        InventoryHelper inventoryHelper = InventoryHelper.getInstance();
        if (inventoryHelper == null) {
            return super.getAmmo();
        }

        return inventoryHelper.getArrowCount();
    }

    @Override
    public String getBigDescription() {
        return super.getBigDescription().replace(
                "\n\nThis ranged weapon has " + getAmmo() + " ammo left.",
                "\n\nThis bow consumes arrows from your pack. It currently has access to " + getAmmo() + " arrows.");
    }
}
