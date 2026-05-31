package com.bilboldev.skillfulpixeldungeonplatformer.items;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SkillsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Blind;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.TemporaryBlind;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ActiveSkill;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.ChoiceDialogWindow;

public class TomeOfMastery extends Item {
    private static final String BLINDED_TEXT = "You can't read while blinded";
    private static final float SUBCLASS_DIALOG_BOTTOM_EXTENSION = 50f;
    private static final float SUBCLASS_DIALOG_BUTTON_BOTTOM_PADDING = 170f;

    public TomeOfMastery() {
        name = "Tome of Mastery";
        description = "This worn leather tome feels far more potent than its size suggests. Reading it lets you choose a subclass path, but only if you can focus long enough to understand it.";
        gs = new GameSprite("images/misc/extracted items/MASTERY.png", 45, 45);
        goldCost = 150;
    }

    public void read() {
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null) {
            return;
        }

        if (hero.getBuff(Blind.class) != null || hero.getBuff(TemporaryBlind.class) != null) {
            WindowHelper.getInstance().addWindow(900f, 120f, Messages.maybeTranslate(BLINDED_TEXT));
            return;
        }

        if (getChosenSubclass(hero) != -1) {
            WindowHelper.getInstance().addWindow(1150f, 120f, Messages.maybeTranslate("You have already chosen a subclass."));
            return;
        }

        int[] choices = getSubclassChoices(hero.getHeroClass());
        if (choices == null) {
            return;
        }

        ChoiceDialogWindow choiceWindow = new ChoiceDialogWindow(
                "images/misc/extracted items/MASTERY.png",
            Messages.maybeTranslate("Choose the path you will master."),
                1500f,
            480f)
            .extendBottom(SUBCLASS_DIALOG_BOTTOM_EXTENSION)
            .setButtonBottomPadding(SUBCLASS_DIALOG_BUTTON_BOTTOM_PADDING);

        for (final int skillId : choices) {
            Skill skill = SkillsHelper.getInstance().getSkill(skillId);
            final String lockedRequirementText = getMissingRequirementText(hero, skillId);
            choiceWindow.addChoice(skill.getName(),
                    new Runnable() {
                @Override
                public void run() {
                    chooseSubclass(skillId);
                }
            }, lockedRequirementText == null);
        }

        WindowHelper.getInstance().addWindow(choiceWindow.build());
    }

    private void chooseSubclass(int skillId) {
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null || hero.hasSkill(skillId) || hero.skillLockedOut(skillId)) {
            return;
        }

        String missingRequirementText = getMissingRequirementText(hero, skillId);
        if (missingRequirementText != null) {
            WindowHelper.getInstance().addWindow(
                    1200f,
                    120f,
                    Messages.maybeTranslate("Requires %s.", missingRequirementText));
            return;
        }

        Skill skill = SkillsHelper.getInstance().getSkill(skillId);
        hero.learnSkill(skill);
        if (skill instanceof ActiveSkill) {
            hero.assignQuickSkillIfNeeded((ActiveSkill) skill);
        }

        InventoryHelper.getInstance().removeItem(this);
        SoundHelper.GetSingleton().play(Sounds.MASTERY);
        EffectsHelper.getInstance().mastery(hero);
        EffectsHelper.getInstance().message(hero, Messages.maybeTranslate("Way of the %s!", skill.getName()), Color.GOLD, 0f);
        WindowHelper.getInstance().refreshAll();
        WindowHelper.getInstance().addWindow(
            1100f,
            120f,
            Messages.maybeTranslate("You have chosen the way of the %s!", skill.getName()));
    }

    private int getChosenSubclass(Hero hero) {
        for (int subclassSkillId : getAllSubclassSkills()) {
            if (hero.hasSkill(subclassSkillId)) {
                return subclassSkillId;
            }
        }

        return -1;
    }

    private int[] getSubclassChoices(HeroClass heroClass) {
        switch (heroClass) {
            case WARRIOR:
                return new int[]{Skills.GLADIATOR, Skills.BERSERKER};
            case WIZARD:
                return new int[]{Skills.BATTLE_MAGE, Skills.WARLOCK};
            case ROGUE:
                return new int[]{Skills.FREE_RUNNER, Skills.ASSASSIN};
            case ARCHER:
                return new int[]{Skills.WARDEN, Skills.SNIPER};
            default:
                return null;
        }
    }

    private int[] getAllSubclassSkills() {
        return new int[]{
                Skills.GLADIATOR,
                Skills.BERSERKER,
                Skills.BATTLE_MAGE,
                Skills.WARLOCK,
                Skills.FREE_RUNNER,
                Skills.ASSASSIN,
                Skills.WARDEN,
                Skills.SNIPER
        };
    }

    private String getMissingRequirementText(Hero hero, int skillId) {
        if (hero == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (int requirement : hero.getHeroClass().getSkillRequirements(skillId)) {
            if (hero.hasSkill(requirement)) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append(" ").append(Messages.maybeTranslate("and")).append(" ");
            }

            builder.append(SkillsHelper.getInstance().getSkill(requirement).getName());
        }

        return builder.length() == 0 ? null : builder.toString();
    }
}