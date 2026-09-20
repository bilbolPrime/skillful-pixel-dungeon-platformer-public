package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

public class ExitRoom extends Room{
    {
        width = 15;
        canSpawn = false;
    }

    public ExitRoom(String identifier) {
        super(identifier);
    }

    public ExitRoom buildFoyer(int depth) {
        RoomFoyers.build(this, depth, false);
        return this;
    }
}

