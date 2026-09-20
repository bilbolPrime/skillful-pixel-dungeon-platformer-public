package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.projectiles.Projectile;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;


public class RangedWeapon extends Weapon {
    protected Projectile projectile;
    protected int ammo;

    public void createProjectile(){
        if(projectile != null && owner != null){
            if (owner.isInvisible()) {
                owner.setInvisible(false);
            }

            ThrownProjectile thrownProjectile = projectile.toThrownProjectile().setOwner(owner).setAttackingItem(this);
            thrownProjectile.setDamage(getDamage());
            thrownProjectile.x = owner.x;
            thrownProjectile.y = owner.y;
            thrownProjectile.x = owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2;
            thrownProjectile.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 3;
            thrownProjectile.speedY = 100;
            thrownProjectile.facingRight = owner.facingRight;
            thrownProjectile.setSpeedX(owner.facingRight ? 700 : -700);
            thrownProjectile.isFriendly = owner.isFriendly;
            UnitHelper.getInstance().addUnit(thrownProjectile);

            projectileCreateSound();
            useAmmo();
        }
    }

    public void projectileCreateSound(){
        SoundHelper.GetSingleton().play(Sounds.MISS,0.4f);
    }

    @Override
    public void draw(Batch batch, float frameAt, int totalFrames){
        if(getGameSprite() == null || owner == null){
            return;
        }

        getGameSprite().setPosition(owner.getVisualAttackX(frameAt / totalFrames), owner.getVisualAttackY());
        getGameSprite().setRotation(owner.facingRight ? -45 : 135);
        getGameSprite().draw(batch);
    }

    @Override
    public boolean showsAttackAnimation() {
        return false;
    }

    public boolean canStackAmmoInInventory() {
        return true;
    }

    public void setEquipped(boolean equipped){
        setEquipped(equipped, true);
    }

    public void setEquipped(boolean equipped, boolean unequipCheck){
        if(unequipCheck){
            unEquipCheck();
        }

        if(equipped){
            UnitHelper.getInstance().getHero().setRangedWeapon(this);
        }
        else {
            UnitHelper.getInstance().getHero().setRangedWeapon(null);
        }

        this.equipped = equipped;
    }

    public void unEquipCheck(){
        for(Item item : InventoryHelper.getInstance().getItems()){
            if(item instanceof RangedWeapon){
                ((RangedWeapon)item).setEquipped(false, false);
            }
        }
    }


    public int getAmmo(){
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = Math.max(0, ammo);
    }

    public void useAmmo(){
        ammo--;

        if(ammo < 1 && owner != null){
            owner.setRangedWeapon(null);
            if(owner instanceof Hero){
                InventoryHelper.getInstance().removeItem(this);
            }
            EffectsHelper.getInstance().message(owner, "No ammo", Color.RED, 0f);
        }
    }


    @Override
    public String getBigDescription(){

        StringBuilder info = new StringBuilder( getDescription() );

        String speedDescription = "";
        if(speed < 1f){
            speedDescription = "slow";
        }

        if(speed > 1f){
            speedDescription = "fast";
        }

        if(prefix != null){
            if (prefix.isEnhancement()) {
                info.append("\n\n").append(Messages.maybeTranslate(
                        "It bears the %s.\n%s",
                        prefix.getName().toLowerCase(),
                        prefix.getDescription()));
            }
            else {
                info.append("\n\n").append(prefix.getDescription());
            }
        }
        else {
            info.append("\n");
        }
        if(speedDescription.equals("")){
            info.append("\n").append(Messages.maybeTranslate(
                    "This %s is a tier %d ranged weapon.",
                    getName().toLowerCase(),
                    tier));
        }
        else {
            info.append("\n").append(Messages.maybeTranslate(
                    "This %s is a tier %d %s ranged weapon.",
                    getName().toLowerCase(),
                    tier,
                    Messages.maybeTranslate(speedDescription)));
        }

        info.append(" ").append(Messages.maybeTranslate(
                "It can do between %d and %d damage.",
                (int)min(),
                (int)max()));

        info.append(getStrengthRequirementText());

        info.append("\n\n").append(getAmmoDescription());

        return info.toString();
    }

    protected String getAmmoDescription() {
        return Messages.maybeTranslate("This ranged weapon has %d ammo left.", getAmmo());
    }

    @Override
    public Weapon setPrefix(){
        return super.setPrefix();
    }

    @Override
    public Weapon setPrefix(Prefix prefix){
        super.setPrefix(prefix);
        Prefix appliedPrefix = getPrefix();
        if(appliedPrefix != null){
            ammo *= appliedPrefix.getModifier();
        }
        return this;
    }
}

