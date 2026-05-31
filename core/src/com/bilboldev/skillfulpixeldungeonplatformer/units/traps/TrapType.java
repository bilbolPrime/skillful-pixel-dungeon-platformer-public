package com.bilboldev.skillfulpixeldungeonplatformer.units.traps;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.library.LibraryEntry;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;

public enum TrapType implements LibraryEntry {
    TOXIC("custom.trap.toxic.name", 1, 1, "A toxic burst that injures the victim and leaves them poisoned."),
    FIRE("custom.trap.fire.name", 3, 1, "A violent flame burst that deals heavy immediate damage."),
    PARALYTIC("custom.trap.paralytic.name", (33 - 1) % 16, (33 - 1) / 16, "A paralyzing discharge that leaves the victim sluggish and easier to catch."),
    POISON("custom.trap.poison.name", 11, 1, "Releases a poison cloud that spreads outward and deals 1 damage per second to anything inside."),
    ALARM("custom.trap.alarm.name", 14, 1, "Alerts nearby enemies and puts the whole room on edge at once."),
    LIGHTNING("custom.trap.lightning.name", 5, 1, "Discharges a violent lightning strike into whoever triggers it."),
    GRIPPING("custom.trap.gripping.name", (38 - 1) % 16, (38 - 1) / 16, "Slices into the victim, causing bleeding while making movement harder."),
    SUMMONING("custom.trap.summoning.name", (40 - 1) % 16, (40 - 1) / 16, "Summons rats onto nearby platforms to overwhelm the intruder.");

    static final String TRAP_TILE_SHEET = "images/tiles/sewers/traps.png";
    static final int TRAP_TILE_ROW_OFFSET = -1;
    private final String displayNameKey;
    private final int tileX;
    private final int tileY;
    private final String description;

    TrapType(String displayNameKey, int tileX, int tileY, String description) {
        this.displayNameKey = displayNameKey;
        this.tileX = tileX;
        this.tileY = tileY;
        this.description = description;
    }

    public String getDisplayName() {
        return Messages.get(displayNameKey);
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    @Override
    public String getLibraryName() {
        return Messages.get(displayNameKey);
    }

    @Override
    public String getLibraryDescription() {
        return Messages.maybeTranslate(description);
    }

    @Override
    public GameFilm getLibraryPreview() {
        GameFilm preview = new GameFilm(TRAP_TILE_SHEET, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS, 1f);
        preview.clipSizeX = 16;
        preview.clipSizeY = 16;
        preview.yClipOffset = TRAP_TILE_ROW_OFFSET;
        preview.tileX = tileX;
        preview.tileY = tileY;
        return preview;
    }

    public static TrapType randomType() {
        TrapType[] values = values();
        return values[RandomHelper.getInstance().randomInt(values.length)];
    }

    public static TrapType fromIndex(Integer index) {
        TrapType[] values = values();
        if (index == null || index < 0 || index >= values.length) {
            return TOXIC;
        }

        return values[index];
    }
}