package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.RatKingAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class RatKing extends Mob {
    private static final float BLOCKED_CALL_HORIZONTAL_RANGE = ConstantsHelper.UNIT_DIMENSIONS * 1.1f;
    private static final float BLOCKED_CALL_VERTICAL_RANGE = ConstantsHelper.UNIT_DIMENSIONS * 0.75f;
    private static final float BLOCKED_CALL_DELAY_SECONDS = 1.25f;

    private float blockedCallTimer;
    private boolean blockedCallTriggered;

    {
        showOnly = true;
        isSummoned = true;
        spawnSpace = false;
        dropChance = 0;
        hp = mhp = 1;
        gf = new GameFilm("images/units/ratking/ratking.png", 128, 16, 1f);
        gf.clipSizeX = 16;
        gf.clipSizeY = 16;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{2, 3, 4, 5, 6 };
        attackFrames = idleFrames;
        dieFrames = idleFrames;
        speedX = 260f;
        makeFriendly();
        ai = new RatKingAI(this);
        experience = 0;
    }

    @Override
    public void act(float delta) {
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null || hero.isDead() || !RatKingSupportHelper.getInstance().isRatKingAvailable()
                || !RatKingSupportHelper.getInstance().isCompanionEnabled()) {
            removeUnit();
            return;
        }

        if (hero.getRoom() == null) {
            return;
        }

        String previousRoom = getRoom();
        setRoom(hero.getRoom());
        boolean changedRoom = previousRoom == null || !previousRoom.equals(hero.getRoom());
        if (changedRoom) {
            x = hero.x + (hero.facingRight ? ConstantsHelper.UNIT_DIMENSIONS : -ConstantsHelper.UNIT_DIMENSIONS);
            y = hero.y;
            floorY = hero.floorY;
            movingLeft = false;
            movingRight = false;
            blockedCallTimer = 0f;
            blockedCallTriggered = false;
        }

        super.act(delta);

        if (y < floorY) {
            y = floorY;
        }

        RatKingHelper.getInstance().maybeSayBored();

        if (hero.floorY > floorY + BLOCKED_CALL_VERTICAL_RANGE
                && Math.abs(hero.x - x) <= BLOCKED_CALL_HORIZONTAL_RANGE) {
            blockedCallTimer += delta;
            if (!blockedCallTriggered && blockedCallTimer >= BLOCKED_CALL_DELAY_SECONDS) {
                RatKingHelper.getInstance().onHeroOutOfReach();
                blockedCallTriggered = true;
            }
        }
        else {
            blockedCallTimer = 0f;
            blockedCallTriggered = false;
        }
    }

    @Override
    public void takeDamage(Unit source, Weapon damagingItem, float damage) {
    }

    @Override
    public void attack() {
    }

    @Override
    public void rangedAttack() {
    }

    @Override
    public void die() {
    }

    @Override
    public String getLibraryDescription() {
        return Messages.get("custom.ui.rat_king.library_desc");
    }
}