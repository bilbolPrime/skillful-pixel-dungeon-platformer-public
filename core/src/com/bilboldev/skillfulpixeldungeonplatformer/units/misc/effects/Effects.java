package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AmbientMusicHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

import java.util.ArrayList;

public class Effects extends Unit {
    private static final int MAX_ACTIVE_EFFECTS = 900;


    public boolean intro = false;

    float lastCheck = 10f;
    ArrayList<Effect> effects = new ArrayList<>();
    private final ArrayList<Effect> pendingEffects = new ArrayList<>();
    private boolean iteratingEffects;

    @Override
    public void draw(Batch batch, float alpha){
        iteratingEffects = true;
        int classMotes = NewClassBurst.budget();
        for (int index = 0; index < effects.size(); index++) {
            Effect effect = effects.get(index);
            if(!effect.active()){
                continue;
            }

            if (effect instanceof NewClassBurst) classMotes -= ((NewClassBurst)effect).drawMotes(batch, classMotes);
            else effect.draw(batch);
        }
        iteratingEffects = false;
        flushPendingEffects();
    }

    @Override
    public void act(float delta){

        iteratingEffects = true;
        for (int index = 0; index < effects.size(); index++) {
            effects.get(index).act(delta);
        }
        iteratingEffects = false;
        flushPendingEffects();

        lastCheck -= delta * 10f;
        if(lastCheck < 0){

            ArrayList<Effect> activeEffects = new ArrayList<>();
            for(Effect effect : effects){
                if(effect.active()){
                    activeEffects.add(effect);
                }
            }

            effects = activeEffects;
            lastCheck = 10f;
            trimEffectsIfNeeded();
        }
    }

    public void add(Effect effect){
        if (effect == null) {
            return;
        }

        if (effect instanceof NewClassBurst) {
            int available = NewClassBurst.budget();
            for (Effect existing : effects) if (existing instanceof NewClassBurst) available -= ((NewClassBurst)existing).moteCount();
            for (Effect pending : pendingEffects) if (pending instanceof NewClassBurst) available -= ((NewClassBurst)pending).moteCount();
            ((NewClassBurst)effect).limitMotes(available);
            if (!effect.active()) return;
        }

        if (iteratingEffects) {
            pendingEffects.add(effect);
            return;
        }

        effects.add(effect);
        trimEffectsIfNeeded();
    }

    public void clearEffects(){
        effects = new ArrayList<>();
        pendingEffects.clear();
    }

    private void flushPendingEffects() {
        if (pendingEffects.isEmpty()) {
            return;
        }

        effects.addAll(pendingEffects);
        pendingEffects.clear();
        trimEffectsIfNeeded();
    }

    private void trimEffectsIfNeeded() {
        int overflow = effects.size() - MAX_ACTIVE_EFFECTS;
        if (overflow <= 0) {
            return;
        }

        effects = new ArrayList<>(effects.subList(overflow, effects.size()));
    }
}

