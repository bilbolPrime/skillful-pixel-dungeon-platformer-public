package com.bilboldev.skillfulpixeldungeonplatformer.units.interactable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.QuestManager;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Ghost extends Interactable {
    {
        gs = new GameSprite("images/units/ghost/ghost.png", 32, 16, 1f);
        hp = mhp = 1000;
        gf = new GameFilm("images/units/ghost/ghost.png", 32, 16, 1f);
        gf.clipSizeX = 14;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        showOnly = true;
        canFly = true;
        facingRight = false;
    }

    @Override
    public void interact() {
        QuestManager.getInstance().interactWithGhost(this);
    }

    @Override
    public GameSprite getInteractGS() {
        TextureRegion iconRegion = new TextureRegion(
                TextureHelper.GetSingleton().getTexture("images/units/ghost/ghost.png"),
                idleFrames[0] * gf.clipSizeX,
                (gf.yClipOffset + gf.tileY) * gf.clipSizeY,
                gf.clipSizeX,
                gf.clipSizeY);
        Sprite iconSprite = new Sprite(iconRegion);
        iconSprite.setCenter(0, 0);
        iconSprite.setOrigin(iconSprite.getWidth() / 2f, iconSprite.getHeight() / 2f);
        if (!facingRight) {
            iconSprite.flip(true, false);
        }
        return new GameSprite(iconSprite, gf.clipSizeX, gf.clipSizeY);
    }
}