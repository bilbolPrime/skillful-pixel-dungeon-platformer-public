package com.bilboldev.skillfulpixeldungeonplatformer.party;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.RaisedSkeletonArcher;
import java.lang.reflect.Constructor;
import java.io.IOException;

public final class PartyObjects {
    private static final Class<?>[] BRAINS = {
        com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.ai.ConfusedAI.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.ai.DefensiveAI.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.ai.FriendlyAI.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.ai.MercenaryRangedAI.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.ai.RatKingAI.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.ai.SkeletonArcherAI.class,
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Goo.class, "$1"),
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Piranha.class, "$WaterBoundAgressiveAI"),
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Shaman.class, "$1"),
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Tengu.class, "$1"),
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Elemental.class, "$1"),
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.DM300.class, "$1"),
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.Succubus.class, "$1"),
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfWarlock.class, "$1"),
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.YogDzewa.class, "$1"),
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.Scorpio.class, "$1"),
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.EvilEye.class, "$1"),
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.BurningFist.class, "$1"),
        nestedBrain(com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.other.Statue.class, "$1")
    };

    private static Class<?> nestedBrain(Class<?> owner, String suffix) {
        try { return Class.forName(owner.getName() + suffix, false, owner.getClassLoader()); }
        catch (ClassNotFoundException missing) { throw new IllegalStateException("Missing registered AI", missing); }
    }
    public static int brainId(com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI brain) { return id(BRAINS, brain.getClass()); }
    public static com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI brain(int id, Unit unit) throws IOException {
        if (id < 0 || id >= BRAINS.length) throw new IOException("brain_type");
        try {
            for (Constructor<?> constructor : BRAINS[id].getDeclaredConstructors()) {
                Class<?>[] parameters = constructor.getParameterTypes();
                Object[] arguments = new Object[parameters.length]; boolean match = parameters.length > 0;
                for (int i = 0; i < parameters.length; i++) { if (!parameters[i].isInstance(unit)) match = false; arguments[i] = unit; }
                if (match) { constructor.setAccessible(true); return (com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI)constructor.newInstance(arguments); }
            }
        } catch (ReflectiveOperationException invalid) { throw new IOException("brain", invalid); }
        throw new IOException("brain_owner");
    }
    private static final Class<?>[] SHOTS = {
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.AvalancheProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles.BlindProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles.BuffProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles.ConfuseProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles.DominateProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles.EnrageProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles.SlowProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles.VulnerableProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles.WeakenProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.CurseProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.DrainLifeProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.EyeBeam.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.FireBall.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.FireBolt.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.HuntressArrowProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.MortarProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.NewClassSpellProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.PoisonBolt.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.PotionProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ReachBolt.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ScorpioShot.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.SeedProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ShamanBolt.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.SkeletonArrowProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.SludgeBomb.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.TeleportBolt.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.TenguKnife.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.WarlockBolt.class
    };
    private static final Class<?>[] BUFFS = {
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Aggression.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.BattleMage.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.BerserkerBuff.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Bleeding.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Blind.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Burning.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Charm.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Confused.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Cripple.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.DeferredDamage.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Dominate.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.EarthrootArmor.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Enrage.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.FireMastery.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Frenzy.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Frost.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.GladiatorBuff.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.GrandMaster.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Health.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Invisible.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Levitation.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Mana.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.ManaArmor.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.ManaRegeneration.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Mastery.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.MercenaryFear.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.NecromancerCurse.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Paralyzed.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Poisoned.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Purity.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Regeneration.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Rooted.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Slow.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Starving.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.SungrassHealth.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.TemporaryBlind.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Terrorized.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Toughness.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Vertigo.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Vulnerable.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.WandMaster.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Warlock.class,
        com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Weaken.class
    };
    public static int shotId(ThrownProjectile shot) { return id(SHOTS, shot.getClass()); }
    public static int buffId(Buff buff) { return id(BUFFS, buff.getClass()); }
    private static int id(Class<?>[] types, Class<?> type) {
        for (int i = 0; i < types.length; i++) if (types[i] == type) return i;
        throw new IllegalArgumentException("Unregistered party type: " + type.getName());
    }
    public static ThrownProjectile shot(int id, Unit owner, Gun gun, float damage) throws IOException {
        if (id < 0 || id >= SHOTS.length || owner == null) throw new IOException("projectile_type");
        Class<?> type = SHOTS[id];
        try {
            if (type == com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.SkeletonArrowProjectile.class)
                return new com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.SkeletonArrowProjectile((RaisedSkeletonArcher)owner, owner, damage);
            try { return (ThrownProjectile)type.getConstructor().newInstance(); } catch (NoSuchMethodException ignored) { }
            try { return (ThrownProjectile)type.getConstructor(Hero.class, Gun.class).newInstance(owner, gun); } catch (NoSuchMethodException ignored) { }
            try { return (ThrownProjectile)type.getConstructor(Hero.class, float.class).newInstance(owner, damage); } catch (NoSuchMethodException ignored) { }
            return (ThrownProjectile)type.getConstructor(Hero.class).newInstance(owner);
        } catch (ReflectiveOperationException | ClassCastException failure) { throw new IOException("projectile_factory", failure); }
    }
    public static Buff buff(int id, Hero source) throws IOException {
        if (id < 0 || id >= BUFFS.length) throw new IOException("buff_type");
        try {
            Constructor<?> constructor;
            try { constructor = BUFFS[id].getDeclaredConstructor(); }
            catch (NoSuchMethodException absent) {
                constructor = BUFFS[id].getDeclaredConstructor(Hero.class);
                constructor.setAccessible(true); return (Buff)constructor.newInstance(source);
            }
            constructor.setAccessible(true); return (Buff)constructor.newInstance();
        } catch (ReflectiveOperationException failure) { throw new IOException("buff_factory", failure); }
    }
    private PartyObjects() { }
}
