package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.MirrorImage;

import java.util.ArrayList;

public class ScrollOfMirrorImage extends Scroll {
    {
        name = "Scroll of Mirror Image";
        description = "An illusion working that creates two weaker copies of the reader to fight alongside them.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        goldCost = 60;
    }

    @Override
    public void consume() {
        for (Unit unit : new ArrayList<Unit>(UnitHelper.getInstance().getUnits())) {
            if (unit instanceof MirrorImage && unit.isFriendly == getHero().isFriendly && unit.isSummoned && !unit.showOnly()) {
                unit.unSummon();
            }
        }

        for (int i = 0; i < 2; i++) {
            MirrorImage image = new MirrorImage().initFromHero(getHero());
            image.setMaxHP(1);
            image.setHP(1);
            image.isSummoned = true;
            image.x = getHero().x + (i == 0 ? -ConstantsHelper.UNIT_DIMENSIONS : ConstantsHelper.UNIT_DIMENSIONS);
            image.y = getHero().y;
            image.floorY = getHero().floorY;
            image.facingRight = i == 0 ? !getHero().facingRight : getHero().facingRight;
            image.setRoom(getHero().getRoom());
            image.makeFriendly();
            UnitHelper.getInstance().addUnit(image);
        }

        EffectsHelper.getInstance().message(getHero(), "Mirrors rise", Color.WHITE, 0f);
        super.consume();
    }
}