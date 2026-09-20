package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpriteTrail;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class FireBall extends FireBolt {
    {
        gs = new GameSprite("images/wands/fire-ball.png", 45, 45);
        showOnly = true;
        speedY = 0;
        damage = 8f + RandomHelper.getInstance().randomFloat(8f);
        lifeSpan = 75f;
    }

    public FireBall setOwner(Unit unit){
        super.setOwner(unit);
        return this;
    }

    public FireBall setSpeedY(float speedY){
        super.setSpeedY(speedY);
        return this;
    }

    public FireBall setGameSprite(GameSprite gameSprite){
        super.setGameSprite(gameSprite);
        return this;
    }

    @Override
    protected void fluctuation(float delta){
        fluctuation -= delta * 100f;
        if(fluctuation < 0){
            gs.setHeight(25 + RandomHelper.getInstance().randomInt(20));
            fluctuation = 10f;
        }
    }

    @Override
    public void draw(Batch batch, float alpha){
        if(gs != null && !used){
            gs.setPosition(getRenderX(), getRenderY() + (facingRight ? 1 : -1) * (45 - gs.getHeight()) / 2);
            gs.setRotation(facingRight  ? 0 : 180);
            SpriteTrail.draw(batch, gs, speedX * 0.025f, speedY * 0.025f, 24f);
            gs.draw(batch);
        }
    }
}

