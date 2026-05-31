package com.bilboldev.skillfulpixeldungeonplatformer.items.quest;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.QuestManager;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Pickaxe extends MeleeWeapon {
    private boolean bloodStained;

    {
        name = "Pickaxe";
        description = "This is a large and sturdy tool for breaking rocks. Probably it can be used as a weapon.";
        gs = new GameSprite("images/misc/extracted items/PICKAXE.png", 45, 45);
        damage = 6f;
        tier = 3;
        baseRequiredStrength = 14;
        speed = 0.9f;
        goldCost = 0;
    }

    public void mine() {
        QuestManager.getInstance().mineWithPickaxe(this);
    }

    public boolean isBloodStained() {
        return bloodStained;
    }

    public Pickaxe setBloodStained(boolean bloodStained) {
        this.bloodStained = bloodStained;
        return this;
    }

    @Override
    public String getBigDescription() {
        String info = super.getBigDescription();
        if (bloodStained) {
            info += "\n\nThe head of the pickaxe is stained with bat blood.";
        }
        return info + "\n\nIt can also be used to mine dark gold veins for the troll blacksmith.";
    }
}