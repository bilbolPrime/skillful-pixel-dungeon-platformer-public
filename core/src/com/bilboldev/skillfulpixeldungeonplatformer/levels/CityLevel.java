package com.bilboldev.skillfulpixeldungeonplatformer.levels;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.QuestManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.city.City;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Elemental;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Monk;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfWarlock;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.Golem;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.Succubus;

public class CityLevel extends Level {
    {
        perRoom = 5;
    }

    @Override
    public Level generateLevel(int depth) {
        if (depth == 20) {
            return new CityBossLevel().generateLevel(depth);
        }

        Level level = super.generateLevel(depth);
        QuestManager.getInstance().onCityLevelGenerated(this);
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
        if (depth == 16) {
            roll = RandomHelper.getInstance().randomInt(220);
            if (roll < 100) {
                return Elemental.class;
            }
            if (roll < 200) {
                return DwarfWarlock.class;
            }

            return Monk.class;
        }

        if (depth == 17) {
            roll = RandomHelper.getInstance().randomInt(300);
            if (roll < 100) {
                return Elemental.class;
            }
            if (roll < 200) {
                return Monk.class;
            }

            return DwarfWarlock.class;
        }

        if (depth == 18) {
            roll = RandomHelper.getInstance().randomInt(500);
            if (roll < 100) {
                return Elemental.class;
            }
            if (roll < 300) {
                return Monk.class;
            }
            if (roll < 400) {
                return Golem.class;
            }

            return DwarfWarlock.class;
        }

        roll = RandomHelper.getInstance().randomInt(702);
        if (roll < 100) {
            return Elemental.class;
        }
        if (roll < 300) {
            return Monk.class;
        }
        if (roll < 600) {
            return Golem.class;
        }
        if (roll < 700) {
            return DwarfWarlock.class;
        }

        return Succubus.class;
    }

    @Override
    protected void setSignMessage(int depth) {
        entryRoom.setSignMessage(new City().getSignMessages()[depth - 16]);
    }
}