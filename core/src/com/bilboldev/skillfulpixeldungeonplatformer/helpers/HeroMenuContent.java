package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.StartingBonus;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import java.util.ArrayList;
import java.util.List;


public final class HeroMenuContent {
    public static final class Entry {
        public final String icon, label, description;
        public final int skillId;
        Entry(String icon, String label, String description) { this(icon, label, description, -1); }
        Entry(String icon, String label, String description, int skillId) {
            this.icon = icon; this.label = label; this.description = description; this.skillId = skillId;
        }
    }
    public final HeroClass heroClass;
    public final List<Entry> stats = new ArrayList<>(), equipment = new ArrayList<>(), affinities = new ArrayList<>();
    public final List<Entry> skills = new ArrayList<>(), darkSkills = new ArrayList<>();

    public HeroMenuContent(HeroClass heroClass) {
        this.heroClass = heroClass;
        stats.add(new Entry("images/stats/health.png", Messages.get("custom.ui.level_gain", heroClass.getHealth(1),
                heroClass.getHealth(2) - heroClass.getHealth(1)),
                Messages.get("custom.generated.arg_starts_with_arg_health_429b446bac", new Object[]{heroClass.getName(),
                        heroClass.getHealth(1), heroClass.getHealth(2) - heroClass.getHealth(1)})));
        stats.add(new Entry("images/stats/mana.png", Messages.get("custom.ui.level_gain", heroClass.getMana(1),
                heroClass.getMana(2) - heroClass.getMana(1)),
                Messages.get("custom.generated.arg_starts_with_arg_mana_e2ffb9715d", new Object[]{heroClass.getName(),
                        heroClass.getMana(1), heroClass.getMana(2) - heroClass.getMana(1)})));
        stats.add(new Entry(heroClass.getJumpButtonArt(), heroClass.getMoveSpeedDescription(),
                Messages.get("custom.generated.arg_s_movement_speed_is_arg_870c8f690a", new Object[]{heroClass.getName(), heroClass.getMoveSpeedDescription()})));
        stats.add(new Entry("images/stats/attack.png", heroClass.getAttackSpeedDescription(),
                Messages.get("custom.generated.arg_s_attack_speed_is_arg_3d8f4c3596", new Object[]{heroClass.getName(), heroClass.getAttackSpeedDescription()})));
        for (HeroClass affinity : new HeroClass[]{HeroClass.WARRIOR, HeroClass.ROGUE, HeroClass.WIZARD, HeroClass.ARCHER}) {
            affinities.add(new Entry(affinity.getClassIcon().spriteString,
                    affinity.getName() + ": " + heroClass.classPenaltyDescription(affinity),
                    Messages.get("custom.generated.arg_is_arg_with_arg_1411df0151", new Object[]{heroClass.getName(),
                            heroClass.classPenaltyDescription(affinity), Messages.lowerCase(affinity.getName())})));
        }
        for (StartingBonus bonus : heroClass.getStartingBonuses())
            equipment.add(new Entry(bonus.getGameSprite().spriteString, bonus.getTitle(), bonus.getDescription()));
        addSkills(heroClass.getSkillIds(), skills);
        addSkills(heroClass.getDarkSkillIds(), darkSkills);
    }

    private void addSkills(List<Integer> ids, List<Entry> target) {
        for (Integer id : ids) {
            Skill skill = SkillsHelper.getInstance().getSkill(id);
            if (skill != null) target.add(new Entry(skill.getGameSpriteString(), skill.getName(),
                    heroClass.getSkillBigDescription(id), id));
        }
    }
}
