package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Slow;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;

public class AvalancheProjectile extends FireBolt {
    {
        gs = new GameSprite("images/misc/grey.png", 24, 24);
        damage = 5f;
        lifeSpan = 55f;
    }

    @Override
    public boolean collidesWithTerrain() {
        return true;
    }

    @Override
    public void onTerrainCollision() {
        explode();
    }

    @Override
    public void onUnitCollision(Unit target) {
        explode();
    }

    private void explode() {
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit == this || unit.showOnly() || unit.isDead()) {
                continue;
            }

            if (room == null || unit.getRoom() == null || !room.equals(unit.getRoom())) {
                continue;
            }

            if (owner != null && unit.isFriendly == owner.isFriendly) {
                continue;
            }

            float dx = unit.x - x;
            float dy = unit.y - y;
            if (dx * dx + dy * dy > ConstantsHelper.TILE * ConstantsHelper.TILE * 6.25f) {
                continue;
            }

            unit.takeDamage(owner != null ? owner : this, attackingItem, damage);
            new Slow().setPermanent(false).setDuration(2f).setOwner(unit);
        }

        EffectsHelper.getInstance().add(new TrapBurst().init(
                x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                y + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                "images/misc/grey.png",
                10f,
                10,
                30f,
                24f,
                40f,
                0.03f));
        playSound(Sounds.BLAST, 1f);
        markUsed();
    }
}