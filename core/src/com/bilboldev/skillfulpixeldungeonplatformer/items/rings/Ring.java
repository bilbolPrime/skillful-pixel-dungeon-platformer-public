package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ItemIdentityHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class Ring extends EquipableItem {
    public static final int MAX_EQUIPPED_RINGS = 2;
    private static final float IDENTIFICATION_TIME_SECONDS = 18f;
    protected Prefix prefix;
    protected int level = 1;

    {
        goldCost = 80;
        setIdentityFamily(ItemIdentityHelper.Family.RING);
        setUnknownDescription("An unidentified ring. Its magic is unknown until it is worn.");
    }

    public boolean canEquip() {
        return equipped || countEquippedRings() < MAX_EQUIPPED_RINGS;
    }

    public String getEquipFailureMessage() {
        return "You can only equip two rings at a time.";
    }

    @Override
    public void setEquipped(boolean equipped) {
        if (this.equipped == equipped) {
            return;
        }

        if (equipped) {
            if (!canEquip()) {
                return;
            }

            Hero hero = UnitHelper.getInstance().getHero();
            owner = hero;
            onEquipped(hero);
            this.equipped = true;
            playCurseFeedback(hero);
            if (hero != null && hero.getHeroClass() == HeroClass.ROGUE) {
                identify();
            }
            return;
        }

        Hero hero = owner instanceof Hero ? (Hero) owner : UnitHelper.getInstance().getHero();
        if (!canUnequip()) {
            showCurseLockedMessage();
            return;
        }

        if (hero != null) {
            onUnequipped(hero);
        }

        owner = null;
        this.equipped = equipped;
    }

    protected void onEquipped(Hero hero) {
    }

    protected void onUnequipped(Hero hero) {
    }

    protected void whileEquipped(Hero hero, float deltaSeconds) {
    }

    public boolean canUnequip() {
        return !equipped || !(prefix instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.rings.Cursed);
    }

    private void showCurseLockedMessage() {
        WindowHelper.getInstance().addWindow(1200f, 120f, "The cursed ring will not come off.");
    }

    private void playCurseFeedback(Hero hero) {
        if (!(prefix instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.rings.Cursed) || hero == null) {
            return;
        }

        SoundHelper.GetSingleton().play(Sounds.CURSE, 0f, 0.45f);
        EffectsHelper.getInstance().blackSpark(hero);
        EffectsHelper.getInstance().blackSpark(hero);
        EffectsHelper.getInstance().blackSpark(hero);
    }

    @Override
    public boolean canUpgrade() {
        return supportsLevel();
    }

    public Ring setPrefix(Prefix prefix) {
        this.prefix = prefix;
        return this;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public void progressIdentification(float deltaSeconds) {
        if (!equipped || isIdentified()) {
            return;
        }

        Hero hero = owner instanceof Hero ? (Hero) owner : UnitHelper.getInstance().getHero();
        if (hero == null) {
            return;
        }

        if (hero.getHeroClass() == HeroClass.ROGUE) {
            identify();
            return;
        }

        ItemIdentityHelper.getInstance().addIdentificationProgress(this, deltaSeconds, IDENTIFICATION_TIME_SECONDS);
    }

    public void actWhileEquipped(float deltaSeconds) {
        if (!equipped) {
            return;
        }

        Hero hero = owner instanceof Hero ? (Hero) owner : UnitHelper.getInstance().getHero();
        if (hero == null) {
            return;
        }

        whileEquipped(hero, deltaSeconds);
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
    public Ring setLevel(int level) {
        this.level = Math.max(1, level);
        return this;
    }

    @Override
    public Ring modifyLevel(int amount) {
        Hero hero = owner instanceof Hero ? (Hero) owner : UnitHelper.getInstance().getHero();
        boolean refreshEquippedEffect = equipped && hero != null;
        if (refreshEquippedEffect) {
            onUnequipped(hero);
        }

        level = Math.max(1, level + amount);

        if (refreshEquippedEffect) {
            onEquipped(hero);
        }

        return this;
    }

    @Override
    public String getName() {
        String baseName = super.getName();
        if (isIdentified() && prefix != null && !prefix.isEnhancement()) {
            baseName = prefix.getName() + " " + baseName;
        }

        return withLevelSuffix(baseName);
    }

    @Override
    public String getBigDescription() {
        if (!isIdentified()) {
            return getDescription();
        }

        StringBuilder info = new StringBuilder(super.getBigDescription());
        if (prefix != null) {
            info.append("\n\nIt is ").append(prefix.getName().toLowerCase()).append(".\n")
                    .append(prefix.getDescription());
        }
        info.append("\n\nYou can equip up to 2 rings at the same time.");
        if (getLevel() > 1) {
            info.append("\nIts magic has been refined to +").append(getLevel() - 1).append(".");
        }

        return info.toString();
    }

    private int countEquippedRings() {
        int count = 0;
        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (item instanceof Ring && ((Ring) item).getEquipped()) {
                count++;
            }
        }
        return count;
    }

    protected static int countEquipped(Class<? extends Ring> ringClass) {
        int count = 0;
        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (ringClass.isInstance(item) && item instanceof Ring && ((Ring) item).getEquipped()) {
                count++;
            }
        }
        return count;
    }

    protected static int getEquippedLevelSum(Class<? extends Ring> ringClass) {
        int totalLevels = 0;
        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (ringClass.isInstance(item) && item instanceof Ring && ((Ring) item).getEquipped()) {
                totalLevels += Math.max(1, ((Ring) item).getLevel());
            }
        }
        return totalLevels;
    }
}