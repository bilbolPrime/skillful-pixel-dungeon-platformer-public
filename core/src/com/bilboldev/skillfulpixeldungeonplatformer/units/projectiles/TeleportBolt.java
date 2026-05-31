package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

import java.util.ArrayList;

public class TeleportBolt extends FireBolt {
    {
        gs = new GameSprite("images/misc/extracted items/SCROLL_NAUDIZ.png", 22, 22);
        damage = 0f;
    }

    @Override
    public void onUnitCollision(Unit target) {
        if (owner != null && UnitHelper.getInstance().tryHit(owner, target, attackingItem, true) && teleportTarget(target)) {
            playSound(Sounds.ZAP, 1f);
        } else {
            playSound(Sounds.MISS, 0.4f);
        }

        markUsed();
        EffectsHelper.getInstance().blackSpark(this);
    }

    private boolean teleportTarget(Unit target) {
        if (target == null || target.getRoom() == null) {
            return false;
        }

        Room currentRoom = MapHelper.getInstance().getRoom(target.getRoom());
        if (currentRoom == null) {
            return false;
        }

        ArrayList<String> candidates = new ArrayList<String>();
        for (String platform : currentRoom.getPlatforms()) {
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int tileY = Integer.parseInt(platform.split("_")[1]);
            float candidateX = tileX * ConstantsHelper.TILE;
            float candidateY = (tileY + 1) * ConstantsHelper.TILE;
            if (!UnitHelper.getInstance().freeSpace((int) candidateX, (int) candidateY, target.getRoom())) {
                continue;
            }

            candidates.add(platform);
        }

        if (candidates.isEmpty()) {
            return false;
        }

        String platform = candidates.get(RandomHelper.getInstance().randomInt(candidates.size()));
        int tileX = Integer.parseInt(platform.split("_")[0]);
        int tileY = Integer.parseInt(platform.split("_")[1]);
        target.x = tileX * ConstantsHelper.TILE;
        target.y = (tileY + 1) * ConstantsHelper.TILE;
        target.floorY = target.y;
        target.speedY = 0f;
        target.momentX = 0f;
        PhysicsHelper.getInstance().syncBodyToUnit(target);
        return true;
    }
}