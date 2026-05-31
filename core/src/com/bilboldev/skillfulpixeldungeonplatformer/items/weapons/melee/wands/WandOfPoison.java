package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.PoisonBolt;

public class WandOfPoison extends Wand {
    {
        manaCost = 4;
        name = "Wand of Poison";
        description = "A wand that launches a venomous blast, dealing damage and poisoning what it strikes.";
        gs = new GameSprite("images/wands/WAND_YEW.png", 45, 45);
    }

    @Override
    public void addProjectile(float variance) {
        PoisonBolt projectile = new PoisonBolt();
        projectile.setRoom(owner.getRoom());
        projectile.isFriendly = owner.isFriendly;
        projectile.facingRight = owner.facingRight;
        projectile.x = owner.x;
        projectile.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        projectile.setOwner(owner);
        projectile.setAttackingItem(this);
        projectile.setSpeedX(owner.facingRight ? 520 + variance : -520 + variance);
        projectile.setDamage(scalePower(projectile.getDamage()));
        projectile.setPoisonDuration(3f + getWandPowerMultiplier() * 2f);
        UnitHelper.getInstance().addUnit(projectile);
    }
}