package com.bilboldev.skillfulpixeldungeonplatformer.units.classes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SkillsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassSkillTree;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Cloth;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.LeatherArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.ArrowItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.BulletItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Handgun;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.HealthPotion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.ManaPotion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfStrength;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.Rations;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Dagger;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Knuckles;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.ShortSword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Bow;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Shuriken;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.FireBoltWand;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Wizard;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

import java.util.ArrayList;

public enum HeroClass {
    NEUTRAL, WARRIOR, ROGUE, WIZARD, ARCHER, NECROMANCER, MERCENARY;

    private static final float SKILL_BUTTON_WIDTH = 400f;
    private static final float SKILL_BUTTON_HEIGHT = 100f;
    private static final float TREE_LINE_THICKNESS = 10f;
    private static final int TREE_FIRST_COLUMN_X = 100;
    private static final int TREE_COLUMN_SPACING = 450;
    private static final int TREE_FIRST_ROW_Y = -480;
    private static final int TREE_ROW_SPACING = 125;
    private static final int TREE_MIN_COLUMN = 1;
    private static final int TREE_MAX_COLUMN = 4;

    public String getName(){
        switch (this){
            case WARRIOR: return Messages.get("custom.generated.warrior_f4c16fcffe");
            case ROGUE: return Messages.get("custom.generated.rogue_f232febd10");
            case WIZARD: return Messages.get("custom.generated.wizard_af2c41eb4e");
            case ARCHER: return Messages.get("custom.generated.huntress_a3bb9e35da");
            case NECROMANCER: return Messages.get("custom.classes.necromancer.name");
            case MERCENARY: return Messages.get("custom.classes.mercenary.name");
            case NEUTRAL: return "Mobs";
        }

        return "..";
    }

    public String getSourceName() {
        switch (this) {
            case WARRIOR: return "Warrior";
            case ROGUE: return "Rogue";
            case WIZARD: return "Wizard";
            case ARCHER: return "Huntress";
            case NECROMANCER: return "Necromancer";
            case MERCENARY: return "Mercenary";
            case NEUTRAL: return "Mobs";
        }

        return "..";
    }


    public String getDescription(){
        switch (this){
            case WARRIOR: return Messages.get("custom.generated.warriors_are_physically_tough_and_02774ddc7a");
            case ROGUE: return Messages.get("custom.generated.rogues_rely_on_agility_stealth_9fa3bff11c");
            case WIZARD: return Messages.get("custom.generated.wizards_master_the_arts_of_3efc4cde47");
            case ARCHER: return Messages.get("custom.generated.huntresses_excel_at_ranged_combat_11d04f6ebd");
            case NECROMANCER: return Messages.get("custom.classes.necromancer.description");
            case MERCENARY: return Messages.get("custom.classes.mercenary.description");
            case NEUTRAL: return "Mobs";
        }

        return "..";
    }

    public String getSourceDescription() {
        switch (this) {
            case WARRIOR: return "Warriors are physically tough and excel at close-range combat, shrugging off blows that would cripple other heroes.";
            case ROGUE: return "Rogues rely on agility, stealth, and careful timing to survive. They strike hard, dodge often, and slip through danger.";
            case WIZARD: return "Wizards master the arts of magic and crowd control, overwhelming enemies with powerful spells and utility.";
            case ARCHER: return "Huntresses excel at ranged combat and battlefield control, picking enemies apart before they can close the gap.";
            case NECROMANCER: return "Necromancers wield daggers and mind magic, drawing power from fallen enemies to raise allies, drain life, and curse their foes.";
            case MERCENARY: return "Mercenaries fight with swords and exclusive firearms, carrying plentiful bullets and using precise shots and battlefield tactics.";
            case NEUTRAL: return "Mobs";
        }

        return "..";
    }

    private String localizedHeroClassName(String bundleName, String fallback) {
        return localizedHeroClassValue(bundleName, fallback, false);
    }

    private String localizedHeroClassDescription(String bundleName, String fallback) {
        return localizedHeroClassValue(bundleName, fallback, true);
    }

    public String getAssetFolderName() {
        switch (this) {
            case WARRIOR: return "warrior";
            case ROGUE: return "rogue";
            case WIZARD: return "wizard";
            case ARCHER: return "huntress";
            case NECROMANCER: return "necromancer";
            case MERCENARY: return "mercenary";
        }

        return "warrior";
    }

    private String localizedHeroClassValue(String bundleName, String fallback, boolean description) {
        if (Messages.lang() == Languages.ENGLISH || bundleName == null || bundleName.isEmpty()) {
            return fallback;
        }

        String suffix = description ? "_desc" : "";
        String localized = Messages.get("actors.hero.heroclass." + bundleName + suffix);
        return Messages.NO_TEXT_FOUND.equals(localized) ? fallback : localized;
    }

    public int getHealth(int level){
        switch (this){
            case WARRIOR: return  30 + 16 * (level - 1);
            case ROGUE: return  26 + 14 * (level - 1);
            case WIZARD: return  20 + 13 * (level - 1);
            case ARCHER: return  24 + 14 * (level - 1);
            case NECROMANCER: return 22 + 14 * (level - 1);
            case MERCENARY: return 28 + 15 * (level - 1);
        }

        return 1;
    }

    public int getMana(int level){
        switch (this){
            case WARRIOR: return  20 + 10 * (level - 1);
            case ROGUE: return  25 + 10 * (level - 1);
            case WIZARD: return  50 + 25 * (level - 1);
            case ARCHER: return  25 + 12 * (level - 1);
            case NECROMANCER: return 50 + 25 * (level - 1);
            case MERCENARY: return 20 + 10 * (level - 1);
        }

        return 1;
    }

    public int getBaseStrength() {
        switch (this) {
            case WARRIOR:
            case MERCENARY:
                return 11;
            case ROGUE:
                return 10;
            case ARCHER:
                return 10;
            case WIZARD:
            case NECROMANCER:
                return 10;
        }

        return 10;
    }

    public int getBaseAttackSkill() {
        switch (this) {
            case WARRIOR:
            case ROGUE:
            case ARCHER:
            case WIZARD:
                return 10;
            default:
                return 10;
        }
    }

    public int getBaseDefenseSkill() {
        switch (this) {
            case WARRIOR:
            case ROGUE:
            case ARCHER:
            case WIZARD:
                return 5;
            default:
                return 5;
        }
    }

    public float getAttackSpeed(){
        switch (this){
            case WARRIOR: return  6f;
            case ROGUE: return  8f;
            case WIZARD: return  7f;
            case ARCHER: return  7.5f;
            case NECROMANCER: return 7f;
            case MERCENARY: return 6f;
        }

        return 1;
    }

    public String getAttackSpeedDescription(){
        switch (this){
            case WARRIOR: return Messages.maybeTranslate("Medium speed");
            case ROGUE: return Messages.maybeTranslate("Fast");
            case WIZARD: return Messages.maybeTranslate("Medium speed");
            case ARCHER: return Messages.maybeTranslate("Medium speed");
            case NECROMANCER:
            case MERCENARY: return Messages.maybeTranslate("Medium speed");
        }

        return "..";
    }

    public float getMoveSpeed(){
        switch (this){
            case WARRIOR: return  500f;
            case ROGUE: return  650f;
            case WIZARD: return  600f;
            case ARCHER: return  625f;
            case NECROMANCER: return 580f;
            case MERCENARY: return 550f;
        }

        return 1;
    }

    public String getMoveSpeedDescription(){
        switch (this){
            case WARRIOR: return Messages.maybeTranslate("Medium speed");
            case ROGUE: return Messages.maybeTranslate("Fast");
            case WIZARD: return Messages.maybeTranslate("Fast");
            case ARCHER: return Messages.maybeTranslate("Fast");
            case NECROMANCER:
            case MERCENARY: return Messages.maybeTranslate("Medium speed");
        }

        return "..";
    }

    public String getJumpButtonArt() {
        if (this == NECROMANCER) return NewClassAssets.NECROMANCER_JUMP;
        if (this == MERCENARY) return NewClassAssets.MERCENARY_JUMP;
        return "images/units/" + getAssetFolderName() + "/button-jump.png";
    }

    public GameSprite getClassPortrait(){
        switch (this){
            case WARRIOR: return new GameSprite("images/units/warrior/portrait.png", 200, 200);
            case ROGUE: return new GameSprite("images/units/rogue/portrait.png", 200, 200);
            case WIZARD: return new GameSprite("images/units/wizard/portrait.png", 200, 200);
            case ARCHER: return new GameSprite("images/units/huntress/portrait.png", 200, 200);
            case NECROMANCER: return new GameSprite(NewClassAssets.NECROMANCER_PORTRAIT, 200, 200);
            case MERCENARY: return new GameSprite(NewClassAssets.MERCENARY_PORTRAIT, 200, 200);
        }

        return new GameSprite("images/units/warrior/portrait.png", 200, 200);
    }

    public GameSprite getClassIcon(){
        switch (this){
            case WARRIOR: return new GameSprite("images/stats/warrior.png", 74, 74);
            case ROGUE: return new GameSprite("images/stats/rogue.png", 74, 74);
            case WIZARD: return new GameSprite("images/stats/magic.png", 74, 74);
            case ARCHER: return new GameSprite("images/stats/ranged.png", 74, 74);
            case NECROMANCER: return new GameSprite(NewClassAssets.NECROMANCER_PORTRAIT, 74, 74);
            case MERCENARY: return new GameSprite(NewClassAssets.MERCENARY_PORTRAIT, 74, 74);
        }

        return new GameSprite("images/stats/warrior.png", 74, 74);
    }

    public int classPenalty(HeroClass otherClass){
        if(this == otherClass){
            return 1;
        }
        if (this == NECROMANCER) return WIZARD.classPenalty(otherClass);
        if (this == MERCENARY) return WARRIOR.classPenalty(otherClass);

        switch (this) {
            case WARRIOR:
                if (otherClass == ROGUE) {
                    return 2;
                }
                if (otherClass == ARCHER) {
                    return 4;
                }
                if (otherClass == WIZARD) {
                    return 4;
                }
                break;
            case ROGUE:
                if (otherClass == WARRIOR) {
                    return 2;
                }
                if (otherClass == WIZARD) {
                    return 4;
                }
                if (otherClass == ARCHER) {
                    return 2;
                }
                break;
            case WIZARD:
                if (otherClass == ARCHER) {
                    return 2;
                }
                if (otherClass == ROGUE) {
                    return 4;
                }
                if (otherClass == WARRIOR) {
                    return 4;
                }
                break;
            case ARCHER:
                if (otherClass == WARRIOR) {
                    return 4;
                }
                if (otherClass == ROGUE) {
                    return 2;
                }
                if (otherClass == WIZARD) {
                    return 2;
                }
                break;
        }

        return -1;
    }

    public String classPenaltyDescription(HeroClass heroClass){
        if (this == NECROMANCER && heroClass != NECROMANCER) return WIZARD.classPenaltyDescription(heroClass);
        if (this == MERCENARY && heroClass != MERCENARY) return WARRIOR.classPenaltyDescription(heroClass);
        if (this == WARRIOR && (heroClass == ARCHER || heroClass == WIZARD)) {
            return Messages.maybeTranslate("Hopeless");
        }

        if (this == WIZARD && (heroClass == WARRIOR || heroClass == ROGUE || heroClass == ARCHER)) {
            return Messages.maybeTranslate("Hopeless");
        }

        if (this == ARCHER && heroClass == WARRIOR) {
            return Messages.maybeTranslate("Hopeless");
        }

        if(classPenalty(heroClass) == 1){
            return Messages.maybeTranslate("Excellent");
        }

        if(classPenalty(heroClass) == 2){
            return Messages.maybeTranslate("Neutral");
        }

        if(classPenalty(heroClass) == 4){
            return Messages.maybeTranslate("Bad");
        }

        return Messages.maybeTranslate("Useless");
    }


    public ArrayList<StartingBonus> getStartingBonuses(){
        ArrayList<StartingBonus> startingBonuses = new ArrayList<>();

        switch (this){
            case WARRIOR:
                startingBonuses.add(new StartingBonus("Short sword", "Warriors start with a short sword", "images/weapons/short-sword.png"));
                startingBonuses.add(new StartingBonus("Leather armor", "Warriors start with leather armor", "images/armor/leather-armor.png"));
                startingBonuses.add(new StartingBonus("2x Rations", "Warriors start with two rations", "images/items/food.png"));
                startingBonuses.add(new StartingBonus("5x Health pots", "Warriors start with five healing potions", "images/items/health-potion.png"));
                break;
            case WIZARD:
                startingBonuses.add(new StartingBonus("Firebolt Wand", "Wizards start with a Firebolt Wand", "images/wands/fire-wand.png"));
                startingBonuses.add(new StartingBonus("2x Rations", "Wizards start with two rations", "images/items/food.png"));
                startingBonuses.add(new StartingBonus("5x Health pots", "Wizards start with five healing potions", "images/items/health-potion.png"));
                startingBonuses.add(new StartingBonus("5x Mana pots", "Wizards start with five mana potions", "images/items/mana-potion.png"));
                break;
            case ROGUE:
                startingBonuses.add(new StartingBonus("Dagger", "Rogues start with a dagger", "images/misc/extracted items/DAGGER.png"));
                startingBonuses.add(new StartingBonus("10x Shuriken", "Rogues start with ten shuriken", "images/misc/extracted items/SHURIKEN.png"));
                startingBonuses.add(new StartingBonus("2x Rations", "Rogues start with two rations", "images/items/food.png"));
                startingBonuses.add(new StartingBonus("3x Health pots", "Rogues start with three healing potions", "images/items/health-potion.png"));
                break;
            case ARCHER:
                startingBonuses.add(new StartingBonus("Bow", "Huntresses start with a bow", "images/misc/extracted items/Bow.png"));
                startingBonuses.add(new StartingBonus("30x Arrows", "Huntresses start with thirty arrows", "images/misc/extracted items/Arrow.png"));
                startingBonuses.add(new StartingBonus("2x Rations", "Huntresses start with two rations", "images/items/food.png"));
                startingBonuses.add(new StartingBonus("4x Health pots", "Huntresses start with four healing potions", "images/items/health-potion.png"));
                break;
            case NECROMANCER:
                startingBonuses.add(StartingBonus.localized("custom.newstarters.necromancer.1", "images/misc/extracted items/DAGGER.png"));
                startingBonuses.add(StartingBonus.localized("custom.newstarters.necromancer.2", "images/armor/cloth.png"));
                startingBonuses.add(StartingBonus.localized("custom.newstarters.necromancer.3", "images/items/health-potion.png"));
                startingBonuses.add(StartingBonus.localized("custom.newstarters.necromancer.4", "images/misc/extracted items/SCROLL_SKILLPOINT.png"));
                break;
            case MERCENARY:
                startingBonuses.add(StartingBonus.localized("custom.newstarters.mercenary.1", "images/weapons/short-sword.png"));
                startingBonuses.add(StartingBonus.localized("custom.newstarters.mercenary.2", NewClassAssets.ItemArt.HANDGUN.key()));
                startingBonuses.add(StartingBonus.localized("custom.newstarters.mercenary.3", NewClassAssets.BULLET_ICON));
                startingBonuses.add(StartingBonus.localized("custom.newstarters.mercenary.4", "images/armor/leather-armor.png"));
                break;
        }
        return startingBonuses;
    }

    public ArrayList<Integer> getSkillIds(){
        if (NewClassSkillTree.isNewClass(this)) return NewClassSkillTree.ids(this, false);
        ArrayList<Integer> toReturn = new ArrayList<>();

        if(this == WARRIOR){
            toReturn.add(Skills.TRAINING);
            toReturn.add(Skills.HEALTH);
            toReturn.add(Skills.REGENERATION);
            toReturn.add(Skills.MELEE_SPEED);
            toReturn.add(Skills.GLADIATOR);
            toReturn.add(Skills.LOCK_SMITH);
            toReturn.add(Skills.SMASH);
            toReturn.add(Skills.SMITE);
        }

        if(this == WIZARD){
            toReturn.add(Skills.MANA);
            toReturn.add(Skills.MANA_REGENERATION);
            toReturn.add(Skills.FIRE_MASTERY);
            toReturn.add(Skills.SUMMON_FIRE);
            toReturn.add(Skills.SUMMON_FIRE_PLUS);
            toReturn.add(Skills.MASS_MANA_ARMOR);
            toReturn.add(Skills.BATTLE_MAGE);
            toReturn.add(Skills.WAND_MASTERY);
            toReturn.add(Skills.SORCERER);
            toReturn.add(Skills.MANA_ARMOR);
        }

        if(this == ARCHER){
            toReturn.add(Skills.AWARENESS);
            toReturn.add(Skills.ACCURACY);
            toReturn.add(Skills.FLETCHING);
            toReturn.add(Skills.WARDEN);
            toReturn.add(Skills.MANA_ARMOR);
            toReturn.add(Skills.HUNTING);
            toReturn.add(Skills.DOUBLE_SHOT);
        }

        if(this == ROGUE){
            toReturn.add(Skills.BANDIT);
            toReturn.add(Skills.LOCK_SMITH);
            toReturn.add(Skills.EVASION);
            toReturn.add(Skills.HEALTH);
            toReturn.add(Skills.FREE_RUNNER);
            toReturn.add(Skills.SHADOW_CLONE);
            toReturn.add(Skills.SMOKE_BOMB);
        }

        return toReturn;
    }

    public ArrayList<Integer> getDarkSkillIds(){
        if (NewClassSkillTree.isNewClass(this)) return NewClassSkillTree.ids(this, true);
        ArrayList<Integer> toReturn = new ArrayList<>();

        if(this == WARRIOR){
            toReturn.add(Skills.TOUGHNESS);
            toReturn.add(Skills.MELEE_DAMAGE);
            toReturn.add(Skills.KNOCK_BACK);
            toReturn.add(Skills.BERSERKER);
            toReturn.add(Skills.FRENZY);
            toReturn.add(Skills.RAMPAGE);
        }

        if(this == WIZARD){
            toReturn.add(Skills.SLOW);
            toReturn.add(Skills.WEAKEN);
            toReturn.add(Skills.VULNERABILITY);
            toReturn.add(Skills.BLIND);
            toReturn.add(Skills.WARLOCK);
            toReturn.add(Skills.CONFUSE);
            toReturn.add(Skills.ENRAGE);
            toReturn.add(Skills.DOMINATE);
        }

        if(this == ARCHER){
            toReturn.add(Skills.AIMED_SHOT);
            toReturn.add(Skills.KNEE_SHOT);
            toReturn.add(Skills.SNIPER);
            toReturn.add(Skills.STEALTH);
            toReturn.add(Skills.IRON_TIP);
            toReturn.add(Skills.BOMBVOYAGE);
        }

        if(this == ROGUE){
            toReturn.add(Skills.VENOM);
            toReturn.add(Skills.STEALTH);
            toReturn.add(Skills.AWARENESS);
            toReturn.add(Skills.SCORPION);
            toReturn.add(Skills.BLIND);
            toReturn.add(Skills.ASSASSIN);
            toReturn.add(Skills.SILENT_DEATH);
        }

        return toReturn;
    }

    public boolean hasSkillInTree(int skillId) {
        return getSkillIds().contains(skillId) || getDarkSkillIds().contains(skillId);
    }

    public ArrayList<Integer> getSkillRequirements(int skillId) {
        NewClassSkillTree.Node node = NewClassSkillTree.node(this, skillId);
        if (node != null) return node.requirements();
        ArrayList<Integer> requirements = new ArrayList<>();

        if (this == WARRIOR && skillId == Skills.LOCK_SMITH) {
            return requirements;
        }

        if (this == ROGUE && skillId == Skills.AWARENESS) {
            requirements.add(Skills.STEALTH);
            return requirements;
        }

        if (this == ARCHER && skillId == Skills.STEALTH) {
            requirements.add(Skills.SNIPER);
            return requirements;
        }

        if (this == ARCHER && skillId == Skills.MANA_ARMOR) {
            requirements.add(Skills.WARDEN);
            return requirements;
        }

        Skill skill = getBaseSkill(skillId);
        return skill == null ? requirements : new ArrayList<>(skill.getRequires());
    }

    public String getSkillBigDescription(int skillId) {
        Skill skill = getBaseSkill(skillId);
        if (skill == null) return SkillsHelper.getInstance().getSkillName(skillId);
        String toReturn = Messages.maybeTranslate("%s: %s", skill.getName(), getSkillDescription(skillId));
        ArrayList<Integer> requirements = getSkillRequirements(skillId);
        String requiresLabel = Messages.maybeTranslate("Requires");

        if (requirements.size() == 1) {
            return Messages.maybeTranslate(
                    "%s\n%s: %s",
                    toReturn,
                    requiresLabel,
                    SkillsHelper.getInstance().getSkillName(requirements.get(0)));
        }

        if (requirements.size() > 1) {
            String requiredString = requiresLabel + ": ";
            for (int index = 0; index < requirements.size(); index++) {
                if (index > 0) {
                    requiredString += ", ";
                }
                requiredString += SkillsHelper.getInstance().getSkillName(requirements.get(index));
            }

            return Messages.maybeTranslate("%s\n%s", toReturn, requiredString);
        }

        return toReturn;
    }

    private String getSkillDescription(int skillId) {
        if (this == WIZARD && skillId == Skills.MANA) {
            return Messages.maybeTranslate("Spiritual training increases a wizard's stamina for magic.\n- +20% base class mana");
        }

        if (this == WARRIOR && skillId == Skills.HEALTH) {
            return Messages.maybeTranslate("Physical training helps warriors endure close combat.\n- +20% base class health");
        }

        if (this == WARRIOR && skillId == Skills.LOCK_SMITH) {
            return Messages.maybeTranslate("Some warriors have survived so many traps they know them inside out.\n- 100% chance to disarm traps on contact\n- Does NOT work on hidden traps");
        }

        if (this == ROGUE && skillId == Skills.HEALTH) {
            return Messages.maybeTranslate("Seasoned rogues learn to stay on their feet when plans go wrong.\n- +15% base class health");
        }

        if (this == ROGUE && skillId == Skills.AWARENESS) {
            return Messages.maybeTranslate("Heightened awareness helps rogues evade incoming shots.\n- +20% chance of dodging ranged attacks");
        }

        if (this == ARCHER && skillId == Skills.STEALTH) {
            return Messages.maybeTranslate("Huntresses learn to stay unseen while stalking prey.\n- Hero is harder to spot");
        }

        return getBaseSkill(skillId).getDescription();
    }

    private Skill getBaseSkill(int skillId) {
        return SkillsHelper.getInstance().getSkill(skillId);
    }

    private int getTreeColumnX(int column) {
        return TREE_FIRST_COLUMN_X + TREE_COLUMN_SPACING * (column - 1);
    }

    private int getTreeRowY(int row) {
        return TREE_FIRST_ROW_Y - TREE_ROW_SPACING * (row - 1);
    }

    public int getSkillButtonXOffset(int skillId, boolean darkSkills) {
        NewClassSkillTree.Node node = NewClassSkillTree.node(this, skillId);
        if (node != null) return getTreeColumnX(node.column);
        Skill skill = getBaseSkill(skillId);

        if (!darkSkills && this == WARRIOR && skillId == Skills.LOCK_SMITH) {
            return getTreeColumnX(4);
        }

        if (!darkSkills && this == WARRIOR && skillId == Skills.MELEE_SPEED) {
            return getTreeColumnX(2);
        }

        if (!darkSkills && this == WARRIOR && skillId == Skills.GLADIATOR) {
            return getTreeColumnX(3);
        }

        if (darkSkills && this == WARRIOR && skillId == Skills.BERSERKER) {
            return getTreeColumnX(3);
        }

        if (!darkSkills && this == ROGUE && skillId == Skills.HEALTH) {
            return getTreeColumnX(2);
        }

        if (darkSkills && this == ROGUE && skillId == Skills.AWARENESS) {
            return getTreeColumnX(3);
        }

        if (darkSkills && this == ROGUE && skillId == Skills.BLIND) {
            return getTreeColumnX(2);
        }

        if (!darkSkills && this == ARCHER && skillId == Skills.MANA_ARMOR) {
            return getTreeColumnX(3);
        }

        if (darkSkills && this == ARCHER && skillId == Skills.STEALTH) {
            return getTreeColumnX(3);
        }

        return skill == null ? TREE_FIRST_COLUMN_X : skill.getXOffset();
    }

    public int getSkillButtonYOffset(int skillId, boolean darkSkills) {
        NewClassSkillTree.Node node = NewClassSkillTree.node(this, skillId);
        if (node != null) return getTreeRowY(node.row);
        Skill skill = getBaseSkill(skillId);

        if (!darkSkills && this == WARRIOR && skillId == Skills.LOCK_SMITH) {
            return getTreeRowY(1);
        }

        if (!darkSkills && this == WARRIOR && skillId == Skills.HEALTH) {
            return getTreeRowY(2);
        }

        if (!darkSkills && this == WARRIOR && skillId == Skills.REGENERATION) {
            return getTreeRowY(3);
        }

        if (!darkSkills && this == WARRIOR && skillId == Skills.MELEE_SPEED) {
            return getTreeRowY(1);
        }

        if (!darkSkills && this == WARRIOR && skillId == Skills.GLADIATOR) {
            return getTreeRowY(2);
        }

        if (darkSkills && this == WARRIOR && skillId == Skills.BERSERKER) {
            return getTreeRowY(2);
        }

        if (!darkSkills && this == ROGUE && skillId == Skills.HEALTH) {
            return getTreeRowY(3);
        }

        if (darkSkills && this == ROGUE && skillId == Skills.AWARENESS) {
            return getTreeRowY(1);
        }

        if (darkSkills && this == ROGUE && skillId == Skills.BLIND) {
            return getTreeRowY(4);
        }

        if (!darkSkills && this == ARCHER && skillId == Skills.MANA_ARMOR) {
            return getTreeRowY(4);
        }

        if (darkSkills && this == ARCHER && skillId == Skills.STEALTH) {
            return getTreeRowY(3);
        }

        return skill == null ? TREE_FIRST_ROW_Y : skill.getYOffset();
    }

    public int getSkillPointCost(int skillId) {
        NewClassSkillTree.Node node = NewClassSkillTree.node(this, skillId);
        if (node != null) return node.cost;
        if (!NewClassSkillTree.allows(this, getBaseSkill(skillId))) return -1;
        return getSkillColumn(skillId);
    }

    public int getSkillColumn(int skillId) {
        NewClassSkillTree.Node node = NewClassSkillTree.node(this, skillId);
        if (node != null) return node.column;
        if (getBaseSkill(skillId) == null) return -1;
        boolean darkSkill = isDarkSkill(skillId);
        int resolvedX = getSkillButtonXOffset(skillId, darkSkill);
        int column = 1 + Math.round((resolvedX - TREE_FIRST_COLUMN_X) / (float) TREE_COLUMN_SPACING);
        return Math.max(TREE_MIN_COLUMN, Math.min(TREE_MAX_COLUMN, column));
    }

    private boolean isDarkSkill(int skillId) {
        return getDarkSkillIds().contains(skillId) && !getSkillIds().contains(skillId);
    }


    public void drawBranches(Batch batch, float x, float y, float width, float height){
        if(this == WARRIOR || this == ROGUE || this == ARCHER || NewClassSkillTree.isNewClass(this)){
            drawSkillRequirementLinks(batch, x, y, height, getSkillIds(), false);
        }

        if(this == WIZARD){
            GameSprite gs = new GameSprite("images/misc/grey.png", 50, 10);
            gs.setPosition(x  + 500, y + height - 600 - 50 - 35 - 125);
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 50, 10);
            gs.setPosition(x  + 950, y + height - 600 - 50 - 35 - 125);
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 10, 350);
            gs.setPosition(x  + 1650, y + height - 800 );
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 300, 10);
            gs.setPosition(x  + 1350, y + height - 560);
            gs.draw(batch);


            gs = new GameSprite("images/misc/grey.png", 250, 10);
            gs.setPosition(x  + 950, y + height - 600 - 50 - 35 + 125 +125);
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 10, 80);
            gs.setPosition(x  + 950  + 240, y + height  - 600 - 50 - 35 + 125 +125 - 80 );
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 10, 100);
            gs.setPosition(x  + 1420 - 675, y + height - 600 + 50 );
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 255, 10);
            gs.setPosition(x  + 500, y + height - 600 - 50 - 35 + 125);
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 50, 10);
            gs.setPosition(x  + 500, y + height - 600 - 50 - 35 + 250);
            gs.draw(batch);
        }
    }

    public void drawDarkSkillsBranches(Batch batch, float x, float y, float width, float height){
        if(this == WARRIOR || this == ROGUE || this == ARCHER || NewClassSkillTree.isNewClass(this)){
            drawSkillRequirementLinks(batch, x, y, height, getDarkSkillIds(), true);
        }

        if(this == WIZARD){
            GameSprite gs = new GameSprite("images/misc/grey.png", 700, 10);
            gs.setPosition(x  + 500, y + height - 600 - 50 - 35 - 125);
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 50, 10);
            gs.setPosition(x  + 950, y + height - 600 - 50 - 35 - 125);
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 10, 400);
            gs.setPosition(x  + 1650, y + height - 800 );
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 300, 10);
            gs.setPosition(x  + 1350, y + height - 560);
            gs.draw(batch);


            gs = new GameSprite("images/misc/grey.png", 250, 10);
            gs.setPosition(x  + 950, y + height - 600 - 50 - 35 + 125 +125);
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 10, 370);
            gs.setPosition(x  + 950  + 240, y + height  - 600 - 50 - 35 + 125 +125 - 20 - 350 );
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 555, 10);
            gs.setPosition(x  + 500, y + height - 600 - 50 - 35 + 125);
            gs.draw(batch);

            gs = new GameSprite("images/misc/grey.png", 500, 10);
            gs.setPosition(x  + 500, y + height - 600 - 50 - 35 + 250);
            gs.draw(batch);
        }
    }

    private void drawSkillRequirementLinks(Batch batch, float x, float y, float height, ArrayList<Integer> skillIds, boolean darkSkills) {
        for (Integer skillId : skillIds) {
            if (getBaseSkill(skillId) == null) continue;
            for (Integer requiredSkillId : getSkillRequirements(skillId)) {
                if (!skillIds.contains(requiredSkillId) || getBaseSkill(requiredSkillId) == null) {
                    continue;
                }

                drawSkillRequirementLink(batch, x, y, height, requiredSkillId, skillId, darkSkills);
            }
        }
    }

    private void drawSkillRequirementLink(Batch batch, float x, float y, float height, int requiredSkillId, int skillId, boolean darkSkills) {
        float requiredButtonX = x + getSkillButtonXOffset(requiredSkillId, darkSkills);
        float requiredButtonY = y + height + getSkillButtonYOffset(requiredSkillId, darkSkills);
        float skillButtonX = x + getSkillButtonXOffset(skillId, darkSkills);
        float skillButtonY = y + height + getSkillButtonYOffset(skillId, darkSkills);

        if (requiredButtonX == skillButtonX) {
            float centerX = requiredButtonX + SKILL_BUTTON_WIDTH / 2f - TREE_LINE_THICKNESS / 2f;
            float upperButtonBottomY = Math.max(requiredButtonY, skillButtonY);
            float lowerButtonTopY = Math.min(requiredButtonY, skillButtonY) + SKILL_BUTTON_HEIGHT;
            drawVerticalTreeLine(batch, centerX, lowerButtonTopY, upperButtonBottomY - lowerButtonTopY);
            return;
        }

        float fromX = requiredButtonX + SKILL_BUTTON_WIDTH;
        float fromY = requiredButtonY + SKILL_BUTTON_HEIGHT / 2f;
        float toX = skillButtonX;
        float toY = skillButtonY + SKILL_BUTTON_HEIGHT / 2f;

        if (fromY == toY) {
            drawHorizontalTreeLine(batch, Math.min(fromX, toX), fromY - TREE_LINE_THICKNESS / 2f, Math.abs(toX - fromX));
            return;
        }

        float elbowX = fromX + (toX - fromX) / 2f;
        drawHorizontalTreeLine(batch, Math.min(fromX, elbowX), fromY - TREE_LINE_THICKNESS / 2f, Math.abs(elbowX - fromX));
        drawVerticalTreeLine(batch, elbowX - TREE_LINE_THICKNESS / 2f, Math.min(fromY, toY), Math.abs(toY - fromY));
        drawHorizontalTreeLine(batch, Math.min(elbowX, toX), toY - TREE_LINE_THICKNESS / 2f, Math.abs(toX - elbowX));
    }

    private void drawHorizontalTreeLine(Batch batch, float x, float y, float width) {
        if (width <= 0f) {
            return;
        }

        GameSprite gs = new GameSprite("images/misc/grey.png", width, TREE_LINE_THICKNESS);
        gs.setPosition(x, y);
        gs.draw(batch);
    }

    private void drawVerticalTreeLine(Batch batch, float x, float y, float height) {
        if (height <= 0f) {
            return;
        }

        GameSprite gs = new GameSprite("images/misc/grey.png", TREE_LINE_THICKNESS, height);
        gs.setPosition(x, y);
        gs.draw(batch);
    }

    public String getFilm(){
        switch (this){
            case WARRIOR:  return "images/units/warrior/warrior.png";
            case ROGUE:  return "images/units/rogue/rogue.png";
            case WIZARD:  return "images/units/wizard/wizard.png";
            case ARCHER:  return "images/units/huntress/huntress.png";
            case NECROMANCER: return NewClassAssets.NECROMANCER;
            case MERCENARY: return NewClassAssets.MERCENARY;
        }
        return "images/units/warrior/warrior.png";
    }

    public ArrayList<Item> getItems(){
        ArrayList<Item> items = new ArrayList<>();

        switch (this){
            case WARRIOR:
                new PotionOfStrength().identify();
                items.add(new ShortSword());
                items.add(new LeatherArmor());
                items.add(new HealthPotion().setQuantity(5));
                break;
            case MERCENARY:
                items.add(new ShortSword());
                items.add(new Handgun());
                items.add(new LeatherArmor());
                items.add(new HealthPotion().setQuantity(5));
                items.add(new BulletItem().setQuantity(10));
                break;
            case NECROMANCER:
                items.add(new Dagger());
                items.add(new Cloth());
                items.add(new HealthPotion().setQuantity(5));
                items.add(new ManaPotion().setQuantity(5));
                break;
            case WIZARD:
                FireBoltWand starterWand = new FireBoltWand();
                starterWand.identify();
                items.add(starterWand);
                items.add(new Cloth());
                items.add(new HealthPotion().setQuantity(5));
                items.add(new ManaPotion().setQuantity(5));
                break;
            case ROGUE:
                Shuriken starterShuriken = new Shuriken();
                starterShuriken.setAmmo(10);
                items.add(new Dagger());
                items.add(starterShuriken);
                items.add(new Cloth());
                items.add(new HealthPotion().setQuantity(3));
                break;
            case ARCHER:
                Bow starterBow = new Bow();
                starterBow.setAmmo(30);
                items.add(new Knuckles());
                items.add(starterBow);
                items.add(new ArrowItem().setQuantity(30));
                items.add(new Cloth());
                items.add(new HealthPotion().setQuantity(4));
                break;
        }
        items.add(new Rations().setQuantity(2));
        return items;
    }

    public Hero getHero(){
        switch (this){
            case WARRIOR: return new Hero(WARRIOR);
            case ROGUE: return new Hero(ROGUE);
            case WIZARD: return new Wizard();
            case ARCHER: return new Hero(ARCHER);
            case NECROMANCER: return new Hero(NECROMANCER);
            case MERCENARY: return new Hero(MERCENARY);
        }

        return new Hero();
    }
}
