package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.other;


import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class Statue extends Mob {
    private boolean triggered;

    {
        hp = mhp = 10 + 5 * MapHelper.getInstance().getDepth();
        experience = MapHelper.getInstance().getDepth();
        gf = new GameFilm("images/units/statue/statue.png",256, 16, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{2, 3, 4, 5, 6, 7};
        attackFrames = new int[]{8, 9, 10, 10};
        dieFrames = new int[]{11, 12, 13, 14, 15, 16, 16};
        ai = new AgressiveAI(this){
            @Override
            public void act(float delta){
                if(!triggered){
                    return;
                }

                super.act(delta);
            }
        };


        speedX = 350;
        attackSpeed = 5f;
        weapon = (MeleeAttack) new MeleeAttack().setDamage(MapHelper.getInstance().getDepth()).setOwner(this);
        showOnly = true;
    }

    @Override
    public String getLibraryDescription() {
        return "A dormant guardian that awakens when challenged, sealing the room until one side falls.";
    }

    @Override
    public String getLibraryStats() {
        return "Health: Scales with dungeon depth\nDamage: Scales with dungeon depth\nMove speed: 350\nAttack speed: 5\nExperience: Scales with dungeon depth\nMovement: Grounded";
    }

    public void trigger(){
        this.triggered = true;
        showOnly = false;
        for(Door door : MapHelper.getInstance().getRoom(room).getDoors()){
            door.lock();
        }
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
        showOnly = !triggered;
    }

    @Override
    public void die(){
        super.die();
        for(Door door : MapHelper.getInstance().getRoom(room).getDoors()){
            door.unlock();
        }
    }
}

