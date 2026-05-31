package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.BlackSpark;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.Blood;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.EnchantingAura;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.Effect;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.Effects;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.ManaShield;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.PoweredDown;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.Spark;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.Heal;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.LevelUp;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.Splash;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.Text;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.WaterSplash;

public class EffectsHelper {
    private static final EffectsHelper ourInstance = new EffectsHelper();

    public static EffectsHelper getInstance() {
        return ourInstance;
    }

    private Effects effects;

    private EffectsHelper(){

    }

    public void addEffects(Effects effects){
        this.effects = effects;
    }

    public void add(Effect effect) {
        if (effects != null && effect != null) {
            effects.add(effect);
        }
    }

    public void blood(Unit attacker, Unit defender, float damage){
        effects.add(new Blood().init(defender.x  + ConstantsHelper.UNIT_DIMENSIONS / 2, defender.y + ConstantsHelper.UNIT_DIMENSIONS / 2, 0, damage * (attacker.x > defender.x ? -50 : 50), damage * 50, 200f));
    }

    public void message(Unit unit, String message, Color color, float momentX){
        effects.add(new Text().init(unit.x, unit.y + ConstantsHelper.UNIT_DIMENSIONS, momentX, message, color));
    }

    public void miss(Unit unit) {
        if (unit == null) {
            return;
        }

        message(unit, Messages.get("custom.ui.miss"), Color.RED, 0f);
    }

    public void heal(Unit owner){
        effects.add(new Heal().init(owner.x, owner.y, 0, 0, 0, 0, owner));
    }

    public void levelUp(Unit owner){
        effects.add(new LevelUp().init(owner.x, owner.y, 0, 0, 0, 0, owner));
    }

    public void mastery(Unit owner) {
        if (owner == null) {
            return;
        }

        levelUp(owner);
        add(new TrapBurst().init(
                owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                "images/misc/yellow-dot.png",
                12f,
                12,
                36f,
                52f,
                80f,
                0.05f));
        add(new TrapBurst().init(
                owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                "images/misc/grey.png",
                10f,
                8,
                28f,
                42f,
                65f,
                0.02f));
    }

    public void spark(Unit owner){
        effects.add(new Spark().init(owner.x, owner.y, 0, owner.speedX, 0, 0));
    }

    public void blackSpark(Unit owner){
        effects.add(new BlackSpark().init(owner.x, owner.y, 0, owner.speedX, 0, 0));
    }

    public void poweredDown(Unit owner){
        effects.add(new PoweredDown().init(owner.x, owner.y, 0, 0, 0, 0, owner));
    }

    public void splash(Unit owner){
        splash(owner, null);
    }

    public void splash(Unit owner, Color color){
        if (owner == null) {
            return;
        }

        Splash splash = (Splash) new Splash().init(owner.x, owner.y, 0, owner.speedX, 0, 0);
        if (color != null) {
            splash.setTint(color);
        }
        add(splash);
    }

    public void waterSplash(Unit owner){
        effects.add(new WaterSplash().init(owner.x, owner.y, 0, owner.speedX, 0, 0));
    }

    public void enchanting(Unit owner, Item item, Prefix prefix) {
        if (owner == null) {
            return;
        }

        add(new EnchantingAura().init(owner, item, prefix));
        spark(owner);
        spark(owner);
        spark(owner);
    }

    public void manaShielded(Unit owner, boolean faceRight){
        effects.add(new ManaShield().init(owner.x + (faceRight ? 10f : -10f), owner.y, faceRight));
    }

    public void clear(){
        effects.clearEffects();
    }
}

