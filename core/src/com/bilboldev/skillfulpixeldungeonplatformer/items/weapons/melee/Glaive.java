package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Glaive extends MeleeWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/GLAIVE.png", 45, 45);
        damage = 20;
        name = "Glaive";
        description =  "A polearm consisting of a sword blade on the end of a pole.";
        reach = 2f;
        tier = 3;
        speed = 0.8f;
        goldCost = 150;
        baseRequiredStrength = 8;
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

