package com.bilboldev.skillfulpixeldungeonplatformer.levels;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.QuestManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.caves.Caves;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Elemental;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Monk;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Spinner;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Bat;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Brute;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Shaman;

public class CavesLevel extends Level {
    {
        perRoom = 5;
    }

    @Override
    public Level generateLevel(int depth) {
        if (depth == 15) {
            return new CavesBossLevel().generateLevel(depth);
        }

        Level level = super.generateLevel(depth);
        QuestManager.getInstance().onCavesLevelGenerated(this);
        return level;
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
        if (depth == 11) {
            roll = RandomHelper.getInstance().randomInt(60);
            if (roll < 50) {
                return Bat.class;
            }

            return Brute.class;
        }

        if (depth == 12) {
            roll = RandomHelper.getInstance().randomInt(55);
            if (roll < 30) {
                return Bat.class;
            }
            if (roll < 50) {
                return Brute.class;
            }

            return Spinner.class;
        }

        if (depth == 13) {
            roll = RandomHelper.getInstance().randomInt(102);
            if (roll < 20) {
                return Bat.class;
            }
            if (roll < 65) {
                return Brute.class;
            }
            if (roll < 80) {
                return Shaman.class;
            }
            if (roll < 100) {
                return Spinner.class;
            }

            return Elemental.class;
        }

        roll = RandomHelper.getInstance().randomInt(103);
        if (roll < 15) {
            return Bat.class;
        }
        if (roll < 60) {
            return Brute.class;
        }
        if (roll < 75) {
            return Shaman.class;
        }
        if (roll < 101) {
            return Spinner.class;
        }
        if (roll == 101) {
            return Elemental.class;
        }

        return Monk.class;
    }

    @Override
    protected void setSignMessage(int depth) {
        entryRoom.setSignMessage(new Caves().getSignMessages()[depth - 11]);
    }
}