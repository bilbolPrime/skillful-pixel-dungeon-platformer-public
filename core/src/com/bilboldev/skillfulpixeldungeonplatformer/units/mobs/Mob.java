package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.DifficultyHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.QuestManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.Potion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.library.LibraryEntry;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpritePose;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.ConfusedAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.FriendlyAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.MercenaryFear;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Poisoned;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Weaken;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

import java.util.Locale;

public class Mob extends Unit implements LibraryEntry {
    private transient SpritePose lastObservedBody;
    private transient String lastObservedRoom;
    private transient boolean lastObservedIdle;
    private static final float MIN_AI_ACTION_DELAY_SECONDS = 0.04f;
    private static final float AI_ACTION_DELAY_VARIANCE_SECONDS = 0.04f;
    private static final float ALERT_ICON_DURATION_SECONDS = 1f;
    private static final float SLEEP_ICON_HOVER_SPEED = 2.4f;
    private static final float SLEEP_ICON_HOVER_DISTANCE = 2f;
    private static final float ALERT_ICON_BUBBLE_DISTANCE = 8f;
    private static final float ALERT_ICON_SCALE_BOOST = 0.18f;
    private static final float CHAMPION_BONUS_EXP_MULTIPLIER = 1.5f;
    private static final float CHAMPION_DEBUFF_DURATION = 5f;
    private static final float CHAMPION_LIFESTEAL_MULTIPLIER = 0.25f;
    private static final float CHAMPION_AURA_ALPHA = 0.5f;
    private static final float CHAMPION_AURA_SIZE = ConstantsHelper.UNIT_DIMENSIONS * 1.72f;
    private static final float CHAMPION_AURA_PULSE_SECONDS = 1.15f;
    private static final float CHAMPION_AURA_VERTICAL_OFFSET = ConstantsHelper.UNIT_DIMENSIONS * 0.08f;
    private static final float SLEEP_WAKE_CHECK_INTERVAL_SECONDS = 1f;
    private static final float SLEEP_WAKE_MAX_DISTANCE = ConstantsHelper.SCREEN_WIDTH * 0.5f;
    private static final int CHAMPION_AURA_TEXTURE_SIZE = 96;
    private static final String CHAMPION_KILL_TEXT = "Champ Killed!";
    private static final String CHAMPION_AURA_TEXTURE_KEY = "generated_champion_aura";

    public enum ChampionType {
        CHIEF("Chief", 2f, 1.30f, new Color(0.94f, 0.98f, 1f, 1f)),
        CURSED("Cursed", 1.5f, 1.15f, new Color(0.34f, 0.22f, 0.46f, 1f)),
        FOUL("Foul", 1.5f, 1.20f, new Color(0.95f, 0.82f, 0.22f, 1f)),
        VAMPIRIC("Vampiric", 1.5f, 1.10f, new Color(0.92f, 0.20f, 0.28f, 1f));

        private final String displayName;
        private final float healthMultiplier;
        private final float defenseMultiplier;
        private final Color auraColor;

        ChampionType(String displayName, float healthMultiplier, float defenseMultiplier, Color auraColor) {
            this.displayName = displayName;
            this.healthMultiplier = healthMultiplier;
            this.defenseMultiplier = defenseMultiplier;
            this.auraColor = auraColor;
        }

        public String getDisplayName() {
            return displayName;
        }

        public float getHealthMultiplier() {
            return healthMultiplier;
        }

        public float getDefenseMultiplier() {
            return defenseMultiplier;
        }

        public Color getAuraColor() {
            return new Color(auraColor);
        }

        public static ChampionType randomType() {
            ChampionType[] championTypes = values();
            return championTypes[RandomHelper.getInstance().randomInt(championTypes.length)];
        }

        public static ChampionType fromName(String name) {
            if (name == null || name.isEmpty()) {
                return null;
            }

            for (ChampionType type : values()) {
                if (type.name().equalsIgnoreCase(name) || type.displayName.equalsIgnoreCase(name)) {
                    return type;
                }
            }

            return null;
        }
    }

    private enum Awareness {
        SLEEPING,
        WANDERING,
        HUNTING
    }

    protected AI ai;

    private AI uncontrolledAI, dominanceAI, confusionAI;
    private boolean uncontrolledFriendly;
    protected boolean boss;
    protected int experience;
    protected Item loot;
    protected int dropChance = 25;
    private Awareness awareness = Awareness.SLEEPING;
    private boolean alerted;
    private float alertIconTimer;
    private float sleepIconAnimationTime;
    private float sleepingWakeCheckTimer;
    private float aiActionDelayRemaining;
    private float bufferedAiDelta;
    private boolean suppressNextSleepNotice;
    private final GameSprite sleepingIcon = new GameSprite("images/buffs/sleeping.png", 20, 20);
    private final GameSprite alertIcon = new GameSprite("images/buffs/alert.png", 20, 20);
    private ChampionType championType;
    private Integer baseMaxHp;
    private Integer baseAttackSkill;
    private Integer baseDefenseSkill;
    private float difficultyHealthMultiplier = 1f;
    private float difficultyAccuracyMultiplier = 1f;
    private float difficultyDefenseMultiplier = 1f;
    private transient GameSprite championAura;

    {
        unitState = UnitState.SPAWNING;
        spawnSpace = true;
        aiActionDelayRemaining = nextAiActionDelay();
    }

    @Override
    public void draw(Batch batch, float alpha) {
        drawChampionAura(batch);
        super.draw(batch, alpha);
    }

    @Override
    protected void drawBodyFilm(Batch batch) {
        super.drawBodyFilm(batch);
        if (isHero || isFriendly || showOnly() || isDead() || isInvisible() || gf.getAlpha() <= 0f) return;
        OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        float halfWidth = gf.getVisualWidth() / 2f, halfHeight = gf.getVisualHeight() / 2f;
        if (camera == null || !camera.frustum.boundsInFrustum(gf.getVisualLeft() + halfWidth,
                gf.getVisualBottom() + halfHeight, 0f, halfWidth, halfHeight, 0f)
                || !UnitHelper.getInstance().canSeeTarget(UnitHelper.getInstance().getHero(), this)) return;

        lastObservedBody = gf.copyDrawnFrame();
        lastObservedRoom = room;
        lastObservedIdle = unitState == UnitState.IDLE && !hasMeleeRecoveryPose();
    }

    public SpritePose getLastObservedBody(String outgoingRoom) {
        return outgoingRoom != null && outgoingRoom.equals(room) && outgoingRoom.equals(lastObservedRoom)
                ? lastObservedBody : null;
    }

    public boolean hasObservedIdlePose(String outgoingRoom) {
        return lastObservedIdle && getLastObservedBody(outgoingRoom) != null;
    }

    @Override
    public void act(float delta){
        sleepIconAnimationTime += delta;
        if (alertIconTimer > 0f) {
            alertIconTimer = Math.max(0f, alertIconTimer - delta);
        }

        if (!canSleep() && awareness == Awareness.SLEEPING) {
            wakeToWandering();
        }

        if (isSleeping() && isInActiveRoom()) {
            sleepingWakeCheckTimer += delta;
        } else {
            sleepingWakeCheckTimer = 0f;
        }

        MercenaryFear fear = (MercenaryFear)getBuff(MercenaryFear.class);
        if (fear != null && fear.active()) {
            fear.moveAway();
            bufferedAiDelta = 0f;
            aiActionDelayRemaining = 0f;
        }
        else if(ai != null && unitState != UnitState.DEAD && unitState != UnitState.SPAWNING){
            bufferedAiDelta += delta;
            aiActionDelayRemaining -= delta;
            if (aiActionDelayRemaining <= 0f) {
                ai.act(bufferedAiDelta);
                bufferedAiDelta = 0f;
                aiActionDelayRemaining = nextAiActionDelay();
            }
        }
        else {
            bufferedAiDelta = 0f;
            aiActionDelayRemaining = nextAiActionDelay();
        }

        super.act(delta);
    }

    private float nextAiActionDelay() {
        return MIN_AI_ACTION_DELAY_SECONDS + RandomHelper.getInstance().randomFloat(AI_ACTION_DELAY_VARIANCE_SECONDS);
    }

    @Override
    public void takeDamage(Unit source, Weapon damagingItem, float damage){
        super.takeDamage(source, damagingItem, damage);
        if (isDead()) {
            return;
        }

        if(ai != null && source != null && !source.showOnly()){
            beginHunting(source);
        }
    }

    public boolean isChampion() {
        return championType != null;
    }

    public ChampionType getChampionType() {
        return championType;
    }

    public void promoteToRandomChampion() {
        promoteToChampion(ChampionType.randomType());
    }

    public void promoteToChampion(ChampionType type) {
        if (type == null) {
            return;
        }

        championType = type;
        recalculateScaledStats(true);
        championAura = null;
    }

    public void applyCurrentRunDifficulty() {
        if (isFriendly) {
            return;
        }

        DifficultyHelper.Difficulty difficulty = DifficultyHelper.getInstance().getCurrentDifficulty();
        difficultyHealthMultiplier = difficulty == null ? 1f : difficulty.getEnemyHealthMultiplier();
        difficultyAccuracyMultiplier = difficulty == null ? 1f : difficulty.getEnemyAccuracyMultiplier();
        difficultyDefenseMultiplier = difficulty == null ? 1f : difficulty.getEnemyDefenseMultiplier();
        recalculateScaledStats(true);
    }

    public void onSuccessfulAttack(Unit target, int damageDealt) {
        if (!isChampion() || target == null || target.showOnly() || damageDealt <= 0) {
            return;
        }

        switch (championType) {
            case VAMPIRIC:
                int previousHp = getHP();
                setHP(getHP() + Math.round(damageDealt * CHAMPION_LIFESTEAL_MULTIPLIER));
                if (getHP() > previousHp) {
                    EffectsHelper.getInstance().heal(this);
                }
                break;
            case CURSED:
                refreshBuff(target, Weaken.class, new Weaken(), CHAMPION_DEBUFF_DURATION);
                break;
            case FOUL:
                refreshBuff(target, Poisoned.class, new Poisoned().setDamageMultiplier(DifficultyHelper.getInstance().getEnemyDamageMultiplier(this)), CHAMPION_DEBUFF_DURATION);
                break;
            case CHIEF:
            default:
                break;
        }
    }

    private void refreshBuff(Unit target, Class<? extends Buff> buffClass, Buff newBuff, float duration) {
        Buff existing = target.getBuff(buffClass);
        if (existing != null) {
            existing.setPermanent(false).setDuration(duration);
            return;
        }

        newBuff.setPermanent(false).setDuration(duration).setOwner(target);
    }

    private void recalculateScaledStats(boolean preserveHealthPercent) {
        captureBaseStatsIfNeeded();

        int previousMaxHp = Math.max(1, mhp);
        float previousHealthPercent = previousMaxHp <= 0 ? 1f : MathUtils.clamp(hp / (float) previousMaxHp, 0f, 1f);
        float championHealthMultiplier = championType == null ? 1f : championType.getHealthMultiplier();
        float championDefenseMultiplier = championType == null ? 1f : championType.getDefenseMultiplier();

        setMaxHP((int) Math.ceil(baseMaxHp * difficultyHealthMultiplier * championHealthMultiplier));
        setBaseAttackSkill(Math.max(1, Math.round(baseAttackSkill * difficultyAccuracyMultiplier)));
        setBaseDefenseSkill(Math.max(1, Math.round(baseDefenseSkill * difficultyDefenseMultiplier * championDefenseMultiplier)));

        if (!preserveHealthPercent) {
            return;
        }

        if (hp >= previousMaxHp) {
            hp = mhp;
            return;
        }

        hp = Math.max(1, Math.min(mhp, Math.round(mhp * previousHealthPercent)));
    }

    private void captureBaseStatsIfNeeded() {
        if (baseMaxHp == null) {
            baseMaxHp = Math.max(1, mhp);
        }
        if (baseAttackSkill == null) {
            baseAttackSkill = Math.max(1, attackSkill);
        }
        if (baseDefenseSkill == null) {
            baseDefenseSkill = Math.max(0, defenseSkill);
        }
    }

    protected final void resetScaledStatBaseline() {
        baseMaxHp = null;
        baseAttackSkill = null;
        baseDefenseSkill = null;
    }

    protected void setLoot(){
        int adjustedDropChance = Math.min(100, Math.round(dropChance * 2.25f));
        if(!RandomHelper.getInstance().randomChance(adjustedDropChance)){
            return;
        }

        loot = InventoryHelper.getInstance().getRandomDrop(MapHelper.getInstance().getDepth());
    }

    @Override
    protected void onDeathCommitted() {
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null || (hero.getHeroClass() != HeroClass.NECROMANCER && hero.getHeroClass() != HeroClass.MERCENARY) || hero.isDead()

                || isFriendly || isSummoned || showOnly || !grantsDeathRewards() || this instanceof MercenaryAlly
                || room == null || !UnitHelper.getInstance().getUnits().contains(this)) {
            return;
        }
        if (hero.getHeroClass() == HeroClass.NECROMANCER) {
            if (boss) return;
            Room deathRoom = MapHelper.getInstance().getRoom(room);
            if (deathRoom != null) deathRoom.recordCorpseDeath(getPersistentId(), x, y);
        } else {

            if (getLastDamageSource() == hero && hero.hasSkill(Skills.WANTED)) {
                InventoryHelper.getInstance().modifyGold(1);
            }
            if (!boss) InventoryHelper.getInstance().createBulletBundle(4).spawnNaturally(x, y, floorY, room);
        }
    }

    @Override
    public void die(){
        if(isFriendly){
            return;
        }

        AchievementManager.getInstance().onEnemyKilled(this);

        if (boss && shouldCelebrateBossDeath()) {
            SoundHelper.GetSingleton().play(Sounds.BOSS, 0f, 1f);
            UIHelper.getInstance().showBossSlainBanner();
        }

        if (!grantsDeathRewards()) return;

        Unit lastDamageSource = getLastDamageSource();
        int awardedExperience = getAwardedExperience();
        if(experience > 0){
            UnitHelper.getInstance().getHero().earnExp(awardedExperience);

            if (lastDamageSource instanceof MercenaryAlly) {
                ((MercenaryAlly) lastDamageSource).earnMercenaryExp(awardedExperience);
            }
        }

        if (lastDamageSource == UnitHelper.getInstance().getHero()) {
            UnitHelper.getInstance().getHero().onEnemyKilled(this);
        }

        if (lastDamageSource != null && lastDamageSource.isFriendly) {
            RatKingHelper.getInstance().onHeroKilledSomething();
        }

        QuestManager.getInstance().onMobKilled(this);

        dropBaseGold();

        if (isChampion()) {
            EffectsHelper.getInstance().message(this, Messages.get("custom.ui.champion_killed"), championType.getAuraColor(), 0f);
            dropChampionGold();
        }

        setLoot();

        if(loot != null){
            loot.spawnNaturally(x, y, floorY, room);
        }
    }

    protected boolean shouldCelebrateBossDeath() {
        return true;
    }


    protected boolean grantsDeathRewards() {
        return true;
    }

    private int getAwardedExperience() {
        if (!isChampion()) {
            return experience;
        }

        return (int) Math.ceil(experience * CHAMPION_BONUS_EXP_MULTIPLIER);
    }

    private void dropChampionGold() {
        Item gold = new Gold().setQuantity(rollChampionGoldAmount());
        gold.spawnNaturally(x, y, floorY, room);
    }

    private void dropBaseGold() {


        if (isSummoned) return;
        Item gold = new Gold().setQuantity(rollBaseGoldAmount());
        gold.spawnNaturally(x, y, floorY, room);
    }

    private int rollBaseGoldAmount() {
        int goldDepth = Math.min(3, Math.max(1, MapHelper.getInstance().getDepth()));
        return goldDepth + RandomHelper.getInstance().randomInt(goldDepth + 1);
    }

    private int rollChampionGoldAmount() {
        int depth = Math.max(1, MapHelper.getInstance().getDepth());
        int lowerBound = depth;
        int upperBound = Math.max(5, depth * 2);
        return lowerBound + RandomHelper.getInstance().randomInt(Math.max(1, upperBound - lowerBound + 1));
    }

    private void drawChampionAura(Batch batch) {
        if (!shouldDrawChampionAura()) {
            return;
        }

        ensureChampionAura();
        float auraOffset = (CHAMPION_AURA_SIZE - ConstantsHelper.UNIT_DIMENSIONS) / 2f;
        championAura.setPosition(getRenderX() - auraOffset, getRenderY() - auraOffset + CHAMPION_AURA_VERTICAL_OFFSET);
        championAura.draw(batch);
    }

    private boolean shouldDrawChampionAura() {
        String activeRoom = MapHelper.getInstance().getActiveRoomIdentifier();
        return championType != null
                && gf != null
                && room != null
                && room.equals(activeRoom)
                && unitState != UnitState.DEAD;
    }

    private void ensureChampionAura() {
        if (championType == null || championAura != null) {
            return;
        }

        championAura = new GameSprite(
                TextureHelper.GetSingleton().getGeneratedRadialAuraSprite(CHAMPION_AURA_TEXTURE_KEY, CHAMPION_AURA_TEXTURE_SIZE),
                CHAMPION_AURA_SIZE,
                CHAMPION_AURA_SIZE);
        championAura.setAlpha(CHAMPION_AURA_ALPHA);
        championAura.setPulseTint(championType.getAuraColor(), CHAMPION_AURA_PULSE_SECONDS);
    }

    public void makeFriendly(){
        forgetControlAI();
        ai = new FriendlyAI(this);
        isFriendly = true;
        awareness = Awareness.WANDERING;
        alerted = false;
    }

    public void makeHostile() {
        if (ai == null) {
            return;
        }

        forgetControlAI();
        isFriendly = false;
        ai = new AgressiveAI(this);
        awareness = Awareness.WANDERING;
        alerted = false;
    }

    public void blinded(){
        movingLeft = false;
        movingRight = false;
        if(ai != null){
            ai.blinded();
        }
    }

    public void clearBlindness() {
        if (ai != null && ai.isBlind()) {
            ai.clearTarget();
            awareness = Awareness.WANDERING;
        }
    }

    public void alert(Unit source) {
        if (ai == null || !ai.canTarget(source) || source.isInvisible()) {
            return;
        }

        showAlertIcon();
        alerted = true;
        if (awareness == Awareness.SLEEPING) {
            awareness = Awareness.WANDERING;
            sleepingWakeCheckTimer = 0f;
            stopMoving();
            ai.clearTarget();
        }

        if (awareness == Awareness.HUNTING) {
            ai.setOther(source);
        }
    }

    public void forgetTarget(Unit source) {
        if (ai != null && ai.getOther() == source) {
            ai.clearTarget();
            onTargetLost();
        }
    }

    public void dominated(){
        if(ai == null){
            return;
        }

        rememberControlAI();
        dominanceAI = new FriendlyAI(this);
        switchControlAI(dominanceAI, true);
    }

    public void unDominate(){
        AI previous = dominanceAI;
        dominanceAI = null;
        if (ai != previous) return;
        if (confusionAI != null && hasActiveConfusion()) switchControlAI(confusionAI, false);
        else restoreUncontrolledAI();
    }

    public void confused(){
        if(ai == null){
            return;
        }

        rememberControlAI();
        confusionAI = new ConfusedAI(this);
        switchControlAI(confusionAI, false);
    }

    public void unConfuse() {
        if (hasActiveConfusion()) return;
        AI previous = confusionAI;
        confusionAI = null;
        if (ai != previous) return;
        if (dominanceAI != null && hasActiveControl(com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Dominate.class)) {
            switchControlAI(dominanceAI, true);
        } else restoreUncontrolledAI();
    }

    private boolean hasActiveConfusion() {
        return hasActiveControl(com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Confused.class)
                || hasActiveControl(com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Terrorized.class);
    }

    private boolean hasActiveControl(Class<? extends Buff> type) {
        Buff buff = getBuff(type);
        return buff != null && buff.active();
    }

    private void rememberControlAI() {
        if (uncontrolledAI == null) {
            uncontrolledAI = ai;
            uncontrolledFriendly = isFriendly;
        }
    }

    private void switchControlAI(AI next, boolean friendly) {
        boolean blind = ai != null && ai.isBlind();
        if (ai != null) ai.clearTarget();
        ai = next;
        isFriendly = friendly;
        ai.clearTarget();
        if (blind) ai.blinded();
        awareness = Awareness.WANDERING;
        alerted = false;
    }

    private void restoreUncontrolledAI() {
        if (uncontrolledAI != null) switchControlAI(uncontrolledAI, uncontrolledFriendly);
        forgetControlAI();
    }

    private void forgetControlAI() {
        uncontrolledAI = dominanceAI = confusionAI = null;
    }


    public boolean isFriendlyWithoutControl() {
        return uncontrolledAI == null ? isFriendly : uncontrolledFriendly;
    }

    @Override
    public void drawBuffs(Batch batch) {
        if (unitState == UnitState.DEAD) {
            return;
        }

        int stateIconCount = (isSleeping() ? 1 : 0) + (alertIconTimer > 0f ? 1 : 0);
        int totalIconCount = buffs.size() + stateIconCount;
        if (totalIconCount < 1) {
            return;
        }

        int at = (int) getRenderX() + (int) ConstantsHelper.UNIT_DIMENSIONS / 2
                - (totalIconCount % 2 == 0 ? 20 * totalIconCount / 2 : 10 + (20 * (totalIconCount - 1) / 2));

        if (isSleeping()) {
            float hoverOffset = MathUtils.sin(sleepIconAnimationTime * MathUtils.PI2 * SLEEP_ICON_HOVER_SPEED)
                    * SLEEP_ICON_HOVER_DISTANCE;
            sleepingIcon.setPosition(at, getRenderY() + ConstantsHelper.UNIT_DIMENSIONS + 5f + hoverOffset);
            sleepingIcon.draw(batch);
            at += 20;
        }

        if (alertIconTimer > 0f) {
            float alertProgress = 1f - alertIconTimer / ALERT_ICON_DURATION_SECONDS;
            float bubbleOffset = Interpolation.sineOut.apply(0f, ALERT_ICON_BUBBLE_DISTANCE, alertProgress);
            float pulse = MathUtils.sin(alertProgress * MathUtils.PI * 3f);
            float scale = 1f + Math.max(0f, pulse) * ALERT_ICON_SCALE_BOOST;
            float alpha = 1f - Interpolation.fade.apply(Math.max(0f, alertProgress - 0.2f) / 0.8f);
            alertIcon.setScale(scale, scale);
            alertIcon.setAlpha(alpha);
            alertIcon.setPosition(at, getRenderY() + ConstantsHelper.UNIT_DIMENSIONS + 5f + bubbleOffset);
            alertIcon.draw(batch);
            alertIcon.setScale(1f, 1f);
            alertIcon.setAlpha(1f);
            at += 20;
        }

        for (Buff buff : buffs) {
            buff.getGameSprite().setPosition(at, getRenderY() + ConstantsHelper.UNIT_DIMENSIONS + 5);
            buff.getGameSprite().draw(batch);
            at += 20;
        }
    }

    public boolean isSleeping() {
        return canSleep() && awareness == Awareness.SLEEPING;
    }

    public boolean consumeAlerted() {
        boolean wasAlerted = alerted;
        alerted = false;
        return wasAlerted;
    }

    public boolean canNoticeTarget(Unit target, boolean alertBoost, boolean sleepingCheck) {
        if (ai == null || !ai.canTarget(target) || target.isInvisible()) {
            return false;
        }

        if (target.getRoom() == null || room == null || !target.getRoom().equals(room)) {
            return false;
        }

        if (!UnitHelper.getInstance().canSeeTarget(this, target)) {
            return false;
        }

        if (!(target instanceof Hero)) {
            return true;
        }

        if (alertBoost) {
            return true;
        }

        return RandomHelper.getInstance().randomInt(getNoticeRollRange((Hero) target, sleepingCheck) + 1) == 0;
    }

    public void suppressNextSleepNotice() {
        if (!isFriendly) {
            suppressNextSleepNotice = true;
        }
    }

    public boolean consumeSleepNoticeSuppression() {
        boolean suppressed = suppressNextSleepNotice;
        suppressNextSleepNotice = false;
        return suppressed;
    }

    public boolean tryWakeFromSleepingHero(Hero hero) {
        if (!isSleeping() || hero == null || hero.showOnly() || hero.isInvisible()) {
            return false;
        }

        if (!isInActiveRoom() || hero.getRoom() == null || room == null || !hero.getRoom().equals(room)) {
            return false;
        }

        if (!UnitHelper.getInstance().canSeeTarget(this, hero)) {
            return false;
        }

        if (sleepingWakeCheckTimer < SLEEP_WAKE_CHECK_INTERVAL_SECONDS) {
            return false;
        }

        sleepingWakeCheckTimer = Math.max(0f, sleepingWakeCheckTimer - SLEEP_WAKE_CHECK_INTERVAL_SECONDS);

        float distance = (float) UtilsHelper.distance(this, hero);
        if (distance > SLEEP_WAKE_MAX_DISTANCE) {
            return false;
        }

        int wakeChance = getSleepingWakeChance(hero, distance);
        return RandomHelper.getInstance().randomInt(100) < wakeChance;
    }

    public void beginHunting(Unit source) {
        if (ai == null || !ai.canTarget(source)) {
            return;
        }

        awareness = Awareness.HUNTING;
        sleepingWakeCheckTimer = 0f;
        suppressNextSleepNotice = false;
        alerted = false;
        showAlertIcon();
        ai.setOther(source);
    }

    public void onTargetLost() {
        if (!isFriendly) {
            awareness = Awareness.WANDERING;
        }
    }

    public void putToSleep() {
        if (!canSleep()) {
            if (awareness == Awareness.SLEEPING) {
                wakeToWandering();
            }
            return;
        }

        if (isFriendly) {
            return;
        }

        awareness = Awareness.SLEEPING;
        sleepingWakeCheckTimer = 0f;
        suppressNextSleepNotice = false;
        alerted = false;
        alertIconTimer = 0f;
        stopForSleep();
        settleIntoIdle();
    }

    public void wakeToWandering() {
        awareness = Awareness.WANDERING;
        sleepingWakeCheckTimer = 0f;
        suppressNextSleepNotice = false;
        alerted = false;
        if (ai != null) {
            ai.clearTarget();
        }
        stopMoving();
    }

    private boolean canSleep() {
        return !isFriendly && !boss && !isSummoned;
    }

    public void stopForSleep() {
        if (ai != null) {
            ai.clearTarget();
        }
        stopMoving();
        settleIntoIdle();
    }

    private int getNoticeRollRange(Hero hero, boolean sleepingCheck) {
        int distanceTiles = Math.max(0, Math.round((float) (UtilsHelper.distance(this, hero) / ConstantsHelper.TILE)));
        int stealthPenalty = hero.getStealthScore();
        if (sleepingCheck) {
            int sleepRange = distanceTiles + stealthPenalty;
            if (hero.isLevitating()) {
                sleepRange += 2;
            }
            return Math.max(0, sleepRange);
        }

        return Math.max(0, distanceTiles / 2 + stealthPenalty);
    }

    private boolean isInActiveRoom() {
        return room != null
                && MapHelper.getInstance().getActiveRoom() != null
                && room.equals(MapHelper.getInstance().getActiveRoom().getIdentifier());
    }

    public boolean shouldForceBossRoomAggro() {
        if (isFriendly || room == null) {
            return false;
        }

        Room activeRoom = MapHelper.getInstance().getActiveRoom();
        return activeRoom != null
                && room.equals(activeRoom.getIdentifier())
                && activeRoom.hasBossEncounterStarted()
                && !activeRoom.isBossDefeated();
    }

    public Unit findBossRoomTarget() {
        if (!shouldForceBossRoomAggro()) {
            return null;
        }

        Unit candidate = UnitHelper.getInstance().findTargetInRoom(this, true);
        Unit current = ai == null ? null : ai.getOther();
        if (candidate != null && current != null && ai.canTarget(current)
                && (candidate.isInvisible() || !current.isInvisible()) && !ai.isBetterTarget(candidate)) {
            return current;
        }
        return candidate;
    }

    private int getSleepingWakeChance(Hero hero, float distance) {
        float distanceFactor = 1f - distance / SLEEP_WAKE_MAX_DISTANCE;
        int attackAdvantage = getAttackSkill(hero, null) - hero.getDefenseSkill(this);
        int wakeChance = 15
                + Math.round(distanceFactor * 45f)
                + MathUtils.clamp(Math.round(attackAdvantage / 4f), -20, 20)
                - hero.getStealthScore() * 8;

        if (hero.isLevitating()) {
            wakeChance += 5;
        }

        return MathUtils.clamp(wakeChance, 5, 90);
    }

    private void showAlertIcon() {
        alertIconTimer = ALERT_ICON_DURATION_SECONDS;
    }

    private void stopMoving() {
        movingLeft = false;
        movingRight = false;
        boolean physicsControlled = PhysicsHelper.getInstance().hasBody(this) && !showOnly();
        if (physicsControlled) {
            PhysicsHelper.getInstance().setVerticalSpeed(this, 0f);
        }
        speedY = 0f;
        momentX = 0f;
        airMomentumX = 0f;
    }

    private void settleIntoIdle() {
        if (unitState != UnitState.DEAD && unitState != UnitState.SPAWNING && unitState != UnitState.ATTACKING) {
            changeState(UnitState.IDLE);
        }
    }

    public boolean isBoss() {
        return boss;
    }

    public String getLibraryName() {
        return getClass().getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    public final String getResolvedLibraryName() {
        String localized = Messages.getDirectOrNull(getClass(), "name");
        String baseName = resolveLibraryBaseName(localized);
        String displayName = championType == null ? baseName : championType.getDisplayName() + " " + baseName;
        return Messages.capitalizeForDisplay(displayName);
    }

    private String resolveLibraryBaseName(String localizedName) {
        String directName = sanitizeLibraryName(localizedName);
        if (isUsableLibraryName(directName)) {
            return directName;
        }

        String translatedName = sanitizeLibraryName(Messages.maybeTranslate(getLibraryName()));
        if (isUsableLibraryName(translatedName)) {
            return translatedName;
        }

        String rawName = sanitizeLibraryName(getLibraryName());
        return isUsableLibraryName(rawName) ? rawName : getClass().getSimpleName();
    }

    private String sanitizeLibraryName(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isUsableLibraryName(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        if (value.length() > 1) {
            return true;
        }

        String rawLibraryName = sanitizeLibraryName(getLibraryName());
        return rawLibraryName == null || rawLibraryName.length() <= 1;
    }

    public String getLibraryDescription() {
        return "A hostile denizen of the dungeon.";
    }

    public final String getResolvedLibraryDescription() {
        String localized = Messages.getDirectOrNull(getClass(), "desc");
        return localized != null && !localized.isEmpty()
                ? localized
                : Messages.maybeTranslate(getLibraryDescription());
    }

    public String getLibraryStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("Health: ").append(getMaxHP());
        if (weapon != null) {
            stats.append("\nDamage: ")
                    .append(formatLibraryValue(weapon.min()))
                    .append("-")
                    .append(formatLibraryValue(weapon.max()));
        }
        stats.append("\nAttack: ").append(getAttackSkill(null, weapon));
        stats.append("\nDefense: ").append(getDefenseSkill(null));
        stats.append("\nDR: ").append(getDamageReduction());
        stats.append("\nMove speed: ").append(formatLibraryValue(getSpeedX()));
        stats.append("\nAttack speed: ").append(formatLibraryValue(getAttackSpeed()));
        stats.append("\nExperience: ").append(experience);
        stats.append("\nMovement: ").append(isCanFly() ? "Flying" : "Grounded");
        return stats.toString();
    }

    public GameFilm getLibraryPreview() {
        if (gf == null) {
            return null;
        }

        GameFilm preview = new GameFilm(gf.spriteString, gf.getWidth(), gf.getHeight(), 1f);
        preview.clipSizeX = gf.clipSizeX;
        preview.clipSizeY = gf.clipSizeY;
        preview.yClipOffset = gf.yClipOffset;
        preview.tileX = idleFrames != null && idleFrames.length > 0 ? idleFrames[0] : 0;
        preview.tileY = gf.tileY;
        return preview;
    }

    public int[] getLibraryGridFrames() {
        if (runFrames != null && runFrames.length > 0) {
            return runFrames;
        }

        if (idleFrames != null && idleFrames.length > 0) {
            return idleFrames;
        }

        return new int[]{0};
    }

    public int[] getLibraryOverlayFrames() {
        if (attackFrames != null && attackFrames.length > 0) {
            return attackFrames;
        }

        return getLibraryGridFrames();
    }

    protected String formatLibraryValue(float value) {
        if (Math.abs(value - Math.round(value)) < 0.05f) {
            return Integer.toString(Math.round(value));
        }

        return String.format(Locale.US, "%.1f", value);
    }
}

