package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Axe extends MeleeWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/BATTLE_AXE.png", 45, 45);
        damage = 25;
        name = "Axe";
        description = "The enormous steel head of this battle axe puts considerable heft behind each stroke.";
        tier = 4;
        speed = 0.8f;
        goldCost = 250;
        baseRequiredStrength = 12;
    }
}

