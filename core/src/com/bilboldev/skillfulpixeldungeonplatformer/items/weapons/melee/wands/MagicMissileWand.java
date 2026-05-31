package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.GrandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.WandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ShamanBolt;

public class MagicMissileWand extends Wand {

    {
        manaCost = 3;
        name = "Magic missile wand";
        description = "A wand that launches pure bolts of arcane force.";
        gs = new GameSprite("images/wands/WAND_MAGIC_MISSILE.png", 45, 45);
    }

    @Override
    public void addProjectile(float variance) {
        ShamanBolt missile = new ShamanBolt();
        missile.setRoom(owner.getRoom());
        missile.isFriendly = owner.isFriendly;
        missile.facingRight = owner.facingRight;
        missile.x = owner.x;
        missile.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        missile.setOwner(owner);
        missile.setSpeedX(owner.facingRight ? 650 + variance : -650 + variance);
        missile.setDamage(scalePower(missile.getDamage()));

        if (owner.getBuff(WandMaster.class) != null) {
            missile.modifyLifeSpan(15f);
        }

        if (owner.getBuff(GrandMaster.class) != null) {
            missile.modifyLifeSpan(20f);
            missile.setDamage(missile.getDamage() * 1.25f);
        }

        UnitHelper.getInstance().addUnit(missile);
    }
}