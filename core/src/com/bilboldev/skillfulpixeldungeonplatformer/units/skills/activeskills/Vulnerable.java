package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.FireMastery;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.WandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles.VulnerableProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles.WeakenProjectile;

public class Vulnerable extends ActiveSkill {

    {
        manaCost = 5;
    }

    public Vulnerable(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public boolean canUse(Unit owner){
        return super.canUse(owner) && owner.canAttack();
    }

    @Override
    public void use(Unit owner, Unit target){
        owner.fakeAttack();
        SoundHelper.GetSingleton().play(Sounds.ZAP, 0f, 1f);
        VulnerableProjectile fireBolt = new VulnerableProjectile();
        fireBolt.setRoom(owner.getRoom());
        fireBolt.isFriendly = owner.isFriendly;
        fireBolt.facingRight = owner.facingRight;
        fireBolt.x = owner.x;
        fireBolt.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2;
        fireBolt.setOwner(owner);
        fireBolt.setSpeedX(owner.facingRight ? 600 : -600);


        if(owner.getBuff(FireMastery.class) != null){
            fireBolt.setDamage(1.2f * fireBolt.getDamage());
        }

        if(owner.getBuff(WandMaster.class) != null){
            fireBolt.setLifeSpan(1.25f * fireBolt.getLifeSpan());
        }

        UnitHelper.getInstance().addUnit(fireBolt);

        if(owner instanceof Hero){
            EffectsHelper.getInstance().message(owner, "Abra something...", Color.WHITE, 0);
            ((Hero)owner).modifyMp(-manaCost);
        }
    }
}

