package com.bilboldev.skillfulpixeldungeonplatformer.units.traps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Bleeding;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Poisoned;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Slow;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.LightningSpread;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Rat;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

public class PlatformTrap extends Unit {
    private static final int TILES_PER_ROW = 16;
    private static final int TRAP_ROW = 1;
    private static final int TRIGGERED_TILE_INDEX = 23;
    private static final int TRIGGERED_TILE_X = TRIGGERED_TILE_INDEX % TILES_PER_ROW;
    private static final int TRIGGERED_TILE_Y = TRIGGERED_TILE_INDEX / TILES_PER_ROW;
    private static final float TRIGGER_INSET_X = 12f;
    private static final float TRIGGER_INSET_Y = 10f;

    private TrapType trapType = TrapType.TOXIC;
    private boolean hidden;
    private boolean triggered;

    {
        showOnly = true;
        hp = mhp = 1;
        gf = new GameFilm(
                TrapType.TRAP_TILE_SHEET,
                ConstantsHelper.UNIT_DIMENSIONS,
                ConstantsHelper.UNIT_DIMENSIONS,
                1f);
        gf.clipSizeX = 16;
        gf.clipSizeY = 16;
        gf.yClipOffset = TrapType.TRAP_TILE_ROW_OFFSET;
    }

    @Override
    public void act(float delta) {
        if (triggered || room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            return;
        }

        Unit triggeringUnit = resolveTriggeringUnit();
        if (triggeringUnit == null) {
            return;
        }

        if (triggeringUnit instanceof Hero) {
            Hero hero = (Hero) triggeringUnit;
            if (hero.hasSkill(Skills.LOCK_SMITH) && (!hidden || hero.getHeroClass() != HeroClass.WARRIOR)) {
                disarm(hero);
            }
            else {
                trigger(triggeringUnit);
            }
            return;
        }

        trigger(triggeringUnit);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            return;
        }

        if (hidden && !triggered) {
            return;
        }

        gf.tileX = triggered ? TRIGGERED_TILE_X : trapType.getTileX();
        gf.tileY = triggered ? TRIGGERED_TILE_Y : trapType.getTileY();
        gf.setAlpha(1f);
        gf.setPosition(x, y);
        gf.draw(batch);
    }

    public void trigger(Unit target) {
        if (triggered || target == null) {
            return;
        }

        triggered = true;
        hidden = false;
        playTriggerSound();
        EffectsHelper.getInstance().message(target,
            Messages.get("custom.generated.arg_triggered_141131fc19",
                new Object[]{trapType.getDisplayName()}),
            Color.RED, 0f);
        if (target instanceof Hero) {
            RatKingHelper.getInstance().onHeroTriggeredTrap();
        }
        applyEffect(target);
    }

    private void disarm(Hero hero) {
        if (triggered || hero == null) {
            return;
        }

        triggered = true;
        hidden = false;
        playTriggerSound();
        EffectsHelper.getInstance().message(hero,
            Messages.get("custom.generated.arg_disarmed_148e661cab",
                new Object[]{trapType.getDisplayName()}),
            Color.GREEN, 0f);
    }

    public TrapType getTrapType() {
        return trapType;
    }

    public PlatformTrap setTrapType(TrapType trapType) {
        this.trapType = trapType == null ? TrapType.TOXIC : trapType;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public PlatformTrap setHidden(boolean hidden) {
        this.hidden = hidden && !triggered;
        return this;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public PlatformTrap setTriggered(boolean triggered) {
        this.triggered = triggered;
        if (triggered) {
            hidden = false;
        }
        return this;
    }


    public Rectangle getTriggerArea() {
        return new Rectangle(
                x + TRIGGER_INSET_X,
                y + TRIGGER_INSET_Y,
                ConstantsHelper.UNIT_DIMENSIONS - TRIGGER_INSET_X * 2f,
                ConstantsHelper.UNIT_DIMENSIONS - TRIGGER_INSET_Y * 2f);
    }

    private void applyEffect(Unit target) {
        switch (trapType) {
            case TOXIC:
                target.takeDamage(this, null, 2f);
                new Poisoned().setDuration(5f).setOwner(target);
                emitBurst("images/misc/green.png", 12f, 12, 40f, 40f, 70f, 0.02f);
                break;
            case FIRE:
                target.takeDamage(this, null, 4f);
                emitBurst("images/misc/yellow-dot.png", 10f, 10, 80f, 90f, 55f, 0.18f);
                emitBurst("images/misc/red.png", 10f, 8, 60f, 70f, 50f, 0.15f);
                break;
            case PARALYTIC:
                target.takeDamage(this, null, 1f);
                new Slow().setPermanent(false).setDuration(4f).setOwner(target);
                emitBurst("images/misc/yellow-dot.png", 10f, 8, 30f, 40f, 60f, 0.03f);
                break;
            case POISON:
                UnitHelper.getInstance().addUnit(createPoisonCloud());
                emitBurst("images/misc/green.png", 10f, 48, 40f, 35f, 70f, 0.02f);
                break;
            case ALARM:
                SoundHelper.GetSingleton().play(Sounds.ALERT, 0f, 1f);
                alertRoom(target);
                emitBurst("images/misc/red.png", 10f, 8, 40f, 60f, 50f, 0.05f);
                break;
            case LIGHTNING:
                target.takeDamage(this, null, Math.max(4f, target.getHP() / 3f));
                for (int i = 0; i < 4; i++) {
                    EffectsHelper.getInstance().spark(target);
                }
                emitLightningSpread();
                break;
            case GRIPPING:
                target.takeDamage(this, null, 3f);
                new Slow().setPermanent(false).setDuration(6f).setOwner(target);
                new Bleeding().setDuration(5f).setOwner(target);
                emitBurst("images/misc/blood.png", 12f, 8, 45f, 35f, 45f, 0.08f);
                break;
            case SUMMONING:
                SoundHelper.GetSingleton().play(Sounds.ALERT, 0f, 1f);
                emitBurst("images/misc/black-particle.png", 10f, 10, 35f, 50f, 65f, -0.01f);
                summonRats();
                break;
            default:
                break;
        }
    }

    private void playTriggerSound() {
        if (trapType == TrapType.LIGHTNING) {
            SoundHelper.GetSingleton().play(Sounds.LIGHTNING, 0f, 1f);
            return;
        }

        SoundHelper.GetSingleton().play(Sounds.TRAP, 1f, 1f);
    }

    private void alertRoom(Unit source) {
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof Mob) || unit.showOnly() || unit.isFriendly) {
                continue;
            }

            if (unit.getRoom() == null || !unit.getRoom().equals(room)) {
                continue;
            }

            ((Mob) unit).alert(source);
        }
    }

    private void summonRats() {
        int roll = RandomHelper.getInstance().randomInt(4);
        int summonCount = roll < 2 ? 1 : (roll == 2 ? 2 : 3);
        int[] offsets = new int[]{-2, -1, 1, 2, 0};
        int spawned = 0;

        for (int offset : offsets) {
            if (spawned >= summonCount) {
                break;
            }

            Rat rat = new Rat();
            float candidateX = x + offset * ConstantsHelper.TILE;
            candidateX = Math.max(1f, Math.min(candidateX, MapHelper.getInstance().getWidth() * ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS - 1f));
            float candidateFloorY = MapHelper.getInstance().calculateFloorY(candidateX, y + ConstantsHelper.TILE);
            rat.x = candidateX;
            rat.y = candidateFloorY;
            rat.floorY = candidateFloorY;
            rat.setRoom(room);

            if (!UnitHelper.getInstance().freeSpace(rat, Math.round(candidateX), Math.round(candidateFloorY), room)) {
                continue;
            }

            UnitHelper.getInstance().addUnit(rat);
            spawned++;
        }
    }

    private void emitBurst(String spritePath, float particleSize, int count, float spread, float rise, float lifeSpan, float gravityScale) {
        EffectsHelper.getInstance().add(new TrapBurst().init(
                x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                y + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                spritePath,
                particleSize,
                count,
                spread,
                rise,
                lifeSpan,
                gravityScale));
    }

    private void emitLightningSpread() {
        EffectsHelper.getInstance().add(new LightningSpread().init(
                x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                y + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                0f,
                0f,
                0f,
                0f));
    }

    private PoisonCloud createPoisonCloud() {
        PoisonCloud poisonCloud = new PoisonCloud();
        poisonCloud.x = x;
        poisonCloud.y = y;
        poisonCloud.floorY = floorY;
        poisonCloud.setRoom(room);
        return poisonCloud;
    }

    private Unit resolveTriggeringUnit() {
        Hero hero = UnitHelper.getInstance().getHero();
        if (canTriggerTrap(hero)) {
            return hero;
        }

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit instanceof MercenaryAlly && canTriggerTrap(unit)) {
                return unit;
            }
        }

        return null;
    }

    private boolean canTriggerTrap(Unit unit) {
        return unit != null
                && !unit.isDead()
                && !unit.isLevitating()
                && unit.getRoom() != null
                && unit.getRoom().equals(room)
                && unit.getHitBox().overlaps(getTriggerArea());
    }
}
