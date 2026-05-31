package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EnhancementVisualHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class Weapon extends EquipableItem {

    protected Prefix prefix;
    protected float damage;
    protected float reach = 1f;
    protected float speed = 1f;
    protected float knockBack = 0f;
    protected int tier = 1;
    protected int level = 1;
    protected int baseRequiredStrength = 1;

    public Rectangle getHitArea(){
        Polygon polygon = new Polygon(new float[]{0,0,ConstantsHelper.UNIT_DIMENSIONS* reach,0,ConstantsHelper.UNIT_DIMENSIONS* reach,ConstantsHelper.UNIT_DIMENSIONS* reach,0,ConstantsHelper.UNIT_DIMENSIONS* reach});
        polygon.setPosition(owner.x + (owner.facingRight ? ConstantsHelper.UNIT_DIMENSIONS  : -ConstantsHelper.UNIT_DIMENSIONS ), owner.y);
        polygon.setOrigin(ConstantsHelper.UNIT_DIMENSIONS / 2, ConstantsHelper.UNIT_DIMENSIONS / 2);
        polygon.setRotation(0);
        return polygon.getBoundingRectangle();
    }

    public void draw(Batch batch, float frameAt, int totalFrames){
        GameSprite sprite = getGameSprite();
        if(sprite == null || owner == null){
            return;
        }

        drawAttackSprite(batch,
                sprite,
                owner.x + (owner.facingRight ? ConstantsHelper.UNIT_DIMENSIONS * frameAt / totalFrames: ConstantsHelper.UNIT_DIMENSIONS / 3 -ConstantsHelper.UNIT_DIMENSIONS * frameAt / totalFrames),
                owner.y + ConstantsHelper.UNIT_DIMENSIONS / 4,
                owner.facingRight);
    }

    public boolean showsAttackAnimation() {
        return true;
    }

    public Weapon setDamage(float damage){
        this.damage = damage;
        return this;
    }

    public Weapon setPrefix(Prefix prefix){
        if (prefix != null && !prefix.isEnhancement()) {
            prefix = null;
        }
        this.prefix = prefix;
        refreshEquippedButton();
        return this;
    }

    protected void drawAttackSprite(Batch batch, GameSprite sprite, float drawX, float drawY, boolean facingRight) {
        float previousX = sprite.getX();
        float previousY = sprite.getY();
        float previousRotation = sprite.getRotation();
        float previousScaleX = sprite.getScaleX();
        sprite.setPosition(drawX, drawY);
        sprite.setRotation(facingRight ? -45f : 45f);
        sprite.setScale(facingRight ? Math.abs(previousScaleX) : -Math.abs(previousScaleX), sprite.getScaleY());
        EnhancementVisualHelper.applyWeaponEnhancementPulse(sprite, this);
        sprite.draw(batch);
        sprite.setPosition(previousX, previousY);
        sprite.setRotation(previousRotation);
        sprite.setScale(previousScaleX, sprite.getScaleY());
    }

    public Prefix getPrefix() {
        return prefix;
    }

    @Override
    public boolean supportsLevel() {
        return true;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Weapon setLevel(int level) {
        this.level = Math.max(1, level);
        return this;
    }

    @Override
    public Weapon modifyLevel(int amount) {
        return setLevel(level + amount);
    }

    public Weapon setPrefix(){
        return setPrefix(null);
    }

    protected int getUpgradeBonusDamage() {
        return Math.max(0, level - 1);
    }

    public float min (){
        float prefixModifier = prefix != null ? prefix.getModifier() : 1f;
        return applyStrengthDamage(prefixModifier * (tier + getUpgradeBonusDamage() + damage * 0.75f));
    }

    public float max (){
        float prefixModifier = prefix != null ? prefix.getModifier() : 1f;
        return applyStrengthDamage(prefixModifier * (tier + getUpgradeBonusDamage() + damage * 1.5f));
    }


    public float getDamage(){
        float prefixModifier = prefix != null ? prefix.getModifier() : 1f;
        float rolledDamage = prefixModifier * owner.getOutgoingDamageModifier() * (tier + getUpgradeBonusDamage() + damage * (0.75f + RandomHelper.getInstance().randomFloat(0.75f)));
        return applyStrengthDamage(rolledDamage);
    }

    @Override
    public int getRequiredStrength(){
        int typicalStrength = 8 + 2 * tier;
        int upgradedRequirement = Math.max(baseRequiredStrength, typicalStrength) - Math.max(0, level - 1);
        return Math.max(1, upgradedRequirement + (prefix != null ? prefix.getLevelModifier() : 0));
    }

    public float getStrengthAttackSpeedMultiplier() {
        int shortfall = getStrengthShortfall();
        return shortfall < 1 ? 1f : (float) Math.pow(1.2f, -shortfall);
    }

    public float getStrengthAccuracyMultiplier() {
        int shortfall = getStrengthShortfall();
        return shortfall < 1 ? 1f : (float) Math.pow(1.5f, -shortfall);
    }

    public int getStrengthShortfall() {
        Unit strengthOwner = getStrengthOwner();
        return strengthOwner == null ? 0 : Math.max(0, getRequiredStrength() - strengthOwner.getStrength());
    }

    public int getExcessStrength() {
        Unit strengthOwner = getStrengthOwner();
        return strengthOwner == null ? 0 : Math.max(0, strengthOwner.getStrength() - getRequiredStrength());
    }

    protected boolean getsExcessStrengthDamageBonus() {
        return false;
    }

    protected String getStrengthRequirementText() {
        StringBuilder info = new StringBuilder();
        info.append("\n\n").append(Messages.maybeTranslate(
                "You need %d Strength to use the %s.",
                getRequiredStrength(),
                getName().toLowerCase()));

        int shortfall = getStrengthShortfall();
        if (shortfall > 0) {
            info.append(" ").append(Messages.maybeTranslate(
                    "You are short by %d, reducing its damage and attack speed.",
                    shortfall));
        }
        else if (getsExcessStrengthDamageBonus() && getExcessStrength() > 0) {
            info.append(" ").append(Messages.maybeTranslate(
                    "Your excess strength adds %d bonus damage.",
                    getExcessStrength()));
        }

        return info.toString();
    }

    private float applyStrengthDamage(float value) {
        float adjustedValue = value;
        int shortfall = getStrengthShortfall();
        if (shortfall > 0) {
            adjustedValue *= Math.max(0.4f, 1f - shortfall * 0.15f);
        }
        else if (getsExcessStrengthDamageBonus()) {
            adjustedValue += getExcessStrength();
        }

        return Math.max(1f, adjustedValue);
    }

    private Unit getStrengthOwner() {
        if (owner == null) {
            return UnitHelper.getInstance().getHero();
        }

        return owner.usesEquipmentStrengthRequirements() ? owner : null;
    }

    @Override
    public String getName(){
        String baseName = prefix != null && !prefix.isEnhancement() ? prefix.getName() + " " + super.getName() : super.getName();
        return withLevelSuffix(baseName);
    }


    public void modifyKnockback(float modification){
        knockBack += modification;
    }

    public float getKnockBack(){
        return knockBack;
    }

    public float getSpeed(){
        return speed;
    }

    public int getTier() {
        return tier;
    }

    private void refreshEquippedButton() {
        if (!(owner instanceof Hero)) {
            return;
        }

        Hero hero = (Hero) owner;
        if (this instanceof MeleeWeapon && hero.getWeapon() == this) {
            UIHelper.getInstance().equiped((MeleeWeapon) this);
            return;
        }

        if (this instanceof RangedWeapon && hero.getRangedWeapon() == this) {
            UIHelper.getInstance().equiped((RangedWeapon) this);
        }
    }
}

