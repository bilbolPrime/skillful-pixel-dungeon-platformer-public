package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Buff {
    protected String name;
    protected String description;
    protected GameSprite gs;
    protected float duration;
    protected boolean permanent;
    protected Unit owner;

    public Buff(String name, String description, String sprite){
        this.name = name;
        this.description = description;
        this.gs = new GameSprite(sprite, 20, 20);
    }

    public Buff setDuration(float duration){
        this.duration = duration;
        return this;
    }

    public Buff setPermanent(boolean permanent){
        this.permanent = permanent;
        return this;
    }

    public Buff setOwner(Unit owner){
        if(owner.addBuff(this)){
            this.owner = owner;
        }

        return this;
    }

    public void act(float delta){
        this.duration -= delta;
    }

    public boolean active(){
        return this.permanent || duration > 0;
    }

    public GameSprite getGameSprite(){
        return gs;
    }

    public String getName(){
        return Messages.capitalizeForDisplay(Messages.maybeTranslate(name));
    }

    public String getDescription(){
        return Messages.maybeTranslate(description);
    }

    public float getRemainingDuration() {
        return duration;
    }

    public void debuff(){

    }

    public boolean preventsAttacks() {
        return false;
    }

    public void resetBuff(){
        debuff();
        if(owner != null){
            setOwner(owner);
        }
    }
}

