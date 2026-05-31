package com.bilboldev.skillfulpixeldungeonplatformer.items.armor;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.Cursed;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Armor extends EquipableItem {

    protected Prefix prefix;
    protected float speed = 1f;
    protected int tier = 1;
    protected int level = 1;
    protected int baseRequiredStrength = 1;

    @Override
    public void setEquipped(boolean equipped){
        setEquipped(equipped, true);
    }

    public void setEquipped(boolean equipped, boolean unequipCheck){
        if (equipped == this.equipped) {
            return;
        }

        if (equipped) {
            if (unequipCheck && !unEquipCheck()) {
                return;
            }

            UnitHelper.getInstance().getHero().setArmor(this);
            this.equipped = true;
            playCurseFeedback();
            return;
        }

        if (!canUnequip()) {
            showCurseLockedMessage();
            return;
        }

        UnitHelper.getInstance().getHero().setArmor((Armor)new BirthdaySuit().setOwner(UnitHelper.getInstance().getHero()));
        this.equipped = false;
    }

    public boolean unEquipCheck(){
        for(Item item : InventoryHelper.getInstance().getItems()){
            if (!(item instanceof Armor) || item == this) {
                continue;
            }

            Armor armor = (Armor) item;
            if (!armor.getEquipped()) {
                continue;
            }

            if (!armor.canUnequip()) {
                armor.showCurseLockedMessage();
                return false;
            }

            armor.setEquipped(false, false);
        }

        return true;
    }

    public boolean canUnequip() {
        return !equipped || !(prefix instanceof Cursed);
    }

    private void showCurseLockedMessage() {
        WindowHelper.getInstance().addWindow(1200f, 120f, "The cursed armor will not come off.");
    }

    private void playCurseFeedback() {
        if (!(prefix instanceof Cursed) || owner == null) {
            return;
        }

        SoundHelper.GetSingleton().play(Sounds.CURSE, 0f, 0.45f);
        EffectsHelper.getInstance().blackSpark(owner);
        EffectsHelper.getInstance().blackSpark(owner);
        EffectsHelper.getInstance().blackSpark(owner);
    }

    public int getProtection(){
        float prefixModifier = prefix != null ? prefix.getModifier() : 1f;
        int protection = Math.round((tier * 2f + Math.max(0, level - 1)) * prefixModifier);
        return Math.max(0, protection);
    }

    @Override
    public String getBigDescription(){

        StringBuilder info = new StringBuilder( getDescription() );

        if(prefix != null){
            if (prefix.isEnhancement()) {
                info.append("\n\n").append(Messages.maybeTranslate(
                        "It bears the %s.\n%s",
                        prefix.getName().toLowerCase(),
                        prefix.getDescription()));
            }
            else {
                info.append("\n\n").append(Messages.maybeTranslate(
                        "It is %s.\n%s",
                        prefix.getName().toLowerCase(),
                        prefix.getDescription()));
            }
        }
        else {
            info.append("\n");
        }

        info.append("\n").append(Messages.maybeTranslate(
                "This %s is a tier %d armor.",
                getName().toLowerCase(),
                tier));

        info.append(" ").append(Messages.maybeTranslate(
                "It can absorb %d damage.",
                getProtection()));

        info.append(getStrengthRequirementText());

        return info.toString();
    }

    @Override
    public String getName(){
        String baseName = prefix != null && !prefix.isEnhancement() ? prefix.getName() + " " + super.getName() : super.getName();
        return withLevelSuffix(baseName);
    }

    public Armor setPrefix(Prefix prefix){
        this.prefix = prefix;
        return this;
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
    public Armor setLevel(int level) {
        this.level = Math.max(1, level);
        return this;
    }

    @Override
    public Armor modifyLevel(int amount) {
        return setLevel(level + amount);
    }

    public Armor setPrefix(){
        return setPrefix(null);
    }

    public int getTier(){
        return tier;
    }

    @Override
    public int getRequiredStrength(){
        int typicalStrength = 7 + 2 * tier;
        int upgradedRequirement = Math.max(baseRequiredStrength, typicalStrength) - Math.max(0, level - 1);
        return Math.max(1, upgradedRequirement + (prefix != null ? prefix.getLevelModifier() : 0));
    }

    public float getStrengthMoveSpeedMultiplier() {
        int shortfall = getStrengthShortfall();
        return shortfall < 1 ? 1f : (float) Math.pow(1.3f, -shortfall);
    }

    public int getStrengthShortfall() {
        Unit strengthOwner = getStrengthOwner();
        return strengthOwner == null ? 0 : Math.max(0, getRequiredStrength() - strengthOwner.getStrength());
    }

    private String getStrengthRequirementText() {
        StringBuilder info = new StringBuilder();
        info.append("\n\n").append(Messages.maybeTranslate(
                "You need %d Strength to use the %s.",
                getRequiredStrength(),
                getName().toLowerCase()));

        int shortfall = getStrengthShortfall();
        if (shortfall > 0) {
            info.append(" ").append(Messages.maybeTranslate(
                    "You are short by %d, reducing movement speed and armor effectiveness.",
                    shortfall));
        }

        return info.toString();
    }

    private Unit getStrengthOwner() {
        if (owner == null) {
            return UnitHelper.getInstance().getHero();
        }

        return owner.usesEquipmentStrengthRequirements() ? owner : null;
    }
}

