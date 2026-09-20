package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ItemIdentityHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

import java.util.ArrayList;

public class Wand extends MeleeWeapon {
    public static final float CAST_ATTACK_DURATION_SECONDS = 0.25f;

    protected int manaCost;
    protected int baseManaCost;
    private long cooldownUntilMillis;

    {
        goldCost = 50;
        setIdentityFamily(ItemIdentityHelper.Family.WAND);
        setUnknownDescription("An unidentified wand. Its power is unknown until it is used.");
    }

    public boolean canCast(){
        boolean hasMana = (!(owner instanceof Hero)) || UnitHelper.getInstance().getHero().getMp() >= manaCost;
        return hasMana && !isOnCooldown();
    }

    public void use(){
        if (owner != null && owner.isInvisible()) {
            owner.setInvisible(false);
        }

        if(owner instanceof Hero){
            UnitHelper.getInstance().getHero().modifyMana(-manaCost);
            UIHelper.getInstance().setMeleeButtonCostCheck(UnitHelper.getInstance().getHero().getMp() >= manaCost);
        }

        identify();

        addProjectile(0);

        SoundHelper.GetSingleton().play(getCastSound(), 0f, 1f);
    }

    protected Sounds getCastSound() {
        return Sounds.ZAP;
    }

    public int getManaCost(){
        return manaCost;
    }

    @Override
    public boolean canUpgrade() {
        return manaCost > 1;
    }

    @Override
    public Wand setPrefix() {
        return setPrefix((Prefix) null);
    }

    @Override
    public Wand setLevel(int level) {
        if (baseManaCost < 1) {
            baseManaCost = Math.max(1, manaCost);
        }

        this.level = Math.max(1, level);
        manaCost = Math.max(1, baseManaCost - Math.max(0, this.level - 1));
        return this;
    }

    @Override
    public Wand modifyLevel(int amount) {
        return setLevel(level + amount);
    }

    @Override
    public Wand setPrefix(Prefix prefix) {
        return (Wand) super.setPrefix(null);
    }

    public void startCooldown(float durationSeconds) {
        cooldownUntilMillis = TimeUtils.millis() + (long) (Math.max(0f, durationSeconds) * 1000f);
    }

    public boolean isOnCooldown() {
        return TimeUtils.millis() < cooldownUntilMillis;
    }

    public float getCooldownRemainingSeconds() {
        return Math.max(0L, cooldownUntilMillis - TimeUtils.millis()) / 1000f;
    }

    protected float getWandPowerMultiplier() {
        if (owner instanceof Hero) {
            return ((Hero) owner).getWandPowerModifier();
        }

        return 1f;
    }

    protected float scalePower(float baseAmount) {
        return baseAmount * getWandPowerMultiplier();
    }

    protected Rectangle getBeamArea(float rangeTiles, float beamHeight) {
        float beamLength = rangeTiles * ConstantsHelper.TILE;
        float beamX = owner.facingRight
                ? owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f
                : owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f - beamLength;
        float beamY = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f - beamHeight / 2f;
        return new Rectangle(beamX, beamY, beamLength, beamHeight);
    }

    protected ArrayList<Unit> getHostilesInBeam(float rangeTiles, float beamHeight) {
        ArrayList<Unit> targets = new ArrayList<Unit>();
        if (owner == null || owner.getRoom() == null) {
            return targets;
        }

        Rectangle beamArea = getBeamArea(rangeTiles, beamHeight);
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit == owner || unit.showOnly() || unit.isDead()) {
                continue;
            }

            if (unit.isFriendly == owner.isFriendly || unit.getRoom() == null || !unit.getRoom().equals(owner.getRoom())) {
                continue;
            }

            if (beamArea.overlaps(unit.getHitBox())) {
                targets.add(unit);
            }
        }

        return targets;
    }

    protected Unit getFirstHostileInBeam(float rangeTiles, float beamHeight) {
        ArrayList<Unit> targets = getHostilesInBeam(rangeTiles, beamHeight);
        Unit closestTarget = null;
        float closestDistance = Float.MAX_VALUE;
        float ownerCenterX = owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;

        for (Unit unit : targets) {
            float targetCenterX = unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
            float distance = Math.abs(targetCenterX - ownerCenterX);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestTarget = unit;
            }
        }

        return closestTarget;
    }

    protected ArrayList<Unit> getHostilesNear(float originX, float originY, float horizontalTiles, float verticalTiles) {
        ArrayList<Unit> targets = new ArrayList<Unit>();
        if (owner == null || owner.getRoom() == null) {
            return targets;
        }

        float originCenterX = originX + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float originCenterY = originY + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float maxDeltaX = horizontalTiles * ConstantsHelper.TILE;
        float maxDeltaY = verticalTiles * ConstantsHelper.TILE;

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit == owner || unit.showOnly() || unit.isDead()) {
                continue;
            }

            if (unit.isFriendly == owner.isFriendly || unit.getRoom() == null || !unit.getRoom().equals(owner.getRoom())) {
                continue;
            }

            float unitCenterX = unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
            float unitCenterY = unit.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
            if (Math.abs(unitCenterX - originCenterX) <= maxDeltaX && Math.abs(unitCenterY - originCenterY) <= maxDeltaY) {
                targets.add(unit);
            }
        }

        return targets;
    }

    @Override
    public String getBigDescription(){
        if (!isIdentified()) {
            return getDescription();
        }

        StringBuilder info = new StringBuilder( getDescription() );

        String speedDescription = "";
        if(speed < 1f){
            speedDescription = "slow";
        }

        if(speed > 1f){
            speedDescription = "fast";
        }

        info.append("\n");
    if(speedDescription.equals("")){
        info.append("\n").append(Messages.maybeTranslate(
            "This %s is a tier %d wand.",
            getName().toLowerCase(),
            tier));
    }
    else {
        info.append("\n").append(Messages.maybeTranslate(
            "This %s is a tier %d %s wand.",
            getName().toLowerCase(),
            tier,
            Messages.maybeTranslate(speedDescription)));
    }

    info.append(" ").append(Messages.maybeTranslate(
        "You need %d mana to use the wand.",
        manaCost));

        info.append(getStrengthRequirementText());

        return info.toString();
    }

    @Override
    public boolean showsAttackAnimation() {
        return false;
    }

    void addProjectile(float variance){

    }
}

