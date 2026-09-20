package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Aggression;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.BattleMage;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.BerserkerBuff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.FireMastery;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.GrandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.GladiatorBuff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Health;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Mana;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.ManaRegeneration;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Mastery;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Regeneration;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Toughness;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.WandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Warlock;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Blind;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Confuse;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Dominate;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.DoubleShot;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Enrage;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Frenzy;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.IronTip;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.KneeShot;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.KnockBack;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ManaArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.MindShot;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.HeadShot;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.RaiseSkeleton;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.DrainLife;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Curse;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.CorpseExplosion;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.SpiritBinder;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Lich;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.MasterOfDeath;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Wanted;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Packrat;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.SteadyAim;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.QuickDraw;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Marshal;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Executioner;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Execute;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.NoWitnesses;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.IAmTheLaw;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.RaiseSkeletonArcher;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.SummonGhost;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.MassManaArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Rampage;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.AimedShot;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Bombvoyage;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ShadowClone;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Slow;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.SmokeBomb;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Smash;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Smite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.SummonElemental;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Venom;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Vulnerable;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.Weaken;

import java.util.ArrayList;

public class SkillsHelper {
    ArrayList<Skill> skills;
    private static final SkillsHelper ourInstance = new SkillsHelper();

    public static SkillsHelper getInstance() {
        return ourInstance;
    }

    private SkillsHelper() {
        init();
    }

    private void init(){
        skills = new ArrayList<>();


        Skill skill = new Skill(Skills.TRAINING, HeroClass.WARRIOR, 1, "Training", "Training", "A warrior can never be prepared enough.\nGain 1 strength","images/skills/warrior-training.png") {
            @Override
            public void affect(Unit owner){

            }};

        skills.add(skill.setTreeOffsets(100, -480));

        skill = new Skill(Skills.HEALTH, HeroClass.WARRIOR, 1, "Endurance", "Endurance", "Champions who find themselves in close combat quite often undergo special physical training to endure damage.\n+20% health","images/skills/warrior-endurance.png") {
            @Override
            public void affect(Unit owner){
                new Health().setOwner(owner);
            }};

        skills.add(skill.setTreeOffsets(100, -480));

        skill = new Skill(Skills.REGENERATION, HeroClass.WARRIOR, 1, "Regeneration", "Regeneration", "The ability to heal at an accelerated rate is crucial when constantly facing enemies.\n+20% regeneration","images/skills/warrior-regeneration.png") {
            @Override
            public void affect(Unit owner){
                Buff existing = owner.getBuff(Regeneration.class);
                if (existing != null) existing.setPermanent(true);
                else new Regeneration().setOwner(owner);
            }};

        skills.add(skill.setRequires(Skills.HEALTH).setTreeOffsets(100, -480 - 125));

        skill = new Skill(Skills.TOUGHNESS, HeroClass.WARRIOR, 1, "Toughness", "+20% defence", "Many years of conflict has made this hero durable. What hurts others barely scratches this one.\n+20% defence","images/skills/warrior-toughness.png") {
            @Override
            public void affect(Unit owner){
                Buff existing = owner.getBuff(Toughness.class);
                if (existing != null) existing.setPermanent(true);
                else new Toughness().setOwner(owner);
            }};

        skills.add(skill.setTreeOffsets(100, -480));

        skill = new Skill(Skills.MELEE_SPEED, HeroClass.WARRIOR, 1, "Mastery", "Mastery", "Mastering melee weapons allows a warrior to attack at a faster rate.\n+20% melee attack speed","images/skills/warrior-mastery.png") {
            @Override
            public void affect(Unit owner){
                new Mastery().setOwner(owner);
            }};

        skills.add(skill.setRequires(Skills.TRAINING).setTreeOffsets(100, -480 - 375));

        skill = new Skill(Skills.MELEE_DAMAGE, HeroClass.WARRIOR, 2, "Aggression", "+20% damage", "Being at war for so long can make warriors... more violent.\n+20% damage","images/skills/warrior-aggression.png") {
            @Override
            public void affect(Unit owner){
                Buff existing = owner.getBuff(Aggression.class);
                if (existing != null) existing.setPermanent(true);
                else new Aggression().setOwner(owner);
            }};

        skills.add(skill.setRequires(Skills.TOUGHNESS).setTreeOffsets(100 + 450, -480 - 125));
        skill = new Skill(Skills.GLADIATOR, HeroClass.WARRIOR, 2, "Gladiator", "Gladiator", "Warriors who embrace the gladiator's discipline become tougher and faster in battle.\n- +50 health\n- +20% attack speed\n- Successful melee hits build a combo\n- From the 3rd chained hit onward, each hit deals bonus damage", "images/skills/warrior-gladiator.png") {
            @Override
            public void affect(Unit owner){
                new GladiatorBuff().setOwner(owner);
            }};

        skills.add(skill.setRequires(Skills.TRAINING).setRequires(Skills.HEALTH).setTreeOffsets(100 + 900, -480 - 125));
        skills.add(new Smash(Skills.SMASH, HeroClass.WARRIOR, 3, "Smash", "Smash", "Smashes the target with might dealing 50% extra damage.","images/skills/warrior-smash.png").setRequires(Skills.GLADIATOR).setTreeOffsets(100 + 900, -480 - 375));
        skills.add(new Smite(Skills.SMITE, HeroClass.WARRIOR, 4, "Smite", "Smite", "Smites the target with full aggression dealing 100% more damage.","images/skills/warrior-smite.png").setRequires(Skills.SMASH).setTreeOffsets(100 + 1350, -480 - 375));
        skills.add(new KnockBack(Skills.KNOCK_BACK, HeroClass.WARRIOR, 2, "Knock back", "Knock back", "Knocking an enemy back is a wise tactic especially when surrounded.","images/skills/warrior-knockback.png").setRequires(Skills.MELEE_DAMAGE).setTreeOffsets(100 + 450, -480));
        skill = new Skill(Skills.BERSERKER, HeroClass.WARRIOR, 3, "Berserker", "Berserker", "When severely wounded, the berserker enters a state of wild fury significantly increasing his damage output.\n- +100 health\n- +20% damage\n- Below 40% health, gain up to +60% extra damage","images/skills/warrior-berserker.png") {
            @Override
            public void affect(Unit owner){
                new BerserkerBuff().setOwner(owner);
            }};
        skills.add(skill.setRequires(Skills.TOUGHNESS).setRequires(Skills.MELEE_DAMAGE).setTreeOffsets(100 + 900, -480 - 125));
        skills.add(new Frenzy(Skills.FRENZY, HeroClass.WARRIOR, 4, "Frenzy", "Frenzy", "Sometimes a warrior should just let go. Entering a frenzy state increases damage by 50% but lowers speed by 25%","images/skills/frenzy.png").setRequires(Skills.BERSERKER).setTreeOffsets(100 + 1350, -480 - 125));
        skills.add(new Rampage(Skills.RAMPAGE, HeroClass.WARRIOR, 4, "Rampage", "Rampage", "Deals damage to all enemies in range.","images/skills/warrior-rampage.png").setRequires(Skills.FRENZY).setTreeOffsets(100 + 1350, -480 ));


        skill = new Skill(Skills.MANA, HeroClass.WIZARD, 1, "Spirituality", "+20% mana", "Spirit is as important as body. Champions who train their spirit can conjure their inner strength with increased stamina.\n- +20% mana","images/skills/mana.png") {
            @Override
            public void affect(Unit owner){
                new Mana().setOwner(owner);
            }};

        skills.add(skill.setTreeOffsets(100, -480));

        skill = new Skill(Skills.MANA_REGENERATION, HeroClass.WIZARD, 1, "Meditation", "+20% regen", "The ability to recover spiritual energy at an accelerated rate is critical for heroes that rely on magic.\n- +20% mana regeneration","images/skills/mana-regeneration.png") {
            @Override
            public void affect(Unit owner){
                new ManaRegeneration().setOwner(owner);
            }};

        skills.add(skill.setTreeOffsets(100, -480 - 125));

        skill = new Skill(Skills.FIRE_MASTERY, HeroClass.WIZARD, 1, "Fire Mastery", "+20% damage", "Fire is an easy element to manipulate. Only after it is mastered can one use it to its full effect.\n- +20% damage from wands","images/skills/fire-mastery.png") {
            @Override
            public void affect(Unit owner){
                new FireMastery().setOwner(owner);
            }};

        skills.add(skill.setTreeOffsets(100, -480 - 375));

        skill = new SummonElemental(Skills.SUMMON_FIRE, HeroClass.WIZARD, 2, "Summon Elemental", "Summon", "Summons a fire elemental to aid the hero on his quest.","images/skills/summon-elemental.png");

        skills.add(skill.setRequires(Skills.FIRE_MASTERY).setTreeOffsets(100 + 450, -480 - 375));

        skill = new Skill(Skills.SUMMON_FIRE_PLUS, HeroClass.WIZARD, 3, "Summoning Mastery", "Better stats", "Practicing summoning skills leads to better summons that can do more damage and withstand more.\n- Fire elementals have 20% more health\n- Fire elementals take 20% less damage","images/skills/summon-elemental-plus.png") {
            @Override
            public void affect(Unit owner){

            }};

        skills.add(skill.setRequires(Skills.SUMMON_FIRE).setTreeOffsets(100 + 900, -480 - 375));

        skill = new Skill(Skills.SUMMON_FIRE_PLUS_PLUS, HeroClass.WIZARD, 4, "Summoning Expert", "Extra summon", "Extensive training and focus allows the champion to control two summons at the same time.","images/skills/summon-elemental-plus-plus.png") {
            @Override
            public void affect(Unit owner){

            }};

        skills.add(skill.setRequires(Skills.SUMMON_FIRE_PLUS).setTreeOffsets(100 + 1350, -480 - 375));



        skill = new Skill(Skills.BATTLE_MAGE, HeroClass.WIZARD, 3, "Battle Mage", "Battle Mage", "Battle mages rely on wands and summoned allies in combat.\n- +50 max health\n- +50 max mana\n- Wand melee damage scales with current mana, up to +5\n- Successful wand hits restore 1 mana\n- Locks out Warlock","images/skills/battle-mage.png") {
            @Override
            public void affect(Unit owner){
                new BattleMage().setOwner(owner);
            }};

        skills.add(skill.setRequires(Skills.MANA_ARMOR).setLocksOut(Skills.WARLOCK).setTreeOffsets(100 + 900, -480 - 125));

        skill = new Skill(Skills.WAND_MASTERY, HeroClass.WIZARD, 4, "Wand Mastery", "Master", "Mastering wands allows a hero to use them with more proficiency.\n- +25% range on wands","images/skills/wand-mastery.png") {
            @Override
            public void affect(Unit owner){
                new WandMaster().setOwner(owner);
            }};

        skills.add(skill.setRequires(Skills.BATTLE_MAGE).setTreeOffsets(100 + 1350, -480 - 250));

        skill = new Skill(Skills.SORCERER, HeroClass.WIZARD, 4, "Grand Master", "Grand master", "Only the rare few reach the rank of grand master.\n- +25% range on wands\n- +25% damage on wands","images/skills/sorcery.png") {
            @Override
            public void affect(Unit owner){
                new GrandMaster().setOwner(owner);
            }};

        skills.add(skill.setRequires(Skills.WAND_MASTERY).setTreeOffsets(100 + 1350, -480 -375));

        skill = new ManaArmor(Skills.MANA_ARMOR, HeroClass.WIZARD, 2, "Mana Armor", "Mana armor", "By projecting a spiritual shield around themselves, heroes can reduce incoming damage.\n- Mana armor absorbs up to 50 damage\n- Cannot absorb more than 90% of incoming damage","images/skills/mana-armor.png");

        skills.add(skill.setRequires(Skills.MANA).setRequires(Skills.MANA_REGENERATION).setTreeOffsets(100 + 450, -480));

        skill = new MassManaArmor(Skills.MASS_MANA_ARMOR, HeroClass.WIZARD, 4, "Mass Mana Armor", "Mass armor", "By projecting a spiritual shield around themselves, heroes can reduce incoming damage.\n- Cast on hero and allies in same room\n- Mana armor absorbs up to 50 damage\n- Cannot absorb more than 90% of incoming damage","images/skills/mass-mana-armor.png");

        skills.add(skill.setRequires(Skills.BATTLE_MAGE).setTreeOffsets(100 + 1350, -480));

        skill = new Skill(Skills.WARLOCK, HeroClass.WIZARD, 3, "Warlock", "Warlock", "Warlocks rely on dark arts manipulating enemies into attacking each other.\n- +50 max mana\n- +20% mana regeneration\n- Killing an enemy heals wounds and satisfies hunger\n- Locks out Battle Mage","images/skills/warlock.png") {
            @Override
            public void affect(Unit owner){
                new Warlock().setOwner(owner);
            }};

        skills.add(skill.setRequires(Skills.WEAKEN).setRequires(Skills.BLIND).setRequires(Skills.SLOW).setLocksOut(Skills.BATTLE_MAGE).setTreeOffsets(100 + 900, -480 - 125));


        skill = new Slow(Skills.SLOW, HeroClass.WIZARD, 2, "Slow", "Slow", "Slows down enemies making them easier to be dealt with.\n - Target loses 50% speed","images/skills/slow.png");

        skills.add(skill.setTreeOffsets(100, -480));

        skill = new Weaken(Skills.WEAKEN, HeroClass.WIZARD, 2, "Weaken", "Weaken", "Weakens enemies reducing their damage by 25%.\n- Target does 25% less damage","images/skills/weaken.png");

        skills.add(skill.setRequires(Skills.VULNERABILITY).setTreeOffsets(100 + 450, -480 - 125));

        skill = new Vulnerable(Skills.VULNERABILITY, HeroClass.WIZARD, 1, "Vulnerability", "Vulnerability", "Makes target more vulnerable to damage.\n- Target takes 50% more damage","images/skills/vulnerability.png");

        skills.add(skill.setTreeOffsets(100 , -480 - 125));

        skill = new Blind(Skills.BLIND, HeroClass.WIZARD, 1, "Blind", "Blind", "Blinds target reducing their line of sight.","images/skills/blind.png");

        skills.add(skill.setTreeOffsets(100 , -480 - 375));

        skill = new Confuse(Skills.CONFUSE, HeroClass.WIZARD, 4, "Confuse", "Confuse", "Confuses the target making it not capable of differentiating friend from foe.","images/skills/confuse.png");
        skills.add(skill.setRequires(Skills.WARLOCK).setTreeOffsets(100 + 900 + 450, -480 - 125));

        skill = new Enrage(Skills.ENRAGE, HeroClass.WIZARD, 4, "Enrage", "Enrage", "Enrages the target making it attack recklessly with no regards for its own safety.\n- Target does 50% more damage\n- Target takes 50% more damage","images/skills/enrage.png");
        skills.add(skill.setRequires(Skills.CONFUSE).setTreeOffsets(100 + 900 + 450, -480));

        skill = new Dominate(Skills.DOMINATE, HeroClass.WIZARD, 4, "Dominate", "Dominate", "Dominate the target turning it into nothing more than a slave of your will for a duration.","images/skills/dominate.png");
        skills.add(skill.setRequires(Skills.CONFUSE).setTreeOffsets(100 + 900 + 450, -480 - 375));


        skills.add(new Skill(Skills.BANDIT, HeroClass.ROGUE, 1, "Bandit", "More loot", "Rogues learn to spot and pocket extra valuables.\n- +100% gold found\n- Gold cannot be stolen","images/skills/Bandit.png").setTreeOffsets(100, -480));
        skills.add(new Skill(Skills.STEALTH, HeroClass.ROGUE, 1, "Stealth", "Stay unseen", "Rogues excel at staying unseen.\n- Hero is harder to spot","images/skills/Stealth.png").setTreeOffsets(100 + 450, -480 - 125));
        skills.add(new Skill(Skills.LOCK_SMITH, HeroClass.ROGUE, 2, "Lock Smith", "Trap handling", "Triggering traps becomes harmless to a practiced rogue.\n- 100% chance to disarm traps on contact\n- Works on hidden traps too","images/skills/Lock Smith.png").setRequires(Skills.BANDIT).setTreeOffsets(100, -480 - 125));
        skills.add(new Skill(Skills.FREE_RUNNER, HeroClass.ROGUE, 2, "Free Runner", "Free Runner", "Embraces the freerunner path.\n- +60% move speed\n- Doubled evasion while not starving\n- +35 health","images/skills/Free Runner.png") {
            @Override
            public void affect(Unit owner) {
                owner.modifySpeedModifier(0.6f);
                owner.setMaxHP(owner.getMaxHP() + 35);
                owner.heal(35);
            }
        }.setRequires(Skills.EVASION).setTreeOffsets(100 + 900, -480 - 125));
        skills.add(new SmokeBomb(Skills.SMOKE_BOMB, HeroClass.ROGUE, 3, "Smoke Bomb", "Disappear", "Disappear into a cloud of smoke, refreshing your invisibility for a short time.\n- 10 mana\n- 6 second cooldown","images/skills/Smoke Bomb.png").setRequires(Skills.FREE_RUNNER).setTreeOffsets(100 + 900, -480 - 375));
        skills.add(new ShadowClone(Skills.SHADOW_CLONE, HeroClass.ROGUE, 4, "Shadow Clone", "Mirror image", "Split off a shadowy duplicate that fights beside you.\n- Summons one stronger mirror image\n- 20 mana\n- 20 second cooldown","images/skills/Shadow Clone.png").setRequires(Skills.FREE_RUNNER).setRequires(Skills.SMOKE_BOMB).setTreeOffsets(100 + 900 + 450, -480 - 125));
        skills.add(new Venom(Skills.VENOM, HeroClass.ROGUE, 2, "Venom", "Poison buff", "Coat your next attacks in venom.\n- 5 mana\n- Next 3 attacks deal +20% damage\n- 33% chance to poison a target","images/skills/Venom.png").setTreeOffsets(100, -480 - 125));
        skills.add(new Skill(Skills.SCORPION, HeroClass.ROGUE, 3, "Scorpion", "Stronger poison", "Improves rogue poison damage against enemies.\n- +50% poison damage","images/skills/Scorpion.png").setRequires(Skills.VENOM).setTreeOffsets(100, -480 - 250));
        skills.add(new Skill(Skills.ASSASSIN, HeroClass.ROGUE, 2, "Assassin", "Assassin", "Embraces the assassin path.\n- +50 health\n- Hero is harder to spot\n- Surprise attacks from stealth or against sleeping foes deal bonus damage","images/skills/Assassin.png") {
            @Override
            public void affect(Unit owner) {
                owner.setMaxHP(owner.getMaxHP() + 50);
                owner.heal(50);
            }
        }.setRequires(Skills.STEALTH).setTreeOffsets(100 + 900, -480 - 250));
        skills.add(new Skill(Skills.EVASION, HeroClass.ROGUE, 4, "Evasion", "Defensive agility", "+25% chance of avoiding attacks","images/skills/Evasion.png") {
            @Override
            public void affect(Unit owner) {
                owner.modifyEvasionMultiplier(0.25f);
            }
        }.setRequires(Skills.BANDIT).setTreeOffsets(100 + 450, -480 - 125));
        skills.add(new Skill(Skills.SILENT_DEATH, HeroClass.ROGUE, 4, "Silent Death", "Execution", "Sleeping non-boss enemies have a 25% chance to die instantly from melee attacks.","images/skills/Silent Death.png").setRequires(Skills.ASSASSIN).setTreeOffsets(100 + 900 + 450, -480 - 250));


        skills.add(new Skill(Skills.AWARENESS, HeroClass.ARCHER, 1, "Awareness", "Awareness", "Heightened awareness keeps the huntress ready for incoming missiles.\n+25% defense from ranged attacks","images/skills/Awareness.png").setTreeOffsets(100 + 450, -480));
        skills.add(new Skill(Skills.ACCURACY, HeroClass.ARCHER, 1, "Accuracy", "Accuracy", "Steady aim with the bow makes every shot more reliable.\n+25% accuracy when using the bow","images/skills/Accuracy.png").setTreeOffsets(100, -480 - 125));
        skills.add(new Skill(Skills.FLETCHING, HeroClass.ARCHER, 2, "Fletching", "Fletching", "Patient crafting keeps arrows ready for the next fight.\nGenerates arrows passively.","images/skills/Fletching.png").setRequires(Skills.ACCURACY).setTreeOffsets(100 + 450, -480 - 125));
        skills.add(new Skill(Skills.WARDEN, HeroClass.ARCHER, 2, "Warden", "Warden", "Embraces the warden path.\n- +50 health\n- Double fletching rate","images/skills/Warden.png") {
            @Override
            public void affect(Unit owner){
                owner.setMaxHP(owner.getMaxHP() + 50);
                owner.heal(50);
            }
        }.setRequires(Skills.FLETCHING).setRequires(Skills.AWARENESS).setTreeOffsets(100 + 900, -480 - 125));
        skills.add(new AimedShot(Skills.AIMED_SHOT, HeroClass.ARCHER, 1, "Aimed Shot", "Aimed Shot", "Fire a carefully lined up bow shot.\n- 2 mana\n- Consumes 1 arrow\n- +100% accuracy\n- +25% damage","images/skills/Aimed Shot.png").setTreeOffsets(100, -480 - 125));
        skills.add(new Skill(Skills.HUNTING, HeroClass.ARCHER, 3, "Hunting", "Hunting", "A seasoned huntress keeps fresh food on hand.\nGenerates food passively.","images/skills/Hunting.png").setRequires(Skills.WARDEN).setTreeOffsets(100 + 1350, -480 - 250));
        skills.add(new DoubleShot(Skills.DOUBLE_SHOT, HeroClass.ARCHER, 3, "Double Shot", "Double Shot", "Loose a pair of arrows at once.\n- 4 mana\n- Consumes 2 arrows\n- Fires two arrows at the same time.","images/skills/Double Shot.png").setRequires(Skills.WARDEN).setTreeOffsets(100 + 1350, -480));
        skills.add(new Skill(Skills.SNIPER, HeroClass.ARCHER, 2, "Sniper", "Sniper", "Embraces the sniper path.\n- +35 health\n- Never miss with the bow\n- Bow attacks ignore enemy armor","images/skills/Sniper.png") {
            @Override
            public void affect(Unit owner){
                owner.setMaxHP(owner.getMaxHP() + 35);
                owner.heal(35);
            }
        }.setRequires(Skills.AIMED_SHOT).setRequires(Skills.KNEE_SHOT).setTreeOffsets(100 + 900, -480 - 125));
        skills.add(new KneeShot(Skills.KNEE_SHOT, HeroClass.ARCHER, 2, "Knee Shot", "Knee Shot", "Fire a cruel shot aimed for the legs.\n- 3 mana\n- Consumes 1 arrow\n- +25% damage\n- Chance to cripple enemy","images/skills/Knee Shot.png").setRequires(Skills.AIMED_SHOT).setTreeOffsets(100 + 450, -480 - 125));
        skills.add(new Bombvoyage(Skills.BOMBVOYAGE, HeroClass.ARCHER, 4, "Bombvoyage", "Bombvoyage", "Fire an explosive arrowhead that spreads damage beyond the main target.\n- 5 mana\n- Consumes 1 arrow\n- 75% AOE damage","images/skills/Bombvoyage.png").setRequires(Skills.SNIPER).setTreeOffsets(100 + 1350, -480 - 375));
        skills.add(new IronTip(Skills.IRON_TIP, HeroClass.ARCHER, 3, "Iron Tip", "Iron Tip", "Fire a heavy iron-tipped arrow that keeps going through the fight.\n- 4 mana\n- Consumes 1 arrow\n- Arrow passes through enemies","images/skills/Iron Tip.png").setRequires(Skills.SNIPER).setTreeOffsets(100 + 1350, -480 - 125));

        registerNewClassSkill(new MindShot());
        registerNewClassSkill(new RaiseSkeleton());
        registerNewClassSkill(new DrainLife());
        registerNewClassSkill(new Curse());
        registerNewClassSkill(new SpiritBinder());
        registerNewClassSkill(new SummonGhost());
        registerNewClassSkill(new Lich());
        registerNewClassSkill(new RaiseSkeletonArcher());
        registerNewClassSkill(new CorpseExplosion());
        registerNewClassSkill(new MasterOfDeath());
        registerNewClassSkill(new Wanted());
        registerNewClassSkill(new Packrat());
        registerNewClassSkill(new SteadyAim());
        registerNewClassSkill(new QuickDraw());
        registerNewClassSkill(new HeadShot());
        registerNewClassSkill(new Marshal());
        registerNewClassSkill(new IAmTheLaw());
        registerNewClassSkill(new Executioner());
        registerNewClassSkill(new NoWitnesses());
        registerNewClassSkill(new Execute());
    }


    public Skill getSkill(int id){
        for(Skill skill : skills){
            if(skill.getId() == id){
                return skill;
            }
        }

        return null;
    }


    private void registerNewClassSkill(Skill skill) {
        if (skill == null || getSkill(skill.getId()) != null)
            throw new IllegalArgumentException("Missing or duplicate class skill");
        NewClassSkillTree.configure(skill);
        skills.add(skill);
    }

    public String getSkillName(int id) {
        Skill skill = getSkill(id);
        if (skill != null) return skill.getName();
        NewClassSkillTree.Node node = NewClassSkillTree.uniqueNode(id);
        return node == null ? "#" + id : com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages.maybeTranslate(node.name);
    }

    public boolean isSupported(Hero hero, Skill skill) {
        return hero != null && skill != null && getSkill(skill.getId()) == skill
                && NewClassSkillTree.allows(hero.getHeroClass(), skill);
    }

    public boolean meetsRequirements(Hero hero, Skill skill) {
        if (!isSupported(hero, skill)) return false;
        HeroClass type = hero.getHeroClass();
        ArrayList<Integer> requirements = type.hasSkillInTree(skill.getId())
                ? type.getSkillRequirements(skill.getId()) : skill.getRequires();
        for (int id : requirements) if (!hero.hasSkill(id)) return false;
        return true;
    }

    public int skillCost(Skill skill){
        if (skill == null || getSkill(skill.getId()) != skill) return -1;
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero != null) {
            if (!isSupported(hero, skill)) return -1;
            HeroClass heroClass = hero.getHeroClass();
            if (heroClass.hasSkillInTree(skill.getId())) {
                return heroClass.getSkillPointCost(skill.getId());
            }
        }

        int baseCost = skill.getSkillClass().getSkillPointCost(skill.getId());
        if (hero == null || skill.getSkillClass() == HeroClass.NEUTRAL) {
            return baseCost;
        }

        int penalty = hero.getHeroClass().classPenalty(skill.getSkillClass());
        return baseCost < 0 || penalty < 0 ? -1 : baseCost * penalty;
    }

    public boolean canLearn(Skill skill){
        Hero hero = UnitHelper.getInstance().getHero();
        if (!isSupported(hero, skill) || hero.isDead()) return false;
        if(UnitHelper.getInstance().getHero().hasSkill(skill.getId())){
            return false;
        }

        if(isSubclassSkill(skill.getId())){
            return false;
        }

        if(UnitHelper.getInstance().getHero().skillLockedOut(skill.getId())){
            return false;
        }

        int skillPoints = UnitHelper.getInstance().getHero().getSkillPoints();
        int skillCost = skillCost(skill);
        if(skillCost < 0 || skillPoints < skillCost){
            return false;
        }

        return meetsRequirements(hero, skill);
    }

    public boolean isSubclassSkill(int skillId) {
        return NewClassSkillTree.opposite(skillId) != 0 || skillId == Skills.GLADIATOR
                || skillId == Skills.BERSERKER
                || skillId == Skills.BATTLE_MAGE
                || skillId == Skills.WARLOCK
                || skillId == Skills.FREE_RUNNER
                || skillId == Skills.ASSASSIN
                || skillId == Skills.WARDEN
                || skillId == Skills.SNIPER;
    }

    public boolean learnSkill(Skill skill){
        if(!canLearn(skill)){
            return false;
        }

        int cost = skillCost(skill);
        if (!UnitHelper.getInstance().getHero().learnSkill(skill)) return false;
        if (skill instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ActiveSkill) {
            UnitHelper.getInstance().getHero().assignQuickSkillIfNeeded((com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ActiveSkill) skill);
        }
        UnitHelper.getInstance().getHero().modifySkillPoints(-cost);
        return true;
    }
}
