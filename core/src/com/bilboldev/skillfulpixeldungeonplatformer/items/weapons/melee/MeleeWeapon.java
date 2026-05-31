package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;

public class MeleeWeapon extends Weapon {

    @Override
    public void setEquipped(boolean equipped){
        setEquipped(equipped, true);
    }

    public void setEquipped(boolean equipped, boolean unequipCheck){
        if(unequipCheck){
            unEquipCheck();
        }

        if(equipped){
            UnitHelper.getInstance().getHero().setWeapon(this);
        }
        else {
            UnitHelper.getInstance().getHero().setWeapon((MeleeAttack)new MeleeAttack().setOwner(UnitHelper.getInstance().getHero()));
        }

        this.equipped = equipped;
    }

    public void unEquipCheck(){
        for(Item item : InventoryHelper.getInstance().getItems()){
            if(item instanceof MeleeWeapon){
                ((MeleeWeapon)item).setEquipped(false, false);
            }
        }
    }


    @Override
    public String getBigDescription(){

        StringBuilder info = new StringBuilder( getDescription() );

        String speedDescription = "";
        if(speed < 1f){
            speedDescription = "slow";
        }

        if(speed > 1f){
            speedDescription = "fast";
        }

        if(prefix != null){
            if (prefix.isEnhancement()) {
                info.append("\n\n").append(Messages.maybeTranslate(
                        "It bears the %s.\n%s",
                        prefix.getName().toLowerCase(),
                        prefix.getDescription()));
            }
            else {
                info.append("\n\n").append(Messages.maybeTranslate(
                        "It is %s.\n%s",
                        prefix.getName().toLowerCase(),
                        prefix.getDescription()));
            }
        }
        else {
            info.append("\n");
        }
        if(speedDescription.equals("")){
            info.append("\n").append(Messages.maybeTranslate(
                    "This %s is a tier %d weapon.",
                    getName().toLowerCase(),
                    tier));
        }
        else {
            info.append("\n").append(Messages.maybeTranslate(
                    "This %s is a tier %d %s weapon.",
                    getName().toLowerCase(),
                    tier,
                    Messages.maybeTranslate(speedDescription)));
        }

        info.append(" ").append(Messages.maybeTranslate(
                "It can do between %d and %d damage.",
                (int)min(),
                (int)max()));

        info.append(getStrengthRequirementText());

        return info.toString();
    }

    @Override
    protected boolean getsExcessStrengthDamageBonus() {
        return true;
    }

    @Override
    public Weapon setPrefix(){
        return super.setPrefix();
    }
}

