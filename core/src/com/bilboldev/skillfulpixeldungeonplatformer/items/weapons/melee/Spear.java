package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Spear extends MeleeWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/SPEAR.png", 45, 45);
        damage = 7;
        name = "Spear";
        description = "A slender wooden rod tipped with sharpened iron.";
        reach = 2f;

        tier = 2;
        speed = 0.9f;
        goldCost = 50;
        baseRequiredStrength = 5;
    }

    @Override
    public void draw(Batch batch, float frameAt, int totalFrames){
        GameSprite sprite = getGameSprite();
        if(sprite == null || owner == null){
            return;
        }

        drawAttackSprite(batch,
                sprite,
                owner.getVisualAttackX(2f * frameAt / totalFrames),
                owner.getVisualAttackY(),
            owner.facingRight);
    }
}

