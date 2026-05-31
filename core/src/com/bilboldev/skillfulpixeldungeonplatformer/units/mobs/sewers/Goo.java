package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;


import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Dagger;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.LongSword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Rod;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.SludgeBomb;

public class Goo extends Mob {
    private static final float WATER_HEAL_INTERVAL = 1f;
    private static final int WATER_HEAL_AMOUNT = 2;
    protected float lastSludge = 0f;
    private float healOnWaterAt = WATER_HEAL_INTERVAL;
    {
        boss = true;
        hp = mhp = 80;
        experience = 10;
        attackSkill = 15;
        defenseSkill = 12;
        damageReduction = 2;
        gf = new GameFilm("images/units/goo/goo.png",256, 16, 1f);
        gf.clipSizeX = 20;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{0, 1};
        attackFrames = new int[]{5, 6, 6};
        dieFrames = new int[]{2, 3, 4, 4};
        ai = new AgressiveAI(this){
            @Override
            public void act(float delta){
                lastSludge += 1f * delta;

                if(lastSludge > 10f && getOther() != null){
                    if(UtilsHelper.distance(getOwner(), getOther()) < ConstantsHelper.UNIT_DIMENSIONS){
                        MeleeAttack attack = (MeleeAttack) getOwner().getWeapon();
                        attack.setDamageRange(5f, 30f);
                        getOwner().getWeapon().modifyKnockback(500f);
                        getOwner().attack(true);
                        getOwner().getWeapon().modifyKnockback(-500f);
                        attack.setDamageRange(2f, 12f);
                        EffectsHelper.getInstance().message(getOwner(), "...", Color.RED, 0f);
                        lastSludge = 9f;
                    }
                    else {
                        getOwner().attack(true);
                        EffectsHelper.getInstance().message(getOwner(), "Burp", Color.RED, 0f);
                        for(int i = 0; i < 5; i++){
                            SludgeBomb sludgeBomb = new SludgeBomb();
                            sludgeBomb.isFriendly = getOwner().isFriendly;
                            sludgeBomb.facingRight =  getOwner().facingRight;
                            sludgeBomb.x =  getOwner().x;
                            sludgeBomb.y =  getOwner().y + ConstantsHelper.UNIT_DIMENSIONS / 2;
                            sludgeBomb.setOwner(getOwner());
                            sludgeBomb.setSpeedX( getOwner().facingRight ? (400 + i * 125) : -(400 + i * 125));
                            sludgeBomb.setSpeedY( 50 + i * 75f);
                            UnitHelper.getInstance().addUnit(sludgeBomb);
                        }

                        lastSludge = 0f;
                    }



                }

                super.act(delta);
            }
        };

        speedX = 350;
        attackSpeed = 5f;
        weapon = new MeleeAttack().setDamageRange(2f, 12f);
        weapon.setOwner(this);

        dropChance = 100;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        MapHelper mapHelper = MapHelper.getInstance();

        if (getRoom() == null || !getRoom().equals(mapHelper.getActiveRoomIdentifier())) {
            resetHealOnWaterTimer();
            return;
        }

        if (!mapHelper.isStandingOnWater(this) || getHP() >= getMaxHP() || isDead()) {
            resetHealOnWaterTimer();
            return;
        }

        healOnWaterAt -= delta;
        if (healOnWaterAt > 0f) {
            return;
        }

        heal(WATER_HEAL_AMOUNT);
        EffectsHelper.getInstance().heal(this);
        resetHealOnWaterTimer();
    }

    private void resetHealOnWaterTimer() {
        healOnWaterAt = WATER_HEAL_INTERVAL;
    }

    @Override
    public void die() {
        super.die();
        Room currentRoom = MapHelper.getInstance().getRoom(room);
        if (currentRoom == null) {
            return;
        }

        currentRoom.markBossDefeated();

        for (Door door : currentRoom.getDoors()) {
            door.unlock();
        }
    }

    @Override
    public String getLibraryDescription() {
        return "The sewer boss. Goo lurches into melee up close but can also belch volleys of corrosive sludge when given room.";
    }
}
