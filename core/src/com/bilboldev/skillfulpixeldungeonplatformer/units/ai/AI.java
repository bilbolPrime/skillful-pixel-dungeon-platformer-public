package com.bilboldev.skillfulpixeldungeonplatformer.units.ai;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;

public class AI {
    protected States state;
    Unit owner, other;
    float lastAction;
    float designationX = -1;
    float los = 100f;

    public AI(Unit owner){
        state = States.IDLE;
        this.owner = owner;
        lastAction = RandomHelper.getInstance().randomFloat(100f);
    }

    public void act(float delta)
    {
        if(state == States.IDLE){
            wander(delta);
        }

        if(state == States.ATTACKED){
            attacked(delta);
        }
    }

    public void wander(float delta){

        if(owner.movingRight || owner.movingLeft){
            if(Math.abs(designationX - owner.x) < 10f){
                designationX = owner.x;
                owner.movingLeft = false;
                owner.movingRight = false;
            }
        }

        if(!owner.movingLeft && !owner.movingRight){
            lastAction += delta * 10f;
        }

        if(lastAction > 100f){
            float movmentX = -300 + RandomHelper.getInstance().randomInt(600);
            if(owner.x + movmentX < 1 || owner.x + movmentX > MapHelper.getInstance().getWidth() * ConstantsHelper.TILE){
                movmentX = 0;
            }

            if(owner.floorY == MapHelper.getInstance().calculateFloorY(owner.x + movmentX, owner.y)){
                designationX = owner.x + movmentX;
                if(owner.x > designationX){
                    owner.movingLeft = true;
                    owner.movingRight = false;
                    owner.facingRight = false;
                }

                if(owner.x < designationX){
                    owner.movingLeft = false;
                    owner.movingRight = true;
                    owner.facingRight = true;
                }
            }

            lastAction = RandomHelper.getInstance().randomFloat(100f);
        }
    }


    public void attacked(float delta){

    }

    public void setOther(Unit other){
        this.other = other;
        this.state = States.ATTACKED;
    }

    public void clearTarget() {
        other = null;
        state = States.IDLE;
    }

    public Unit getOther(){
        return other;
    }

    public Unit getOwner(){
        return owner;
    }

    public void blinded(){
        other = null;
        state = States.BLINDED;
    }

    public boolean isBlind(){
        return state == States.BLINDED;
    }
}

