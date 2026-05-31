package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AmuletHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.AmuletOfYendor;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class ItemOnScreen extends Unit {
    protected Item item;
    protected float yFloat;
    protected boolean floatingUp;

    {
        showOnly = true;
        speedY = 0;
    }

    public ItemOnScreen(Item item){
        super();
        this.item = item;
        x = Float.NaN;
        y = Float.NaN;
        floorY = Float.NaN;
    }

    public Item getItem(){
        return item;
    }

    public boolean isPlaced() {
        return !Float.isNaN(x) && !Float.isNaN(y) && !Float.isNaN(floorY);
    }

    public float getInteractionX() {
        return x;
    }

    public float getInteractionWidth() {
        GameSprite gameSprite = item != null ? item.getGameSprite() : null;
        return gameSprite != null ? gameSprite.getWidth() : ConstantsHelper.UNIT_DIMENSIONS / 2f;
    }

    public float getInteractionFloorY() {
        return Float.isNaN(floorY) ? y : floorY;
    }

    @Override
    public void act(float delta){
        if (!isPlaced()) {
            return;
        }

        if(y > floorY){
            speedY -= delta * ConstantsHelper.GRAVITY;
            y = Math.max(floorY, y + delta * speedY);
        }

        if(floatingUp){
            yFloat += delta * 20f;
            if(yFloat > 0){
                yFloat = 0;
                floatingUp = false;
            }
        }
        else {
            yFloat -= delta * 20f;
            if(yFloat < -ConstantsHelper.UNIT_DIMENSIONS / 4){
                yFloat = -ConstantsHelper.UNIT_DIMENSIONS / 4;
                floatingUp = true;
            }
        }
    }

    @Override
    public void draw(Batch batch, float alpha){
        if(!isPlaced() || room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())){
            return;
        }

        item.getGameSprite().setPosition(x, y + ConstantsHelper.UNIT_DIMENSIONS / 2 + yFloat);
        item.getGameSprite().setAlpha(alpha);
        item.draw(batch);
    }

    public void pickedUp(){
        pickedUp(true);
    }

    public void pickedUp(boolean refreshEnvironment){
        if(item instanceof Gold){
            Hero hero = UnitHelper.getInstance().getHero();
            int goldAmount = hero == null ? item.getQuantity() : hero.adjustGoldPickup(item.getQuantity());
            InventoryHelper.getInstance().modifyGold(goldAmount);
            EffectsHelper.getInstance().message(this, "+" + goldAmount + " gold", Color.GOLD, 0f);
            SoundHelper.GetSingleton().play(Sounds.GOLD, 0f, 1f);
            RatKingHelper.getInstance().onHeroPickedUpItem(item);
        }
        else if (item instanceof AmuletOfYendor) {
            AmuletHelper.claimVictory(this);
            return;
        }
        else {
            SoundHelper.GetSingleton().play(Sounds.ITEM, 0f, 1f);
            RatKingHelper.getInstance().onHeroPickedUpItem(item);
        }
        UnitHelper.getInstance().removeUnit(this);

        if (refreshEnvironment) {
            MapHelper.getInstance().refreshHeroEnvironment();
        }
    }
}

