package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.TeleportBolt;

public class WandOfTeleportation extends Wand {
    {
        manaCost = 4;
        name = "Wand of Teleportation";
        description = "A wand that violently teleports the first creature it strikes to another spot in the room.";
        gs = new GameSprite("images/wands/WAND_EBONY.png", 45, 45);
    }

    @Override
    public void addProjectile(float variance) {
        TeleportBolt projectile = new TeleportBolt();
        projectile.setRoom(owner.getRoom());
        projectile.isFriendly = owner.isFriendly;
        projectile.facingRight = owner.facingRight;
        projectile.x = owner.x;
        projectile.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        projectile.setOwner(owner);
        projectile.setAttackingItem(this);
        projectile.setSpeedX(owner.facingRight ? 520 + variance : -520 + variance);
        UnitHelper.getInstance().addUnit(projectile);
    }
}