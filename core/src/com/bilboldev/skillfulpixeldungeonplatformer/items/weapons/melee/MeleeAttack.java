package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;


public class MeleeAttack extends MeleeWeapon {
    private Float fixedMinDamage;
    private Float fixedMaxDamage;

    {
        gs = new GameSprite("images/misc/transparent.png", 1, 1);
        damage = 1;
        name = "Fists";
    }

    public MeleeAttack setDamageRange(float minDamage, float maxDamage) {
        fixedMinDamage = Math.min(minDamage, maxDamage);
        fixedMaxDamage = Math.max(minDamage, maxDamage);
        return this;
    }

    @Override
    public float min() {
        return fixedMinDamage != null ? fixedMinDamage : super.min();
    }

    @Override
    public float max() {
        return fixedMaxDamage != null ? fixedMaxDamage : super.max();
    }

    @Override
    public float getDamage() {
        if (fixedMinDamage == null || fixedMaxDamage == null) {
            return super.getDamage();
        }

        float rolledDamage = fixedMinDamage;
        if (fixedMaxDamage > fixedMinDamage) {
            rolledDamage += RandomHelper.getInstance().randomFloat(fixedMaxDamage - fixedMinDamage);
        }

        return owner != null ? rolledDamage * owner.getOutgoingDamageModifier() : rolledDamage;
    }

}

