package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.AvalancheProjectile;

public class WandOfAvalanche extends Wand {
    {
        manaCost = 5;
        name = "Wand of Avalanche";
        description = "A wand that hurls a crashing burst of stone which breaks into a damaging shockwave on impact.";
        gs = new GameSprite("images/wands/WAND_MAHOGANY.png", 45, 45);
    }

    @Override
    protected Sounds getCastSound() {
        return Sounds.BLAST;
    }

    @Override
    public void addProjectile(float variance) {
        AvalancheProjectile projectile = new AvalancheProjectile();
        projectile.setRoom(owner.getRoom());
        projectile.isFriendly = owner.isFriendly;
        projectile.facingRight = owner.facingRight;
        projectile.x = owner.x;
        projectile.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        projectile.setOwner(owner);
        projectile.setAttackingItem(this);
        projectile.setSpeedX(owner.facingRight ? 420 + variance : -420 + variance);
        projectile.setDamage(scalePower(projectile.getDamage()));
        UnitHelper.getInstance().addUnit(projectile);
    }
}