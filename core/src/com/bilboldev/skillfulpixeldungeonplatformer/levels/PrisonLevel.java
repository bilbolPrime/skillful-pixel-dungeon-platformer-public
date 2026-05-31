package com.bilboldev.skillfulpixeldungeonplatformer.levels;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.QuestManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.prison.Prison;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Bat;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Brute;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Shaman;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Gnoll;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Skeleton;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Swarm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Thief;

public class PrisonLevel extends Level {
    {
        perRoom = 5;
    }

    @Override
    public Level generateLevel(int depth) {
        if (depth == 10) {
            return new PrisonBossLevel().generateLevel(depth);
        }

        Level level = super.generateLevel(depth);
        QuestManager.getInstance().onPrisonLevelGenerated(this);
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
        if (depth == 6) {
            roll = RandomHelper.getInstance().randomInt(72);
            if (roll < 40) {
                return Skeleton.class;
            }
            if (roll < 60) {
                return Thief.class;
            }
            if (roll < 70) {
                return Swarm.class;
            }
            return Shaman.class;
        }

        if (depth == 7) {
            roll = RandomHelper.getInstance().randomInt(60);
            if (roll < 30) {
                return Skeleton.class;
            }
            if (roll < 40) {
                return Shaman.class;
            }
            if (roll < 50) {
                return Thief.class;
            }
            return Swarm.class;
        }

        if (depth == 8) {
            roll = RandomHelper.getInstance().randomInt(73);
            if (roll < 30) {
                return Skeleton.class;
            }
            if (roll < 50) {
                return Shaman.class;
            }
            if (roll < 60) {
                return Gnoll.class;
            }
            if (roll < 66) {
                return Thief.class;
            }
            if (roll < 71) {
                return Swarm.class;
            }
            return Bat.class;
        }

        roll = RandomHelper.getInstance().randomInt(74);
        if (roll < 28) {
            return Skeleton.class;
        }
        if (roll < 56) {
            return Shaman.class;
        }
        if (roll < 62) {
            return Thief.class;
        }
        if (roll < 67) {
            return Swarm.class;
        }
        if (roll < 72) {
            return Bat.class;
        }
        return Brute.class;
    }

    @Override
    protected void setSignMessage(int depth) {
        entryRoom.setSignMessage(new Prison().getSignMessages()[depth - 6]);
    }
}