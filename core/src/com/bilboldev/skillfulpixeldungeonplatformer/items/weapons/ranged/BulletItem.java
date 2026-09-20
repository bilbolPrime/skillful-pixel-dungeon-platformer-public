package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;


public final class BulletItem extends Item {
    @Override public String getTrueName() { return Messages.get("custom.newitems.bulletitem.name"); }
    @Override public String getTrueDescription() { return Messages.get("custom.newitems.bulletitem.description"); }

    {
        name = "Bullet";
        description = "Ammunition for the Mercenary's guns. Keep bullets in your pack; each gun shot uses one bullet, including a full volley. Only the playable Mercenary can use guns. Bullets cannot be sold.";
        gs = new GameSprite(NewClassAssets.ItemArt.BULLET.key(), 45, 45);
        quantity = 1;
        goldCost = 0;
    }

    @Override public BulletItem setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
        return this;
    }

    @Override public String getBigDescription() {
        return getDescription() + "\n\n" + Messages.get(
                "custom.bullets.stack", new Object[]{quantity});
    }
}
