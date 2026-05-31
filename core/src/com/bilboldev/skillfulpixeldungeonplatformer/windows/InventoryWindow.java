package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EnhancementVisualHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SkillsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.AmuletOfYendor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.ConsumableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.TomeOfMastery;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.Potion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.quest.Pickaxe;
import com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.Seed;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ActiveSkill;

import java.util.ArrayList;

public class InventoryWindow extends InteractiveTabbedWindow {

    private static final float LOCKED_SKILL_ALPHA = 0.25f;
    private static final float TITLE_DESCRIPTION_X = 340f;
    private static final float TITLE_DESCRIPTION_RIGHT_PADDING = 120f;

    private static final String HERO_LEVEL_ICON = "images/achievements/LEVEL_REACHED_1.png";
    private static final String HERO_STRENGTH_ICON = "images/achievements/STRENGTH_ATTAINED_1.png";
    private static final String HERO_ATTACK_ICON = "images/weapons/sword.png";
    private static final String HERO_DEFENSE_ICON = "images/misc/extracted items/ARMOR_PLATE.png";
    private static final String HERO_STEALTH_ICON = "images/skills/Stealth.png";

    protected enum MODE {
        HERO,
        INVENTORY,
        SKILLS,
        SKILLS_OTHER
    }

    private GameSprite heroSprite;
    private MODE mode = MODE.HERO;

    private final ArrayList<ActionButton> skillButtons;
    private final ArrayList<ActionButton> heroButtons;
    private final ArrayList<ActionButton> inventoryButtons;
    private final ArrayList<ActionButton> darkSkillButtons;
    private final ArrayList<Item> previewItems;

    private final HeroClass heroClass;
    private final boolean readOnlyPreview;
    private final SaveHelper.HeroSaveData previewHeroData;
    private final SaveHelper.InventorySaveData previewInventoryData;
    private ActionButton journalButton;

    private Item previewMeleeItem;
    private Item previewArmorItem;
    private Item previewRangedItem;

    String titleDescription;
    private float titleDescriptionFontSize = 3f;

    public InventoryWindow(HeroClass heroClass, float width, float height) {
        this(heroClass, width, height, null, null, false);
    }

    public InventoryWindow(SaveHelper.RankingRunData rankingRunData, float width, float height) {
        this(SaveHelper.getInstance().resolveRankingHeroClass(rankingRunData),
                width,
                height,
                rankingRunData != null ? rankingRunData.heroSaveData : null,
                rankingRunData != null ? rankingRunData.inventorySaveData : null,
                true);
    }

    private InventoryWindow(HeroClass heroClass,
                            float width,
                            float height,
                            SaveHelper.HeroSaveData previewHeroData,
                            SaveHelper.InventorySaveData previewInventoryData,
                            boolean readOnlyPreview) {
        super(width, height);
        skillButtons = new ArrayList<>();
        heroButtons = new ArrayList<>();
        inventoryButtons = new ArrayList<>();
        darkSkillButtons = new ArrayList<>();
        previewItems = new ArrayList<>();
        this.heroClass = heroClass;
        this.previewHeroData = previewHeroData;
        this.previewInventoryData = previewInventoryData;
        this.readOnlyPreview = readOnlyPreview;
    }

    @Override
    public Window build() {
        super.build();

        rebuildPreviewItems();
        heroSprite = heroClass.getClassPortrait();
        heroSprite.setPosition(x + 100, y + height - 300);

        buildSkillButtons();
        buildHeroButtons();
        buildInventoryButtons();
        buildJournalButton();
        addTabs();

        if (readOnlyPreview) {
            setTitleDescription(getInventoryTitleDescriptionSource(), getInventoryTitleDescription());
            mode = MODE.INVENTORY;
            if (tabs.size() > 1) {
                tabs.get(1).click();
            }
        } else {
            setTitleDescription(heroClass.getSourceDescription(), heroClass.getDescription());
        }

        return this;
    }

    private void rebuildPreviewItems() {
        previewItems.clear();
        previewMeleeItem = null;
        previewArmorItem = null;
        previewRangedItem = null;

        if (!readOnlyPreview || previewInventoryData == null || previewInventoryData.items == null) {
            return;
        }

        for (SaveHelper.ItemSaveData itemSaveData : previewInventoryData.items) {
            Item item = SaveHelper.getInstance().createPreviewItem(itemSaveData);
            if (item == null) {
                continue;
            }

            previewItems.add(item);
            if (itemSaveData.equippedMeleeSlot) {
                previewMeleeItem = item;
            }
            if (itemSaveData.equippedArmorSlot) {
                previewArmorItem = item;
            }
            if (itemSaveData.equippedRangedSlot) {
                previewRangedItem = item;
            }
        }
    }

    private void buildSkillButtons() {
        for (Integer skillId : heroClass.getSkillIds()) {
            Skill skill = SkillsHelper.getInstance().getSkill(skillId);
            skillButtons.add(new SelectSkillButton(
                    x + heroClass.getSkillButtonXOffset(skillId, false),
                    y + height + heroClass.getSkillButtonYOffset(skillId, false),
                    skill));
        }

        for (Integer skillId : heroClass.getDarkSkillIds()) {
            Skill skill = SkillsHelper.getInstance().getSkill(skillId);
            darkSkillButtons.add(new SelectSkillButton(
                    x + heroClass.getSkillButtonXOffset(skillId, true),
                    y + height + heroClass.getSkillButtonYOffset(skillId, true),
                    skill));
        }
    }

    private void buildHeroButtons() {
        float offsetY = y + height - 480f;
        float leftX = x + 100f;
        float middleX = x + 550f;
        float rightX = x + 1000f;

        heroButtons.add(new SelectSkillButton(leftX, offsetY, HERO_LEVEL_ICON, getLevelText()));
        heroButtons.add(new SelectSkillButton(middleX, offsetY, HERO_STRENGTH_ICON, getStrengthText()));
        heroButtons.add(createHeroItemButton(rightX, offsetY, previewMeleeItem, getLiveWeapon(), "No melee"));

        offsetY -= 125f;
        heroButtons.add(new SelectSkillButton(leftX, offsetY, "images/stats/health.png", getHealthText()));
        heroButtons.add(new SelectSkillButton(middleX, offsetY, HERO_ATTACK_ICON, getAttackText()));
        heroButtons.add(createHeroItemButton(rightX, offsetY, previewArmorItem, getLiveArmor(), "No armor"));

        offsetY -= 125f;
        heroButtons.add(new SelectSkillButton(leftX, offsetY, "images/stats/mana.png", getManaText()));
        heroButtons.add(new SelectSkillButton(middleX, offsetY, HERO_DEFENSE_ICON, getDefenseText()));
        heroButtons.add(createHeroItemButton(rightX, offsetY, previewRangedItem, getLiveRangedWeapon(), "No ranged"));

        offsetY -= 125f;
        heroButtons.add(new SelectSkillButton(leftX, offsetY, "images/misc/exp.png", getExperienceText()));
        heroButtons.add(new SelectSkillButton(middleX, offsetY, HERO_STEALTH_ICON, getStealthText()));
        heroButtons.add(new SelectSkillButton(rightX, offsetY, "images/misc/gold.png", getGoldText()));
    }

    private void buildInventoryButtons() {
        float offsetY = y + height - 480f;
        float offsetX = x + 100f;
        int counter = 0;

        for (Item item : getDisplayedInventoryItems()) {
            counter++;
            inventoryButtons.add(new InventoryButton(offsetX, offsetY, item));
            offsetY -= 125f;
            if (counter % 4 == 0) {
                offsetX += 450f;
                offsetY = y + height - 480f;
            }
        }

        for (int i = counter + 1; i <= 16; i++) {
            inventoryButtons.add(new InventoryButton(offsetX, offsetY, "images/misc/transparent.png", "-"));
            offsetY -= 125f;
            if (i % 4 == 0) {
                offsetX += 450f;
                offsetY = y + height - 480f;
            }
        }
    }

    private void buildJournalButton() {
        if (readOnlyPreview) {
            journalButton = null;
            return;
        }

        journalButton = new JournalButton(x + width - 155f, y + height - 155f);
    }

    private void addTabs() {
        addTab(new Tab("Hero", x - 100, y + 700, heroClass.getClassPortrait().spriteString) {
            @Override
            public void click() {
                super.click();
                setTitleDescription(heroClass.getSourceDescription(), heroClass.getDescription());
                mode = MODE.HERO;
            }
        });

        addTab(new Tab("Inventory", x - 100, y + 500, "images/buttons/back-pack.png") {
            @Override
            public void click() {
                super.click();
                setTitleDescription(getInventoryTitleDescriptionSource(), getInventoryTitleDescription());
                mode = MODE.INVENTORY;
            }
        });

        addTab(new Tab("Skills", x - 100, y + 300, "images/skills/light-skills.png") {
            @Override
            public void click() {
                super.click();
                setTitleDescription(getSkillsTitleDescriptionSource(), getSkillsTitleDescription());
                mode = MODE.SKILLS;
            }
        });

        addTab(new Tab("Skills Other", x - 100, y + 100, "images/skills/dark-skills.png") {
            @Override
            public void click() {
                super.click();
                setTitleDescription(getOtherSkillsTitleDescriptionSource(), getOtherSkillsTitleDescription());
                mode = MODE.SKILLS_OTHER;
            }
        });
    }

    private float getTitleDescriptionWrapWidth() {
        return Math.max(100f, width - TITLE_DESCRIPTION_X - TITLE_DESCRIPTION_RIGHT_PADDING);
    }

    private String getInventoryTitleDescriptionSource() {
        if (!readOnlyPreview) {
            return "You can hold up to 16 items in your backpack.";
        }

        return "Read-only final inventory snapshot for this run.";
    }

    private String getInventoryTitleDescription() {
        return Messages.maybeTranslate(getInventoryTitleDescriptionSource());
    }

    private String getSkillsTitleDescriptionSource() {
        return getSkillsIntroSource() + "\n" + getSkillPointsLeftDescriptionSource();
    }

    private String getSkillsTitleDescription() {
        return Messages.maybeTranslate(getSkillsIntroSource()) + "\n" + getSkillPointsLeftDescription();
    }

    private String getSkillsIntroSource() {
        switch (heroClass) {
            case WIZARD:
                return "Wizards choose between Battle Mage and Warlock after reading the Tome of Mastery dropped by Tengu.";
            case ARCHER:
                return "Huntresses choose between Warden and Sniper after reading the Tome of Mastery dropped by Tengu.";
            case ROGUE:
                return "Rogues choose between Free Runner and Assassin after reading the Tome of Mastery dropped by Tengu.";
            case WARRIOR:
            default:
                return "Warriors choose between Gladiator and Berserker after reading the Tome of Mastery dropped by Tengu.";
        }
    }

    private String getOtherSkillsTitleDescriptionSource() {
        return getOtherSkillsIntroSource() + "\n" + getSkillPointsLeftDescriptionSource();
    }

    private String getOtherSkillsTitleDescription() {
        return Messages.maybeTranslate(getOtherSkillsIntroSource()) + "\n" + getSkillPointsLeftDescription();
    }

    private String getOtherSkillsIntroSource() {
        switch (heroClass) {
            case WIZARD:
                return "Warlock skills rely on curses and turning enemies against each other. Unlock Warlock through the Tome of Mastery dropped by Tengu.";
            case ARCHER:
                return "Sniper skills specialize in crippling, disabling, and explosive arrow attacks. Unlock Sniper through the Tome of Mastery dropped by Tengu.";
            case ROGUE:
                return "Assassin skills rely on venom, evasion, and finishing blows. Unlock Assassin through the Tome of Mastery dropped by Tengu.";
            case WARRIOR:
            default:
                return "Berserker skills reward aggression and low-health fighting. Unlock Berserker through the Tome of Mastery dropped by Tengu.";
        }
    }

    private String getSkillPointsLeftDescriptionSource() {
        if (readOnlyPreview) {
            return (heroClass != null ? heroClass.getSourceName() : "") + " had " + getSkillPoints() + " skill points left.";
        }

        return "You have " + getSkillPoints() + " skill points left.";
    }

    private String getSkillPointsLeftDescription() {
        if (readOnlyPreview) {
            return Messages.get("custom.ui.rankings.skill_points_left",
                    new Object[]{heroClass != null ? heroClass.getName() : "", getSkillPoints()});
        }

        return Messages.get("custom.generated.you_have_arg_skill_points_b2c3e75456",
                new Object[]{getSkillPoints()});
    }

    private int getSkillPoints() {
        if (readOnlyPreview) {
            return previewHeroData != null ? previewHeroData.skillPoints : 0;
        }

        return UnitHelper.getInstance().getHero().getSkillPoints();
    }

    private String getHealthText() {
        if (readOnlyPreview) {
            return previewHeroData != null ? previewHeroData.hp + " / " + previewHeroData.maxHp : "- / -";
        }

        return UnitHelper.getInstance().getHero().getHP() + " / " + UnitHelper.getInstance().getHero().getMaxHP();
    }

    private String getManaText() {
        if (readOnlyPreview) {
            return previewHeroData != null ? previewHeroData.mp + " / " + previewHeroData.maxMp : "- / -";
        }

        return UnitHelper.getInstance().getHero().getMp() + " / " + UnitHelper.getInstance().getHero().getMmp();
    }

    private String getLevelText() {
        if (!readOnlyPreview) {
            return "Level " + UnitHelper.getInstance().getHero().getLevel();
        }

        if (previewHeroData == null) {
            return "Level ?";
        }

        return "Level " + previewHeroData.level;
    }

    private String getStrengthText() {
        if (!readOnlyPreview) {
            return Integer.toString(UnitHelper.getInstance().getHero().getStrength());
        }

        if (previewHeroData == null) {
            return "-";
        }

        return Integer.toString(getPreviewStrength());
    }

    private int getPreviewStrength() {
        if (previewHeroData == null) {
            return 0;
        }

        return heroClass.getBaseStrength()
                + Math.max(0, (previewHeroData.level - 1) / 2)
                + previewHeroData.bonusStrength
                + (hasPreviewSkill(Skills.TRAINING) ? 1 : 0);
    }

    private String getAttackText() {
        if (!readOnlyPreview) {
            return Integer.toString(UnitHelper.getInstance().getHero().getAttackSkill(null, UnitHelper.getInstance().getHero().getWeapon()));
        }

        if (previewHeroData == null) {
            return "-";
        }

        int baseAttackSkill = heroClass.getBaseAttackSkill() + Math.max(0, previewHeroData.level - 1);
        float weaponModifier = 1f;
        if (previewMeleeItem instanceof Weapon) {
            Weapon previewWeapon = (Weapon) previewMeleeItem;
            int shortfall = Math.max(0, previewWeapon.getRequiredStrength() - getPreviewStrength());
            if (shortfall > 0) {
                weaponModifier = (float) Math.pow(1.5f, -shortfall);
            }
        }

        return Integer.toString(Math.max(0, Math.round(baseAttackSkill * weaponModifier)));
    }

    private String getDefenseText() {
        if (!readOnlyPreview) {
            return Integer.toString(UnitHelper.getInstance().getHero().getDefenseSkill(null));
        }

        if (previewHeroData == null) {
            return "-";
        }

        float totalDefense = heroClass.getBaseDefenseSkill() + Math.max(0, previewHeroData.level - 1);
        if (previewArmorItem instanceof Armor) {
            Armor previewArmor = (Armor) previewArmorItem;
            int shortfall = Math.max(0, previewArmor.getRequiredStrength() - getPreviewStrength());
            if (shortfall > 0) {
                totalDefense /= (float) Math.pow(1.5f, shortfall);
            }
        }

        return Integer.toString(Math.max(0, Math.round(totalDefense)));
    }

    private String getStealthText() {
        if (!readOnlyPreview) {
            return Integer.toString(UnitHelper.getInstance().getHero().getStealthScore());
        }

        if (previewHeroData == null) {
            return "-";
        }

        int stealthScore = 0;
        if (hasPreviewSkill(Skills.STEALTH)) {
            stealthScore++;
        }
        if (hasPreviewSkill(Skills.ASSASSIN)) {
            stealthScore++;
        }
        return Integer.toString(stealthScore);
    }

    private boolean hasPreviewSkill(int skillId) {
        return previewHeroData != null
                && previewHeroData.unlockedSkillIds != null
                && previewHeroData.unlockedSkillIds.contains(skillId);
    }

    private String getExperienceText() {
        if (!readOnlyPreview) {
            return UnitHelper.getInstance().getHero().expString();
        }

        if (previewHeroData == null) {
            return "Unknown";
        }

        int nextExp = 10 + ((previewHeroData.level - 1) * 20)
                + (previewHeroData.level > 2 ? 30 * (int) Math.pow(1.1, previewHeroData.level) : 0);
        return String.format("%,d / %,d", previewHeroData.experience, nextExp);
    }

    private String getGoldText() {
        if (readOnlyPreview) {
            return previewInventoryData != null ? String.valueOf(previewInventoryData.gold) : "-";
        }

        return String.valueOf(InventoryHelper.getInstance().getGold());
    }

    private Item getLiveWeapon() {
        return !readOnlyPreview ? UnitHelper.getInstance().getHero().getWeapon() : null;
    }

    private Item getLiveArmor() {
        return !readOnlyPreview ? UnitHelper.getInstance().getHero().getArmor() : null;
    }

    private Item getLiveRangedWeapon() {
        return !readOnlyPreview ? UnitHelper.getInstance().getHero().getRangedWeapon() : null;
    }

    private ActionButton createHeroItemButton(float x, float y, Item previewItem, Item liveItem, String fallbackText) {
        Item item = readOnlyPreview ? previewItem : liveItem;
        if (item != null) {
            return new SelectSkillButton(x, y, item, item.getName());
        }
        return new SelectSkillButton(x, y, "images/misc/transparent.png", fallbackText);
    }

    private ArrayList<Item> getDisplayedInventoryItems() {
        return readOnlyPreview ? previewItems : InventoryHelper.getInstance().getItems();
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);

        heroSprite.draw(batch);
        FontHelper.getSingleton().writeRaw(Color.WHITE, batch, titleDescriptionFontSize, x + TITLE_DESCRIPTION_X, y + height - 150, titleDescription);

        if (mode == MODE.SKILLS) {
            heroClass.drawBranches(batch, x, y, width, height);

            for (ActionButton actionButton : skillButtons) {
                actionButton.draw(batch);
            }
        }

        if (mode == MODE.HERO) {
            for (ActionButton actionButton : heroButtons) {
                actionButton.draw(batch);
            }
        }

        if (mode == MODE.INVENTORY) {
            for (ActionButton actionButton : inventoryButtons) {
                actionButton.draw(batch);
            }
        }

        if (mode == MODE.SKILLS_OTHER) {
            heroClass.drawDarkSkillsBranches(batch, x, y, width, height);

            for (ActionButton actionButton : darkSkillButtons) {
                actionButton.draw(batch);
            }
        }

        if (mode == MODE.INVENTORY && journalButton != null) {
            journalButton.draw(batch);
        }
    }

    @Override
    public boolean click(float x, float y) {
        if (mode == MODE.INVENTORY && journalButton != null && journalButton.isHitProjected(x, y)) {
            journalButton.click();
            return true;
        }

        if (mode == MODE.SKILLS) {
            for (ActionButton actionButton : skillButtons) {
                if (actionButton.isHitProjected(x, y)) {
                    actionButton.click();
                    return true;
                }
            }
        }

        if (mode == MODE.SKILLS_OTHER) {
            for (ActionButton actionButton : darkSkillButtons) {
                if (actionButton.isHitProjected(x, y)) {
                    actionButton.click();
                    return true;
                }
            }
        }

        if (mode == MODE.HERO) {
            for (ActionButton actionButton : heroButtons) {
                if (actionButton.isHitProjected(x, y)) {
                    actionButton.click();
                    return true;
                }
            }
        }

        if (mode == MODE.INVENTORY) {
            for (ActionButton actionButton : inventoryButtons) {
                if (actionButton.isHitProjected(x, y)) {
                    actionButton.click();
                    return true;
                }
            }
        }

        return super.click(x, y);
    }

    private void showSkill(Skill skill) {
        if (readOnlyPreview) {
            return;
        }

        SkillWindow skillWindow = new SkillWindow(skill);
        if (SkillsHelper.getInstance().canLearn(skill)) {
            skillWindow = (SkillWindow) skillWindow.addUnlock();
        }

        if (skill instanceof ActiveSkill && UnitHelper.getInstance().getHero().hasSkill(skill.getId())) {
            skillWindow = (SkillWindow) skillWindow.addSetQuickSkill();
        }

        WindowHelper.getInstance().addWindow(skillWindow.build());
    }

    private boolean shouldFadeSkillButton(Skill skill) {
        if (skill == null || readOnlyPreview) {
            return false;
        }

        if (UnitHelper.getInstance().getHero() == null || UnitHelper.getInstance().getHero().hasSkill(skill.getId())) {
            return false;
        }

        if (UnitHelper.getInstance().getHero().skillLockedOut(skill.getId())) {
            return true;
        }

        for (Integer requirement : heroClass.getSkillRequirements(skill.getId())) {
            if (!UnitHelper.getInstance().getHero().hasSkill(requirement)) {
                return true;
            }
        }

        return SkillsHelper.getInstance().isSubclassSkill(skill.getId()) && hasChosenSubclass();
    }

    private boolean hasChosenSubclass() {
        if (UnitHelper.getInstance().getHero() == null) {
            return false;
        }

        return UnitHelper.getInstance().getHero().hasSkill(Skills.GLADIATOR)
                || UnitHelper.getInstance().getHero().hasSkill(Skills.BERSERKER)
                || UnitHelper.getInstance().getHero().hasSkill(Skills.BATTLE_MAGE)
                || UnitHelper.getInstance().getHero().hasSkill(Skills.WARLOCK)
                || UnitHelper.getInstance().getHero().hasSkill(Skills.FREE_RUNNER)
                || UnitHelper.getInstance().getHero().hasSkill(Skills.ASSASSIN)
                || UnitHelper.getInstance().getHero().hasSkill(Skills.WARDEN)
                || UnitHelper.getInstance().getHero().hasSkill(Skills.SNIPER);
    }

    private class SelectSkillButton extends ActionButton {

        private final String text;
        private final boolean selected;
        private final Skill skill;
        private final float buttonAlpha;
        protected float fontSize = 3f;
        private final GameSprite highlight;
        private final Color textColor = new Color(Color.WHITE);

        public SelectSkillButton(float x, float y, String gsString, String text) {
            super(x, y, 400, 100, "images/misc/grey.png", "images/misc/grey.png");

            String localizedText = Messages.maybeTranslate(text);
            fontSize = FontHelper.getSingleton().fitSizeToEnglishFootprint(text, localizedText, 3f, 300f);

            GameSprite gs = new GameSprite(gsString, 72, 72);
            gs.setPosition(14, 14);
            addGameSprite(gs);

            highlight = new GameSprite("images/misc/yellow-highlight.png", 400, 100, 0.3f);
            highlight.setPosition(x, y);

            this.text = text;
            this.selected = false;
            this.skill = null;
            this.buttonAlpha = 1f;
        }

        public SelectSkillButton(float x, float y, Item item, String text) {
            this(x, y, item.getGameSprite().spriteString, text);

            GameSprite itemSprite = item.getGameSprite();
            if (itemSprite != null) {
                clearSprites();
                GameSprite iconSprite = itemSprite.clone();
                iconSprite.setWidth(72);
                iconSprite.setHeight(72);
                iconSprite.setPosition(14, 14);
                iconSprite.setRotation(0f);
                EnhancementVisualHelper.applyItemEnhancementPulse(iconSprite, item);
                addGameSprite(iconSprite);
            }
        }

        public SelectSkillButton(float x, float y, Skill skill) {
            super(x, y, 400, 100, "images/misc/grey.png", "images/misc/grey.png");

            GameSprite gs = skill.getGameSprite().clone();
            gs.setWidth(72);
            gs.setHeight(72);
            gs.setPosition(14, 14);
            addGameSprite(gs);

            this.text = skill.getName();
            fontSize = FontHelper.getSingleton().fitSizeToEnglishFootprint(skill.getSourceName(), this.text, 3f, 300f);
            this.highlight = new GameSprite("images/misc/yellow-highlight.png", 400, 100, 0.3f);
            this.highlight.setPosition(x, y);
            this.selected = readOnlyPreview ? hasPreviewSkill(skill.getId()) : UnitHelper.getInstance().getHero().hasSkill(skill.getId());
            this.skill = skill;
            this.buttonAlpha = !selected && (readOnlyPreview || shouldFadeSkillButton(skill)) ? LOCKED_SKILL_ALPHA : 1f;
        }

        @Override
        public void draw(Batch batch) {
            Color previousColor = new Color(batch.getColor());
            if (buttonAlpha < 1f) {
                batch.setColor(previousColor.r, previousColor.g, previousColor.b, previousColor.a * buttonAlpha);
            }

            super.draw(batch);
            batch.setColor(previousColor);

            textColor.set(1f, 1f, 1f, buttonAlpha);
            FontHelper.getSingleton().write(textColor, batch, fontSize, x + 100, y + 65, Messages.maybeTranslate(text));

            if (selected) {
                highlight.draw(batch);
            }
        }

        @Override
        public void click() {
            if (skill != null) {
                showSkill(skill);
            }
        }
    }

    private void setTitleDescription(String englishSource, String localizedText) {
        FontHelper.FittedTextBlock fittedDescription = FontHelper.getSingleton().fitMultilineToEnglishFootprint(
                englishSource == null ? "" : englishSource,
                localizedText == null ? "" : localizedText,
                3f,
                getTitleDescriptionWrapWidth());
        titleDescription = fittedDescription.text;
        titleDescriptionFontSize = fittedDescription.size;
    }

    private class InventoryButton extends SelectSkillButton {

        private final boolean equippable;
        private final GameSprite equippedHighlight;
        private final Item item;

        public InventoryButton(float x, float y, String spriteString, String description) {
            super(x, y, spriteString, description);
            this.item = null;
            this.equippable = false;
            equippedHighlight = new GameSprite("images/misc/yellow-highlight.png", 400, 100, 0.3f);
            equippedHighlight.setPosition(x, y);
        }

        public InventoryButton(float x, float y, Item item) {
            super(x, y, item, item.getNameWithQuantity());
            fontSize = FontHelper.getSingleton().fitSize(item.getNameWithQuantity(), 3f, 300f, 70f);
            this.item = item;
            equippedHighlight = new GameSprite("images/misc/yellow-highlight.png", 400, 100, 0.3f);
            equippedHighlight.setPosition(x, y);
            equippable = item instanceof EquipableItem;
        }

        @Override
        public void draw(Batch batch) {
            super.draw(batch);

            if (equippable && ((EquipableItem) item).getEquipped()) {
                equippedHighlight.draw(batch);
            }
        }

        @Override
        public void click() {
            if (item == null) {
                return;
            }

            ItemWindow itemWindow = new ItemWindow(item);
            if (readOnlyPreview) {
                WindowHelper.getInstance().addWindow(itemWindow.build());
                return;
            }

            if (item instanceof AmuletOfYendor) {
                itemWindow = (ItemWindow) itemWindow.addAmuletActions();
            }
            else {
                if (item instanceof Seed) {
                    itemWindow = (ItemWindow) itemWindow.addSeedActions((Seed) item);
                }
                else if (item instanceof Potion) {
                    itemWindow = (ItemWindow) itemWindow.addPotionActions((Potion) item);
                }
                else if (item instanceof Pickaxe) {
                    itemWindow = (ItemWindow) itemWindow.addPickaxeActions((Pickaxe) item);
                }
                else if (item instanceof TomeOfMastery) {
                    itemWindow = (ItemWindow) itemWindow.addTomeActions((TomeOfMastery) item);
                }
                else if (item instanceof EquipableItem) {
                    itemWindow = (ItemWindow) itemWindow.addEquipUnequip();
                }

                if (item instanceof ConsumableItem) {
                    itemWindow = (ItemWindow) itemWindow.addConsume((ConsumableItem) item);
                }
            }

            WindowHelper.getInstance().addWindow(itemWindow.build());
        }
    }

    @Override
    public void refresh() {
        skillButtons.clear();
        heroButtons.clear();
        inventoryButtons.clear();
        darkSkillButtons.clear();
        previewItems.clear();
        journalButton = null;
        super.refresh();
    }

    private class JournalButton extends ActionButton {

        private static final float SIZE = 60f;

        public JournalButton(float x, float y) {
            super(x, y, SIZE, SIZE, "images/misc/transparent.png", "images/misc/transparent.png");

            GameSprite icon = new GameSprite("images/buttons/journal.png", SIZE, SIZE);
            icon.setPosition(0f, 0f);
            addGameSprite(icon);
        }

        @Override
        public void clicked() {
            WindowHelper.getInstance().addWindow(new JournalWindow().build());
        }
    }
}
