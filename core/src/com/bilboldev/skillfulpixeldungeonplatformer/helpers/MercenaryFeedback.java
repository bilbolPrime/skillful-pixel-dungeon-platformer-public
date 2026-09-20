package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ActiveSkill;


public final class MercenaryFeedback {
    private MercenaryFeedback() { }
    public static String unavailableReasonKey(Hero hero, ActiveSkill skill) {
        if (hero == null || skill == null || hero.getHeroClass() != HeroClass.MERCENARY || !hero.hasSkill(skill.getId())) return null;
        if (hero.getMp() < skill.getManaCost()) return "custom.hud.no_mana";
        if (skill.getId() != Skills.HEAD_SHOT && skill.getId() != Skills.NO_WITNESSES) return null;
        if (!(hero.getRangedWeapon() instanceof Gun)) return "custom.mercenary.need_gun";
        if (hero.getRangedWeapon().getAmmo() <= 0) return "custom.guns.empty";
        if (!((Gun)hero.getRangedWeapon()).hasProjectileSpace()) return "custom.mercenary.shot_limit";
        return null;
    }
    public static String hint(String reasonKey) {
        if ("custom.mercenary.need_gun".equals(reasonKey) || "custom.mercenary.shot_limit".equals(reasonKey))
            return Messages.get(reasonKey + "_hint");
        if ("custom.guns.empty".equals(reasonKey)) return Messages.get("custom.mercenary.bullets_hint");
        return null;
    }
    public static String summary(Hero hero) {
        if (hero == null || hero.getHeroClass() != HeroClass.MERCENARY || hero.isDead() || hero.getHP() <= 0) return "";
        float remaining = hero.getNewClassActions().duration(Skills.I_AM_THE_LAW);
        return hero.hasSkill(Skills.I_AM_THE_LAW) && remaining > 0f
                ? Messages.get("custom.mercenary.law_remaining", (int)Math.ceil(remaining)) : "";
    }
    public static String skillDetails(Hero hero, Skill skill) {
        if (hero == null || hero.getHeroClass() != HeroClass.MERCENARY || skill == null || !hero.hasSkill(skill.getId())) return "";
        if (skill.getId() == Skills.I_AM_THE_LAW) {
            String status = summary(hero);
            return status.isEmpty() ? "" : "\n" + status;
        }
        if (skill.getId() == Skills.EXECUTIONER) return "\n" + Messages.get((long)hero.getHP() * 5 < (long)hero.getMaxHP() * 2
                ? "custom.mercenary.executioner_low" : "custom.mercenary.executioner_normal");
        if (skill instanceof ActiveSkill) {
            String reason = unavailableReasonKey(hero, (ActiveSkill)skill);
            String detail = hint(reason);
            return detail == null ? "" : "\n" + detail;
        }
        return "";
    }
}
