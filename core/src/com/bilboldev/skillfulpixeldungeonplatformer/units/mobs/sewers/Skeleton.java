package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.DifficultyHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;

import java.util.ArrayList;

public class Skeleton extends Mob {
    private boolean burstTriggered;

    {
        hp = mhp = 25;
        experience = 5;
        attackSkill = 12;
        defenseSkill = 9;
        damageReduction = 5;
        gf = new GameFilm("images/units/skeleton/skeleton.png", 256, 32, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{4, 5, 6, 7};
        attackFrames = new int[]{2, 3, 3, 3};
        dieFrames = new int[]{8, 9, 10, 11, 12, 13, 14};
        ai = new AgressiveAI(this);

        speedX = 325;
        attackSpeed = 4.5f;
        weapon = new MeleeAttack().setDamageRange(3f, 8f);
        weapon.setOwner(this);
    }

    @Override
    public void die() {
        if (!burstTriggered) {
            burstTriggered = true;
            for (Unit unit : new ArrayList<Unit>(UnitHelper.getInstance().getUnits())) {
                if (unit == this || unit.isDead() || unit.showOnly()) {
                    continue;
                }

                if (room == null || unit.getRoom() == null || !room.equals(unit.getRoom())) {
                    continue;
                }

                if (Math.abs(unit.x - x) > ConstantsHelper.TILE || Math.abs(unit.y - y) > ConstantsHelper.TILE) {
                    continue;
                }

                unit.takeDamage(this, null, DifficultyHelper.getInstance().scaleEnemyDamage(this, weapon.getDamage()));
            }
        }

        super.die();
    }

    @Override
    public String getLibraryDescription() {
        return "A durable late-sewer brute that cracks apart in a burst of bone shards when it finally goes down.";
    }
}
