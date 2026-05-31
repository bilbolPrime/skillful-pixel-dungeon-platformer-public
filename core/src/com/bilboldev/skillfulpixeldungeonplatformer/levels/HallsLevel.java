package com.bilboldev.skillfulpixeldungeonplatformer.levels;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.halls.Halls;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.Succubus;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.EvilEye;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.Scorpio;

public class HallsLevel extends Level {
    {
        perRoom = 5;
    }

    @Override
    public Level generateLevel(int depth) {
        if (depth == 21) {
            return new LastShopLevel().generateLevel(depth);
        }
        if (depth == 25) {
            return new HallsBossLevel().generateLevel(depth);
        }
        if (depth == 26) {
            return new AmuletLevel().generateLevel(depth);
        }

        return super.generateLevel(depth);
    }

    @Override
    protected void spawnUnit(Room room) {
        Class<? extends Unit> toSpawn = chooseSpawnClass(depth);
        if (toSpawn == null) {
            return;
        }

        spawnUnit(toSpawn, room);
    }

    private Class<? extends Unit> chooseSpawnClass(int depth) {
        int roll;
        if (depth == 22) {
            roll = RandomHelper.getInstance().randomInt(200);
            if (roll < 100) {
                return Succubus.class;
            }
            return EvilEye.class;
        }

        if (depth == 23) {
            roll = RandomHelper.getInstance().randomInt(400);
            if (roll < 100) {
                return Succubus.class;
            }
            if (roll < 300) {
                return EvilEye.class;
            }
            return Scorpio.class;
        }

        roll = RandomHelper.getInstance().randomInt(600);
        if (roll < 100) {
            return Succubus.class;
        }
        if (roll < 300) {
            return EvilEye.class;
        }
        return Scorpio.class;
    }

    @Override
    protected void setSignMessage(int depth) {
        if (depth < 22 || depth > 24) {
            return;
        }

        entryRoom.setSignMessage(new Halls().getSignMessages()[depth - 22]);
        if (entryRoom.getSign() != null) {
            entryRoom.getSign().setBurnOnDismiss(true);
        }
    }

    @Override
    protected int chooseSpecialRoom(int depth) {
        if (depth == 22) {
            return ConstantsHelper.ROOM_MERCENARY;
        }

        return -1;
    }
}