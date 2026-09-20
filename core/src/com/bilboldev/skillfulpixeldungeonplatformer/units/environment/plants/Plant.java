package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.ContactShadow;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Plant extends Unit {

    private static final String PLANT_SHEET = "images/misc/plants.png";

    protected GameFilm plantFilm;
    protected String plantDescription;

    {
        showOnly = true;
        hp = mhp = 1;
    }

    protected void initPlant(int frame, String plantDescription) {
        this.plantFilm = new GameFilm(PLANT_SHEET, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS, 1f);
        this.plantFilm.clipSizeX = 16;
        this.plantFilm.clipSizeY = 16;
        this.plantFilm.tileX = frame;
        this.plantFilm.tileY = 0;
        this.plantDescription = plantDescription;
    }

    public String getPlantDescription() {
        return plantDescription;
    }

    @Override
    public void act(float delta) {
        if (room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            return;
        }

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit == null || unit == this || unit.showOnly() || unit.getRoom() == null || !unit.getRoom().equals(room)
                    || unit.getHP() < 1 || unit.isDead()) {
                continue;
            }

            if (getTriggerArea().overlaps(unit.getHitBox())) {
                activate(unit);
                return;
            }
        }
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (plantFilm == null || room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            return;
        }

        ContactShadow.draw(batch, x + ConstantsHelper.TILE / 2f, y, ConstantsHelper.UNIT_DIMENSIONS * 0.6f, alpha, false);
        plantFilm.setAlpha(alpha);
        plantFilm.setPosition(x + (ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS) / 2f, y);
        plantFilm.draw(batch);
    }

    protected Rectangle getTriggerArea() {
        return new Rectangle(x, y, ConstantsHelper.TILE, ConstantsHelper.TILE);
    }

    protected void activate(Unit target) {
        if (target == null || target.getHP() < 1) {
            return;
        }

        onActivate(target);
        wither();
    }

    protected abstract void onActivate(Unit target);

    protected void wither() {
        emitBurst("images/misc/green.png", 5, 6f, 18f);
        playSound(Sounds.TRAP, 0.5f);
        UnitHelper.getInstance().removeUnit(this);
    }

    protected void emitBurst(String spritePath, int count, float particleSize, float speed) {
        for (int i = 0; i < count; i++) {
            float particleX = x + ConstantsHelper.TILE / 2f + RandomHelper.getInstance().randomFloat(ConstantsHelper.TILE / 2f) - ConstantsHelper.TILE / 4f;
            float particleY = y + ConstantsHelper.UNIT_DIMENSIONS / 2f + RandomHelper.getInstance().randomFloat(ConstantsHelper.UNIT_DIMENSIONS / 2f);
            EffectsHelper.getInstance().add(new TrapBurst().init(
                    particleX,
                    particleY,
                    spritePath,
                    particleSize,
                    3,
                    speed,
                    speed * 1.5f,
                    45f,
                    0.02f));
        }
    }

    protected ArrayList<Unit> getUnitsNearPlant(float horizontalTiles, float verticalTiles) {
        ArrayList<Unit> nearbyUnits = new ArrayList<Unit>();
        float centerX = x + ConstantsHelper.TILE / 2f;
        float centerY = y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float maxDeltaX = horizontalTiles * ConstantsHelper.TILE;
        float maxDeltaY = verticalTiles * ConstantsHelper.TILE;

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit == null || unit == this || unit.showOnly() || unit.getRoom() == null || !unit.getRoom().equals(room)
                    || unit.getHP() < 1 || unit.isDead()) {
                continue;
            }

            float unitCenterX = unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
            float unitCenterY = unit.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
            if (Math.abs(unitCenterX - centerX) <= maxDeltaX && Math.abs(unitCenterY - centerY) <= maxDeltaY) {
                nearbyUnits.add(unit);
            }
        }

        return nearbyUnits;
    }

    protected boolean isUnitOnPlantTile(Unit unit) {
        if (unit == null || unit.getRoom() == null || !unit.getRoom().equals(room)) {
            return false;
        }

        float centerX = unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        return centerX >= x && centerX <= x + ConstantsHelper.TILE && Math.abs(unit.y - y) <= ConstantsHelper.TILE / 2f;
    }

    protected boolean teleportUnitToRandomPlatform(Unit target) {
        Room currentRoom = MapHelper.getInstance().getRoom(room);
        if (target == null || currentRoom == null) {
            return false;
        }

        ArrayList<String> candidates = new ArrayList<String>();
        for (String platform : currentRoom.getPlatforms()) {
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int tileY = Integer.parseInt(platform.split("_")[1]);
            float candidateX = tileX * ConstantsHelper.TILE;
            float candidateY = (tileY + 1) * ConstantsHelper.TILE;

            if (Math.abs(candidateX - target.x) < ConstantsHelper.TILE) {
                continue;
            }

            if (!UnitHelper.getInstance().freeSpace(target, (int) candidateX, (int) candidateY, room)) {
                continue;
            }

            candidates.add(platform);
        }

        if (candidates.isEmpty()) {
            return false;
        }

        Collections.sort(candidates);
        String platform = candidates.get(RandomHelper.getInstance().randomInt(candidates.size()));
        int tileX = Integer.parseInt(platform.split("_")[0]);
        int tileY = Integer.parseInt(platform.split("_")[1]);
        target.x = tileX * ConstantsHelper.TILE;
        target.y = (tileY + 1) * ConstantsHelper.TILE;
        target.floorY = target.y;
        target.speedY = 0f;
        PhysicsHelper.getInstance().syncBodyToUnit(target);
        return true;
    }

    protected void dropItem(Item item) {
        if (item == null) {
            return;
        }

        ItemOnScreen itemOnScreen = new ItemOnScreen(item);
        itemOnScreen.x = x;
        itemOnScreen.y = y;
        itemOnScreen.floorY = y;
        itemOnScreen.setRoom(room);
        UnitHelper.getInstance().addUnit(itemOnScreen);
    }
}
