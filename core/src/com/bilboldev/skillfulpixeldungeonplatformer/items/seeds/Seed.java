package com.bilboldev.skillfulpixeldungeonplatformer.items.seeds;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.SeedProjectile;

public abstract class Seed extends RangedWeapon {

    private static final float THROW_SPEED_X = 700f;
    private static final float THROW_SPEED_Y = 100f;

    {
        damage = 0f;
        speed = 1f;
        ammo = 1;
        tier = 1;
        goldCost = 10;
    }

    protected abstract Plant createPlant();

    protected abstract String getPlantName();

    protected abstract String getAlchemyResultName();

    @Override
    public String getBigDescription() {
        return Messages.get(
            "custom.ui.seed.big_description",
            new Object[]{
                Messages.maybeTranslate(getPlantName()),
                getDescription(),
                Messages.maybeTranslate(getAlchemyResultName()),
                Math.max(1, quantity)});
    }

    @Override
    public Seed setQuantity(int quantity) {
        super.setQuantity(quantity);
        ammo = Math.max(0, quantity);
        return this;
    }

    @Override
    public int getRequiredStrength() {
        return 1;
    }

    @Override
    public int getAmmo() {
        return Math.max(0, quantity);
    }

    @Override
    public void setAmmo(int ammo) {
        setQuantity(ammo);
    }

    @Override
    public int getGoldCost() {
        return goldCost * Math.max(1, quantity);
    }

    @Override
    public void useAmmo() {
        spendSeed();
    }

    @Override
    public void createProjectile() {
        if (owner == null || gs == null) {
            return;
        }

        if (owner.isInvisible()) {
            owner.setInvisible(false);
        }

        SeedProjectile seedProjectile = new SeedProjectile().setSeed(this);
        seedProjectile.setOwner(owner);
        seedProjectile.setGameSprite(gs.clone());
        seedProjectile.x = owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        seedProjectile.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 3f;
        seedProjectile.speedY = THROW_SPEED_Y;
        seedProjectile.facingRight = owner.facingRight;
        seedProjectile.setSpeedX(owner.facingRight ? THROW_SPEED_X : -THROW_SPEED_X);
        seedProjectile.isFriendly = owner.isFriendly;
        seedProjectile.setRoom(owner.getRoom());
        UnitHelper.getInstance().addUnit(seedProjectile);

        projectileCreateSound();
        useAmmo();
    }

    public void plant() {
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null) {
            return;
        }

        createPlantAt(hero.getRoom(), hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f, hero.y);
        spendSeed();
    }

    public void land(float impactX, float impactY, String roomIdentifier, Unit thrower, Unit directTarget) {
        createPlantAt(roomIdentifier, impactX, impactY);
    }

    private void spendSeed() {
        quantity--;
        ammo = Math.max(0, quantity);

        if (quantity < 1) {
            if (getEquipped()) {
                setEquipped(false, false);
            }
            InventoryHelper.getInstance().removeItem(this);
        }
    }

    private void createPlantAt(String roomIdentifier, float impactX, float impactY) {
        if (roomIdentifier == null) {
            return;
        }

        Plant plant = createPlant();
        int tileX = (int) Math.floor((impactX + ConstantsHelper.TILE / 2f) / ConstantsHelper.TILE);
        float plantX = tileX * ConstantsHelper.TILE;
        float plantY = MapHelper.getInstance().calculateFloorY(plantX + ConstantsHelper.TILE / 2f, impactY);

        plant.x = plantX;
        plant.y = plantY;
        plant.floorY = plantY;
        plant.setRoom(roomIdentifier);
        UnitHelper.getInstance().addUnit(plant);
        SoundHelper.GetSingleton().play(Sounds.PLANT, 0f, 0.9f);
    }
}