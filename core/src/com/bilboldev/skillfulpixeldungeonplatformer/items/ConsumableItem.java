package com.bilboldev.skillfulpixeldungeonplatformer.items;

import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class ConsumableItem extends Item {
    protected Unit owner;
    protected boolean equipped;

    public ConsumableItem setOwner(Unit unit){
        this.owner = unit;
        return this;
    }

    public Unit getOwner(){
        return owner;
    }

    public void consume(){

    }

    @Override
    public String getBigDescription(){
        return Messages.get("custom.generated.arg_n_nthis_is_a_stack_of_ccb755d32a",
                new Object[]{getDescription(), quantity, getName()});
    }
}

