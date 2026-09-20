package com.bilboldev.skillfulpixeldungeonplatformer.units.hero;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.QuestManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SkillsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.Meat;
import com.bilboldev.skillfulpixeldungeonplatformer.items.quest.Pickaxe;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfHerbalism;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.Ring;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.Wand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.ArrowItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Bow;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassSkillTree;
import com.bilboldev.skillfulpixeldungeonplatformer.items.TomeOfMastery;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.AirbornePose;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Cripple;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Poisoned;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Starving;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.NewClassSpellProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.NecromancerCurse;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.MercenaryFear;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ActiveSkill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.NewClassActiveSkill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.BuffSkill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.SpikeTrap;

import java.util.ArrayList;
import java.util.HashMap;

public class Hero extends Unit {
    public static final float BASE_JUMP_SPEED = 840f;
    public static final int QUICK_SKILL_SLOT_COUNT = 7;
    private static final float LEDGE_GRACE_SECONDS = 0.1f;
    private static final float JUMP_BUFFER_SECONDS = 0.12f;
    private static final float JUMP_RELEASE_SPEED_RATIO = 0.5f;
    private transient float ledgeGraceRemaining;
    private transient float jumpBufferRemaining;
    private transient boolean jumpBufferFresh;
    private transient boolean jumpHeld;
    private transient boolean playerJumpRising;
    private transient boolean levitationAirJumpSpent;
    private static final float MELEE_CONTACT_CYCLE_FRACTION = 0.12f;
    private static final float MELEE_MOVEMENT_CYCLE_FRACTION = 0.35f;
    private transient Weapon pendingMeleeWeapon;
    private transient String pendingMeleeRoom;
    private transient float ordinaryMeleeDuration;
    private transient Weapon ordinaryMeleeWeapon;
    private static final float COMBAT_BUFFER_SECONDS = 0.12f;
    private static final int PRIMARY_ACTION = 1, RANGED_ACTION = 2, FIRST_SKILL_ACTION = 3;
    private transient int pendingCombatAction;
    private transient float combatBufferRemaining;
    private transient boolean combatBufferFresh;
    private transient Weapon queuedMeleeWeapon, queuedRangedWeapon;
    private transient ActiveSkill queuedSkill;
    private transient String queuedCombatRoom;
    private static final float EAT_ICON_DURATION_SECONDS = 0.85f;
    private static final float EAT_ICON_SIZE = 24f;
    private static final float EAT_ICON_RISE = 14f;
    private static final float EAT_ICON_BOB_DISTANCE = 3f;
    private static final float FLETCHING_INTERVAL_SECONDS = 10f;
    private static final float HUNTING_INTERVAL_SECONDS = 60f;
    private static final int MAX_FLETCHED_ARROWS_PER_FLOOR = 10;
    private static final float GLADIATOR_COMBO_OPEN_SECONDS = 1.1f;
    private static final float GLADIATOR_COMBO_BASE_EXTENSION_SECONDS = 1.41f;
    private static final float GLADIATOR_COMBO_EXTENSION_STEP_SECONDS = 0.1f;
    private static final float GLADIATOR_COMBO_MIN_EXTENSION_SECONDS = 0.5f;
    private static final int GLADIATOR_COMBO_DAMAGE_START_COUNT = 3;
    private static final int MASTERY_COMBO_THRESHOLD = 7;
    private static final float GLADIATOR_COMBO_DAMAGE_SCALE = 5f;
    private static final float BERSERKER_FURY_THRESHOLD = 0.4f;
    private static final float BERSERKER_FURY_MAX_BONUS = 0.6f;
    private static final int BATTLEMAGE_MAX_WAND_MELEE_BONUS = 5;
    private static final int WARLOCK_SOUL_HUNGER_RESTORE = 10;
    private static final float STARVATION_DAMAGE_INTERVAL_SECONDS = 10f;
    private static final float STARVATION_DAMAGE = 1f;
    private static final float CURSED_PARTICLE_INTERVAL_SECONDS = 1.8f;
    private static final int KNEE_SHOT_CRIPPLE_CHANCE = 25;
    private static final float BOMBVOYAGE_SPLASH_DAMAGE_FACTOR = 0.75f;
    private static final float BOMBVOYAGE_SPLASH_RADIUS = ConstantsHelper.TILE * 2f;
    private static final float BOMBVOYAGE_BURST_PARTICLE_SIZE = 12f;
    private static final int BOMBVOYAGE_BURST_PARTICLE_COUNT = 20;
    private static final float BOMBVOYAGE_BURST_SPREAD = 48f;
    private static final float BOMBVOYAGE_BURST_RISE = 34f;
    private static final float BOMBVOYAGE_BURST_LIFESPAN = 52f;
    private static final float BOMBVOYAGE_BURST_GRAVITY = 0.02f;
    private static final float BOMBVOYAGE_SMOKE_PARTICLE_SIZE = 10f;
    private static final int BOMBVOYAGE_SMOKE_PARTICLE_COUNT = 14;
    private static final float BOMBVOYAGE_SMOKE_SPREAD = 30f;
    private static final float BOMBVOYAGE_SMOKE_RISE = 24f;
    private static final float BOMBVOYAGE_SMOKE_LIFESPAN = 58f;
    private static final float BOMBVOYAGE_SMOKE_GRAVITY = -0.015f;
    private static final float BOMBVOYAGE_FIRE_PARTICLE_SIZE = 8f;
    private static final int BOMBVOYAGE_FIRE_PARTICLE_COUNT = 10;
    private static final float BOMBVOYAGE_FIRE_SPREAD = 46f;
    private static final float BOMBVOYAGE_FIRE_RISE = 72f;
    private static final float BOMBVOYAGE_FIRE_LIFESPAN = 44f;
    private static final float BOMBVOYAGE_FIRE_GRAVITY = 0.14f;
    private static final int ROGUE_GOLD_PICKUP_MULTIPLIER = 2;
    private static final int ROGUE_VENOM_ATTACKS_PER_CAST = 3;
    private static final float ROGUE_VENOM_DAMAGE_MULTIPLIER = 1.2f;
    private static final int ROGUE_VENOM_POISON_CHANCE = 33;
    private static final float ROGUE_VENOM_POISON_DURATION = 4f;
    private static final float ROGUE_FREE_RUNNER_EVASION_MULTIPLIER = 2f;
    private static final int ROGUE_SILENT_DEATH_CHANCE = 25;

    protected float hunger = 100f;
    protected boolean hungerNotified = false;
    protected boolean deathHandled = false;
    private boolean masterOfDeathUsed;
    private transient boolean masterOfDeathRecovering;
    protected int experience = 0;
    protected int level = 1;
    protected int bonusStrength = 0;
    protected int attackSkillFloor = 0;
    protected float hungerRateModifier = 1f;
    protected float wandPowerModifier = 1f;
    protected int gladiatorComboCount;
    protected float gladiatorComboTimer;
    protected float cursedParticleTimer;
    protected float starvationDamageTimer;
    protected float eatIconTimer;
    protected boolean starvationDamagePending;

    protected  HeroClass heroClass;

    protected int skillPoints;

    protected float lastX, lastY;
    protected float fletchingTimer;
    protected float huntingTimer;
    protected int venomAttacksRemaining;
    protected HashMap<Integer, Integer> fletchedArrowsPerDepth = new HashMap<>();
    protected HashMap<Integer, Integer> activeSkillUsageCounts = new HashMap<>();
    private transient NewClassActionState newClassActions;
    private transient boolean marshalHealthApplied;
    private transient int lastRequestedQuickSkillSlot = -1;

    protected transient GameSprite eatIcon;

    protected ArrayList<Integer> unlockedSkills = new ArrayList<>();

    protected ActiveSkill[] activeSkills = new ActiveSkill[QUICK_SKILL_SLOT_COUNT];
    private final AirbornePose airbornePose = new AirbornePose();

    public Hero() {

    }

    public Hero(HeroClass heroClass) {
        applyHeroClass(heroClass);
    }

    {
        isHero = true;
        jumpSpeed = BASE_JUMP_SPEED;
        applyHeroClass(HeroClass.WARRIOR);

        skillPoints = 1;
    }

    protected final void applyHeroClass(HeroClass heroClass) {
        this.heroClass = heroClass == null ? HeroClass.WARRIOR : heroClass;
        hp = mhp = this.heroClass.getHealth(Math.max(1, level));
        mp = mmp = this.heroClass.getMana(Math.max(1, level));
        isFriendly = true;
        gf = this.heroClass == HeroClass.NECROMANCER || this.heroClass == HeroClass.MERCENARY
                ? NewClassAssets.heroFilm(this.heroClass.getFilm())
                : new GameFilm(this.heroClass.getFilm(),256, 128, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 15;
        gf.yClipOffset = 1;
        idleFrames = new int[]{0, 1, 1};
        runFrames = new int[]{2, 3, 4, 5, 6, 7};
        attackFrames = new int[]{13, 14, 15, 15};
        dieFrames = new int[]{8, 9, 10, 11, 12};
        jumpFrames =  new int[]{4};

        attackSpeed = this.heroClass.getAttackSpeed();
        speedX = this.heroClass.getMoveSpeed();
        refreshCombatStats();
    }

    public void updateAirbornePose(float delta) {
        airbornePose.update(this, delta);
    }


    public void updateJumpSupport(float delta) {
        if (showOnly() || !PhysicsHelper.getInstance().hasBody(this)) {
            ledgeGraceRemaining = 0f;
        } else if (PhysicsHelper.getInstance().isGrounded(this)) {
            ledgeGraceRemaining = LEDGE_GRACE_SECONDS;
            playerJumpRising = false;
            levitationAirJumpSpent = false;

            refreshAirJumps(true);
        } else {
            ledgeGraceRemaining = Math.max(0f, ledgeGraceRemaining - PhysicsHelper.boundGameDelta(delta));
        }
        if (speedY <= 0f) playerJumpRising = false;

        if (jumpBufferFresh) jumpBufferFresh = false;
        else jumpBufferRemaining = Math.max(0f, jumpBufferRemaining - PhysicsHelper.boundGameDelta(delta));
        updateCombatBuffer(delta, tryBufferedJump());
    }

    public void pressJump() {
        if (jumpHeld || showOnly()) return;
        jumpHeld = true;
        jumpBufferRemaining = JUMP_BUFFER_SECONDS;
        jumpBufferFresh = true;
        tryBufferedJump();
    }

    public void releaseJump() {
        if (jumpHeld) cutReleasedJump();
        jumpHeld = false;
    }

    private void cutReleasedJump() {
        if (!playerJumpRising) return;
        playerJumpRising = false;
        float releaseSpeed = jumpSpeed * JUMP_RELEASE_SPEED_RATIO;
        if (speedY > releaseSpeed) {
            if (PhysicsHelper.getInstance().hasBody(this)) PhysicsHelper.getInstance().setVerticalSpeed(this, releaseSpeed);
            else speedY = releaseSpeed;
        }
    }


    public void clearControlIntent() {
        ledgeGraceRemaining = 0f;
        jumpBufferRemaining = 0f;
        jumpBufferFresh = false;
        jumpHeld = false;
        playerJumpRising = false;
        pendingMeleeWeapon = null;
        pendingMeleeRoom = null;
        ordinaryMeleeDuration = 0f;
        ordinaryMeleeWeapon = null;
        clearCombatBuffer();
    }

    public void requestPrimaryAction() { requestCombatAction(PRIMARY_ACTION); }

    public void requestRangedAction() { requestCombatAction(RANGED_ACTION); }

    public void requestQuickSkill(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < QUICK_SKILL_SLOT_COUNT) {
            lastRequestedQuickSkillSlot = slotIndex;
            requestCombatAction(FIRST_SKILL_ACTION + slotIndex);
        }
    }

    public int getLastRequestedQuickSkillSlot() { return lastRequestedQuickSkillSlot; }

    private void requestCombatAction(int action) {
        clearCombatBuffer();
        if (showOnly() || WindowHelper.getInstance().windowOpen()) return;
        if (canExecuteCombatAction(action)) {
            executeCombatAction(action);
            return;
        }
        float remaining = getCombatWaitSeconds(action);
        if (remaining <= 0f || remaining > COMBAT_BUFFER_SECONDS) return;
        if (action == PRIMARY_ACTION && weapon == null || action == RANGED_ACTION && rangedWeapon == null
                || action >= FIRST_SKILL_ACTION && getQuickSkill(action - FIRST_SKILL_ACTION) == null) return;
        pendingCombatAction = action;
        combatBufferRemaining = COMBAT_BUFFER_SECONDS;
        combatBufferFresh = true;
        queuedMeleeWeapon = weapon;
        queuedRangedWeapon = rangedWeapon;
        queuedSkill = action >= FIRST_SKILL_ACTION ? getQuickSkill(action - FIRST_SKILL_ACTION) : null;
        queuedCombatRoom = getRoom();
    }

    private float getCombatWaitSeconds(int action) {
        float remaining = getAttackCycleRemainingSeconds();
        if (action == PRIMARY_ACTION && weapon instanceof Wand) {
            remaining = Math.max(remaining, ((Wand) weapon).getCooldownRemainingSeconds());
        } else if (action >= FIRST_SKILL_ACTION) {
            ActiveSkill skill = getQuickSkill(action - FIRST_SKILL_ACTION);
            if (skill != null) remaining = Math.max(remaining, skill.getCooldownRemainingSeconds());
        }
        return remaining;
    }

    private boolean canExecuteCombatAction(int action) {
        if (!canAttack()) return false;
        if (action == PRIMARY_ACTION) return weapon != null && (!(weapon instanceof Wand) || ((Wand) weapon).canCast());
        if (action == RANGED_ACTION) return rangedWeapon instanceof Gun
                ? ((Gun)rangedWeapon).canAttemptShot(this) : rangedWeapon != null && rangedWeapon.getAmmo() > 0;
        ActiveSkill skill = getQuickSkill(action - FIRST_SKILL_ACTION);
        return skill != null && skill.canUse(this);
    }

    private void executeCombatAction(int action) {

        if (action == PRIMARY_ACTION) attack();
        else if (action == RANGED_ACTION) rangedAttack();
        else useQuickSkillSlot(action - FIRST_SKILL_ACTION);
    }

    private void updateCombatBuffer(float delta, boolean jumped) {
        if (pendingCombatAction == 0) return;
        if (combatBufferFresh) combatBufferFresh = false;
        else combatBufferRemaining = Math.max(0f, combatBufferRemaining - PhysicsHelper.boundGameDelta(delta));
        boolean equipmentChanged = pendingCombatAction == PRIMARY_ACTION ? weapon != queuedMeleeWeapon
                : pendingCombatAction == RANGED_ACTION ? rangedWeapon != queuedRangedWeapon
                : weapon != queuedMeleeWeapon || rangedWeapon != queuedRangedWeapon
                || getQuickSkill(pendingCombatAction - FIRST_SKILL_ACTION) != queuedSkill;
        if (combatBufferRemaining <= 0f || showOnly() || WindowHelper.getInstance().windowOpen() || equipmentChanged
                || queuedCombatRoom == null || !queuedCombatRoom.equals(getRoom())
                || !queuedCombatRoom.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            clearCombatBuffer();
            return;
        }
        if (jumped || !canExecuteCombatAction(pendingCombatAction)) return;
        int action = pendingCombatAction;
        clearCombatBuffer();
        executeCombatAction(action);
    }

    private void clearCombatBuffer() {
        pendingCombatAction = 0;
        combatBufferRemaining = 0f;
        combatBufferFresh = false;
        queuedMeleeWeapon = null;
        queuedRangedWeapon = null;
        queuedSkill = null;
        queuedCombatRoom = null;
    }

    @Override
    public boolean canJumpNow() {
        return !showOnly() && super.canJumpNow();
    }

    @Override
    public void changeState(UnitState state, boolean forced) {
        super.changeState(state, forced);
        if (forced && (state == UnitState.DEAD || state == UnitState.SPAWNING)) clearControlIntent();
    }

    private boolean tryBufferedJump() {
        if (jumpBufferRemaining <= 0f || showOnly() || !canJumpNow()) return false;
        if (!tryJump()) return false;
        jumpBufferRemaining = 0f;
        jumpBufferFresh = false;
        playerJumpRising = true;
        if (!jumpHeld) cutReleasedJump();
        return true;
    }

    @Override
    protected boolean canUseGroundJump(boolean grounded) {
        return !showOnly() && !isAttacking() && (grounded || ledgeGraceRemaining > 0f);
    }

    @Override
    protected void onJumpStarted(boolean groundJump) {
        ledgeGraceRemaining = 0f;
        playerJumpRising = false;
        if (!groundJump) levitationAirJumpSpent = true;
    }

    @Override
    protected boolean canGrantAirJump() { return !levitationAirJumpSpent; }

    @Override
    public void appear(float x, float y) {
        NewClassSpellProjectile.clearFor(this);
        GunProjectile.clearFor(this);
        clearControlIntent();
        super.appear(x, y);
    }

    @Override
    public void setRoom(String room) {
        if (getRoom() == null || !getRoom().equals(room)) {
            NecromancerCurse.clearOutside(room);
            MercenaryFear.clearOutside(room);
            NewClassSpellProjectile.clearFor(this);
            GunProjectile.clearFor(this);
            clearControlIntent();
        }
        super.setRoom(room);
    }

    @Override
    protected void drawBodyFilm(Batch batch) {
        float compression = airbornePose.compressionFor(this);
        gf.drawFrame(batch, airbornePose.frameFor(this, gf.tileX), 1f + compression / 2f, 1f - compression);
    }

    protected void refreshCombatStats() {
        setBaseAttackSkill(heroClass.getBaseAttackSkill() + Math.max(0, level - 1));
        setBaseDefenseSkill(heroClass.getBaseDefenseSkill() + Math.max(0, level - 1));
    }

    @Override
    public void act(float delta){
        super.act(delta);
        if (newClassActions != null && !isDead() && !showOnly() && getRoom() != null
                && getRoom().equals(MapHelper.getInstance().getActiveRoomIdentifier())
                && !WindowHelper.getInstance().windowOpen()) newClassActions.tick(delta);
        updateMeleeContact();

        if (eatIconTimer > 0f) {
            eatIconTimer = Math.max(0f, eatIconTimer - delta);
            if (eatIconTimer == 0f) {
                eatIcon = null;
            }
        }

        updateGladiatorCombo(delta);

        updateEquippedRings(delta);
        updateCursedEquipmentEffects(delta);
        updateHuntressPassives(delta);


        hunger -= 1f * delta * getHungerRateModifier();
        updateStarvationState(delta);

        if(lastX != x || lastY != y){
            lastX = x;
            lastY = y;
            MapHelper.getInstance().refreshHeroEnvironment();
        }

        if(armor != null){
            gf.tileY = armor.getTier() - 1;
        }
    }

    private void updateEquippedRings(float delta) {
        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (item instanceof Ring && ((Ring) item).getEquipped()) {
                ((Ring) item).progressIdentification(delta);
                ((Ring) item).actWhileEquipped(delta);
            }
        }
    }

    private void updateCursedEquipmentEffects(float delta) {
        int cursedItemCount = countEquippedCursedItems();
        if (cursedItemCount < 1) {
            cursedParticleTimer = 0f;
            return;
        }

        cursedParticleTimer += delta;
        while (cursedParticleTimer >= CURSED_PARTICLE_INTERVAL_SECONDS) {
            cursedParticleTimer -= CURSED_PARTICLE_INTERVAL_SECONDS;
            for (int particleIndex = 0; particleIndex < Math.min(3, cursedItemCount); particleIndex++) {
                EffectsHelper.getInstance().blackSpark(this);
            }
        }
    }

    private int countEquippedCursedItems() {
        int cursedItemCount = 0;

        if (getArmor() != null
                && getArmor().getPrefix() instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.Cursed) {
            cursedItemCount++;
        }

        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (!(item instanceof Ring)) {
                continue;
            }

            Ring ring = (Ring) item;
            if (ring.getEquipped()
                    && ring.getPrefix() instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.rings.Cursed) {
                cursedItemCount++;
            }
        }

        return cursedItemCount;
    }

    private void updateHuntressPassives(float delta) {
        int currentDepth = MapHelper.getInstance().getDepth();
        if (currentDepth < 1) {
            return;
        }

        if (hasSkill(Skills.FLETCHING)) {
            fletchingTimer += delta;
            float fletchingInterval = hasSkill(Skills.WARDEN) ? FLETCHING_INTERVAL_SECONDS / 2f : FLETCHING_INTERVAL_SECONDS;
            while (fletchingTimer >= fletchingInterval) {
                if (getFletchedArrowsOnDepth(currentDepth) >= MAX_FLETCHED_ARROWS_PER_FLOOR) {
                    fletchingTimer = fletchingInterval;
                    break;
                }

                ArrowItem arrowItem = new ArrowItem();
                if (!InventoryHelper.getInstance().canAddItem(arrowItem)) {
                    fletchingTimer = fletchingInterval;
                    break;
                }

                InventoryHelper.getInstance().addItem(arrowItem);
                fletchedArrowsPerDepth.put(currentDepth, getFletchedArrowsOnDepth(currentDepth) + 1);
                EffectsHelper.getInstance().message(this, "Fletched arrow", Color.GREEN, 0f);
                fletchingTimer -= fletchingInterval;
            }
        } else {
            fletchingTimer = 0f;
        }

        if (hasSkill(Skills.HUNTING)) {
            huntingTimer += delta;
            while (huntingTimer >= HUNTING_INTERVAL_SECONDS) {
                Meat meat = new Meat();
                if (!InventoryHelper.getInstance().canAddItem(meat)) {
                    huntingTimer = HUNTING_INTERVAL_SECONDS;
                    break;
                }

                InventoryHelper.getInstance().addItem(meat);
                huntingTimer -= HUNTING_INTERVAL_SECONDS;
            }
        } else {
            huntingTimer = 0f;
        }
    }

    private int getFletchedArrowsOnDepth(int depth) {
        Integer count = fletchedArrowsPerDepth.get(depth);
        return count == null ? 0 : count;
    }

    public float getFletchingTimer() {
        return fletchingTimer;
    }

    public float getHuntingTimer() {
        return huntingTimer;
    }

    public HashMap<Integer, Integer> getFletchedArrowsPerDepth() {
        return new HashMap<>(fletchedArrowsPerDepth);
    }

    public void restoreHuntressPassives(float fletching, float hunting, HashMap<Integer, Integer> arrowsPerDepth) {
        float fletchingInterval = hasSkill(Skills.WARDEN) ? FLETCHING_INTERVAL_SECONDS / 2f : FLETCHING_INTERVAL_SECONDS;
        fletchingTimer = Float.isNaN(fletching) ? 0f : Math.max(0f, Math.min(fletchingInterval, fletching));
        huntingTimer = Float.isNaN(hunting) ? 0f : Math.max(0f, Math.min(HUNTING_INTERVAL_SECONDS, hunting));
        fletchedArrowsPerDepth.clear();
        if (arrowsPerDepth != null) {
            for (Integer depth : arrowsPerDepth.keySet()) {
                Integer count = arrowsPerDepth.get(depth);
                if (depth != null && depth > 0 && count != null && count > 0) {
                    fletchedArrowsPerDepth.put(depth, Math.min(MAX_FLETCHED_ARROWS_PER_FLOOR, count));
                }
            }
        }
    }

    private void updateStarvationState(float delta) {
        if (hunger > 0f) {
            starvationDamageTimer = 0f;
            hungerNotified = false;
            removeBuff(new Starving());
            return;
        }

        if (!hungerNotified) {
            hungerNotified = true;
            EffectsHelper.getInstance().message(this, "Starving!", Color.RED, 0f);
        }

        if (getBuff(Starving.class) == null) {
            new Starving().setOwner(this);
        }

        starvationDamageTimer += delta;
        while (starvationDamageTimer >= STARVATION_DAMAGE_INTERVAL_SECONDS && !isDead()) {
            starvationDamageTimer -= STARVATION_DAMAGE_INTERVAL_SECONDS;
            starvationDamagePending = true;
            takeDamage(this, null, STARVATION_DAMAGE);
            starvationDamagePending = false;
        }
    }

    @Override
    public void takeDamage(Unit source, Weapon damagingItem, float damage) {

        if (this == UnitHelper.getInstance().getHero() && !isDead() && getHP() > 0
                && getMasterOfDeathRecoveryRemaining() > 0f) return;
        int hpBeforeDamage = getHP();

        if (!starvationDamagePending) {
            super.takeDamage(source, damagingItem, damage);
            if (getHP() < hpBeforeDamage) {
                resetGladiatorCombo();
            }
            return;
        }

        applyStarvationDamage(source, damagingItem, damage);
        if (getHP() < hpBeforeDamage) {
            resetGladiatorCombo();
        }
    }

    private void applyStarvationDamage(Unit source, Weapon damagingItem, float damage) {
        Unit damageSource = source == null ? this : source;
        int appliedDamage = Math.max(1, Math.round(Math.max(0f, damage)));

        lastDamageSource = damageSource;
        lastDamagingItem = damagingItem;
        hp -= appliedDamage;

        EffectsHelper.getInstance().blood(damageSource, this, appliedDamage);
        EffectsHelper.getInstance().message(this,
                Integer.toString(appliedDamage),
                Color.RED,
                damageSource.x > x ? -200f : 200f);

        if (hp > 0) {
            return;
        }

        if (preventDeath(damageSource, damagingItem, appliedDamage)) {
            return;
        }

        if (movingRight) {
            momentX += getSpeedX();
        }

        if (movingLeft) {
            momentX -= getSpeedX();
        }

        movingLeft = false;
        movingRight = false;

        changeState(UnitState.DEAD, true);
        notifyDeathCommitted();
        die();
    }

    private void updateGladiatorCombo(float delta) {
        if (gladiatorComboCount <= 0) {
            gladiatorComboTimer = 0f;
            return;
        }

        if (!hasSkill(Skills.GLADIATOR)) {
            resetGladiatorCombo();
            return;
        }

        gladiatorComboTimer = Math.max(0f, gladiatorComboTimer - delta);
        if (gladiatorComboTimer <= 0f) {
            resetGladiatorCombo();
        }
    }

    private void resetGladiatorCombo() {
        gladiatorComboCount = 0;
        gladiatorComboTimer = 0f;
    }

    @Override
    protected void removeUnit(){

    }

    public boolean earnExp(int experience){
        int totalExperience =   this.experience + experience;
        if(totalExperience >= nextExp()){
            this.experience = totalExperience - nextExp();
            level();
            SoundHelper.GetSingleton().play(Sounds.LEVEL_UP, 0.7f, 1f);
            UIHelper.getInstance().setExpString(expString());
            return true;
        }

        this.experience = totalExperience;

        UIHelper.getInstance().setExpString(expString());
        return false;
    }

    public String expString(){
        return  String.format("%,d / %,d", this.experience , this.nextExp());
    }

    public int getExperienceToNextLevel() {
        return Math.max(1, nextExp() - experience);
    }

    protected int nextExp(){
        return 10 + ((level - 1) * 20) + (level > 2 ? 30 * (int) Math.pow(1.1, level) : 0);
    }

    protected void level(){
        this.level++;
        this.skillPoints++;
        refreshCombatStats();
        int hpGain = heroClass.getHealth(this.level) - heroClass.getHealth(this.level - 1);
        if (heroClass == HeroClass.MERCENARY && marshalHealthApplied && hasSkill(Skills.MARSHAL)) {

            hpGain = Math.round(heroClass.getHealth(this.level) * 1.1f)
                    - Math.round(heroClass.getHealth(this.level - 1) * 1.1f);
        }
        this.mhp += hpGain;
        this.hp += hpGain;

        int mpGain = heroClass.getMana(this.level) - heroClass.getMana(this.level - 1);
        this.mmp += mpGain;
        this.mp += mpGain;

        for(Buff buff : buffs){
            buff.resetBuff();
        }


        EffectsHelper.getInstance().levelUp(this);
        EffectsHelper.getInstance().message(this, "LEVEL UP!", Color.GREEN, 0f);
        AchievementManager.getInstance().onLevelReached(level);
        RatKingHelper.getInstance().onHeroLevelUp(this);
    }

    public HeroClass getHeroClass(){
        return heroClass;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = Math.max(0, experience);
    }

    public int getLevel(){
        return level;
    }

    public int getStrength() {
        return heroClass.getBaseStrength()
                + Math.max(0, (level - 1) / 2)
                + bonusStrength
                + (hasSkill(Skills.TRAINING) ? 1 : 0);
    }

    public int getBonusStrength() {
        return bonusStrength;
    }

    public int getAttackSkillFloor() {
        return attackSkillFloor;
    }

    public void setAttackSkillFloor(int attackSkillFloor) {
        this.attackSkillFloor = Math.max(0, attackSkillFloor);
    }

    public void setBonusStrength(int bonusStrength) {
        this.bonusStrength = Math.max(0, bonusStrength);
    }

    public void modifyStrength(int amount) {
        setBonusStrength(bonusStrength + amount);
        AchievementManager.getInstance().onStrengthChanged(getStrength());
    }

    public void modifyHungerRateModifier(float modification) {
        hungerRateModifier += modification;
    }

    public float getHungerRateModifier() {
        return Math.max(0.1f, hungerRateModifier);
    }

    public void modifyWandPowerModifier(float modification) {
        wandPowerModifier += modification;
    }

    public float getWandPowerModifier() {
        return Math.max(0.1f, wandPowerModifier);
    }

    public void setLevelDirect(int level) {
        this.level = Math.max(1, level);
        refreshCombatStats();
    }

    @Override
    public int getDefenseSkill(Unit attacker) {
        float totalDefense = getBaseDefenseSkill() * getEvasionMultiplier();
        if (attacker != null && attacker.isRangedAttacking() && hasSkill(Skills.AWARENESS)) {
            totalDefense *= heroClass == HeroClass.ROGUE ? 1.2f : 1.25f;
        }
        if (hasSkill(Skills.FREE_RUNNER) && getBuff(Starving.class) == null) {
            totalDefense *= ROGUE_FREE_RUNNER_EVASION_MULTIPLIER;
        }
        if (armor != null) {
            int shortfall = armor.getStrengthShortfall();
            if (shortfall > 0) {
                totalDefense /= (float) Math.pow(1.5f, shortfall);
            }
        }
        return Math.max(0, Math.round(totalDefense));
    }

    @Override
    public int getAttackSkill(Unit target, Weapon attackingItem) {
        int totalAttackSkill = super.getAttackSkill(target, attackingItem);
        if (attackingItem instanceof Bow) {
            totalAttackSkill = Math.max(0, Math.round(totalAttackSkill * getBowAccuracyMultiplier(attackingItem)));
        }
        return Math.max(attackSkillFloor, totalAttackSkill);
    }

    @Override
    public void attack(){
        if (canAttack() && weapon instanceof Pickaxe && QuestManager.getInstance().tryMineWithEquippedPickaxe((Pickaxe) weapon)) {
            fakeAttack();
            if (isCanFly()) {
                fly(false, true);
            }
            return;
        }

        super.attack();
    }

    @Override
    protected void beginMeleeContact(boolean forced, float durationSeconds) {
        pendingMeleeWeapon = null;
        pendingMeleeRoom = null;
        ordinaryMeleeDuration = 0f;
        ordinaryMeleeWeapon = null;
        if (forced || weapon instanceof Wand) {
            super.beginMeleeContact(forced, durationSeconds);
            return;
        }

        pendingMeleeWeapon = weapon;
        pendingMeleeRoom = getRoom();
        ordinaryMeleeDuration = durationSeconds;
        ordinaryMeleeWeapon = weapon;
    }

    @Override
    protected boolean canLeaveAttackForMovement() {
        return hasMeleeRecoveryPose() && pendingMeleeWeapon == null
                && getAttackCycleRemainingSeconds() <= ordinaryMeleeDuration * (1f - MELEE_MOVEMENT_CYCLE_FRACTION);
    }

    @Override
    protected boolean hasMeleeRecoveryPose() {
        return !showOnly() && !rangedAttack && ordinaryMeleeDuration > 0f
                && weapon == ordinaryMeleeWeapon && getAttackCycleRemainingSeconds() > 0f;
    }

    @Override
    public void startAttackAnimation(float durationSeconds) {
        ordinaryMeleeDuration = 0f;
        pendingMeleeWeapon = null;
        ordinaryMeleeWeapon = null;
        pendingMeleeRoom = null;
        super.startAttackAnimation(durationSeconds);
    }

    @Override
    public void startRangedAttackAnimation(float durationSeconds) {
        ordinaryMeleeDuration = 0f;
        pendingMeleeWeapon = null;
        ordinaryMeleeWeapon = null;
        pendingMeleeRoom = null;
        super.startRangedAttackAnimation(durationSeconds);
    }

    private void updateMeleeContact() {
        if (pendingMeleeWeapon != null) {
            if (showOnly() || weapon != pendingMeleeWeapon || pendingMeleeRoom == null
                    || !pendingMeleeRoom.equals(getRoom())
                    || !pendingMeleeRoom.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
                pendingMeleeWeapon = null;
                pendingMeleeRoom = null;
                ordinaryMeleeDuration = 0f;
                ordinaryMeleeWeapon = null;
            } else if (getAttackCycleRemainingSeconds() <= ordinaryMeleeDuration * (1f - MELEE_CONTACT_CYCLE_FRACTION)) {
                Weapon contactWeapon = pendingMeleeWeapon;
                pendingMeleeWeapon = null;
                pendingMeleeRoom = null;
                applyMeleeContact(contactWeapon);
            }
        }
        if (getAttackCycleRemainingSeconds() <= 0f || weapon != ordinaryMeleeWeapon) {
            ordinaryMeleeDuration = 0f;
            ordinaryMeleeWeapon = null;
        }
    }

    @Override
    protected float getAttackDrawPhase(float normalPhase) {
        if (ordinaryMeleeDuration <= 0f || rangedAttack) return normalPhase;
        float phase = Math.max(0f, Math.min(1f, 1f - getAttackCycleRemainingSeconds() / ordinaryMeleeDuration));

        return phase <= MELEE_CONTACT_CYCLE_FRACTION ? 0.5f * phase / MELEE_CONTACT_CYCLE_FRACTION
                : 0.5f + 0.5f * (phase - MELEE_CONTACT_CYCLE_FRACTION) / (1f - MELEE_CONTACT_CYCLE_FRACTION);
    }

    @Override
    public void drawHP(Batch batch){

    }

    public void eat(){
        hunger = 100f;
        hungerNotified = false;
        removeBuff(new Starving());
        int bonusHealing = RingOfHerbalism.getFoodHealingBonus();
        int bonusMana = RingOfHerbalism.getFoodManaBonus();
        if (bonusHealing > 0) {
            heal(bonusHealing);
        }
        if (bonusMana > 0) {
            modifyMana(bonusMana);
        }
        AchievementManager.getInstance().onFoodEaten();
        RatKingHelper.getInstance().onHeroAte(this);
        showEatIcon();
    }

    @Override
    public void rangedAttack(){
        if (rangedWeapon instanceof Gun) {
            if (canAttack()) {
                float duration = getAttackAnimationDurationSeconds(true);
                if (((Gun)rangedWeapon).tryFire(this)) startRangedAttackAnimation(duration);
            }
            UIHelper.getInstance().updateRangedButton();
            return;
        }
        super.rangedAttack();

        UIHelper.getInstance().updateRangedButton();
    }

    @Override public void setRangedWeapon(RangedWeapon weapon) {
        if (weapon instanceof Gun && !Gun.supports(this)) return;
        super.setRangedWeapon(weapon);
    }



    public int getSkillPoints(){
        return skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = Math.max(0, skillPoints);
    }

    public float getHunger() {
        return hunger;
    }

    public void setHunger(float hunger) {
        this.hunger = Math.max(0f, hunger);
        hungerNotified = this.hunger <= 0f;
        if (this.hunger > 0f) {
            starvationDamageTimer = 0f;
        }
    }

    public void satisfyHunger(float amount) {
        if (amount <= 0f) {
            return;
        }

        setHunger(Math.min(100f, hunger + amount));
        if (hunger > 0f) {
            starvationDamageTimer = 0f;
            hungerNotified = false;
            removeBuff(new Starving());
        }
    }

    public ArrayList<Integer> getUnlockedSkills() {
        return new ArrayList<>(unlockedSkills);
    }

    public ActiveSkill getActiveSkill() {
        return getQuickSkill(0);
    }

    public ActiveSkill getActiveSkill2() {
        return getQuickSkill(1);
    }

    public ActiveSkill getActiveSkill3() {
        return getQuickSkill(2);
    }

    public ActiveSkill getActiveSkill4() {
        return getQuickSkill(3);
    }

    public ActiveSkill getActiveSkill5() {
        return getQuickSkill(4);
    }

    public ActiveSkill getActiveSkill6() {
        return getQuickSkill(5);
    }

    public ActiveSkill getActiveSkill7() {
        return getQuickSkill(6);
    }

    public ActiveSkill getQuickSkill(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= QUICK_SKILL_SLOT_COUNT) {
            return null;
        }

        return activeSkills[slotIndex];
    }

    public int getQuickSkillSlotCount() {
        if (SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()) {
            return 2;
        }

        return QUICK_SKILL_SLOT_COUNT;
    }

    public boolean hasSkill(int skill){
        return unlockedSkills.contains(skill);
    }

    public boolean skillLockedOut(int skill){
        for(int skill1 : unlockedSkills){
            Skill learned = SkillsHelper.getInstance().getSkill(skill1);
            if(NewClassSkillTree.opposite(skill1) == skill
                    || learned != null && learned.getLocksOut() == skill){
                return true;
            }
        }

        return false;
    }

    public void modifySkillPoints(int modification){
        this.skillPoints += modification;
    }

    public boolean learnSkill(Skill skill){
        return grantSkill(skill, false);
    }


    public boolean restoreSkill(Skill skill) {
        return grantSkill(skill, true);
    }

    public boolean learnMastery(Skill skill, TomeOfMastery tome) {
        if (tome == null || !tome.canGrantTo(this, skill) || !grantSkill(skill, true)) return false;
        InventoryHelper.getInstance().removeItem(tome);
        return true;
    }

    private boolean grantSkill(Skill skill, boolean allowNewMastery) {
        SkillsHelper helper = SkillsHelper.getInstance();
        if (!helper.isSupported(this, skill) || hasSkill(skill.getId()) || skillLockedOut(skill.getId())) return false;
        if (NewClassSkillTree.isNewClass(heroClass)) {
            if (!helper.meetsRequirements(this, skill)) return false;
            if (NewClassSkillTree.opposite(skill.getId()) != 0 && !allowNewMastery) return false;
        }
        this.unlockedSkills.add(skill.getId());
        skill.affect(this);
        return true;
    }

    public void assignQuickSkillIfNeeded(ActiveSkill activeSkill) {
        if (activeSkill == null || !canAssignClassSkill(activeSkill)) {
            return;
        }

        for (int slotIndex = 0; slotIndex < QUICK_SKILL_SLOT_COUNT; slotIndex++) {
            ActiveSkill assignedSkill = getQuickSkill(slotIndex);
            if (assignedSkill != null && assignedSkill.getId() == activeSkill.getId()) {
                return;
            }
        }

        int[] preferredSlots = preferredQuickSkillSlots(activeSkill instanceof BuffSkill);

        for (int slotIndex : preferredSlots) {
            if (getQuickSkill(slotIndex) == null) {
                setQuickSkill(slotIndex, activeSkill);
                return;
            }
        }

        setQuickSkill(preferredSlots[0], activeSkill);
    }

    private int[] preferredQuickSkillSlots(boolean buffSkill) {
        if (getQuickSkillSlotCount() <= 2) {
            return buffSkill ? new int[]{1, 0} : new int[]{0, 1};
        }

        return buffSkill
                ? new int[]{1, 2, 3, 4, 5, 6, 0}
                : new int[]{0, 2, 3, 4, 5, 6, 1};
    }

    public boolean neverMissesWithBow(Bow bow) {
        return bow != null && hasSkill(Skills.SNIPER);
    }

    public float getBowAccuracyMultiplier(Weapon attackingItem) {
        if (!(attackingItem instanceof Bow)) {
            return 1f;
        }

        float multiplier = 1f;
        if (hasSkill(Skills.ACCURACY)) {
            multiplier *= 1.25f;
        }
        return multiplier;
    }

    public float getBowDamageMultiplier(Weapon attackingItem) {
        if (!(attackingItem instanceof Bow)) {
            return 1f;
        }

        return 1f;
    }

    public int adjustGoldPickup(int amount) {
        if (amount <= 0) {
            return 0;
        }

        return hasSkill(Skills.BANDIT) ? amount * ROGUE_GOLD_PICKUP_MULTIPLIER : amount;
    }

    public boolean preventsGoldTheft() {
        return hasSkill(Skills.BANDIT);
    }

    public int getStealthScore() {
        int stealthScore = 0;
        if (hasSkill(Skills.STEALTH)) {
            stealthScore++;
        }
        if (hasSkill(Skills.ASSASSIN)) {
            stealthScore++;
        }
        return stealthScore;
    }

    public float getRogueDamageMultiplier(Weapon attackingItem) {
        float multiplier = 1f;
        if (venomAttacksRemaining > 0) {
            multiplier *= ROGUE_VENOM_DAMAGE_MULTIPLIER;
        }
        return multiplier;
    }

    public float adjustSuccessfulHitDamage(Unit target, Weapon attackingItem, float damage, boolean attackedFromInvisibility) {
        float adjustedDamage = damage * getRogueDamageMultiplier(attackingItem);

        if (hasSkill(Skills.ASSASSIN) && isAssassinSurpriseAttack(target, attackedFromInvisibility) && adjustedDamage > 0f) {
            adjustedDamage += 1 + RandomHelper.getInstance().randomInt(Math.max(1, Math.round(adjustedDamage)));
        }

        if (hasSkill(Skills.BATTLE_MAGE) && attackingItem instanceof Wand) {
            adjustedDamage += getBattleMageMeleeBonus((Wand) attackingItem);
        }

        if (hasSkill(Skills.GLADIATOR) && usesGladiatorCombo(attackingItem)) {
            adjustedDamage += registerGladiatorComboHit(adjustedDamage);
        }

        adjustedDamage *= getBerserkerDamageMultiplier();
        return adjustedDamage;
    }

    private boolean isAssassinSurpriseAttack(Unit target, boolean attackedFromInvisibility) {
        if (!(target instanceof Mob)) {
            return false;
        }

        return attackedFromInvisibility || ((Mob) target).isSleeping();
    }

    private int getBattleMageMeleeBonus(Wand wand) {
        if (wand == null) {
            return 0;
        }

        return Math.min(BATTLEMAGE_MAX_WAND_MELEE_BONUS, getMp() / Math.max(1, wand.getManaCost()));
    }

    private boolean usesGladiatorCombo(Weapon attackingItem) {
        return !rangedAttack && !(attackingItem instanceof Bow) && !(attackingItem instanceof Wand);
    }

    private float registerGladiatorComboHit(float damage) {
        gladiatorComboCount++;
        if (gladiatorComboCount >= GLADIATOR_COMBO_DAMAGE_START_COUNT) {
            gladiatorComboTimer = Math.max(
                    GLADIATOR_COMBO_MIN_EXTENSION_SECONDS,
                    GLADIATOR_COMBO_BASE_EXTENSION_SECONDS - gladiatorComboCount * GLADIATOR_COMBO_EXTENSION_STEP_SECONDS);
            AchievementManager.getInstance().onGladiatorCombo(gladiatorComboCount);
            EffectsHelper.getInstance().message(this, gladiatorComboCount + " hit combo!", Color.YELLOW, 0f);
            return damage * (gladiatorComboCount - 2f) / GLADIATOR_COMBO_DAMAGE_SCALE;
        }

        gladiatorComboTimer = GLADIATOR_COMBO_OPEN_SECONDS;
        return 0f;
    }

    public float getBerserkerDamageMultiplier() {
        if (!hasSkill(Skills.BERSERKER) || getHP() <= 0 || getMaxHP() <= 0) {
            return 1f;
        }

        float healthFraction = getHP() / (float) getMaxHP();
        if (healthFraction > BERSERKER_FURY_THRESHOLD) {
            return 1f;
        }

        float furyProgress = (BERSERKER_FURY_THRESHOLD - healthFraction) / BERSERKER_FURY_THRESHOLD;
        return 1f + furyProgress * BERSERKER_FURY_MAX_BONUS;
    }

    public boolean ignoresDamageReduction(Weapon attackingItem) {
        return hasSkill(Skills.SNIPER) && attackingItem instanceof Bow;
    }

    public void activateVenom() {
        venomAttacksRemaining = ROGUE_VENOM_ATTACKS_PER_CAST;
    }

    public void handleSuccessfulAttack(Unit target, Weapon attackingItem, int damageDealt) {
        if (target == null) {
            return;
        }

        if (hasSkill(Skills.BATTLE_MAGE) && attackingItem instanceof Wand) {
            modifyMana(1);
        }

        if (damageDealt <= 0) {
            return;
        }

        if (!target.isDead() && venomAttacksRemaining > 0 && RandomHelper.getInstance().randomChance(ROGUE_VENOM_POISON_CHANCE)) {
            Poisoned poisoned = (Poisoned) target.getBuff(Poisoned.class);
            if (poisoned != null) {
                poisoned.setDuration(Math.max(poisoned.getRemainingDuration(), ROGUE_VENOM_POISON_DURATION));
            } else {
                new Poisoned().setDuration(ROGUE_VENOM_POISON_DURATION).setOwner(target);
            }
        }

        if (venomAttacksRemaining > 0) {
            venomAttacksRemaining = Math.max(0, venomAttacksRemaining - 1);
        }
    }

    public void onEnemyKilled(Mob mob) {
        if (mob == null || !hasSkill(Skills.WARLOCK)) {
            return;
        }

        int healing = Math.min(getMaxHP() - getHP(), 1 + Math.max(0, MapHelper.getInstance().getDepth() - 1) / 5);
        if (healing > 0) {
            heal(healing);
            EffectsHelper.getInstance().heal(this);
        }

        satisfyHunger(WARLOCK_SOUL_HUNGER_RESTORE);
    }


    public void applyMarshalHealth() {
        if (marshalHealthApplied || heroClass != HeroClass.MERCENARY || !hasSkill(Skills.MARSHAL)
                || hasSkill(Skills.EXECUTIONER) || isDead() || getHP() <= 0) return;
        int previousMax = getMaxHP();
        float fraction = getHP() / (float)previousMax;
        marshalHealthApplied = true;
        setMaxHP(Math.round(previousMax * 1.1f));
        setHP(Math.max(1, Math.round(getMaxHP() * fraction)));
    }

    @Override public float getIncomingDamageModifier() {
        float modifier = super.getIncomingDamageModifier();
        if (heroClass == HeroClass.MERCENARY && hasSkill(Skills.MARSHAL) && hasSkill(Skills.I_AM_THE_LAW)
                && newClassActions != null && newClassActions.duration(Skills.I_AM_THE_LAW) > 0f) {
            modifier *= .75f;
        }
        return modifier;
    }


    public int adjustExecuteDamage(Unit target, int appliedDamage) {
        if (appliedDamage > 0 && this == UnitHelper.getInstance().getHero() && heroClass == HeroClass.MERCENARY
                && hasSkill(Skills.EXECUTE) && !isDead() && getHP() > 0 && target instanceof Mob
                && !((Mob)target).isBoss() && !target.isFriendly && !target.showOnly()
                && !target.isDead() && target.getHP() > 0 && (long)target.getHP() * 10 <= target.getMaxHP()) {
            return Math.max(appliedDamage, target.getHP());
        }
        return appliedDamage;
    }

    public boolean shouldInstantKill(Unit target, Weapon attackingItem) {
        if (!hasSkill(Skills.SILENT_DEATH) || !(target instanceof Mob)) {
            return false;
        }

        Mob mob = (Mob) target;
        if (mob.isBoss() || !mob.isSleeping()) {
            return false;
        }

        if (rangedAttack || attackingItem instanceof Bow || attackingItem instanceof Wand) {
            return false;
        }

        boolean triggered = RandomHelper.getInstance().randomChance(ROGUE_SILENT_DEATH_CHANCE);
        if (triggered) {
            EffectsHelper.getInstance().message(this, "Nighty night", Color.WHITE, 0f);
        }
        return triggered;
    }

    public boolean handleBowProjectileImpact(Unit target, Weapon attackingItem, float damage, boolean hit, ThrownProjectile projectile) {
        if (!hit || !(attackingItem instanceof Bow) || target == null) {
            return false;
        }

        if (projectile.causesKneeShot()) {
            applyKneeShot(target);
        }

        if (projectile.causesBombvoyageSplash()) {
            applyBombvoyageSplash(target, attackingItem, damage, projectile);
        }

        return projectile.piercesTargets();
    }

    private void applyKneeShot(Unit target) {
        if (!RandomHelper.getInstance().randomChance(KNEE_SHOT_CRIPPLE_CHANCE)) {
            return;
        }

        Cripple cripple = (Cripple) target.getBuff(Cripple.class);
        if (cripple != null) {
            cripple.setPermanent(false).setDuration(Cripple.DURATION);
            return;
        }

        new Cripple().setPermanent(false).setDuration(Cripple.DURATION).setOwner(target);
    }

    private void applyBombvoyageSplash(Unit primaryTarget, Weapon attackingItem, float damage, ThrownProjectile projectile) {
        float impactX = projectile.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float impactY = projectile.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        EffectsHelper.getInstance().splash(projectile);
        emitBombvoyageImpactEffects(impactX, impactY);
        SoundHelper.GetSingleton().play(Sounds.EXPLOSION, 0f, 1f);

        for (Unit candidate : new ArrayList<Unit>(UnitHelper.getInstance().getUnits())) {
            if (candidate == null || candidate == this || candidate == primaryTarget || candidate.showOnly() || candidate.isDead()) {
                continue;
            }

            if (candidate.isFriendly == isFriendly) {
                continue;
            }

            if (candidate.getRoom() == null || !candidate.getRoom().equals(primaryTarget.getRoom())) {
                continue;
            }

            double distance = Math.sqrt(Math.pow(candidate.x - primaryTarget.x, 2) + Math.pow(candidate.y - primaryTarget.y, 2));
            if (distance > BOMBVOYAGE_SPLASH_RADIUS) {
                continue;
            }

            candidate.takeDamage(this, attackingItem, damage * BOMBVOYAGE_SPLASH_DAMAGE_FACTOR);
        }
    }

            private void emitBombvoyageImpactEffects(float impactX, float impactY) {
            emitBombvoyageBurst(impactX, impactY,
                "images/misc/grey.png",
                BOMBVOYAGE_BURST_PARTICLE_SIZE,
                BOMBVOYAGE_BURST_PARTICLE_COUNT,
                BOMBVOYAGE_BURST_SPREAD,
                BOMBVOYAGE_BURST_RISE,
                BOMBVOYAGE_BURST_LIFESPAN,
                BOMBVOYAGE_BURST_GRAVITY);
            emitBombvoyageBurst(impactX, impactY,
                "images/misc/black-particle.png",
                BOMBVOYAGE_SMOKE_PARTICLE_SIZE,
                BOMBVOYAGE_SMOKE_PARTICLE_COUNT,
                BOMBVOYAGE_SMOKE_SPREAD,
                BOMBVOYAGE_SMOKE_RISE,
                BOMBVOYAGE_SMOKE_LIFESPAN,
                BOMBVOYAGE_SMOKE_GRAVITY);
            emitBombvoyageBurst(impactX, impactY,
                "images/misc/yellow-dot.png",
                BOMBVOYAGE_FIRE_PARTICLE_SIZE,
                BOMBVOYAGE_FIRE_PARTICLE_COUNT,
                BOMBVOYAGE_FIRE_SPREAD,
                BOMBVOYAGE_FIRE_RISE,
                BOMBVOYAGE_FIRE_LIFESPAN,
                BOMBVOYAGE_FIRE_GRAVITY);
            emitBombvoyageBurst(impactX, impactY,
                "images/misc/red.png",
                BOMBVOYAGE_FIRE_PARTICLE_SIZE - 1f,
                BOMBVOYAGE_FIRE_PARTICLE_COUNT - 2,
                BOMBVOYAGE_FIRE_SPREAD - 6f,
                BOMBVOYAGE_FIRE_RISE - 12f,
                BOMBVOYAGE_FIRE_LIFESPAN - 6f,
                BOMBVOYAGE_FIRE_GRAVITY);
            }

            private void emitBombvoyageBurst(float impactX,
                             float impactY,
                             String spritePath,
                             float particleSize,
                             int particleCount,
                             float spread,
                             float rise,
                             float lifeSpan,
                             float gravityScale) {
            EffectsHelper.getInstance().add(new TrapBurst().init(
                impactX,
                impactY,
                spritePath,
                particleSize,
                particleCount,
                spread,
                rise,
                lifeSpan,
                gravityScale));
            }

    public void useQuickSkill(){
        useQuickSkillSlot(0);
    }

    public void useQuickSkill2(){
        useQuickSkillSlot(1);
    }

    public void useQuickSkill3(){
        useQuickSkillSlot(2);
    }

    public void useQuickSkill4(){
        useQuickSkillSlot(3);
    }

    public void useQuickSkill5(){
        useQuickSkillSlot(4);
    }

    public void useQuickSkill6(){
        useQuickSkillSlot(5);
    }

    public void useQuickSkill7(){
        useQuickSkillSlot(6);
    }

    public void useQuickSkillSlot(int slotIndex){
        ActiveSkill activeSkill = getQuickSkill(slotIndex);
        if (activeSkill instanceof NewClassActiveSkill) {
            if (!((NewClassActiveSkill)activeSkill).tryUse(this, null))
                EffectsHelper.getInstance().message(this, "I can't", Color.RED, 0);
            UIHelper.getInstance().setQuickSkillCostCheck(slotIndex, activeSkill.getManaCost() <= mp);
            return;
        }
        if(activeSkill != null && activeSkill.canUse(this)){
            boolean usesRangedAttackAnimation = activeSkill.usesRangedAttackAnimation(this);
            float cooldownDuration = getAttackAnimationDurationSeconds(usesRangedAttackAnimation);
            if (activeSkill.shouldPlayCastAnimation(this)) {
                if (usesRangedAttackAnimation) {
                    startRangedAttackAnimation(ActiveSkill.CAST_ATTACK_DURATION_SECONDS);
                }
                else {
                    startAttackAnimation(ActiveSkill.CAST_ATTACK_DURATION_SECONDS);
                }
            }
            activeSkill.use(this, null);
            activeSkill.startCooldown(cooldownDuration);
            recordActiveSkillUse(activeSkill);
            UIHelper.getInstance().setQuickSkillCostCheck(slotIndex, activeSkill.getManaCost() <= mp);
        }
        else {
            EffectsHelper.getInstance().message(this, "I can't", Color.RED, 0);
        }
    }

    public void modifyMp(int modification){
        if(mp + modification > mmp){
            mp = mmp;
            return;
        }

        this.mp += modification;
    }

    @Override
    public void modifyMana(int modification) {
        super.modifyMana(modification);

        for (int slotIndex = 0; slotIndex < QUICK_SKILL_SLOT_COUNT; slotIndex++) {
            ActiveSkill activeSkill = getQuickSkill(slotIndex);
            UIHelper.getInstance().setQuickSkillCostCheck(slotIndex, activeSkill != null && activeSkill.getManaCost() <= mp);
        }

        if (weapon instanceof Wand) {
            UIHelper.getInstance().setMeleeButtonCostCheck(((Wand) weapon).getManaCost() <= mp);
        }
    }

    public void setActiveSkill(ActiveSkill activeSkill){
        setQuickSkill(0, activeSkill);
    }

    public void setActiveSkill2(ActiveSkill activeSkill){
        setQuickSkill(1, activeSkill);
    }

    public void setActiveSkill3(ActiveSkill activeSkill){
        setQuickSkill(2, activeSkill);
    }

    public void setActiveSkill4(ActiveSkill activeSkill){
        setQuickSkill(3, activeSkill);
    }

    public void setActiveSkill5(ActiveSkill activeSkill){
        setQuickSkill(4, activeSkill);
    }

    public void setActiveSkill6(ActiveSkill activeSkill){
        setQuickSkill(5, activeSkill);
    }

    public void setActiveSkill7(ActiveSkill activeSkill){
        setQuickSkill(6, activeSkill);
    }

    public void setQuickSkill(int slotIndex, ActiveSkill activeSkill){
        if (activeSkill != null && !canAssignClassSkill(activeSkill)) return;
        if (SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()
                && slotIndex >= getQuickSkillSlotCount()) {
            return;
        }

        if (slotIndex < 0 || slotIndex >= QUICK_SKILL_SLOT_COUNT) {
            return;
        }

        activeSkills[slotIndex] = activeSkill;
        UIHelper.getInstance().setQuickSkill(slotIndex, activeSkill);
    }

    private boolean canAssignClassSkill(ActiveSkill skill) {
        if (!NewClassSkillTree.isNewClass(heroClass) && !NewClassSkillTree.isReserved(skill.getId())) return true;
        return SkillsHelper.getInstance().isSupported(this, skill) && hasSkill(skill.getId());
    }

    public void recordActiveSkillUse(ActiveSkill activeSkill) {
        if (activeSkill == null) {
            return;
        }

        Integer usageCount = activeSkillUsageCounts.get(activeSkill.getId());
        activeSkillUsageCounts.put(activeSkill.getId(), usageCount == null ? 1 : usageCount + 1);
    }

    public HashMap<Integer, Integer> getActiveSkillUsageCounts() {
        return new HashMap<>(activeSkillUsageCounts);
    }

    public NewClassActionState getNewClassActions() {
        if (newClassActions == null) newClassActions = new NewClassActionState();
        return newClassActions;
    }

    public void suspendNewClassActions(boolean suspended) {
        if (NewClassSkillTree.isNewClass(heroClass)) getNewClassActions().setSuspended(suspended);
    }

    public void setActiveSkillUsageCounts(HashMap<Integer, Integer> activeSkillUsageCounts) {
        this.activeSkillUsageCounts.clear();
        if (activeSkillUsageCounts == null) {
            return;
        }

        this.activeSkillUsageCounts.putAll(activeSkillUsageCounts);
    }

    @Override
    public void drawBuffs(Batch batch){
        if (unitState == UnitState.DEAD || eatIconTimer <= 0f) {
            return;
        }

        GameSprite currentEatIcon = getEatIcon();
        float progress = 1f - eatIconTimer / EAT_ICON_DURATION_SECONDS;
        float riseOffset = progress * EAT_ICON_RISE;
        float bobOffset = (float) Math.sin(progress * Math.PI) * EAT_ICON_BOB_DISTANCE;
        float stretchProgress = progress * progress;
        float scaleX = 1f + stretchProgress * 0.55f;
        float scaleY = 1f - stretchProgress * 0.08f;
        float alpha = 1f - progress * 0.85f;
        float drawX = x + ConstantsHelper.UNIT_DIMENSIONS / 2f - EAT_ICON_SIZE / 2f;
        float drawY = y + ConstantsHelper.UNIT_DIMENSIONS + 8f + riseOffset + bobOffset;

        currentEatIcon.setScale(scaleX, scaleY);
        currentEatIcon.setAlpha(alpha);
        currentEatIcon.setPosition(drawX, drawY);
        currentEatIcon.draw(batch);
        currentEatIcon.setScale(1f, 1f);
        currentEatIcon.setAlpha(1f);
    }

    private void showEatIcon() {
        eatIconTimer = EAT_ICON_DURATION_SECONDS;
        eatIcon = new GameSprite("images/misc/eat_icon.png", EAT_ICON_SIZE, EAT_ICON_SIZE);
    }

    private GameSprite getEatIcon() {
        if (eatIcon == null) {
            eatIcon = new GameSprite("images/misc/eat_icon.png", EAT_ICON_SIZE, EAT_ICON_SIZE);
        }

        return eatIcon;
    }


    public void getFriendlies(){
        for(Unit unit : UnitHelper.getInstance().getUnits()){
            if(unit.isFriendly && !unit.showOnly && !unit.isHero && !(unit instanceof NecromancerMinion)){
                unit.setRoom(getRoom());
                unit.appear(x, y);
                unit.floorY = floorY;
                PhysicsHelper.getInstance().syncBodyToUnit(unit);
                if (unit instanceof Mob) {
                    ((Mob) unit).wakeToWandering();
                }
            }
        }
    }

    @Override
    protected boolean preventDeath(Unit source, Weapon damagingItem, float damage) {
        if (heroClass != HeroClass.NECROMANCER || !hasSkill(Skills.MASTER_OF_DEATH)
                || this != UnitHelper.getInstance().getHero() || masterOfDeathUsed || masterOfDeathRecovering
                || deathHandled || isDead() || showOnly() || getHP() > 0 || !Float.isFinite(damage) || damage <= 0f) return false;
        masterOfDeathRecovering = true;
        try {

            masterOfDeathUsed = true;
            getNewClassActions().setDuration(Skills.MASTER_OF_DEATH, 1f);
            setHP(Math.max(1, Math.round(getMaxHP() * .35f)));
            clearControlIntent();
            movingLeft = movingRight = false;
            momentX = airMomentumX = speedY = 0f;
            lastDamageSource = null;
            lastDamagingItem = null;
            changeState(UnitState.IDLE, true);
            PhysicsHelper.getInstance().syncBodyToUnit(this);
            EffectsHelper.getInstance().message(this, SkillsHelper.getInstance().getSkillName(Skills.MASTER_OF_DEATH),
                    new Color(.55f, 1f, .65f, 1f), 0f);
            SoundHelper.GetSingleton().play(Sounds.MASTERY, 0f, .65f);
            if (!SaveHelper.getInstance().saveCurrentRun() && Gdx.app != null)
                Gdx.app.log("Hero", "Could not save Master of Death recovery; the charge remains spent in this run.");
            return true;
        } finally {
            masterOfDeathRecovering = false;
        }
    }

    public boolean hasUsedMasterOfDeath() { return masterOfDeathUsed; }


    public float getMasterOfDeathRecoveryRemaining() {
        float remaining = heroClass == HeroClass.NECROMANCER && hasSkill(Skills.MASTER_OF_DEATH) && masterOfDeathUsed && newClassActions != null
                ? newClassActions.duration(Skills.MASTER_OF_DEATH) : 0f;
        return remaining <= .000001f ? 0f : remaining;
    }


    public void restoreMasterOfDeath(Boolean savedUsed) {
        boolean eligible = heroClass == HeroClass.NECROMANCER && hasSkill(Skills.MASTER_OF_DEATH);
        masterOfDeathUsed = eligible && (masterOfDeathUsed || Boolean.TRUE.equals(savedUsed));
        if (newClassActions != null) {
            float remaining = eligible && Boolean.TRUE.equals(savedUsed) ? Math.min(1f, newClassActions.duration(Skills.MASTER_OF_DEATH)) : 0f;
            newClassActions.setDuration(Skills.MASTER_OF_DEATH, remaining);
        }
    }

    @Override
    protected void die() {
        if (deathHandled) {
            return;
        }

        deathHandled = true;
        NecromancerCurse.clearAll();
        MercenaryFear.clearAll();
        NewClassSpellProjectile.clearFor(this);
        GunProjectile.clearFor(this);
        NecromancerMinion.clearFor(this);
        MapHelper.getInstance().clearRoomPresentation();
        MapHelper.getInstance().clearCorpses();
        if (starvationDamagePending) {
            AchievementManager.getInstance().onDeathFromHunger();
        }
        RatKingHelper.getInstance().onHeroDied(this);
        if (!SaveHelper.getInstance().recordDefeatedRun() && Gdx.app != null)
            Gdx.app.log("Hero", "Failed to record ranking; the run has still ended.");
        SaveHelper.getInstance().deleteCurrentRun();
        SoundHelper.GetSingleton().play(Sounds.DEATH, 0f, 1f);
    }

    @Override
    protected void regenerate(float delta){
        regeneration += mhp * delta * regenerationRate;
        if (hunger > 0f) {
            regenerationMana += mmp * delta * manaRegenerationRate;
        }

        if(regeneration > 100f && hp < mhp && hunger > 0){
            hp += 1;
            regeneration = 0f;
        }

        if(hunger > 0f && regenerationMana > 100f && mp < mmp){
            mp += 1;
            regenerationMana = 0f;
            for (int slotIndex = 0; slotIndex < QUICK_SKILL_SLOT_COUNT; slotIndex++) {
                ActiveSkill activeSkill = getQuickSkill(slotIndex);
                UIHelper.getInstance().setQuickSkillCostCheck(slotIndex, activeSkill != null && activeSkill.getManaCost() <= mp);
            }

            if(weapon instanceof Wand && ((Wand)weapon).getManaCost() <= mp){
                UIHelper.getInstance().setMeleeButtonCostCheck(true);
            }
            else{
                UIHelper.getInstance().setMeleeButtonCostCheck(false);
            }
        }
    }
}
