package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Charm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

import java.util.ArrayList;
import java.util.Collections;

public class Succubus extends Mob {
    {
        canFly = true;
        hp = mhp = 80;
        experience = 13;
        attackSkill = 40;
        defenseSkill = 25;
        damageReduction = 10;
        gf = new GameFilm("images/units/succubus/succubus.png", 256, 16, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1, 2};
        runFrames = new int[]{3, 4, 5, 6, 7, 8};
        attackFrames = new int[]{9, 10, 11, 11};
        dieFrames = new int[]{0};
        ai = new AgressiveAI(this) {
            private float blinkAt = 1.25f;

            @Override
            public void act(float delta) {
                blinkAt -= delta;
                super.act(delta);
            }

            @Override
            public void attacked(float delta) {
                Unit target = getOther();
                if (target == null || target.getHP() < 1 || target.getRoom() == null || getOwner().getRoom() == null || !target.getRoom().equals(getOwner().getRoom())) {
                    super.attacked(delta);
                    return;
                }

                float horizontalDistance = Math.abs(target.x - getOwner().x);
                if (horizontalDistance > ConstantsHelper.TILE * 3f && blinkAt <= 0f) {
                    ((Succubus) getOwner()).blinkNear(target);
                    getOwner().fakeAttack();
                    blinkAt = 3f;
                    return;
                }

                super.attacked(delta);
            }
        };

        speedX = 360;
        attackSpeed = 5.5f;
        weapon = new MeleeAttack().setDamageRange(15f, 25f);
        weapon.setOwner(this);
    }

    @Override
    public void attack(boolean forced) {
        if (!unitState.canAttack() && !forced) {
            return;
        }

        rangedAttack = false;
        changeState(UnitState.ATTACKING, true);
        Unit target = PhysicsHelper.getInstance().queryFirstHit(this, weapon.getHitArea());
        if (UnitHelper.getInstance().attackTarget(this, target, weapon, weapon.getDamage(), false)) {
            if (target != null && RandomHelper.getInstance().randomInt(3) == 0) {
                float charmDuration = 3f + RandomHelper.getInstance().randomInt(5);
                Charm charm = (Charm) target.getBuff(Charm.class);
                if (charm != null) {
                    charm.setPermanent(false).setDuration(charmDuration);
                } else {
                    new Charm().setPermanent(false).setDuration(charmDuration).setOwner(target);
                }
            }
            playSound(Sounds.HIT, 0.4f);
        }
        else {
            playSound(Sounds.MISS, 0.4f);
        }

        if (isCanFly()) {
            fly(false, true);
        }
    }

    private void blinkNear(Unit target) {
        Room currentRoom = MapHelper.getInstance().getRoom(room);
        if (currentRoom == null) {
            return;
        }

        ArrayList<String> candidates = new ArrayList<String>();
        for (String platform : currentRoom.getPlatforms()) {
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int tileY = Integer.parseInt(platform.split("_")[1]);
            float candidateX = tileX * ConstantsHelper.TILE;
            float distance = Math.abs(candidateX - target.x);
            if (distance < ConstantsHelper.TILE || distance > ConstantsHelper.TILE * 3f) {
                continue;
            }

            float candidateY = (tileY + 1) * ConstantsHelper.TILE;
            if (!MapHelper.getInstance().getActiveRoomIdentifier().equals(room)
                    || !com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper.getInstance().freeSpace((int) candidateX, (int) candidateY, room)) {
                continue;
            }

            candidates.add(platform);
        }

        if (candidates.isEmpty()) {
            return;
        }

        Collections.sort(candidates);
        String platform = candidates.get(RandomHelper.getInstance().randomInt(candidates.size()));
        int tileX = Integer.parseInt(platform.split("_")[0]);
        int tileY = Integer.parseInt(platform.split("_")[1]);
        x = tileX * ConstantsHelper.TILE;
        y = (tileY + 1) * ConstantsHelper.TILE;
        floorY = y;
        speedY = 0f;
        momentX = 0f;
        facingRight = x < target.x;
        PhysicsHelper.getInstance().syncBodyToUnit(this);
    }

    @Override
    public String getLibraryDescription() {
        return "A late-city skirmisher that blinks back into pressure range whenever a target gives it too much open space.";
    }
}