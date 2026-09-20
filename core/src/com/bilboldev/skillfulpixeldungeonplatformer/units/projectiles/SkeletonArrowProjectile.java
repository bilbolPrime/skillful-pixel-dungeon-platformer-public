package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpriteTrail;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.RaisedSkeletonArcher;


public final class SkeletonArrowProjectile extends NewClassSpellProjectile {
    public static final float TTL = 2f;
    public SkeletonArrowProjectile(RaisedSkeletonArcher caster, Unit target, float damage) {
        super(caster, damage);
        float dx = target.x + 48f - (x + 6f), dy = target.y + 64f - (y + 6f);
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        if (distance > .001f) { speedX = SPEED * dx / distance; speedY = SPEED * dy / distance; }
        gs = new GameSprite("images/misc/extracted items/Arrow.png", 45, 45);
        magicAttack = false;
    }
    @Override protected float maximumLifetime() { return TTL; }
    @Override public void onUnitCollision(Unit target) {
        if (!(target instanceof Mob) || !target.isVisible() || target.isInvisible()) return;
        super.onUnitCollision(target);
    }
    @Override protected void resolveHit(Unit target) {
        boolean hit = UnitHelper.getInstance().attackTarget(getOwner(), target, getOwner().getWeapon(), damage, false);
        playSound(hit ? Sounds.HIT : Sounds.MISS, .4f);
    }
    @Override public void draw(Batch batch, float alpha) {
        if (isUsed() || !validCaster()) return;
        gs.setPosition(getRenderX() + 6f - gs.getWidth() / 2f, getRenderY() + 6f - gs.getHeight() / 2f);
        gs.setRotation(MathUtils.atan2(speedY, speedX) * MathUtils.radiansToDegrees - 45f);
        gs.setAlpha(.95f * alpha);
        SpriteTrail.draw(batch, gs, speedX * .012f, speedY * .012f, 18f);
        gs.draw(batch);
    }
}
