package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class WandOfBlink extends Wand {
    {
        manaCost = 3;
        name = "Wand of Blink";
        description = "A wand that blinks the caster forward to the farthest safe spot in the chosen direction.";
        gs = new GameSprite("images/wands/WAND_BIRCH.png", 45, 45);
    }

    @Override
    public void addProjectile(float variance) {
        if (owner == null || owner.getRoom() == null) {
            return;
        }

        Room currentRoom = MapHelper.getInstance().getRoom(owner.getRoom());
        if (currentRoom == null) {
            return;
        }

        String bestPlatform = null;
        float bestDistance = 0f;
        float maxDistance = ConstantsHelper.TILE * (3.5f + getWandPowerMultiplier());
        for (String platform : currentRoom.getPlatforms()) {
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int tileY = Integer.parseInt(platform.split("_")[1]);
            float candidateX = tileX * ConstantsHelper.TILE;
            float distance = candidateX - owner.x;
            if ((!owner.facingRight && distance >= 0f) || (owner.facingRight && distance <= 0f)) {
                continue;
            }

            distance = Math.abs(distance);
            if (distance < ConstantsHelper.TILE || distance > maxDistance) {
                continue;
            }

            float candidateY = (tileY + 1) * ConstantsHelper.TILE;
            if (!UnitHelper.getInstance().freeSpace((int) candidateX, (int) candidateY, owner.getRoom())) {
                continue;
            }

            if (distance > bestDistance) {
                bestDistance = distance;
                bestPlatform = platform;
            }
        }

        if (bestPlatform == null) {
            return;
        }

        int tileX = Integer.parseInt(bestPlatform.split("_")[0]);
        int tileY = Integer.parseInt(bestPlatform.split("_")[1]);
        owner.x = tileX * ConstantsHelper.TILE;
        owner.y = (tileY + 1) * ConstantsHelper.TILE;
        owner.floorY = owner.y;
        owner.speedY = 0f;
        owner.momentX = 0f;
        PhysicsHelper.getInstance().syncBodyToUnit(owner);
    }
}