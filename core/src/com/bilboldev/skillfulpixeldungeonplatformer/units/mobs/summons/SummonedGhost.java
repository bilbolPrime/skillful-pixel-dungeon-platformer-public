package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;


public final class SummonedGhost extends NecromancerMinion {
    public static final float SECONDS = 20f;
    public static final float COLLISION_WIDTH = 86.4f;
    private float remainingLifetime = SECONDS;

    public SummonedGhost(Hero owner) { this(owner, owner == null ? 0 : owner.getLevel()); }
    public SummonedGhost(Hero owner, int createdAtLevel) {
        super(owner, createdAtLevel);
        hp = mhp = 12 + 3 * (getSummonedLevel() - 1);
        float damage = 3f + .5f * (getSummonedLevel() - 1);
        attackSkill = 10 + getSummonedLevel(); defenseSkill = attackSkill * 5; damageReduction = 0;
        speedX = 390f; attackSpeed = 5.5f; canFly = true;
        setWeapon(new MeleeAttack().setDamageRange(damage, damage));
        gf = new GameFilm("images/units/ghost/wraith.png", 112, 15, 1f);
        gf.clipSizeX = 14; gf.clipSizeY = 15;
        gf.setColor(new Color(.68f, .94f, 1f, 1f));
        idleFrames = runFrames = jumpFrames = new int[]{0, 1};
        attackFrames = new int[]{0, 2, 3}; dieFrames = new int[]{0, 4, 5, 6, 7};
        changeState(UnitState.IDLE, true);
        applySpiritBinder(owner);
    }
    @Override public float getCollisionWidth() { return COLLISION_WIDTH; }
    public float getRemainingLifetime() { return remainingLifetime; }
    public void restoreLifetime(float remaining) {
        if (!Float.isFinite(remaining) || remaining <= 0f || remaining > SECONDS) throw new IllegalArgumentException("Invalid ghost lifetime");
        remainingLifetime = remaining;
    }
    @Override public void act(float delta) {
        Hero hero = getOwnerHero();
        if (hero != null && !hero.isDead() && !isDead() && room != null && room.equals(hero.getRoom())
                && room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {

            remainingLifetime = Math.max(0f, remainingLifetime - PhysicsHelper.boundGameDelta(delta));
            if (remainingLifetime == 0f) { unSummon(); return; }
        }
        super.act(delta);
    }
    @Override public String getLibraryDescription() {
        return "A summoned ghost that flies alongside the Necromancer and fights for a short time.";
    }
}
