package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class WaterSplash extends Effect {

    public enum Contact { STEP, TAKEOFF, LANDING }
    private Contact contact;
    private boolean wetContact;
    private float contactAge;
    protected ArrayList<XY> xyArrayList;


    public Effect init(float x, float y, float rotation, float speedX, float speedY, float speedRotation){
        super.init(x, y, rotation, speedX, speedY, speedRotation);
        gs = new GameSprite("images/skills/sludge-bomb.png", 10, 10);
        xyArrayList = new ArrayList<XY>();

        for(int i = 0; i < 4; i++){
            xyArrayList.add(new XY(
                    0,
                    0,
                    speedX / 20f + 28f - RandomHelper.getInstance().randomFloat(56f),
                    45f + RandomHelper.getInstance().randomFloat(65f)));
        }

        lifeSpan = 55f;
        return this;
    }


    public WaterSplash initContact(float footX, float footY, boolean wet, Contact kind) {
        x = footX;
        y = footY;
        contact = kind;
        wetContact = wet;
        contactAge = 0f;
        lifeSpan = 1f;
        float strength = kind == Contact.STEP ? 0.4f : kind == Contact.TAKEOFF ? 0.65f : 1f;
        gs = new GameSprite(new Sprite(TextureHelper.GetSingleton().getSolidPixel()), wet ? 4f : 6f, wet ? 6f : 3f);
        gs.setColor(wet ? new Color(0.65f, 0.88f, 0.94f, 1f) : new Color(0.72f, 0.66f, 0.53f, 1f));
        xyArrayList = new ArrayList<XY>();
        int count = kind == Contact.STEP ? 2 : 4;
        for (int i = 0; i < count; i++) {
            float side = i % 2 == 0 ? -1f : 1f;
            xyArrayList.add(new XY(side * ((kind == Contact.STEP ? 16f : 20f) + i * 3f), 0f,
                    side * (30f + i * 11f) * strength, (wet ? 65f : 22f) + (i % 2) * 14f));
        }
        return this;
    }


    @Override
    public void act(float delta){
        if (contact != null) {
            contactAge += delta;
            lifeSpan = Math.max(0f, 1f - contactAge / 0.42f);
            for (XY xy : xyArrayList) {
                xy.x += xy.speedx * delta;
                xy.y = Math.max(0f, xy.y + xy.speedy * delta);
                xy.speedy -= delta * ConstantsHelper.GRAVITY * (wetContact ? 0.28f : 0.08f);
            }
            return;
        }
        for(XY xy : xyArrayList){
            xy.y += xy.speedy * delta;
            xy.speedy -= delta * ConstantsHelper.GRAVITY / 3.5f;
            xy.x += xy.speedx * delta;
        }

        lifeSpan = Math.max(0, lifeSpan - 22f * delta);

        if(gs != null){
            gs.setAlpha(lifeSpan / 100f);
            gs.setPosition(x, y);
            gs.setRotation(rotation);
        }
    }

    @Override
    void gravity(float delta){

    }


    @Override
    public void draw(Batch batch){
        if (contact != null) {
            float intensity = GameSettingsHelper.getInstance().isReducedVisualEffects() ? 0.35f : 1f;
            gs.setAlpha(0.7f * lifeSpan * lifeSpan * intensity);
        }
        if(gs != null){
            for(XY xy : xyArrayList){
                gs.setPosition(x + xy.x - (contact == null ? 0f : gs.getWidth() / 2f), y + xy.y);
                gs.draw(batch);
            }
        }
    }


    class XY {
        public float x;
        public float y;
        public float speedx;
        public float speedy;

        public XY(float x, float y, float speedx, float speedy){
            this.x = x;
            this.y = y;
            this.speedx = speedx;
            this.speedy = speedy;
        }
    }
}
