package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ItemIdentityHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.PotionProjectile;

import java.util.ArrayList;

public class Potion extends RangedWeapon {

    private static final float THROW_SPEED_X = 700f;
    private static final float THROW_SPEED_Y = 100f;

    {
        setIdentityFamily(ItemIdentityHelper.Family.POTION);
        setUnknownDescription("An unidentified potion. Its contents are unknown until it is used.");
        damage = 0f;
        speed = 1f;
        ammo = 1;
    }

    @Override
    public String getBigDescription() {
        if (!isIdentified()) {
            return getDescription();
        }

        return Messages.get(
                "custom.generated.arg_n_nthis_is_a_stack_of_ccb755d32a",
            new Object[]{getDescription(), quantity, getName()});
    }

    @Override
    public Potion setQuantity(int quantity) {
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
    public Potion setPrefix() {
        return setPrefix((Prefix) null);
    }

    @Override
    public Potion setPrefix(Prefix prefix) {
        super.setPrefix(null);
        return this;
    }

    @Override
    public int getGoldCost() {
        return goldCost * Math.max(1, quantity);
    }

    @Override
    public void useAmmo() {
        spendPotion(false);
    }

    @Override
    public void createProjectile() {
        GameSprite projectileSprite = getGameSprite();
        if (owner == null || projectileSprite == null) {
            return;
        }

        if (owner.isInvisible()) {
            owner.setInvisible(false);
        }

        PotionProjectile potionProjectile = new PotionProjectile().setPotion(this);
        potionProjectile.setOwner(owner);
        potionProjectile.setGameSprite(projectileSprite.clone());
        potionProjectile.setAlignRotationToVelocity(false);
        potionProjectile.setRotationOffset(0f);
        potionProjectile.setSpinSpeed(720f);
        potionProjectile.x = owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        potionProjectile.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 3f;
        potionProjectile.speedY = THROW_SPEED_Y;
        potionProjectile.facingRight = owner.facingRight;
        potionProjectile.setSpeedX(owner.facingRight ? THROW_SPEED_X : -THROW_SPEED_X);
        potionProjectile.isFriendly = owner.isFriendly;
        potionProjectile.setRoom(owner.getRoom());
        UnitHelper.getInstance().addUnit(potionProjectile);

        projectileCreateSound();
        useAmmo();
    }

    public void consume() {
    }

    protected Hero getHero() {
        return UnitHelper.getInstance().getHero();
    }

    protected ArrayList<Unit> getActiveRoomUnits(String roomIdentifier) {
        ArrayList<Unit> units = new ArrayList<Unit>();
        if (roomIdentifier == null) {
            return units;
        }

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit == null || unit.showOnly() || unit.getRoom() == null || unit.isDead() || unit.getHP() < 1) {
                continue;
            }

            if (roomIdentifier.equals(unit.getRoom())) {
                units.add(unit);
            }
        }

        return units;
    }

    protected ArrayList<Unit> getActiveRoomUnits() {
        return getActiveRoomUnits(MapHelper.getInstance().getActiveRoomIdentifier());
    }

    protected ArrayList<Unit> getNearbyUnits(float horizontalTiles, float verticalTiles, boolean includeHero) {
        Hero hero = getHero();
        if (hero == null) {
            return new ArrayList<Unit>();
        }

        return getUnitsNearPosition(hero.getRoom(), hero.x, hero.y, horizontalTiles, verticalTiles, includeHero);
    }

    protected ArrayList<Unit> getUnitsNearPosition(String roomIdentifier, float originX, float originY, float horizontalTiles, float verticalTiles, boolean includeHero) {
        ArrayList<Unit> nearbyUnits = new ArrayList<Unit>();
        Hero hero = getHero();
        float originCenterX = originX + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float originCenterY = originY + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float maxDeltaX = horizontalTiles * ConstantsHelper.TILE;
        float maxDeltaY = verticalTiles * ConstantsHelper.TILE;

        for (Unit unit : getActiveRoomUnits(roomIdentifier)) {
            if (!includeHero && hero != null && unit == hero) {
                continue;
            }

            float unitCenterX = unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
            float unitCenterY = unit.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
            if (Math.abs(unitCenterX - originCenterX) <= maxDeltaX && Math.abs(unitCenterY - originCenterY) <= maxDeltaY) {
                nearbyUnits.add(unit);
            }
        }

        return nearbyUnits;
    }

    public void shatter(float impactX, float impactY, String roomIdentifier, Unit thrower, Unit directTarget) {
        identify();
    }

    protected void finishConsume() {
        spendPotion(true);
    }

    protected void spendPotion(boolean identifyPotion) {
        if (identifyPotion) {
            identify();
        }

        quantity--;
        ammo = Math.max(0, quantity);

        if (quantity < 1) {
            if (getEquipped()) {
                setEquipped(false, false);
            }
            InventoryHelper.getInstance().removeItem(this);
        }
    }
}