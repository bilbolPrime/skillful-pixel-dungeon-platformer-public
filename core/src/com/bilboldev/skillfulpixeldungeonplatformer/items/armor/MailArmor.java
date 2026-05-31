package com.bilboldev.skillfulpixeldungeonplatformer.items.armor;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class MailArmor extends Armor {
    {
        gs = new GameSprite("images/misc/extracted items/ARMOR_MAIL.png", 45, 45);
        name = "Mail armor";
        description =  "Interlocking metal links make for a tough but flexible suit of armor.";
        tier = 3;
        goldCost = 40;
        baseRequiredStrength = 5;
    }
}

