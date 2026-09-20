package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.MinionPlacement;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.SummonedGhost;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ActiveSkill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.CorpseActiveSkill;


public final class NecromancerFeedback {
    private NecromancerFeedback() { }
    public static String unavailableReasonKey(Hero hero, ActiveSkill skill) {
        if (hero == null || skill == null || hero.getHeroClass() != HeroClass.NECROMANCER || !hero.hasSkill(skill.getId())) return null;
        if (hero.getMp() < skill.getManaCost()) return "custom.hud.no_mana";
        if (skill instanceof CorpseActiveSkill) return ((CorpseActiveSkill)skill).unavailableReasonKey(hero);
        if (skill.getId() == Skills.SUMMON_GHOST) {
            if (NecromancerMinion.occupiedSlots(hero) >= 2) return "custom.necromancer.cap_full";
            if (MinionPlacement.findFlying(hero, SummonedGhost.COLLISION_WIDTH) == null) return "custom.necromancer.no_space";
        }
        return null;
    }
    public static String chargeText(Hero hero) {
        return Messages.get(hero.getMasterOfDeathRecoveryRemaining() > 0f ? "custom.necromancer.protected"
                : hero.hasUsedMasterOfDeath() ? "custom.necromancer.spent" : "custom.necromancer.charge");
    }
    public static String hint(String reasonKey) {
        if ("custom.necromancer.no_corpse".equals(reasonKey) || "custom.necromancer.cap_full".equals(reasonKey)
                || "custom.necromancer.no_space".equals(reasonKey)) return Messages.get(reasonKey + "_hint");
        return null;
    }
    public static String summary(Hero hero) {
        if (hero == null || hero.getHeroClass() != HeroClass.NECROMANCER) return "";
        String text = Messages.get("custom.necromancer.minions", NecromancerMinion.occupiedSlots(hero), hero.hasSkill(Skills.LICH) ? 3 : 2);
        return hero.hasSkill(Skills.MASTER_OF_DEATH) ? text + "   |   " + chargeText(hero) : text;
    }
    public static String skillDetails(Hero hero, Skill skill) {
        if (hero == null || hero.getHeroClass() != HeroClass.NECROMANCER) return "";
        NewClassSkillTree.Node node = NewClassSkillTree.node(hero.getHeroClass(), skill.getId());
        if (node == null) return "";
        String text = "";
        if (hero.hasSkill(skill.getId())) {
            if (skill.getId() == Skills.MASTER_OF_DEATH) text += "\n" + chargeText(hero);
            if (skill instanceof CorpseActiveSkill || skill.getId() == Skills.SUMMON_GHOST)
                text += "\n" + summary(hero);
        }
        return text;
    }
}
