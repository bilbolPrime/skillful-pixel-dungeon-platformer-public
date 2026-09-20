package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MercenaryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MercenaryHelper.MercenaryType;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.BirthdaySuit;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.Wand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfSlowness;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.FriendlyAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.MercenaryRangedAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class MercenaryAlly extends Mob {
    private static final float ROGUE_SHURIKEN_MIN_DISTANCE = ConstantsHelper.UNIT_DIMENSIONS * 1.5f;
    private static final float ROGUE_SHURIKEN_MAX_DISTANCE = ConstantsHelper.UNIT_DIMENSIONS * 4.5f;

    private MercenaryType mercenaryType = MercenaryType.BRUTE;
    private int mercenaryLevel = 1;
    private int mercenaryPrice = MercenaryHelper.getPrice(MercenaryType.BRUTE, 1);
    private String mercenaryName = MercenaryHelper.getName(MercenaryType.BRUTE);
    private MeleeWeapon mercenaryWeapon;
    private Armor mercenaryArmor;
    private RangedWeapon mercenaryRangedWeapon;
    private int mercenaryExperience;
    private float rogueShurikenCooldown;
    private float wizardSlowCooldown;

    {
        experience = 0;
        dropChance = 0;
        spawnSpace = false;
    }

    public MercenaryAlly() {
        applyMercenaryProfile(MercenaryType.BRUTE,
                MercenaryHelper.getName(MercenaryType.BRUTE),
                1,
                MercenaryHelper.getPrice(MercenaryType.BRUTE, 1),
                MercenaryHelper.createPrimaryWeapon(MercenaryType.BRUTE, 1),
                MercenaryHelper.createArmor(MercenaryType.BRUTE, 1),
                MercenaryHelper.createRangedWeapon(MercenaryType.BRUTE));
    }

    public void applyMercenaryProfile(MercenaryType type,
                                      String name,
                                      int level,
                                      int price,
                                      MeleeWeapon weapon,
                                      Armor armor,
                                      RangedWeapon rangedWeapon) {
        mercenaryType = type == null ? MercenaryType.BRUTE : type;
        mercenaryLevel = Math.max(1, level);
        mercenaryName = MercenaryHelper.normalizeMercenaryName(mercenaryType, name);
        mercenaryPrice = Math.max(0, price);
        mercenaryWeapon = MercenaryHelper.sanitizePrimaryWeapon(mercenaryType, mercenaryLevel, weapon);
        mercenaryArmor = MercenaryHelper.sanitizeArmor(mercenaryType, mercenaryLevel, armor);
        mercenaryRangedWeapon = MercenaryHelper.sanitizeRangedWeapon(mercenaryType, rangedWeapon);
        mercenaryExperience = 0;

        refreshAppearance();
        refreshStats(false);
        resetScaledStatBaseline();
        applyCurrentRunDifficulty();
        syncEquipment();

        makeFriendly();
        updateCombatMode();
    }

    public void levelWithHero() {
        mercenaryLevel = Math.max(1, mercenaryLevel + 1);
        refreshStats(true);
        resetScaledStatBaseline();
        applyCurrentRunDifficulty();
    }

    @Override
    public void setRoom(String room) {
        boolean changedRoom = getRoom() == null ? room != null : !getRoom().equals(room);
        super.setRoom(room);
        if (changedRoom) {

            if (ai != null) {
                boolean blind = ai.isBlind();
                ai.clearTarget();
                if (blind) ai.blinded();
                onTargetLost();
            }
            stopMercenaryMovement();
        }
    }

    @Override
    public void act(float delta) {
        rogueShurikenCooldown = Math.max(0f, rogueShurikenCooldown - delta);
        wizardSlowCooldown = Math.max(0f, wizardSlowCooldown - delta);
        updateArmorFrame();
        super.act(delta);

        if (mercenaryType == MercenaryType.ROGUE) {
            maybeThrowShuriken();
        }
    }

    public void performMercenaryAttack() {
        Unit target = resolveMercenaryAttackTarget();
        if (target == null || !canPerformMercenaryAttack()) {
            return;
        }

        facingRight = x < target.x;

        switch (mercenaryType) {
            case WIZARD:
                if (wizardSlowCooldown <= 0f && RandomHelper.getInstance().randomChance(30)) {
                    castSupportWand(new WandOfSlowness());
                    wizardSlowCooldown = 4.5f;
                }
                else if (canUsePrimaryWand()) {
                    attack();
                }
                break;
            case HUNTRESS:
                if (usesHuntressBow()) {
                    rangedAttack();
                }
                else {
                    attack();
                }
                break;
            default:
                attack();
                break;
        }
    }

    public float getPreferredMinDistance() {
        switch (mercenaryType) {
            case HUNTRESS:
                return usesHuntressBow() ? ConstantsHelper.UNIT_DIMENSIONS * 3f : ConstantsHelper.UNIT_DIMENSIONS;
            case WIZARD:
                return ConstantsHelper.UNIT_DIMENSIONS * 2.5f;
            default:
                return ConstantsHelper.UNIT_DIMENSIONS;
        }
    }

    public float getPreferredMaxDistance() {
        switch (mercenaryType) {
            case HUNTRESS:
                return usesHuntressBow() ? ConstantsHelper.UNIT_DIMENSIONS * 6f : ConstantsHelper.UNIT_DIMENSIONS * 4f;
            case WIZARD:
                return ConstantsHelper.UNIT_DIMENSIONS * 5f;
            default:
                return ConstantsHelper.UNIT_DIMENSIONS * 4f;
        }
    }

    public MercenaryType getMercenaryType() {
        return mercenaryType;
    }

    public int getMercenaryLevel() {
        return mercenaryLevel;
    }

    public int getMercenaryExperience() {
        return mercenaryExperience;
    }

    public void setMercenaryExperience(int mercenaryExperience) {
        this.mercenaryExperience = Math.max(0, mercenaryExperience);
        clampMercenaryExperience();
    }

    public int getMercenaryNextExp() {
        return 10 + ((mercenaryLevel - 1) * 20) + (mercenaryLevel > 2 ? 30 * (int) Math.pow(1.1, mercenaryLevel) : 0);
    }

    public String getMercenaryExperienceText() {
        return mercenaryExperience + " / " + getMercenaryNextExp();
    }

    public float getMercenaryExperienceRatio() {
        return Math.max(0f, Math.min(1f, mercenaryExperience / (float) Math.max(1, getMercenaryNextExp())));
    }

    public boolean earnMercenaryExp(int experienceAmount) {
        if (experienceAmount <= 0) {
            return false;
        }

        int totalExperience = mercenaryExperience + experienceAmount;
        boolean leveled = false;

        while (true) {
            int nextExp = getMercenaryNextExp();
            if (isAtHeroLevelCap()) {
                mercenaryExperience = Math.min(Math.max(0, nextExp - 1), totalExperience);
                return leveled;
            }

            if (totalExperience < nextExp) {
                mercenaryExperience = totalExperience;
                return leveled;
            }

            totalExperience -= nextExp;
            levelFromExperience();
            leveled = true;
        }
    }

    public int getMercenaryPrice() {
        return mercenaryPrice;
    }

    @Override
    public int getStrength() {
        return MercenaryHelper.getStrength(mercenaryType, mercenaryLevel);
    }

    @Override
    public boolean usesEquipmentStrengthRequirements() {
        return true;
    }

    public String getMercenaryName() {
        return mercenaryName;
    }

    public MeleeWeapon getMercenaryWeapon() {
        return mercenaryWeapon;
    }

    public Armor getMercenaryArmor() {
        return mercenaryArmor;
    }

    public RangedWeapon getMercenaryRangedWeapon() {
        return mercenaryRangedWeapon;
    }

    public RangedWeapon equipMercenaryRangedWeapon(RangedWeapon rangedWeapon) {

        if (rangedWeapon instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun) return null;
        RangedWeapon previousRangedWeapon = mercenaryRangedWeapon;
        mercenaryRangedWeapon = MercenaryHelper.sanitizeRangedWeapon(mercenaryType, rangedWeapon);
        if (mercenaryRangedWeapon != null) {
            mercenaryRangedWeapon.identify();
            mercenaryRangedWeapon.setEquippedState(false);
        }
        syncEquipment();
        return previousRangedWeapon;
    }

    public int getWeaponStrengthShortfall() {
        return mercenaryWeapon == null ? 0 : mercenaryWeapon.getStrengthShortfall();
    }

    public int getArmorStrengthShortfall() {
        return mercenaryArmor == null ? 0 : mercenaryArmor.getStrengthShortfall();
    }

    public boolean isOverwhelmed() {
        return getWeaponStrengthShortfall() > 0 || getArmorStrengthShortfall() > 0;
    }

    public MeleeWeapon equipMercenaryWeapon(MeleeWeapon weapon) {
        MeleeWeapon previousWeapon = mercenaryWeapon;
        mercenaryWeapon = weapon;
        if (mercenaryWeapon != null) {
            mercenaryWeapon.identify();
            mercenaryWeapon.setEquippedState(false);
        }
        syncEquipment();
        return previousWeapon;
    }

    public Armor equipMercenaryArmor(Armor armor) {
        Armor previousArmor = mercenaryArmor;
        mercenaryArmor = armor;
        if (mercenaryArmor != null) {
            mercenaryArmor.identify();
            mercenaryArmor.setEquippedState(false);
        }
        syncEquipment();
        return previousArmor;
    }

    public MeleeWeapon clearMercenaryWeapon() {
        MeleeWeapon previousWeapon = mercenaryWeapon;
        mercenaryWeapon = null;
        syncEquipment();
        return previousWeapon;
    }

    public Armor clearMercenaryArmor() {
        Armor previousArmor = mercenaryArmor;
        mercenaryArmor = null;
        syncEquipment();
        return previousArmor;
    }

    public RangedWeapon clearMercenaryRangedWeapon() {
        RangedWeapon previousRangedWeapon = mercenaryRangedWeapon;
        mercenaryRangedWeapon = null;
        syncEquipment();
        return previousRangedWeapon;
    }

    @Override
    public String getLibraryDescription() {
        return MercenaryHelper.getDescription(mercenaryType);
    }

    @Override
    public void die() {
        dropEquipmentOnDeath();
        ai = null;
        movingLeft = false;
        movingRight = false;
        PhysicsHelper.getInstance().unregister(this);
    }

    private void dropEquipmentOnDeath() {
        dropEquipmentItem(mercenaryWeapon);
        dropEquipmentItem(mercenaryArmor);
        if (MercenaryHelper.isBowWeapon(mercenaryRangedWeapon)) {
            dropEquipmentItem(mercenaryRangedWeapon);
        }
    }

    private void dropEquipmentItem(Item item) {
        if (item == null || getRoom() == null) {
            return;
        }

        if (item instanceof EquipableItem) {
            ((EquipableItem) item).setOwner(null);
            ((EquipableItem) item).setEquippedState(false);
        }

        item.drop(x, y, getRoom());
    }

    private void refreshAppearance() {
        HeroClass heroClass = MercenaryHelper.getHeroClass(mercenaryType);
        gf = new GameFilm(heroClass.getFilm(), 256, 128, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 15;
        gf.yClipOffset = 1;
        idleFrames = new int[]{0, 1, 1};
        runFrames = new int[]{2, 3, 4, 5, 6, 7};
        attackFrames = new int[]{13, 14, 15, 15};
        dieFrames = new int[]{8, 9, 10, 11, 12};
        jumpFrames = new int[]{4};
        updateArmorFrame();
    }

    private void refreshStats(boolean preserveCurrentResources) {
        HeroClass heroClass = MercenaryHelper.getHeroClass(mercenaryType);
        int previousMaxHp = mhp;
        int previousHp = hp;
        int previousMaxMp = mmp;
        int previousMp = mp;

        mhp = MercenaryHelper.getHealth(mercenaryType, mercenaryLevel);
        if (!preserveCurrentResources || previousMaxHp <= 0) {
            hp = mhp;
        }
        else {
            hp = Math.min(mhp, Math.max(0, previousHp + Math.max(0, mhp - previousMaxHp)));
        }

        mmp = heroClass.getMana(mercenaryLevel);
        if (!preserveCurrentResources || previousMaxMp <= 0) {
            mp = mmp;
        }
        else {
            mp = Math.min(mmp, Math.max(0, previousMp + Math.max(0, mmp - previousMaxMp)));
        }
        attackSpeed = MercenaryHelper.getAttackSpeed(mercenaryType);
        speedX = MercenaryHelper.getMoveSpeed(mercenaryType);
        setBaseAttackSkill(MercenaryHelper.getAttackSkill(mercenaryType, mercenaryLevel));
        setBaseDefenseSkill(MercenaryHelper.getDefenseSkill(mercenaryType, mercenaryLevel));
    }

    private void updateArmorFrame() {
        if (gf != null) {
            gf.tileY = getArmor() != null ? getArmor().getTier() - 1 : 0;
        }
    }

    private boolean isDistanceMercenary() {
        return mercenaryType == MercenaryType.WIZARD || usesHuntressBow();
    }

    private void syncEquipment() {
        setWeapon(getCombatWeapon());
        setArmor(mercenaryArmor != null ? mercenaryArmor : createFallbackArmor());
        setRangedWeapon(mercenaryRangedWeapon);
        updateArmorFrame();
        updateCombatMode();
    }

    private MeleeWeapon getCombatWeapon() {
        if (mercenaryWeapon == null) {
            return createFallbackWeapon();
        }

        if (mercenaryType != MercenaryType.WIZARD && mercenaryWeapon instanceof Wand) {
            return createFallbackWeapon();
        }

        return mercenaryWeapon;
    }

    private MeleeWeapon createFallbackWeapon() {
        return (MeleeWeapon) new MeleeAttack().setOwner(this);
    }

    private Armor createFallbackArmor() {
        return (Armor) new BirthdaySuit().setOwner(this);
    }

    private void levelFromExperience() {
        mercenaryLevel = Math.max(1, mercenaryLevel + 1);
        refreshStats(true);
        resetScaledStatBaseline();
        applyCurrentRunDifficulty();
        syncEquipment();
        clampMercenaryExperience();
        EffectsHelper.getInstance().levelUp(this);
        EffectsHelper.getInstance().message(this, "LEVEL UP!", Color.GREEN, 0f);
    }

    private boolean isAtHeroLevelCap() {
        if (UnitHelper.getInstance().getHero() == null) {
            return false;
        }

        return mercenaryLevel >= UnitHelper.getInstance().getHero().getLevel();
    }

    private void clampMercenaryExperience() {
        mercenaryExperience = Math.min(mercenaryExperience, Math.max(0, getMercenaryNextExp() - 1));
        if (isAtHeroLevelCap()) {
            mercenaryExperience = Math.min(mercenaryExperience, Math.max(0, getMercenaryNextExp() - 1));
        }
    }

    private void maybeThrowShuriken() {

        if (ai == null || ai.getOther() == null) return;
        if (rogueShurikenCooldown > 0f || mercenaryRangedWeapon == null || isAttacking()) {
            return;
        }

        Unit target = resolveMercenaryAttackTarget();
        if (target == null) {
            return;
        }

        float horizontalDistance = Math.abs(target.x - x);
        float verticalDistance = Math.abs(target.y - y);
        if (horizontalDistance < ROGUE_SHURIKEN_MIN_DISTANCE || horizontalDistance > ROGUE_SHURIKEN_MAX_DISTANCE
                || verticalDistance > ConstantsHelper.UNIT_DIMENSIONS * 1.5f || !canAttack()) {
            return;
        }

        facingRight = x < target.x;
        rangedAttack();
        rogueShurikenCooldown = 2f + RandomHelper.getInstance().randomFloat(1f);
    }

    private void castSupportWand(Wand wand) {
        if (wand == null || !canAttack()) {
            return;
        }

        wand.setOwner(this);
        wand.use();
        wand.startCooldown(getAttackAnimationDurationSeconds());
        startAttackAnimation(Wand.CAST_ATTACK_DURATION_SECONDS);
    }

    private boolean canPerformMercenaryAttack() {
        if (!canAttack()) {
            return false;
        }

        if (mercenaryType == MercenaryType.WIZARD && !canUsePrimaryWand() && wizardSlowCooldown > 0f) {
            return false;
        }

        return true;
    }

    private Unit resolveMercenaryAttackTarget() {
        if (ai == null) {
            stopMercenaryMovement();
            return null;
        }

        Unit target = ai.getOther();
        if (ai.canTarget(target)) {
            return target;
        }

        ai.clearTarget();
        stopMercenaryMovement();
        return null;
    }

    private void stopMercenaryMovement() {
        movingLeft = false;
        movingRight = false;
    }

    private boolean usesHuntressBow() {
        return mercenaryType == MercenaryType.HUNTRESS
                && mercenaryWeapon == null
                && MercenaryHelper.isBowWeapon(mercenaryRangedWeapon);
    }

    private void updateCombatMode() {
        ai = isDistanceMercenary() ? new MercenaryRangedAI(this) : new FriendlyAI(this);
    }

    private boolean canUsePrimaryWand() {
        return !(mercenaryWeapon instanceof Wand) || ((Wand) mercenaryWeapon).canCast();
    }
}
