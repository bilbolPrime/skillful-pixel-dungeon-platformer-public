package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;


public final class CorpseRecord {

    public static final float ART_SIZE = 64f;
    public static final float HALF_WIDTH = 30f;
    public static final float BOTTOM_PADDING = 8f;
    public final String victimId;
    public final String roomId;
    public final float deathX;
    public final float deathY;

    public final float x;
    public final float y;
    public final long order;


    public CorpseRecord(String victimId, String roomId, float deathX, float deathY, float x, float y, long order) {
        this.victimId = victimId;
        this.roomId = roomId;
        this.deathX = deathX;
        this.deathY = deathY;
        this.x = x;
        this.y = y;
        this.order = order;
    }
}
