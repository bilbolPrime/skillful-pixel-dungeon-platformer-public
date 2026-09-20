package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.NecromancerCurse;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.MercenaryFear;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.Wand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Bow;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.NewClassSpellProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ActiveSkill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap;

import java.util.ArrayList;


public class UnitHelper {
    private static final boolean UNIT_MOVEMENT_COLLISION_BLOCKING_ENABLED = true;
    private static final float PLATFORM_SIGHT_EYE_HEIGHT_RATIO = 0.72f;

    private static final UnitHelper ourInstance = new UnitHelper();

    public static UnitHelper getInstance() {
        return ourInstance;
    }

    ArrayList<Unit> units;
    private Hero hero;
    private Stage unitStage;


    private UnitHelper(){
        units = new ArrayList<>();
    }

    public void setStage(Stage stage){
        this.unitStage = stage;
    }

    public void setHero(Hero hero){
        this.hero = hero;
    }

    public void reset() {
        ArrayList<Unit> existingUnits = new ArrayList<>(units);
        for (Unit unit : existingUnits) {
            removeUnit(unit);
        }
        units.clear();
        hero = null;
        unitStage = null;
    }

    public void addUnit(Unit unit, boolean hero){
        if(unit.getRoom() == null){
            unit.setRoom(MapHelper.getInstance().getActiveRoomIdentifier());
        }

        if (unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob) {
            ((com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob) unit).applyCurrentRunDifficulty();
        }


        units.add(unit);
        unitStage.addActor(unit);
        PhysicsHelper.getInstance().register(unit);

        if(hero){
            this.hero = (Hero) unit;
            UIHelper.getInstance().setExpString(this.hero.expString());
        }

        refreshUnitLayers();
    }


    public void addUnit(Unit unit){
        addUnit(unit, false);
    }



    public void removeUnit(Unit unit){
        if(units.contains(unit)){
            if (unit instanceof NecromancerMinion) NewClassSpellProjectile.clearFrom(unit);
            NecromancerCurse.clear(unit);
            MercenaryFear.clear(unit);
            PhysicsHelper.getInstance().unregister(unit);
            unit.remove();
            units.remove(unit);
            refreshUnitLayers();
        }
    }

    private void refreshUnitLayers() {
        if (unitStage == null) {
            return;
        }

        int zIndex = 0;
        for (Unit unit : units) {
            if (unit instanceof PlatformTrap) {
                unit.setZIndex(zIndex++);
            }
        }

        for (Unit unit : units) {
            if (unit instanceof PlatformTrap || unit == hero) {
                continue;
            }

            unit.setZIndex(zIndex++);
        }

        if (hero != null && units.contains(hero)) {
            hero.setZIndex(zIndex);
        }
    }

    public void act(float delta){
        PhysicsHelper.getInstance().step(delta);
    }

    public float getHeroX(){
        return hero.x;
    }

    public Hero getHero(){
        return hero;
    }

    public Unit findTarget(Unit searcher, float los){
        return  findTarget( searcher, los,false);
    }

    public Unit findTarget(Unit searcher, float los, boolean any){
        Unit target = null;
        float distance = -1f;

        for(Unit unit : units){
            if (unit == null || unit.isDead() || unit.getHP() < 1) {
                continue;
            }

            if(!any && unit.isFriendly && searcher.isFriendly){
                continue;
            }

            if(!any && !unit.isFriendly && !searcher.isFriendly){
                continue;
            }

            if(any && unit == searcher){
                continue;
            }

            if(unit.showOnly()) {
                continue;
            }

            if (unit.isInvisible() && unit != searcher) {
                continue;
            }

            if(unit.getRoom() == null || searcher.getRoom() == null || !unit.getRoom().equals(searcher.getRoom())){
                continue;
            }

            if (!canSeeTarget(searcher, unit)) {
                continue;
            }

            if(target == null && UtilsHelper.distance(searcher, unit) <= los){
                distance = (float) UtilsHelper.distance(searcher, unit);
                target = unit;
            }
            else {
                float newDist = (float) UtilsHelper.distance(searcher, unit);

                if(newDist > los){
                    continue;
                }

                if(newDist < distance){
                    distance = newDist;
                    target = unit;
                }
            }
        }

        return target;
    }

    public Unit findTargetInRoom(Unit searcher) {
        return findTargetInRoom(searcher, false);
    }

    public Unit findTargetInRoom(Unit searcher, boolean allowInvisible) {
        if (searcher == null || searcher.getRoom() == null) {
            return null;
        }

        Unit visibleTarget = null;
        float visibleDistance = -1f;
        Unit invisibleTarget = null;
        float invisibleDistance = -1f;

        for (Unit unit : units) {
            if (unit == null || unit == searcher || unit.showOnly() || unit.isDead() || unit.getHP() < 1) {
                continue;
            }

            if (unit.getRoom() == null || !unit.getRoom().equals(searcher.getRoom())) {
                continue;
            }

            if (unit.isFriendly == searcher.isFriendly) {
                continue;
            }

            if (!canSeeTarget(searcher, unit)) {
                continue;
            }

            float newDistance = (float) UtilsHelper.distance(searcher, unit);
            if (unit.isInvisible()) {
                if (!allowInvisible) {
                    continue;
                }

                if (invisibleTarget == null || newDistance < invisibleDistance) {
                    invisibleDistance = newDistance;
                    invisibleTarget = unit;
                }
                continue;
            }

            if (visibleTarget == null || newDistance < visibleDistance) {
                visibleDistance = newDistance;
                visibleTarget = unit;
            }
        }

        return visibleTarget != null ? visibleTarget : invisibleTarget;
    }

    public boolean canSeeTarget(Unit searcher, Unit target) {
        if (searcher == null || target == null) {
            return false;
        }

        if (searcher == target) {
            return true;
        }

        if (searcher.getRoom() == null || target.getRoom() == null || !searcher.getRoom().equals(target.getRoom())) {
            return false;
        }

        if (!MapHelper.getInstance().shouldUsePlatformSightLines()) {
            return true;
        }

        Room activeRoom = MapHelper.getInstance().getActiveRoom();
        if (activeRoom == null || !searcher.getRoom().equals(activeRoom.getIdentifier())) {
            return true;
        }

        float startX = searcher.x + ConstantsHelper.UNIT_DIMENSIONS * 0.5f;
        float endX = target.x + ConstantsHelper.UNIT_DIMENSIONS * 0.5f;
        float startY = searcher.y + ConstantsHelper.UNIT_DIMENSIONS * PLATFORM_SIGHT_EYE_HEIGHT_RATIO;
        float endY = target.y + ConstantsHelper.UNIT_DIMENSIONS * PLATFORM_SIGHT_EYE_HEIGHT_RATIO;
        return MapHelper.getInstance().hasPlatformLineOfSight(activeRoom, startX, startY, endX, endY);
    }

    public boolean attack(Unit source, Weapon attackingItem){
        Rectangle rectangle = attackingItem.getHitArea();
        Unit target = PhysicsHelper.getInstance().queryFirstHit(source, rectangle);
        return attackTarget(source, target, attackingItem, attackingItem.getDamage(), attackingItem instanceof Wand);
    }

    public boolean tryHit(Unit source, Unit target, Weapon attackingItem, boolean magic) {
        return tryHit(source, target, attackingItem, magic, 1f);
    }

    public boolean tryHit(Unit source, Unit target, Weapon attackingItem, boolean magic, float accuracyMultiplier) {
        if (source == null) {
            return false;
        }

        if (target == null || !didAttackHit(source, target, attackingItem, magic, accuracyMultiplier)) {
            if (source instanceof Hero && target instanceof Mob && ((Mob) target).isSleeping()) {
                ((Mob) target).suppressNextSleepNotice();
            }

            EffectsHelper.getInstance().miss(source);
            return false;
        }

        return true;
    }

    public boolean attackTarget(Unit source, Unit target, Weapon attackingItem, float damage, boolean magic) {
        return attackTarget(source, target, attackingItem, damage, magic, 1f);
    }

    public boolean attackTarget(Unit source, Unit target, Weapon attackingItem, float damage, boolean magic, float accuracyMultiplier) {
        return attackTarget(source, target, attackingItem, damage, magic, accuracyMultiplier, null, 1f);
    }


    public boolean attackGunTarget(Unit source, Unit target, Weapon attackingItem, GunProjectile shot,
                                   float scale, boolean splash, float accuracyMultiplier) {
        return attackTarget(source, target, attackingItem, shot.getDamage(), splash, accuracyMultiplier, shot, scale);
    }

    private boolean attackTarget(Unit source, Unit target, Weapon attackingItem, float damage, boolean magic,
                                 float accuracyMultiplier, GunProjectile gunShot, float scale) {
        boolean attackedFromInvisibility = source != null && source.isInvisible();
        if (source != null && source.isInvisible()) {
            source.setInvisible(false);
        }

        if (!tryHit(source, target, attackingItem, magic, accuracyMultiplier)) {
            return false;
        }

        if (gunShot != null) {
            damage = gunShot.resolveHitDamage(target, scale);
        } else if (attackingItem != null && attackingItem.getPrefix() != null) {
            damage = attackingItem.getPrefix().modifyAttackDamage(source, target, attackingItem, damage);
        }

        if (source instanceof Hero) {
            Hero heroSource = (Hero) source;
            damage = heroSource.adjustSuccessfulHitDamage(target, attackingItem, damage, attackedFromInvisibility);
            if (heroSource.shouldInstantKill(target, attackingItem)) {
                damage = Math.max(damage, target.getHP() + 9999f);
            }
        }

        damage = DifficultyHelper.getInstance().scaleEnemyDamage(source, damage);
        int previousHp = target.getHP();
        target.takeDamage(source, attackingItem, damage);
        int damageDealt = Math.max(0, previousHp - target.getHP());


        boolean hitEffects = gunShot == null || (damageDealt > 0 && gunShot.claimHitEffects(target));
        if (hitEffects && source instanceof Hero) {
            ((Hero) source).handleSuccessfulAttack(target, attackingItem, damageDealt);
        }

        if (hitEffects && attackingItem != null && attackingItem.getPrefix() != null) {
            attackingItem.getPrefix().onAttack(source, target, attackingItem, damageDealt);
        }

        if (hitEffects && source instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob) {
            ((com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob) source).onSuccessfulAttack(target, damageDealt);
        }

        return true;
    }

    private boolean didAttackHit(Unit source, Unit target, Weapon attackingItem, boolean magic, float accuracyMultiplier) {
        if (magic) {
            return true;
        }

        if (source instanceof Hero && attackingItem instanceof Bow && ((Hero) source).neverMissesWithBow((Bow) attackingItem)) {
            return true;
        }

        float effectiveAttackSkill = Math.max(1f, source.getAttackSkill(target, attackingItem) * Math.max(0.1f, accuracyMultiplier) * 2f);
        float attackRoll = RandomHelper.getInstance().randomFloat(effectiveAttackSkill + 1f);
        float defenseRoll = RandomHelper.getInstance().randomFloat(Math.max(1, target.getDefenseSkill(source) + 1));

        if (target instanceof Hero) {
            defenseRoll *= 0.8f;
        }

        return attackRoll >= defenseRoll;
    }

    public Unit attackRanged(Unit source, ThrownProjectile attackingItem){
        return PhysicsHelper.getInstance().queryFirstHit(source, attackingItem.getHitArea());
    }

    public boolean freeSpace(Unit source, int x, int y, String room){
        if (!UNIT_MOVEMENT_COLLISION_BLOCKING_ENABLED) {
            return true;
        }

        if(source == null || source.showOnly() || room == null){
            return true;
        }

        Rectangle sourceHitBox = source.getHitBox();
        Rectangle projectedHitBox = source.getHitBoxAt(x, y);

        for(Unit unit : units){
            if(!blocksMovement(source, unit, room)) {
                continue;
            }

            Rectangle blockingHitBox = unit.getHitBox();
            if(!projectedHitBox.overlaps(blockingHitBox)) {
                continue;
            }

            if(sourceHitBox.overlaps(blockingHitBox) && isResolvingOverlap(sourceHitBox, projectedHitBox, blockingHitBox)) {
                continue;
            }

            if(projectedHitBox.overlaps(blockingHitBox)){
                return false;
            }
        }

        return true;
    }

    public ArrayList<Unit> getMovementBlockers(Unit source, int x, int y, String room) {
        ArrayList<Unit> blockers = new ArrayList<Unit>();
        if (!UNIT_MOVEMENT_COLLISION_BLOCKING_ENABLED) {
            return blockers;
        }

        if (source == null || source.showOnly() || room == null) {
            return blockers;
        }

        Rectangle sourceHitBox = source.getHitBox();
        Rectangle projectedHitBox = source.getHitBoxAt(x, y);

        for (Unit unit : units) {
            if (!blocksMovement(source, unit, room)) {
                continue;
            }

            Rectangle blockingHitBox = unit.getHitBox();
            if (!projectedHitBox.overlaps(blockingHitBox)) {
                continue;
            }

            if (sourceHitBox.overlaps(blockingHitBox) && isResolvingOverlap(sourceHitBox, projectedHitBox, blockingHitBox)) {
                continue;
            }

            blockers.add(unit);
        }

        return blockers;
    }

    private boolean isResolvingOverlap(Rectangle sourceHitBox, Rectangle projectedHitBox, Rectangle blockingHitBox) {
        float currentCenterDistance = Math.abs(sourceHitBox.x + sourceHitBox.width / 2f - (blockingHitBox.x + blockingHitBox.width / 2f));
        float projectedCenterDistance = Math.abs(projectedHitBox.x + projectedHitBox.width / 2f - (blockingHitBox.x + blockingHitBox.width / 2f));
        return projectedCenterDistance > currentCenterDistance;
    }

    private boolean blocksMovement(Unit source, Unit unit, String room) {
        if(unit == null || unit == source || unit.showOnly()) {
            return false;
        }

        String activeRoom = MapHelper.getInstance().getActiveRoomIdentifier();
        if(activeRoom == null || !activeRoom.equals(room)) {
            return false;
        }

        if(unit.getRoom() == null || !unit.getRoom().equals(room)) {
            return false;
        }

        return source.isFriendly != unit.isFriendly;
    }

    public boolean freeSpace( int x, int y, String room){
        for(Unit unit : units){




            if(unit.getRoom() == null || !unit.getRoom().equals(room)){
                continue;
            }





            if(unit.getHitBox().contains(x, y)){
                return false;
            }
        }

        return true;
    }

    public ArrayList<Unit> getUnits(){
        return units;
    }

    public ArrayList<Unit> getUnitsSnapshot() {
        return new ArrayList<>(units);
    }

    public boolean isUnitMovementCollisionBlockingEnabled() {
        return UNIT_MOVEMENT_COLLISION_BLOCKING_ENABLED;
    }


}
