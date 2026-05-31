package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.FireMastery;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.GrandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.WandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.FireBall;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.FireBolt;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

public class FireBallWand extends Wand {

    {
        manaCost = 5;
        name = "Fireball wand";
        description = "A wand capable of hurling fireballs into enemies.";

        gs = new GameSprite("images/wands/fire-ball-wand.png",45, 45);
    }




    @Override
    public void addProjectile(float variance){
        FireBall fireBall = new FireBall();
        fireBall.setRoom(owner.getRoom());
        fireBall.isFriendly = owner.isFriendly;
        fireBall.facingRight = owner.facingRight;
        fireBall.x = owner.x;
        fireBall.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 3;
        fireBall.setOwner(owner);
        fireBall.setSpeedX(owner.facingRight ? 400 + variance: -400 + variance);

        if(owner.getBuff(FireMastery.class) != null){
            fireBall.setDamage(1.2f * fireBall.getDamage());
        }

        fireBall.setDamage(scalePower(fireBall.getDamage()));

        if(owner.getBuff(WandMaster.class) != null){
            fireBall.setLifeSpan(1.25f * fireBall.getLifeSpan());
        }

        if(owner.getBuff(GrandMaster.class) != null){
            fireBall.setLifeSpan(1.25f * fireBall.getLifeSpan());
            fireBall.setDamage(1.25f * fireBall.getDamage());
        }

        UnitHelper.getInstance().addUnit(fireBall);
    }
}

