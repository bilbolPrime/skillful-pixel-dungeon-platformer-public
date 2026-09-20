package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PoisonCloud;

import java.util.ArrayList;

public class YogDzewa extends Mob {
    private static final int MAX_LARVAE = 5;

    private boolean fistsSpawned;

    {
        boss = true;
        hp = mhp = 300;
        experience = 50;
        gf = new GameFilm("images/units/yog/yog.png", 256, 32, 1f);
        gf.clipSizeY = 19;
        gf.clipSizeX = 20;
        idleFrames = new int[]{0, 1, 2, 3, 4, 5, 6};
        runFrames = new int[]{0, 1, 2, 3, 4, 5, 6};
        attackFrames = new int[]{0, 1, 2, 3, 4, 5, 6};
        dieFrames = new int[]{7, 8, 9};
        ai = new AI(this) {
            @Override
            public void wander(float delta) {
                getOwner().movingLeft = false;
                getOwner().movingRight = false;
                Unit target = UnitHelper.getInstance().findTarget(getOwner(), ConstantsHelper.TILE * 12f);
                if (target != null) {
                    setOther(target);
                    getOwner().facingRight = getOwner().x < target.x;
                }
            }

            @Override
            public void attacked(float delta) {
                getOwner().movingLeft = false;
                getOwner().movingRight = false;
            }
        };

        speedX = 0;
        attackSpeed = 1f;
        weapon = (MeleeAttack) new MeleeAttack().setDamage(1).setOwner(this);
    }

    public void spawnFists() {
        if (fistsSpawned) {
            return;
        }

        fistsSpawned = true;
        spawnFist(new RottingFist(), -5);
        spawnFist(new BurningFist(), 5);
    }

    private void spawnFist(Mob fist, int tileOffset) {
        fist.x = x + tileOffset * ConstantsHelper.TILE;
        fist.y = y;
        fist.floorY = y;
        fist.setRoom(room);
        fist.facingRight = tileOffset < 0;
        UnitHelper.getInstance().addUnit(fist);
    }

    @Override
    public void takeDamage(Unit source, Weapon damagingItem, float damage) {
        int fists = countLivingFists();
        if (fists > 0) {
            damage = Math.max(1f, damage / (float) (1 << fists));
        }

        super.takeDamage(source, damagingItem, damage);



        Unit cloudOwner = source instanceof PoisonCloud ? ((PoisonCloud) source).getOwner() : null;
        boolean guardianGas = cloudOwner instanceof RottingFist
                && room != null && room.equals(cloudOwner.getRoom())
                && cloudOwner.isFriendly == isFriendly;
        if (!isDead() && !guardianGas) {
            spawnLarvaNear(source);
        }
    }

    private int countLivingFists() {
        int fists = 0;
        for (Unit unit : new ArrayList<Unit>(UnitHelper.getInstance().getUnits())) {
            if (unit.getRoom() == null || !unit.getRoom().equals(room) || unit.isDead()) {
                continue;
            }

            if (unit instanceof RottingFist || unit instanceof BurningFist) {
                fists++;
            }
        }
        return fists;
    }

    private void spawnLarvaNear(Unit source) {
        if (countLivingLarvae() >= MAX_LARVAE) {
            return;
        }

        int[] offsets = new int[]{-2, -1, 1, 2};
        for (int offset : offsets) {
            float spawnX = x + offset * ConstantsHelper.TILE;
            if (!UnitHelper.getInstance().freeSpace(this, (int) spawnX, (int) y, room)) {
                continue;
            }

            Larva larva = new Larva();
            larva.x = spawnX;
            larva.y = y;
            larva.floorY = y;
            larva.setRoom(room);
            larva.facingRight = source == null || spawnX < source.x;
            UnitHelper.getInstance().addUnit(larva);
            return;
        }
    }

    private int countLivingLarvae() {
        int larvae = 0;
        for (Unit unit : new ArrayList<Unit>(UnitHelper.getInstance().getUnits())) {
            if (unit.getRoom() == null || !unit.getRoom().equals(room) || unit.isDead()) {
                continue;
            }

            if (unit instanceof Larva) {
                larvae++;
            }
        }
        return larvae;
    }

    @Override
    public void die() {
        super.die();
        Room currentRoom = MapHelper.getInstance().getRoom(room);
        if (currentRoom == null) {
            return;
        }

        currentRoom.markBossDefeated();

        for (Door door : currentRoom.getDoors()) {
            door.unlock();
        }
    }

    @Override
    public String getLibraryDescription() {
        return "The Halls boss. Yog is a stationary horror that hides behind its fists and spills fresh larvae into any opening in the fight.";
    }
}
