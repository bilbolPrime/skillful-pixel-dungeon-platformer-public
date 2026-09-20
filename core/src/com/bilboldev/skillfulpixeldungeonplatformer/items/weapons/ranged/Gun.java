package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.NewClassBurst;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;


public abstract class Gun extends RangedWeapon {
    @Override public String getTrueName() {
        return Messages.get("custom.newitems." + getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT) + ".name");
    }
    @Override public String getTrueDescription() {
        return Messages.get("custom.newitems." + getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT) + ".description");
    }

    private static int reservedProjectiles;
    private boolean releasing;
    private boolean naturalCompanionProvided;


    public final void suppressNaturalCompanion() { naturalCompanionProvided = true; }

    public final boolean claimNaturalCompanion() {
        if (naturalCompanionProvided || !supports(UnitHelper.getInstance().getHero())) return false;
        naturalCompanionProvided = true;
        return true;
    }

    public static boolean supports(Hero hero) {
        return hero != null && hero.getHeroClass() == HeroClass.MERCENARY;
    }

    private boolean validOwner(Hero hero) {
        return supports(hero) && hero == UnitHelper.getInstance().getHero() && owner == hero
                && hero.getRangedWeapon() == this && !hero.isDead() && hero.getHP() > 0 && !hero.showOnly()
                && Float.isFinite(hero.x) && Float.isFinite(hero.y) && hero.getRoom() != null
                && hero.getRoom().equals(MapHelper.getInstance().getActiveRoomIdentifier())
                && !WindowHelper.getInstance().windowOpen() && !hero.getNewClassActions().isSuspended();
    }


    public final boolean canAttemptShot(Hero hero) {
        return !releasing && validOwner(hero) && hero.canAttack()
                && (getAmmo() == 0 || hasProjectileSpace());
    }


    public final boolean hasProjectileSpace() {
        return GunProjectile.liveCount() + reservedProjectiles + projectilesPerShot() <= GunProjectile.MAX_LIVE;
    }

    public final boolean canFire(Hero hero) { return canAttemptShot(hero) && getAmmo() > 0; }

    protected GunProjectile buildProjectile(Hero hero) { return new GunProjectile(hero, this); }
    protected int projectilesPerShot() { return 1; }
    protected GunProjectile buildProjectile(Hero hero, int index) { return buildProjectile(hero); }


    public final boolean tryFire(Hero hero) {
        return tryFire(hero, 1f);
    }


    public final boolean tryFire(Hero hero, float damageMultiplier) {
        if (!Float.isFinite(damageMultiplier) || damageMultiplier <= 0f) return false;
        if (!canAttemptShot(hero)) return false;
        if (getAmmo() == 0) {
            EffectsHelper.getInstance().message(hero, Messages.get("custom.guns.empty"), Color.RED, 0f);
            SoundHelper.GetSingleton().playGunDryFire();
            return false;
        }
        releasing = true;
        int count = projectilesPerShot();
        reservedProjectiles += count;
        GunProjectile[] shots = new GunProjectile[count];
        boolean committed = false;
        try {

            for (int i = 0; i < count; i++) {
                shots[i] = buildProjectile(hero, i);
                if (shots[i] == null) return false;
                UnitHelper.getInstance().addUnit(shots[i]);
                if (!PhysicsHelper.getInstance().hasBody(shots[i])) return false;
            }
            if (!InventoryHelper.getInstance().consumeBullets(1)) return false;
            shots[0].setDamage(getDamage() * damageMultiplier);
            for (int i = 0; i < count; i++) shots[i].shareDamage(shots[0], i, count);
            committed = true;
            if (hero.isInvisible()) hero.setInvisible(false);
            EffectsHelper.getInstance().add(NewClassBurst.gunSmoke(hero,
                    shots[0].x + shots[0].getPhysicsCollisionSize() / 2f, shots[0].y + shots[0].getPhysicsCollisionSize() / 2f));
            projectileCreateSound();
            return true;
        } finally {
            if (!committed) for (GunProjectile shot : shots) if (shot != null) {
                shot.markUsed();
                UnitHelper.getInstance().removeUnit(shot);
            }
            reservedProjectiles -= count;
            releasing = false;
        }
    }

    @Override public final void createProjectile() { if (owner instanceof Hero) tryFire((Hero)owner); }
    @Override public void projectileCreateSound() { SoundHelper.GetSingleton().play(Sounds.BLAST, 0f, .35f); }
    @Override public boolean canStackAmmoInInventory() { return false; }
    @Override public boolean showsAttackAnimation() { return false; }
    @Override public int getAmmo() {

        InventoryHelper inventory = InventoryHelper.getInstance();
        return inventory == null ? 0 : inventory.getBulletCount();
    }

    @Override public final void useAmmo() { }
    @Override public final void setAmmo(int ignored) { }

    @Override public float getSpeed() {
        float classRateBonus = 0f;
        if (owner instanceof Hero && supports((Hero)owner)) {
            Hero hero = (Hero)owner;
            if (hero.hasSkill(Skills.QUICK_DRAW)) classRateBonus += .10f;
            if (hero.hasSkill(Skills.EXECUTIONER)) {
                classRateBonus += .15f;
                if ((long)hero.getHP() * 5 < (long)hero.getMaxHP() * 2) classRateBonus += .20f;
            }
        }
        return super.getSpeed() * (1f + classRateBonus);
    }

    @Override public void setEquipped(boolean equipped, boolean unequipCheck) {
        if (equipped && !supports(UnitHelper.getInstance().getHero())) return;
        super.setEquipped(equipped, unequipCheck);
    }

    private float baseMinimum() { return tier + (float)getUpgradeBonusDamage(); }
    private float baseMaximum() { return 5f * (tier + 1) + tier * (float)getUpgradeBonusDamage(); }
    private float prefixModifier() { return prefix == null ? 1f : prefix.getModifier(); }
    @Override public float min() { return applyStrengthDamage(prefixModifier() * baseMinimum()); }
    @Override public float max() { return applyStrengthDamage(prefixModifier() * baseMaximum()); }
    @Override public float getDamage() {
        float rolled = baseMinimum() + RandomHelper.getInstance().randomFloat(baseMaximum() - baseMinimum());
        return applyStrengthDamage(rolled * prefixModifier() * (owner == null ? 1f : owner.getOutgoingDamageModifier()));
    }
    @Override protected String getAmmoDescription() {
        return Messages.get("custom.guns.ammo", new Object[]{getAmmo()});
    }
}
