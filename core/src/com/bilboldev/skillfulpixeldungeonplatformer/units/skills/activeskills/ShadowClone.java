package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveRegistry;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.MirrorImage;

public class ShadowClone extends ActiveSkill {
    private static final float COOLDOWN_SECONDS = 20f;

    {
        manaCost = 20;
    }

    public ShadowClone(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public boolean canUse(Unit owner) {
        return super.canUse(owner) && owner instanceof Hero && owner.getRoom() != null;
    }

    @Override
    public void use(Unit owner, Unit target) {
        Hero hero = (Hero) owner;
        clearExistingClones(hero);

        MirrorImage image = new MirrorImage().initFromHero(hero);
        empowerClone(hero, image);
        image.isSummoned = true;
        image.x = hero.x + (hero.facingRight ? ConstantsHelper.UNIT_DIMENSIONS : -ConstantsHelper.UNIT_DIMENSIONS);
        image.y = hero.y;
        image.floorY = hero.floorY;
        image.facingRight = hero.facingRight;
        image.setRoom(hero.getRoom());
        image.makeFriendly();
        UnitHelper.getInstance().addUnit(image);

        Unit summonTarget = UnitHelper.getInstance().findTargetInRoom(image);
        if (summonTarget != null) {
            image.beginHunting(summonTarget);
        }

        hero.modifyMana(-manaCost);
        startCooldown(COOLDOWN_SECONDS);
        EffectsHelper.getInstance().message(hero, "Shadow clone", Color.WHITE, 0f);
    }

    private void clearExistingClones(Hero hero) {
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit instanceof MirrorImage && unit.isFriendly == hero.isFriendly && unit.isSummoned && !unit.showOnly()) {
                unit.unSummon();
            }
        }
    }

    private void empowerClone(Hero hero, MirrorImage image) {
        int cloneMaxHp = Math.max(image.getMaxHP(), Math.max(12, hero.getMaxHP() / 2));
        image.setMaxHP(cloneMaxHp);
        image.setHP(cloneMaxHp);
        image.setBaseAttackSkill(hero.getBaseAttackSkill());
        image.setBaseDefenseSkill(hero.getBaseDefenseSkill());

        Weapon heroWeapon = hero.getWeapon();
        MeleeWeapon clonedWeapon = copyMeleeWeapon(heroWeapon);
        if (clonedWeapon != null) {
            image.setWeapon(clonedWeapon);
        }
        else if (heroWeapon != null) {
            float minDamage = Math.max(4f, heroWeapon.min() * 0.75f);
            float maxDamage = Math.max(minDamage, heroWeapon.max() * 0.75f);
            image.setWeapon(new MeleeAttack().setDamageRange(minDamage, maxDamage));
        }

        Armor clonedArmor = copyArmor(hero.getArmor());
        if (clonedArmor != null) {
            image.setArmor(clonedArmor);
            image.gf.tileY = Math.max(0, clonedArmor.getTier() - 1);
        }
    }

    private MeleeWeapon copyMeleeWeapon(Weapon heroWeapon) {
        if (!(heroWeapon instanceof MeleeWeapon)) {
            return null;
        }

        Item item = SaveRegistry.createItem(SaveRegistry.getItemId(heroWeapon));
        if (!(item instanceof MeleeWeapon)) {
            return null;
        }

        MeleeWeapon clonedWeapon = (MeleeWeapon) item;
        clonedWeapon.setLevel(heroWeapon.getLevel());
        clonedWeapon.setPrefix(copyPrefix(heroWeapon.getPrefix()));
        return clonedWeapon;
    }

    private Armor copyArmor(Armor heroArmor) {
        if (heroArmor == null) {
            return null;
        }

        Item item = SaveRegistry.createItem(SaveRegistry.getItemId(heroArmor));
        if (!(item instanceof Armor)) {
            return null;
        }

        Armor clonedArmor = (Armor) item;
        clonedArmor.setLevel(heroArmor.getLevel());
        clonedArmor.setPrefix(copyPrefix(heroArmor.getPrefix()));
        return clonedArmor;
    }

    private Prefix copyPrefix(Prefix prefix) {
        if (prefix == null) {
            return null;
        }

        return SaveRegistry.createPrefix(SaveRegistry.getPrefixId(prefix));
    }
}