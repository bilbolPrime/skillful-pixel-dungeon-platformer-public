package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.FireMastery;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.GrandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.WandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Wizard;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.FireBolt;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

public class FireBoltWand extends Wand {

    {
        manaCost = 2;
        name = "Firebolt wand";
        description = "A wand capable of conjuring firebolts.";

        gs = new GameSprite("images/wands/fire-wand.png",45, 45);
    }


    @Override
    public void addProjectile(float variance){
        FireBolt fireBolt = new FireBolt();
        fireBolt.setRoom(owner.getRoom());
        fireBolt.isFriendly = owner.isFriendly;
        fireBolt.facingRight = owner.facingRight;
        fireBolt.x = owner.x;
        fireBolt.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2;
        fireBolt.setOwner(owner);
        fireBolt.setSpeedX(owner.facingRight ? 600 + variance: -600 + variance);


        if(owner.getBuff(FireMastery.class) != null){
            fireBolt.setDamage(1.2f * fireBolt.getDamage());
        }

        fireBolt.setDamage(scalePower(fireBolt.getDamage()));

        if(owner.getBuff(WandMaster.class) != null){
            fireBolt.setLifeSpan(1.25f * fireBolt.getLifeSpan());
        }

        if(owner.getBuff(GrandMaster.class) != null){
            fireBolt.setLifeSpan(1.25f * fireBolt.getLifeSpan());
            fireBolt.setDamage(1.25f * fireBolt.getDamage());
        }

        UnitHelper.getInstance().addUnit(fireBolt);
    }
}

