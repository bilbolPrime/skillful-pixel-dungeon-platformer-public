package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.projectiles.Dart;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class ThrowDart extends RangedWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/DART.png", 45, 45);
        projectile = new Dart();
        name = "Throw Darts";
        description = "A basic ranged weapon for taunting enemies from distance.";
        damage = 2f;
        speed = 1.25f;
        ammo = 10;
        goldCost = 1;
    }


    @Override
    public void draw(Batch batch, float frameAt, int totalFrames){

    }

    @Override
    public int getGoldCost(){
        return ammo * goldCost;
    }
}

