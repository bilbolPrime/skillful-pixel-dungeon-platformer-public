package com.bilboldev.skillfulpixeldungeonplatformer.units.interactable;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MercenaryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MercenaryHelper.MercenaryType;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.MercenaryHireWindow;

public class MercenaryRecruit extends Interactable {
    private MercenaryType mercenaryType = MercenaryType.BRUTE;
    private int mercenaryLevel = 1;
    private int mercenaryPrice = MercenaryHelper.getPrice(MercenaryType.BRUTE, 1);
    private String mercenaryName = MercenaryHelper.getName(MercenaryType.BRUTE);
    private MeleeWeapon mercenaryWeapon;
    private Armor mercenaryArmor;
    private RangedWeapon mercenaryRangedWeapon;

    {
        hp = mhp = 1000;
        showOnly = true;
        facingRight = false;
        speedX = 350f;
    }

    public MercenaryRecruit() {
        applyMercenaryProfile(MercenaryType.BRUTE,
                MercenaryHelper.getName(MercenaryType.BRUTE),
                1,
                MercenaryHelper.getPrice(MercenaryType.BRUTE, 1),
                MercenaryHelper.createPrimaryWeapon(MercenaryType.BRUTE, 1),
                MercenaryHelper.createArmor(MercenaryType.BRUTE, 1),
                MercenaryHelper.createRangedWeapon(MercenaryType.BRUTE));
    }

    public static MercenaryRecruit createRandom(int depth) {
        Hero hero = UnitHelper.getInstance().getHero();
        MercenaryType type = MercenaryHelper.rollType(hero);
        if (type == null) {
            return null;
        }

        int level = MercenaryHelper.rollLevel(depth, hero);

        MercenaryRecruit recruit = new MercenaryRecruit();
        recruit.applyMercenaryProfile(type,
                MercenaryHelper.getName(type),
                level,
                MercenaryHelper.getPrice(type, level),
                MercenaryHelper.createPrimaryWeapon(type, level),
                MercenaryHelper.createArmor(type, level),
                MercenaryHelper.createRangedWeapon(type));
        return recruit;
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
        refreshAppearance();
    }

    @Override
    public void act(float delta) {
        updateArmorFrame();
        super.act(delta);
    }

    @Override
    public void interact() {
        WindowHelper.getInstance().addWindow(new MercenaryHireWindow(this).build());
    }

    public MercenaryAlly hire() {
        MercenaryAlly ally = new MercenaryAlly();
        ally.setPersistentId(getPersistentId());
        ally.setRoom(getRoom());
        ally.x = x;
        ally.y = y;
        ally.floorY = floorY;
        ally.facingRight = facingRight;
        ally.applyMercenaryProfile(mercenaryType,
                mercenaryName,
                mercenaryLevel,
                mercenaryPrice,
                mercenaryWeapon,
                mercenaryArmor,
                mercenaryRangedWeapon);
        UnitHelper.getInstance().addUnit(ally);
        UnitHelper.getInstance().removeUnit(this);
        MapHelper.getInstance().refreshHeroEnvironment();
        EffectsHelper.getInstance().message(
            ally,
            Messages.get("custom.ui.mercenary.joined",
                new Object[]{MercenaryHelper.getDisplayName(mercenaryType, mercenaryName)}),
            Color.GOLD,
            0f);
        return ally;
    }

    public String getHireText() {
        StringBuilder builder = new StringBuilder();
        builder.append("Hire ").append(mercenaryName).append("?\n\n");
        builder.append("Class: ").append(mercenaryType.getDisplayName()).append("\n");
        builder.append("Level: ").append(mercenaryLevel).append("\n");
        builder.append("Description: ").append(MercenaryHelper.getDescription(mercenaryType)).append("\n");
        builder.append("Gear: ").append(MercenaryHelper.getLoadoutSummary(mercenaryWeapon, mercenaryArmor, mercenaryRangedWeapon)).append("\n");
        builder.append("Special: ").append(MercenaryHelper.getSpecialAction(mercenaryType)).append("\n");
        builder.append("Price: ").append(mercenaryPrice).append(" gold");
        return builder.toString();
    }

    public MercenaryType getMercenaryType() {
        return mercenaryType;
    }

    public int getMercenaryLevel() {
        return mercenaryLevel;
    }

    public int getMercenaryPrice() {
        return mercenaryPrice;
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

    @Override
    public Armor getArmor() {
        return mercenaryArmor != null ? mercenaryArmor : super.getArmor();
    }

    public RangedWeapon getMercenaryRangedWeapon() {
        return mercenaryRangedWeapon;
    }

    private void refreshAppearance() {
        HeroClass heroClass = MercenaryHelper.getHeroClass(mercenaryType);
        gs = heroClass.getClassPortrait();
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

    private void updateArmorFrame() {
        if (gf != null) {
            gf.tileY = getArmor() != null ? getArmor().getTier() - 1 : 0;
        }
    }
}