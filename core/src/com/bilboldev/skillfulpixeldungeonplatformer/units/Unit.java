package com.bilboldev.skillfulpixeldungeonplatformer.units;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfThorns;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.Wand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.ContactShadow;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.NecromancerCurse;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.EarthrootArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.ManaArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.MercenaryFear;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Unit extends Actor {
    private static final float DEBUG_TEXT_SIZE = 1.2f;
    private static final float COLLISION_WIDTH_RATIO = 0.9f;
    private static final float CAST_ATTACK_DURATION_SECONDS = 0.5f;

    protected float regenerationRate = 1f;
    protected float manaRegenerationRate = 1f;
    private float attackAnimationSpeedOverride = -1f;
    private transient float attackCycleRemaining;
    private transient int presentationPlacementVersion;
    private boolean committedDeathNotified;
    private boolean repayingDeferredDamage;
    private int repaidDeferredDamage;
    protected float incomingDamageModifier = 1f, outgoingDamageModifier = 1f, speedModifier = 1f, attackSpeedModifier = 1f;
    protected float accuracyMultiplier = 1f, evasionMultiplier = 1f;

    protected float lastTileX = 0;
    protected float lastTileY = 0;
    public boolean isFriendly, showOnly, spawnSpace, isHero, isSummoned;
    public GameFilm gf;
    public float x;
    public float y, floorY;
    public float speedX = 500, momentX = 0, speedY = 0, jumpSpeed = 800, attackSpeed = 10f, airMomentumX = 0f;
    float frameAt, frameAlpha = 1, frameSpawning;

    public boolean movingLeft, movingRight, facingRight = true;

    protected boolean canFly = false;
    protected boolean invisible = false;
    protected boolean levitating = false;

    protected int[] spawnFrames = null;
    protected int[] idleFrames = null;
    protected int[] runFrames = null;
    protected int[] dieFrames = null;
    protected int[] attackFrames = null;
    protected int[] jumpFrames = null;

    protected UnitState unitState;
    protected MeleeWeapon weapon;
    protected RangedWeapon rangedWeapon;
    protected Armor armor;

    protected boolean rangedAttack;
    private boolean spellCastAnimation;

    protected int hp = 1, mhp = 1, mp = 1, mmp = 1;
    protected int attackSkill = 1, defenseSkill = 0, damageReduction = 0;
    protected float regeneration, regenerationMana;

    protected float stepSound;
    private boolean wasMovingOnWater;
    private int lastWaterMoveDirection;
    private int lastWaterSplashTileX = Integer.MIN_VALUE;
    private int lastWaterSplashTileY = Integer.MIN_VALUE;
    private int availableAirJumps;

    protected float soundLevel = 0f;

    protected String persistentId;
    protected String room;

    protected ArrayList<Buff> buffs = new ArrayList<>();

    protected GameSprite greenGS, redGS;
    protected transient Unit lastDamageSource;
    protected transient Weapon lastDamagingItem;

    public Unit(){
        unitState = UnitState.IDLE;
        greenGS = new GameSprite("images/misc/green.png", ConstantsHelper.UNIT_DIMENSIONS, 5f);
        redGS = new GameSprite("images/misc/red.png", ConstantsHelper.UNIT_DIMENSIONS, 5f);
        persistentId = RandomHelper.getInstance().uniqueId();
    }

    @Override
    public void draw(Batch batch, float alpha){
        if(gf == null || room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())){
            return;
        }

        boolean drawWeapon = false;
        float attackDrawFrame = frameAt;

        if(unitState == UnitState.SPAWNING && spawnFrames != null && spawnFrames.length > 0){
            frameAt = (frameAt) % spawnFrames.length;
            gf.tileX = spawnFrames[(int)frameAt];
        }

        if(unitState == UnitState.IDLE && idleFrames != null && idleFrames.length > 0){
            frameAt = (frameAt) % idleFrames.length;
            gf.tileX = idleFrames[(int)frameAt];
        }

        if(unitState == UnitState.RUNNING && runFrames != null && runFrames.length > 0){
            frameAt = (frameAt) % runFrames.length;
            gf.tileX = runFrames[(int)frameAt];
        }

        if(unitState == UnitState.ATTACKING && attackFrames != null && attackFrames.length > 0){
            frameAt = (frameAt) % attackFrames.length;
            attackDrawFrame = Math.min(attackFrames.length - 0.001f,
                    getAttackDrawPhase(frameAt / attackFrames.length) * attackFrames.length);
            gf.tileX = attackFrames[(int)attackDrawFrame];
            drawWeapon = (!rangedAttack && weapon != null && weapon.showsAttackAnimation())
                    || (rangedAttack && rangedWeapon != null && rangedWeapon.showsAttackAnimation());
        }

        if (unitState != UnitState.ATTACKING && hasMeleeRecoveryPose() && attackFrames != null && attackFrames.length > 0) {
            attackDrawFrame = Math.min(attackFrames.length - 0.001f, getAttackDrawPhase(0f) * attackFrames.length);
            drawWeapon = weapon != null && weapon.showsAttackAnimation();
        }

        if(unitState == UnitState.JUMPING && jumpFrames != null && jumpFrames.length > 0){
            frameAt = (frameAt) % jumpFrames.length;
            gf.tileX = jumpFrames[(int)frameAt];
        }

        if(unitState == UnitState.DEAD && dieFrames != null && dieFrames.length > 0){
            gf.tileX = dieFrames[Math.min((int)frameAt, dieFrames.length - 1)];
        }

        float drawAlpha = Math.min(frameAlpha, frameSpawning);
        if (isHero && invisible) {
            drawAlpha *= 0.5f;
        }

        gf.setAlpha(drawAlpha);
        gf.setPosition(getRenderX(), getRenderY());
        gf.faceRight(facingRight);
        ContactShadow.draw(batch, getRenderX() + ConstantsHelper.UNIT_DIMENSIONS / 2f, getRenderY(),
                ConstantsHelper.UNIT_DIMENSIONS * gf.clipSizeX / gf.clipSizeY * 0.85f,
                drawAlpha * alpha, canFly || levitating);
        drawBodyFilm(batch);

        if(drawWeapon && !spellCastAnimation){
            if(!rangedAttack && weapon != null){
                weapon.draw(batch, attackDrawFrame, attackFrames.length);
            }

            if(rangedAttack && rangedWeapon != null){
                rangedWeapon.draw(batch, attackDrawFrame, attackFrames.length);
            }
        }

        drawHP(batch);
        drawBuffs(batch);

        if (PhysicsHelper.getInstance().isDebugEnabled()) {
            drawDebugInfo(batch);
        }
    }

    protected void drawBodyFilm(Batch batch) {
        gf.draw(batch);
    }

    protected float getAttackDrawPhase(float normalPhase) {
        return normalPhase;
    }

    protected boolean hasMeleeRecoveryPose() { return false; }

    protected boolean canLeaveAttackForMovement() { return false; }


    public float getVisualAttackX(float phase) {
        float left = gf == null ? getRenderX() : gf.getVisualLeft();
        float width = gf == null ? ConstantsHelper.UNIT_DIMENSIONS : gf.getVisualWidth();
        return left + width * (facingRight ? phase : 1f / 3f - phase);
    }

    public float getVisualAttackY() {
        return gf == null ? getRenderY() + ConstantsHelper.UNIT_DIMENSIONS / 4f
                : gf.getVisualBottom() + gf.getVisualHeight() / 4f;
    }

    public void drawHP(Batch batch){
        if(unitState != UnitState.DEAD && hp < mhp){
            greenGS.setPosition(getRenderX(), getRenderY() + ConstantsHelper.UNIT_DIMENSIONS);
            greenGS.draw(batch);
            int width = (int) (ConstantsHelper.UNIT_DIMENSIONS * (mhp - hp) / mhp);
            redGS.setWidth(width);
            redGS.setPosition(getRenderX() + ConstantsHelper.UNIT_DIMENSIONS - width, getRenderY() + ConstantsHelper.UNIT_DIMENSIONS);
            redGS.draw(batch);
        }
    }

    public void drawBuffs(Batch batch){
        if(unitState != UnitState.DEAD && buffs.size() > 0){
            int at = (int)getRenderX() +  (int) ConstantsHelper.UNIT_DIMENSIONS / 2 - (buffs.size() % 2 == 0 ? 20 * buffs.size() / 2 : 10 + (20 * (buffs.size() - 1) / 2));
            for(Buff buff : buffs){
                buff.getGameSprite().setPosition(at, getRenderY() + ConstantsHelper.UNIT_DIMENSIONS + 5);
                buff.getGameSprite().draw(batch);

                at += 20;
            }
        }
    }

    private void drawDebugInfo(Batch batch) {
        BitmapFont debugFont = FontHelper.getSingleton().getFont(Color.WHITE, DEBUG_TEXT_SIZE);
        GlyphLayout glyphLayout = new GlyphLayout();
        float lineHeight = Math.max(14f, debugFont.getCapHeight() + 4f);
        boolean physicsControlled = PhysicsHelper.getInstance().hasBody(this) && !showOnly();
        int debugLineCount = isHero ? 4 : 3;
        float topY = y + ConstantsHelper.UNIT_DIMENSIONS + lineHeight * debugLineCount + (buffs.isEmpty() ? 6f : 24f);
        float velocityX = physicsControlled
                ? PhysicsHelper.toPixelSpeed(PhysicsHelper.getInstance().getHorizontalSpeed(this))
                : getDebugHorizontalSpeed();

        drawCenteredDebugLine(batch, debugFont, glyphLayout, topY,
                "state: " + unitState.name());
        drawCenteredDebugLine(batch, debugFont, glyphLayout, topY - lineHeight,
                "xy: " + formatDebugValue(x) + ", " + formatDebugValue(y));
        drawCenteredDebugLine(batch, debugFont, glyphLayout, topY - lineHeight * 2f,
                "spd: " + formatDebugValue(velocityX) + ", " + formatDebugValue(speedY)
                        + " mom: " + formatDebugValue(momentX)
                        + " air: " + formatDebugValue(PhysicsHelper.toPixelSpeed(airMomentumX)));

        if (isHero) {
            drawCenteredDebugLine(batch, debugFont, glyphLayout, topY - lineHeight * 3f,
                "block: " + (physicsControlled
                    ? PhysicsHelper.getInstance().describeHorizontalMovementBlockers(this)
                    : "none"));
        }
    }

    private void drawCenteredDebugLine(Batch batch, BitmapFont font, GlyphLayout glyphLayout, float drawY, String text) {
        glyphLayout.setText(font, text);
        float drawX = x + ConstantsHelper.UNIT_DIMENSIONS / 2f - glyphLayout.width / 2f;
        FontHelper.getSingleton().write(Color.WHITE, batch, DEBUG_TEXT_SIZE, drawX, drawY, text);
    }

    private float getDebugHorizontalSpeed() {
        float velocityX = 0f;
        if (movingLeft) {
            velocityX -= getSpeedX();
        }
        if (movingRight) {
            velocityX += getSpeedX();
        }
        return velocityX + momentX;
    }

    private String formatDebugValue(float value) {
        float rounded = Math.round(value * 10f) / 10f;
        if (Math.abs(rounded) < 0.05f) {
            return "0";
        }

        int integerValue = Math.round(rounded);
        if (Math.abs(rounded - integerValue) < 0.05f) {
            return Integer.toString(integerValue);
        }

        return Float.toString(rounded);
    }

    @Override
    public void act(float delta){

        if(unitState != UnitState.DEAD){
            checkBuffs(delta);
            regenerate(delta);
        }

        if(room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())){
            return;
        }

        attackCycleRemaining = Math.max(0f, attackCycleRemaining - PhysicsHelper.boundGameDelta(delta));
        if (gf != null) gf.updateImpactFlash(delta);
        boolean physicsControlled = PhysicsHelper.getInstance().hasBody(this) && !showOnly();
        if (physicsControlled) {
            PhysicsHelper.getInstance().syncUnitFromPhysics(this);
            floorY = PhysicsHelper.getInstance().getFloorY(this);
        }

        if(!physicsControlled && tileChange()){
            floorY = MapHelper.getInstance().calculateFloorY(this);
        }


        if(momentX != 0){
            momentX -= 0.8f * momentX * delta;
            if(Math.abs(momentX) < 50){
                momentX = 0;
            }
        }

        if (unitState == UnitState.ATTACKING && canLeaveAttackForMovement()) {

            changeState(UnitState.IDLE, true);
        }
        boolean attackMovementLocked = unitState == UnitState.ATTACKING;
        boolean standingOnGround = false;

        if(physicsControlled){
            boolean grounded = PhysicsHelper.getInstance().isGrounded(this);
            standingOnGround = grounded;
            refreshAirJumps(grounded);
            if (grounded && !attackMovementLocked) {
                if (movingLeft == movingRight) {
                    airMomentumX = 0f;
                } else if (movingLeft) {
                    airMomentumX = -PhysicsHelper.toWorldSpeed(getSpeedX());
                } else {
                    airMomentumX = PhysicsHelper.toWorldSpeed(getSpeedX());
                }
            }

            if(y > MapHelper.getInstance().getHeight() * ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS){
                y = MapHelper.getInstance().getHeight() * ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS;
                PhysicsHelper.getInstance().syncBodyToUnit(this);
                PhysicsHelper.getInstance().setVerticalSpeed(this, 0f);
                speedY = 0f;
            }

            if((movingLeft || movingRight || momentX != 0) && !attackMovementLocked){
                changeState(UnitState.RUNNING);
            }

            PhysicsHelper.getInstance().applyMovement(this, grounded, !attackMovementLocked, delta);

            if(grounded){
                speedY = 0f;
                if(unitState == UnitState.JUMPING){
                    changeState(UnitState.IDLE);
                    playSound(Sounds.STEP, 1f);
                }
            }
            else if (!attackMovementLocked) {
                changeState(UnitState.JUMPING);
            }

            if(grounded && Math.abs(momentX) < 1f && !movingRight && !movingLeft && !attackMovementLocked){
                changeState(UnitState.IDLE);
            }
        }
        else {

            if(speedY != 0 || y != floorY){
                y += speedY * delta;

                if(!canFly || showOnly){
                    speedY -= ConstantsHelper.GRAVITY * getGravityMultiplier() * delta;
                }

                if(!attackMovementLocked){
                    changeState(UnitState.JUMPING);
                }
            }


            if(y < floorY && (!canFly || showOnly)){
                y = floorY;
                speedY = 0;
                playSound(Sounds.STEP, 1f);
                if(unitState == UnitState.JUMPING){
                    changeState(UnitState.IDLE);
                }
            }


            if(x < 1){
                movingLeft = false;
                x = 2;
            }

            if(x > MapHelper.getInstance().getWidth() * ConstantsHelper.TILE - 1 - ConstantsHelper.UNIT_DIMENSIONS){
                movingRight = false;
                x = MapHelper.getInstance().getWidth() * ConstantsHelper.TILE - 2 - ConstantsHelper.UNIT_DIMENSIONS;
            }

            if(y > MapHelper.getInstance().getHeight() * ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS){
                y = MapHelper.getInstance().getHeight() * ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS;
                speedY = 0;
            }


            if(movingLeft || movingRight || momentX != 0){
                if(!attackMovementLocked){
                    changeState(UnitState.RUNNING);
                }
                if(!attackMovementLocked && (unitState == UnitState.RUNNING || y != floorY)){
                    float candidateX = x + (movingLeft ? -1 : 0) * delta * getSpeedX() + (movingRight ? 1 : 0) * delta * getSpeedX();

                    if(showOnly || UnitHelper.getInstance().freeSpace(this, (int)candidateX, (int)y, room)){
                        x = Math.min(Math.max(1, candidateX),
                                MapHelper.getInstance().getWidth() * ConstantsHelper.TILE - 1 - ConstantsHelper.UNIT_DIMENSIONS);
                    }
                }

                if(momentX != 0){
                    float candidateX = x + momentX * delta;
                    if(showOnly || UnitHelper.getInstance().freeSpace(this, (int)candidateX, (int)y, room)){
                        x = Math.min(Math.max(1, candidateX),
                                MapHelper.getInstance().getWidth() * ConstantsHelper.TILE - 1 - ConstantsHelper.UNIT_DIMENSIONS);
                    }
                }
            }

            if(speedY == 0 && y == floorY && !movingRight && !movingLeft && !attackMovementLocked){
                changeState(UnitState.IDLE);
            }

            standingOnGround = speedY == 0 && y == floorY;
            refreshAirJumps(standingOnGround);
        }

        clampToRoomBounds(physicsControlled);
        updateWaterEffects(delta, standingOnGround);

        if(unitState == UnitState.SPAWNING && spawnFrames != null && spawnFrames.length > 0){
            frameAt = (frameAt + delta * 1f) % spawnFrames.length;
        }

        if(unitState == UnitState.IDLE && idleFrames != null && idleFrames.length > 0){
            frameAt = (frameAt + delta * 1f) % idleFrames.length;
        }

        if(unitState == UnitState.RUNNING && runFrames != null && runFrames.length > 0){
            frameAt = (frameAt + delta * 10f) % runFrames.length;
            stepSound(delta);
        }

        if(unitState == UnitState.ATTACKING && attackFrames != null && attackFrames.length > 0){
            frameAt = (frameAt + delta * getAttackAnimationSpeed());
            if(frameAt >= attackFrames.length){
                changeState(UnitState.IDLE, true);
            }
        }

        if(unitState == UnitState.JUMPING && jumpFrames != null && jumpFrames.length > 0){
            frameAt = (frameAt + delta * 1f) % jumpFrames.length;
        }

        if(unitState == UnitState.DEAD && dieFrames != null && dieFrames.length > 0){
            frameAt = Math.min(dieFrames.length, frameAt + delta * 7f);
            if(frameAt >= dieFrames.length){
                frameAlpha = Math.max(0, frameAlpha - 1f * delta);
                if(frameAlpha == 0f){
                    removeUnit();
                }
            }
        }


        frameSpawning += 4f * delta;
    }

    private void clampToRoomBounds(boolean physicsControlled) {
        if (isHero || room == null) {
            return;
        }

        Room currentRoom = MapHelper.getInstance().getRoom(room);
        if (currentRoom == null) {
            return;
        }

        float minX = 1f;
        float maxX = Math.max(minX, currentRoom.getWidth() * ConstantsHelper.TILE - 1f - ConstantsHelper.UNIT_DIMENSIONS);
        float clampedX = Math.min(Math.max(minX, x), maxX);
        if (Math.abs(clampedX - x) < 0.01f) {
            return;
        }

        if (clampedX <= minX + 0.01f) {
            movingLeft = false;
        }

        if (clampedX >= maxX - 0.01f) {
            movingRight = false;
        }

        x = clampedX;
        momentX = 0f;
        airMomentumX = 0f;

        if (physicsControlled) {
            PhysicsHelper.getInstance().syncBodyToUnit(this);
        }
    }

    private void updateWaterEffects(float delta, boolean standingOnGround) {
        if (showOnly || canFly) {
            wasMovingOnWater = false;
            lastWaterMoveDirection = 0;
            resetWaterSplashTile();
            return;
        }

        int moveDirection = movingLeft == movingRight ? 0 : (movingRight ? 1 : -1);
        int carriedDirection = moveDirection;
        if (carriedDirection == 0) {
            if (Math.abs(momentX) >= 20f) {
                carriedDirection = momentX > 0f ? 1 : -1;
            }
            else if (Math.abs(airMomentumX) >= 0.01f) {
                carriedDirection = airMomentumX > 0f ? 1 : -1;
            }
        }

        if (!standingOnGround) {
            if (carriedDirection != 0) {
                lastWaterMoveDirection = carriedDirection;
            }
            wasMovingOnWater = false;
            resetWaterSplashTile();
            return;
        }

        if (!MapHelper.getInstance().isStandingOnWater(this)) {
            wasMovingOnWater = false;
            lastWaterMoveDirection = 0;
            resetWaterSplashTile();
            return;
        }

        if (moveDirection != 0) {
            lastWaterMoveDirection = moveDirection;
            wasMovingOnWater = true;
            splashCurrentWaterTile();
            return;
        }

        if (!wasMovingOnWater && lastWaterMoveDirection == 0) {
            return;
        }

        float slipSpeed = Math.max(60f, getSpeedX() * 0.18f);
        if (Math.abs(momentX) < slipSpeed) {
            momentX += lastWaterMoveDirection * slipSpeed;
        }

        EffectsHelper.getInstance().waterSplash(this);
        wasMovingOnWater = false;
        lastWaterMoveDirection = 0;
        resetWaterSplashTile();
    }

    private void splashCurrentWaterTile() {
        int tileX = (int) ((x + ConstantsHelper.UNIT_DIMENSIONS / 2f) / ConstantsHelper.TILE);
        int tileY = Math.max(0, (int) (floorY / ConstantsHelper.TILE) - 1);
        if (tileX == lastWaterSplashTileX && tileY == lastWaterSplashTileY) {
            return;
        }

        EffectsHelper.getInstance().waterSplash(this);
        lastWaterSplashTileX = tileX;
        lastWaterSplashTileY = tileY;
    }

    private void resetWaterSplashTile() {
        lastWaterSplashTileX = Integer.MIN_VALUE;
        lastWaterSplashTileY = Integer.MIN_VALUE;
    }

    public void changeState(UnitState unitState){
        changeState(unitState, false);
    }

    public void changeState(UnitState unitState, boolean forced){

        if(unitState != UnitState.ATTACKING){
            attackAnimationSpeedOverride = -1f;
        }

        if(unitState != UnitState.RUNNING){
            stepSound = 0f;
        }


        if(this.unitState == UnitState.SPAWNING && !forced){
            if(frameSpawning < 1f){
                return;
            }
        }


        if(forced){
            this.unitState = unitState;
            frameAt = 0;
            if (unitState == UnitState.DEAD || unitState == UnitState.SPAWNING) {
                attackCycleRemaining = 0f;
                PhysicsHelper.getInstance().resetRenderTransform(this);
            }
            return;
        }


        if(this.unitState == UnitState.DEAD){
            return;
        }


        if(unitState == UnitState.IDLE && this.unitState != UnitState.ATTACKING && this.unitState != UnitState.IDLE){
            frameAt = 0;
            this.unitState = unitState;
            return;
        }


        if(unitState == UnitState.JUMPING && this.unitState == UnitState.RUNNING){
            frameAt = 0;
            this.unitState = unitState;
            return;
        }


        if(this.unitState == UnitState.IDLE && unitState != UnitState.IDLE){
            frameAt = 0;
            this.unitState = unitState;
            return;
        }
    }

    public void jump(){
        tryJump();
    }

    protected boolean tryJump() {
        boolean physicsControlled = PhysicsHelper.getInstance().hasBody(this) && !showOnly();
        boolean grounded = physicsControlled ? PhysicsHelper.getInstance().isGrounded(this) : y == floorY;
        boolean groundJump = canUseGroundJump(grounded);
        boolean canUseAirJump = !grounded && !groundJump && availableAirJumps > 0;
        boolean jumpAllowed = groundJump || canUseAirJump;
        if(jumpAllowed){
            if (canUseAirJump) {
                availableAirJumps--;
            }
            changeState(UnitState.JUMPING);
            if (physicsControlled) {
                airMomentumX = PhysicsHelper.getInstance().getHorizontalSpeed(this) - PhysicsHelper.toWorldSpeed(momentX);
                PhysicsHelper.getInstance().jump(this, jumpSpeed);
            }
            else {
                if (movingLeft == movingRight) {
                    airMomentumX = 0f;
                } else if (movingLeft) {
                    airMomentumX = -PhysicsHelper.toWorldSpeed(getSpeedX());
                } else {
                    airMomentumX = PhysicsHelper.toWorldSpeed(getSpeedX());
                }
                speedY = jumpSpeed;
            }
            playSound(Sounds.MISS, 0.2f);
            onJumpStarted(groundJump);
            return true;
        }
        return false;
    }

    protected boolean canUseGroundJump(boolean grounded) {
        return unitState.canJump() && grounded;
    }

    protected void onJumpStarted(boolean groundJump) { }

    public boolean canJumpNow() {
        boolean physicsControlled = PhysicsHelper.getInstance().hasBody(this) && !showOnly();
        boolean grounded = physicsControlled ? PhysicsHelper.getInstance().isGrounded(this) : y == floorY;
        boolean canUseAirJump = !grounded && availableAirJumps > 0;
        return canUseGroundJump(grounded) || canUseAirJump;
    }

    public void fly(boolean up, boolean stop){
        if(canFly){
            boolean physicsControlled = PhysicsHelper.getInstance().hasBody(this) && !showOnly();
            changeState(UnitState.JUMPING);
            if(!stop){
                if (physicsControlled) {
                    PhysicsHelper.getInstance().setVerticalSpeed(this, up ? jumpSpeed : -jumpSpeed);
                }
                speedY = up ? jumpSpeed : -jumpSpeed;
            }
            else {
                if (physicsControlled) {
                    PhysicsHelper.getInstance().setVerticalSpeed(this, 0f);
                }
                speedY = 0f;
            }
        }
    }

    public void fakeAttack(){
        startAttackAnimation(CAST_ATTACK_DURATION_SECONDS);
    }

    public void attack(boolean forced){
        if(canAttack() || forced){

            float duration = forced && attackAnimationSpeedOverride > 0f
                    ? getAttackFrameCount() / attackAnimationSpeedOverride
                    : getAttackAnimationDurationSeconds(false);
            rangedAttack = false;
            spellCastAnimation = false;
            frameAt = 0f;
            changeState(UnitState.ATTACKING, true);
            if (isHero) {
                attackAnimationSpeedOverride = getAttackFrameCount() / Math.max(0.01f, duration);
                reserveAttackCycle(duration);
            }
            beginMeleeContact(forced, duration);

            if(isCanFly()){
                fly(false, true);
            }
        }
    }

    protected void beginMeleeContact(boolean forced, float durationSeconds) {
        applyMeleeContact(weapon);
    }

    protected void applyMeleeContact(Weapon attackingWeapon) {
        boolean hitTarget = UnitHelper.getInstance().attack(this, attackingWeapon);
        playSound(hitTarget ? Sounds.HIT : Sounds.MISS, 0.4f);
    }

    public void attack(){
        if(weapon instanceof Wand){
            useWand();
        } else{
            attack(false);
        }
    }

    public void useWand(){
        Wand wand = (Wand) weapon;
        if(!canAttack() || !wand.canCast()){
            EffectsHelper.getInstance().message(this, "I can't", Color.RED, 0);
            return;
        }
        float cooldownDuration = getAttackAnimationDurationSeconds();
        wand.use();
        wand.startCooldown(cooldownDuration);
        startAttackAnimation(Wand.CAST_ATTACK_DURATION_SECONDS);
    }

    public void rangedAttack(){
        if(canAttack() && rangedWeapon != null){
            float duration = getAttackAnimationDurationSeconds(true);
            rangedAttack = true;
            spellCastAnimation = false;
            frameAt = 0f;
            changeState(UnitState.ATTACKING, true);
            if (isHero) {
                attackAnimationSpeedOverride = getAttackFrameCount() / Math.max(0.01f, duration);
                reserveAttackCycle(duration);
            }
            rangedWeapon.createProjectile();
            if(isCanFly()){
                fly(false, true);
            }
        }
    }

    public void setWeapon(MeleeWeapon weapon){
        weapon.setOwner(this);
        if(this instanceof Hero){
            UIHelper.getInstance().equiped(weapon);
        }
        this.weapon = weapon;
    }

    public void setRangedWeapon(RangedWeapon weapon){
        if(weapon != null){
            weapon.setOwner(this);
        }

        if(this instanceof Hero){
            UIHelper.getInstance().equiped(weapon);
        }

        this.rangedWeapon = weapon;
    }

    public RangedWeapon getRangedWeapon(){
        return this.rangedWeapon;
    }

    public boolean isRangedAttacking() {
        return rangedAttack;
    }

    public void setArmor(Armor armor){
        armor.setOwner(this);

        this.armor = armor;
    }

    public Weapon getWeapon(){
        return this.weapon;
    }

    public Armor getArmor(){return this.armor; }

    public Rectangle getHitBox(){
        return getHitBoxAt(x, y);
    }

    public Rectangle getHitBoxAt(float x, float y) {
        return new Rectangle(x + (ConstantsHelper.UNIT_DIMENSIONS - getCollisionWidth()) / 2f,
                y,
                getCollisionWidth(),
                ConstantsHelper.UNIT_DIMENSIONS);
    }

    public float getCollisionWidth() {
        return ConstantsHelper.UNIT_DIMENSIONS * COLLISION_WIDTH_RATIO;
    }



    public int repayDeferredDamage(int amount) {
        if (amount < 1 || isDead() || hp < 1) return 0;
        boolean previousRepayment = repayingDeferredDamage;
        int previousPaid = repaidDeferredDamage;
        repayingDeferredDamage = true;
        repaidDeferredDamage = 0;
        try {
            takeDamage(this, null, amount);
            return repaidDeferredDamage;
        } finally {
            repayingDeferredDamage = previousRepayment;
            repaidDeferredDamage = previousPaid;
        }
    }

    public void takeDamage(Unit source, Weapon damagingItem, float damage){
        if (isDead() || hp < 1) {
            return;
        }

        lastDamageSource = source;
        lastDamagingItem = damagingItem;
        damage *= repayingDeferredDamage ? 1f : getIncomingDamageModifier();
        damage = Math.max(0f, damage - (repayingDeferredDamage ? 0 : rollDamageReduction(source, damagingItem)));

        ManaArmor manaArmor = repayingDeferredDamage ? null : (ManaArmor)getBuff(ManaArmor.class);
        if(manaArmor != null){
            damage = manaArmor.absorbDamage(damage);
            EffectsHelper.getInstance().manaShielded(this, source.x > x);
        }

        EarthrootArmor earthrootArmor = repayingDeferredDamage ? null : (EarthrootArmor)getBuff(EarthrootArmor.class);
        if(earthrootArmor != null){
            damage = earthrootArmor.absorbDamage(damage);
        }

        int appliedDamage = Math.max(0, Math.round(damage));
        if (source instanceof Hero && damagingItem instanceof Gun) {
            appliedDamage = ((Hero)source).adjustExecuteDamage(this, appliedDamage);
        }

        hp -= appliedDamage;
        if (repayingDeferredDamage) repaidDeferredDamage += appliedDamage;
        if(damagingItem != null && appliedDamage > 0){
            momentX += source.x < x ? damagingItem.getKnockBack() : -damagingItem.getKnockBack();
        }

        if(appliedDamage > 0){
            EffectsHelper.getInstance().blood(source, this, appliedDamage);
        }
        EffectsHelper.getInstance().hitResult(source, this, appliedDamage);

        if (appliedDamage > 0 && this instanceof Hero && source != null && source != this && !source.isDead()) {
            int reflectedDamage = RingOfThorns.getReflectedDamage(appliedDamage);
            if (reflectedDamage > 0) {
                source.takeDamage(this, null, reflectedDamage);
            }
        }

        if (!repayingDeferredDamage && appliedDamage > 0 && armor != null) {
            Prefix armorPrefix = armor.getPrefix();
            if (armorPrefix != null) {
                armorPrefix.onDefend(this, source, damagingItem, appliedDamage);
            }
        }

        if(hp > 0){
           return;
        }

        if (preventDeath(source, damagingItem, appliedDamage)) {
            return;
        }

        if(movingRight){
            momentX += getSpeedX();
        }

        if(movingLeft){
            momentX -= getSpeedX();
        }

        movingLeft = false;
        movingRight = false;

        changeState(UnitState.DEAD, true);

        notifyDeathCommitted();
        die();
    }

    public void heal(int hp){
        hp = Math.min(hp, mhp - this.hp);
        this.hp += hp;
    }

    public int getStrength() {
        return 0;
    }

    public boolean usesEquipmentStrengthRequirements() {
        return false;
    }

    public boolean showOnly(){
        return unitState.showOnly() || showOnly;
    }


    public boolean isDroppingThroughPlatform() { return false; }

    public boolean isDead() {
        return unitState == UnitState.DEAD;
    }

    public boolean spawnSpace(){
        return spawnSpace;
    }

    protected boolean tileChange(){
        int tileX = (int) (x / ConstantsHelper.TILE * 2);
        int tileY = (int) (y / ConstantsHelper.TILE * 2);

        if(lastTileX != tileX || lastTileY != tileY) {
            lastTileX = tileX;
            lastTileY = tileY;

            return true;
        }

        return false;
    }

    public void appear(float x, float y){
        this.x = x;
        this.y = y;
        if (gf != null) gf.clearImpactFlash();
        PhysicsHelper.getInstance().syncBodyToUnit(this);
        presentationPlacementVersion++;
    }


    public float getRenderX() { return PhysicsHelper.getInstance().getRenderX(this); }
    public float getRenderY() { return PhysicsHelper.getInstance().getRenderY(this); }


    public int getPresentationPlacementVersion() {
        return presentationPlacementVersion;
    }


    public int getRunFootstepPhase() {
        return unitState == UnitState.RUNNING ? (int) (frameAt / (runFrames.length / 2f)) : -1;
    }

    public int getHP(){
        return hp;
    }

    public Unit getLastDamageSource() {
        return lastDamageSource;
    }

    public Weapon getLastDamagingItem() {
        return lastDamagingItem;
    }

    public void setHP(int hp) {
        this.hp = Math.max(0, Math.min(mhp, hp));
    }

    public int getMaxHP(){
        return mhp;
    }

    public void setMaxHP(int mhp){
        this.mhp = Math.max(1, mhp);
    }

    protected void stepSound(float delta){
        stepSound -= 10f * delta;
        if(stepSound < 0f){
            calculateSoundLevel();
            playSound(Sounds.STEP, 1f);
            stepSound = 2f;
        }
    }

    protected void calculateSoundLevel(){
        float distance = (float) UtilsHelper.distance(this, UnitHelper.getInstance().getHero());
        if(distance > 1000){
            soundLevel = 0f;
        }
        else {
            soundLevel = 0.1f + (1000 - distance) / 2000;
        }
    }

    protected void playSound(Sounds sounds, float modifier){
        calculateSoundLevel();
        float volume = Math.min(1, soundLevel * modifier);
        if (sounds == Sounds.STEP) {


            SoundHelper.GetSingleton().playLegacyFootstep(volume,
                    !isHero || showOnly() || !PhysicsHelper.getInstance().hasBody(this));
        } else {
            SoundHelper.GetSingleton().play(sounds, 0f, volume);
        }
    }

    public void setRoom(String room){
        if (this.room == null || !this.room.equals(room)) {
            NecromancerCurse.clear(this);
            PhysicsHelper.getInstance().resetRenderTransform(this);
        }
        this.room = room;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public String getRoom(){
        return room;
    }

    protected void removeUnit(){
        UnitHelper.getInstance().removeUnit(this);
    }

    protected boolean preventDeath(Unit source, Weapon damagingItem, float damage) {
        return false;
    }


    protected final void notifyDeathCommitted() {
        if (committedDeathNotified || !isDead() || hp > 0) return;
        committedDeathNotified = true;
        NecromancerCurse.clear(this);
        MercenaryFear.clear(this);
        onDeathCommitted();
    }

    protected void onDeathCommitted() {
    }

    public boolean isCanFly(){
        return canFly;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean isLevitating() {
        return levitating;
    }

    public void setLevitating(boolean levitating) {
        this.levitating = levitating;
        if (!levitating) {
            availableAirJumps = 0;
        }
        else if (canGrantAirJump()) {
            availableAirJumps = Math.max(availableAirJumps, getMaxAirJumps());
        }
    }

    protected boolean canGrantAirJump() { return true; }

    protected float getGravityMultiplier() {
        return levitating && !canFly ? 0.5f : 1f;
    }

    private int getMaxAirJumps() {
        return levitating && !canFly ? 1 : 0;
    }

    protected void refreshAirJumps(boolean grounded) {
        if (grounded || canFly) {
            availableAirJumps = getMaxAirJumps();
            return;
        }

        availableAirJumps = Math.min(availableAirJumps, getMaxAirJumps());
    }

    protected void die(){

    }

    public void setSpeedX(float speedX){
        this.speedX = speedX;
    }

    public float getSpeedX(){
        float totalSpeed = speedX * speedModifier;
        if (armor != null && usesEquipmentStrengthRequirements()) {
            totalSpeed *= armor.getStrengthMoveSpeedMultiplier();
        }
        return totalSpeed;
    }

    public float getAttackSpeed(){
        Weapon activeWeapon = resolveAttackWeapon(rangedAttack);
        float weaponModifier = activeWeapon == null ? 1f : activeWeapon.getSpeed() * activeWeapon.getStrengthAttackSpeedMultiplier();
        return weaponModifier * attackSpeed * attackSpeedModifier;
    }

    protected float getAttackAnimationDurationSeconds(boolean useRangedAttack) {
        Weapon activeWeapon = resolveAttackWeapon(useRangedAttack);
        float weaponModifier = activeWeapon == null ? 1f : activeWeapon.getSpeed() * activeWeapon.getStrengthAttackSpeedMultiplier();
        float resolvedAttackSpeed = weaponModifier * attackSpeed * attackSpeedModifier;
        return getAttackFrameCount() / Math.max(0.01f, resolvedAttackSpeed);
    }

    public int getAttackSkill(Unit target, Weapon attackingItem) {
        Weapon activeWeapon = attackingItem != null ? attackingItem : (rangedAttack && rangedWeapon != null ? rangedWeapon : weapon);
        float weaponModifier = activeWeapon == null ? 1f : activeWeapon.getStrengthAccuracyMultiplier();
        return Math.max(0, Math.round(attackSkill * getAccuracyMultiplier() * weaponModifier));
    }

    public int getDefenseSkill(Unit attacker) {
        return Math.max(0, Math.round(defenseSkill * getEvasionMultiplier()));
    }

    public int getDamageReduction() {
        int totalReduction = Math.max(0, damageReduction);
        if (armor != null) {
            totalReduction += armor.getProtection();
        }
        return Math.max(0, totalReduction);
    }

    public int rollDamageReduction() {
        int maxReduction = getDamageReduction();
        return maxReduction < 1 ? 0 : RandomHelper.getInstance().randomInt(maxReduction + 1);
    }

    protected int rollDamageReduction(Unit source, Weapon damagingItem) {
        if (source instanceof Hero && ((Hero) source).ignoresDamageReduction(damagingItem)) {
            return 0;
        }

        return rollDamageReduction();
    }

    public float getAttackAnimationDurationSeconds(){
        return getAttackAnimationDurationSeconds(rangedAttack);
    }

    public boolean isAttacking() {
        return unitState == UnitState.ATTACKING;
    }

    public float getBaseAttackSpeed(){
        return attackSpeed;
    }

    public void startAttackAnimation(float durationSeconds){
        beginAttackAnimation(durationSeconds, false);
    }

    public void startRangedAttackAnimation(float durationSeconds) {
        beginAttackAnimation(durationSeconds, true);
    }


    public void startSpellCastAnimation(float durationSeconds) {
        startAttackAnimation(durationSeconds);
        spellCastAnimation = true;
    }

    private void beginAttackAnimation(float durationSeconds, boolean useRangedAttack) {
        spellCastAnimation = false;
        rangedAttack = useRangedAttack;
        frameAt = 0f;
        attackAnimationSpeedOverride = getAttackFrameCount() / Math.max(0.01f, durationSeconds);
        changeState(UnitState.ATTACKING, true);
        if (isHero) reserveAttackCycle(durationSeconds);
    }


    private void reserveAttackCycle(float durationSeconds) {
        attackCycleRemaining = Math.max(attackCycleRemaining, Math.max(0.01f, durationSeconds));
    }

    public float getAttackCycleRemainingSeconds() {
        return attackCycleRemaining;
    }

    private Weapon resolveAttackWeapon(boolean useRangedAttack) {
        return useRangedAttack && rangedWeapon != null ? rangedWeapon : weapon;
    }

    private float getAttackAnimationSpeed(){
        return attackAnimationSpeedOverride > 0f ? attackAnimationSpeedOverride : getAttackSpeed();
    }

    private int getAttackFrameCount(){
        return attackFrames != null && attackFrames.length > 0 ? attackFrames.length : 1;
    }

    public boolean addBuff(Buff buff){
        for(Buff buff1 : buffs){
            if(buff1.getName().equals(buff.getName())){
                return false;
            }
        }

        buffs.add(buff);
        return true;
    }

    public ArrayList<Buff> getBuffs(){
        return buffs;
    }

    public Buff getBuff(Class<? extends Buff> buffClass){
        for(Buff buff : buffs){
            if(buff.getClass() == buffClass){
                return  buff;
            }
        }

        return null;
    }

    public void checkBuffs(float delta){
        ArrayList<Buff> buffsNew = new ArrayList<>();

        for(Buff buff : buffs){
            buff.act(delta);
            if(buff.active()){
                buffsNew.add(buff);
            } else{
                buff.debuff();
            }
        }

        buffs = buffsNew;
    }

    public boolean getFacingRight(){
        return facingRight;
    }

    public void removeBuff(Buff buff){
        for(Buff buff1 : buffs){
            if(buff1.getName().equals(buff.getName())){
                buff1.setDuration(-1);
                buff1.setPermanent(false);
                return;
            }
        }
    }

    public void modifyRegenerationRate(float modification){
        this.regenerationRate += modification;
    }

    public void modifyAccuracyMultiplier(float modification) {
        this.accuracyMultiplier += modification;
    }

    public void modifyEvasionMultiplier(float modification) {
        this.evasionMultiplier += modification;
    }

    public float getAccuracyMultiplier() {
        return Math.max(0.1f, accuracyMultiplier);
    }

    public float getEvasionMultiplier() {
        return Math.max(0.1f, evasionMultiplier);
    }

    public void modifyIncomingDamageModifier(float modification){
        this.incomingDamageModifier += modification;
    }

    public void modifyOutgoingDamageModifier(float modification){
        this.outgoingDamageModifier += modification;
    }

    public void modifySpeedModifier(float modification){
        this.speedModifier += modification;
    }

    public float getOutgoingDamageModifier(){
        return (float) Math.max(0.1, outgoingDamageModifier);
    }

    public float getIncomingDamageModifier(){
        return (float) Math.max(0.1, incomingDamageModifier);
    }

    public int getBaseAttackSkill() {
        return attackSkill;
    }

    public int getBaseDefenseSkill() {
        return defenseSkill;
    }

    public void setBaseAttackSkill(int attackSkill) {
        this.attackSkill = Math.max(0, attackSkill);
    }

    public void setBaseDefenseSkill(int defenseSkill) {
        this.defenseSkill = Math.max(0, defenseSkill);
    }

    public void setBaseDamageReduction(int damageReduction) {
        this.damageReduction = Math.max(0, damageReduction);
    }

    public boolean canAttack(){
        if (!unitState.canAttack() || attackCycleRemaining > 0f) {
            return false;
        }

        for (Buff buff : buffs) {
            if (buff.preventsAttacks()) {
                return false;
            }
        }

        return true;
    }

    public void modifyAttackSpeedModifier(float modification){
        this.attackSpeedModifier += modification;
    }

    public void unSummon(){
        if(!isSummoned || unitState == UnitState.DEAD){
            return;
        }

        hp = 0;
        changeState(UnitState.DEAD, true);
    }

    public int getMp(){
        return mp;
    }

    public void setMp(int mp) {
        this.mp = Math.max(0, Math.min(mmp, mp));
    }

    public int getMmp(){
        return mmp;
    }

    public void setMaxMP(int mmp){
        this.mmp = Math.max(1, mmp);
    }

    public void modifyMana(int mp){
        if(mp > 0){
            mp = Math.min(mp, mmp - this.mp);
        }

        this.mp += mp;
    }




    public void modifyManaRegenerationRate(float modification){
        this.manaRegenerationRate += modification;
    }



    protected void regenerate(float delta){

        regeneration += mhp * delta * regenerationRate * (this instanceof Mob && !isFriendly ? 0.5f : 1f);
        regenerationMana += mmp * delta * manaRegenerationRate;

        if(regeneration > 100f && hp < mhp){
            hp += 1;
            regeneration = 0f;
        }

        if(regenerationMana > 100f && mp < mmp){
            mp += 1;
            regenerationMana = 0f;
        }
    }
}
