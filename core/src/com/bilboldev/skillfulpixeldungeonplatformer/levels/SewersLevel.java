package com.bilboldev.skillfulpixeldungeonplatformer.levels;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.QuestManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.sewers.Sewers;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Crab;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Gnoll;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Rat;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Skeleton;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Swarm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Thief;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;

public class SewersLevel extends Level {
    {
        perRoom = 5;
    }

    @Override
    protected int chooseSpecialRoom(int depth) {
        return super.chooseSpecialRoom(depth);
    }

    @Override
    protected boolean allowsGeneratedRoomWater() {
        return true;
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
        if (depth <= 1) {
            return Rat.class;
        }

        if (depth == 2) {
            return RandomHelper.getInstance().randomBoolean() ? Rat.class : Gnoll.class;
        }

        if (depth == 3) {
            if (RandomHelper.getInstance().randomChance(2)) {
                return Swarm.class;
            }

            int roll = RandomHelper.getInstance().randomInt(4);
            if (roll == 0) {
                return Rat.class;
            }

            if (roll < 3) {
                return Gnoll.class;
            }

            return Crab.class;
        }

        if (RandomHelper.getInstance().randomChance(1)) {
            return Thief.class;
        }

        if (RandomHelper.getInstance().randomChance(1)) {
            return Skeleton.class;
        }

        if (RandomHelper.getInstance().randomChance(2)) {
            return Swarm.class;
        }

        int roll = RandomHelper.getInstance().randomInt(6);
        if (roll == 0) {
            return Rat.class;
        }

        if (roll < 3) {
            return Gnoll.class;
        }

        return Crab.class;
    }

    @Override
    public Level generateLevel(int depth){
        if(depth == 5){
            return new SewersBossLevel().generateLevel(depth);
        }

        Level level = super.generateLevel(depth);
        QuestManager.getInstance().onSewersLevelGenerated(this);
        return level;
    }

    @Override
    protected void setSignMessage(int depth){
        entryRoom.setSignMessage(new Sewers().getSignMessages()[depth - 1]);
    }
}

