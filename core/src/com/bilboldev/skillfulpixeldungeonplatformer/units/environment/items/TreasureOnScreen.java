package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Treasure;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class TreasureOnScreen extends ItemOnScreen {

    protected GameSprite gameSprite = new GameSprite("images/misc/extracted items/CHEST.png", 90, 90);
    {
        showOnly = true;
        speedY = 0;
    }

    public TreasureOnScreen(){
        super(new Treasure());
    }

    public Item getItem(){
        return item;
    }

    @Override
    public float getInteractionWidth() {
        return gameSprite.getWidth();
    }

    @Override
    public void act(float delta){

    }

    @Override
    public void draw(Batch batch, float alpha){
        if(room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())){
            return;
        }

        drawContactShadow(batch, alpha, 0f);
        gameSprite.setPosition(x, y);
        gameSprite.setAlpha(alpha);
        gameSprite.draw(batch);
        rememberDisplayedItem(gameSprite);
    }

    @Override
    public void pickedUp(){
        pickedUp(true);
    }

    @Override
    public void pickedUp(boolean refreshEnvironment){
        UnitHelper.getInstance().removeUnit(this);
        Item item = InventoryHelper.getInstance().getRandomTreasure(MapHelper.getInstance().getDepth());
        item.spawnNaturally(x, y, floorY, room);

        if (refreshEnvironment) {
            MapHelper.getInstance().refreshHeroEnvironment();
        }
    }
}

