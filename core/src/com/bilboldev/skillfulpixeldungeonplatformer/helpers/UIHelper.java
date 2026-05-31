package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.Achievement;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SkillsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.Wand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.PauseMenuRowButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.InputGestureListener;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.TitleScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Aggression;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.BattleMage;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.BerserkerBuff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.FireMastery;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.GladiatorBuff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.GrandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Health;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Mana;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.ManaRegeneration;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Mastery;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Regeneration;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Toughness;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.WandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Warlock;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ActiveSkill;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.InventoryWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.MercenaryWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.PauseMenuWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class UIHelper {
    private static final int BOSS_SLAIN_BANNER_X = 6;
    private static final int BOSS_SLAIN_BANNER_Y = 68;
    private static final int BOSS_SLAIN_BANNER_WIDTH = 122;
    private static final int BOSS_SLAIN_BANNER_HEIGHT = 39;
    private static final float BOSS_SLAIN_BANNER_SCALE = 4.5f;
    private static final float BOSS_SLAIN_BANNER_DURATION = 4f;
    private static final float ACHIEVEMENT_BANNER_WIDTH = 720f;
    private static final float ACHIEVEMENT_BANNER_HEIGHT = 130f;
    private static final float ACHIEVEMENT_BANNER_ICON_SIZE = 96f;
    private static final float ACHIEVEMENT_BANNER_DURATION = 3f;
    private static final float ACHIEVEMENT_BANNER_Y = ConstantsHelper.SCREEN_HEIGHT - 250f;
    private static final float DEPTH_TRANSITION_BANNER_DURATION = 1.4f;
    private static final float DEPTH_TRANSITION_FLASH_PERIOD_SECONDS = 1.2f;
    private static final int GAME_OVER_BANNER_X = 28;
    private static final int GAME_OVER_BANNER_Y = 113;
    private static final int GAME_OVER_BANNER_WIDTH = 72;
    private static final int GAME_OVER_BANNER_HEIGHT = 19;
    private static final float GAME_OVER_BANNER_SCALE = 8f;
    private static final float DESKTOP_ACTION_SLOT_SIZE = ConstantsHelper.TILE * 0.6f;
    private static final float DESKTOP_ACTION_SLOT_GAP = DESKTOP_ACTION_SLOT_SIZE * 0.2f;
    private static final int DESKTOP_ACTIONS_PER_ROW = 5;
    private static final float DESKTOP_ACTION_ROW_GAP = ConstantsHelper.TILE * 0.7f;
    private static final float DESKTOP_ACTION_ICON_SCALE = 1f;
    private static final float DESKTOP_ACTION_LABEL_OFFSET = 20f;
    private static final float DESKTOP_ACTION_COOLDOWN_ALPHA = 0.5f;
    private static final float DESKTOP_ACTION_MANA_BADGE_PADDING_X = 8f;
    private static final float DESKTOP_ACTION_MANA_BADGE_PADDING_Y = 5f;
    private static final float DESKTOP_ACTION_MANA_BADGE_ALPHA = 0.8f;
    private static final float MERC_HUD_MARGIN_RATIO = 0.1f;
    private static final float MERC_HUD_PANEL_WIDTH = 330f;
    private static final float MERC_HUD_PANEL_HEIGHT = 110f;
    private static final float MERC_HUD_PANEL_GAP = 20f;
    private static final float MERC_HUD_PORTRAIT_SIZE = 86f;
    private static final float MERC_HUD_PANEL_PADDING = 12f;
    private static final float MERC_HUD_TEXT_GAP = 14f;
    private static final float MERC_HUD_HP_BAR_WIDTH = 190f;
    private static final float MERC_HUD_HP_BAR_HEIGHT = 15f;
    private static final float MERC_HUD_EXP_TEXT_GAP = 6f;
    private static final float MERC_HUD_OVERLAY_EXTRA_HEIGHT = (MERC_HUD_PANEL_HEIGHT - MERC_HUD_PORTRAIT_SIZE) / 2f;
    private static final float MERC_HUD_OVERLAY_ALPHA = 0.45f;
    private static final float MERC_HUD_LEVEL_TEXT_SIZE = 1.9f;
    private static final float MERC_HUD_HP_TEXT_SIZE = 1.25f;
    private static final float DESKTOP_DEPTH_ICON_RAISE_RATIO = 0.2f;
    private static final float DESKTOP_KEY_TEXT_HEIGHT_RATIO = -0.25f;
    private static final float DESKTOP_CONSUMABLE_SLOT_GAP = 10f;
    private static final float DESKTOP_CONSUMABLE_LABEL_GAP = 18f;
    private static final float DESKTOP_CONSUMABLE_INVENTORY_GAP = 16f;
    private static final float HUD_BAR_WIDTH = 500f;
    private static final float HUD_BAR_HEIGHT = 30f;
    private static final float HUD_BAR_X = 1075f;
    private static final float HP_BAR_Y = 230f;
    private static final float MP_BAR_Y = 200f;
    private static final float HUD_BAR_VALUE_PADDING = 10f;
    private static final float HUD_BAR_VALUE_RIGHT_PADDING = 30f;
    private static final float HUD_BAR_VALUE_MAX_SIZE = 2.1f;
    private static final float HUD_BAR_VALUE_MIN_SIZE = 1.2f;
    private static final float LOW_RESOURCE_THRESHOLD = 0.25f;
    private static final float LOW_RESOURCE_PULSE_SPEED = 0.01f;
    private static final float LOW_RESOURCE_PULSE_MIN_BRIGHTNESS = 0.68f;
    private static final float TOUCH_CONTEXT_BUTTON_X = 220f;
    private static final float TOUCH_CONTEXT_BUTTON_Y = 500f;
    private static final float TOUCH_CONTEXT_BUTTON_SIZE = 200f;
    private static final float TOUCH_CONTEXT_BUTTON_GAP = 45f;
    private static final float TOUCH_RIGHT_PRIMARY_BUTTON_SIZE = 300f;
    private static final float TOUCH_RIGHT_SECONDARY_BUTTON_SIZE = 150f;
    private static final float TOUCH_RIGHT_BUTTON_MARGIN = 75f;
    private static final float TOUCH_RIGHT_BUTTON_VERTICAL_OFFSET = 60f;
    private static final float TOUCH_RIGHT_BUTTON_GAP = 24f;
    private static final float TOUCH_RIGHT_ORBIT_BUTTON_SPACING = 12f;
    private static final int TOUCH_RIGHT_ORBIT_BUTTON_COUNT = 7;
    private static final float TOUCH_RIGHT_VISIBLE_ORBIT_RADIUS_SCALE = 0.75f;
    private static final float TOUCH_RIGHT_ORBIT_START_ANGLE_DEGREES = 210f;
    private static final float TOUCH_RIGHT_ORBIT_END_ANGLE_DEGREES = 60f;
    private static final float TOUCH_RIGHT_PRIMARY_ICON_SIZE = ConstantsHelper.TILE;
    private static final float TOUCH_RIGHT_SECONDARY_ICON_SIZE = ConstantsHelper.TILE / 2f;
    private static final float TOUCH_HUD_AVAILABLE_ALPHA = 0.5f;
    private static final float TOUCH_HUD_PRESSED_ALPHA = 0.9f;
    private static final float TOUCH_HUD_UNAVAILABLE_ALPHA = 0.1f;
    private static final float DESKTOP_ACTION_ROW_OFFSET = ConstantsHelper.TILE * 1.5f;
    private static final float DESKTOP_ACTION_ADDITIONAL_ROW_DROP = ConstantsHelper.TILE * 0.5f;
    private static final float GAME_EXIT_BUTTON_SIZE = 50f;
    private static final float GAME_EXIT_BUTTON_MARGIN = 40f;
    private static final Color ACTIVE_HP_FILL_COLOR = new Color(0.58f, 0.06f, 0.06f, 1f);
    private static final Color ACTIVE_MP_FILL_COLOR = new Color(1f, 1f, 1f, 1f);

    private ActionButton jumpButton, meleeButton, rangedButton, exitButton;
    private ActionButton healthPotionButton, manaPotionButton, foodButton;
    private ActionButton doorButton, signButton, backPackButton, pickupButton, interactButton;
    private PauseMenuRowButton mainMenuButton;
    private GameSprite bossSlainBanner;
    private float bossSlainBannerTimer;
    private GameSprite achievementBannerBackground;
    private GameSprite achievementBannerIcon;
    private String achievementBannerName;
    private float achievementBannerTimer;
    private GameSprite depthTransitionBannerBackground;
    private String depthTransitionBannerText;
    private float depthTransitionBannerFrame;
    private float depthTransitionBannerTimer;
    private Runnable depthTransitionBannerCompleteAction;
    private GameSprite gameOverBanner;
    private Button pressedButton;
    private final IntMap<Button> pressedButtonsByPointer = new IntMap<>();

    private ArrayList<Button> buttons;
    private ArrayList<ActionButton> mercenaryHudButtons;

    private GameSprite hpBar, mpBar;
    private final ActionButton[] quickSkillButtons;
    private GameSprite desktopFoodIcon;
    private GameSprite desktopHealthPotionIcon;
    private GameSprite desktopManaPotionIcon;
    private GameSprite desktopDepthIcon;
    private GameSprite desktopKeyIcon;

    private String expString, inventoryString;
    private float expStringXOffset = 0, inventoryStringXOffset;

    GameSprite heroPortrait = new GameSprite("images/units/warrior/portrait.png", 200, 200, 0.8f);
    private GameSprite mercHpBar;
    private GameSprite mercWarriorPortrait;
    private GameSprite mercRoguePortrait;
    private GameSprite mercWizardPortrait;
    private GameSprite mercHuntressPortrait;

    private static final UIHelper ourInstance = new UIHelper();

    public static UIHelper getInstance() {
        return ourInstance;
    }

    private UIHelper() {
        buttons = new ArrayList<>();
        mercenaryHudButtons = new ArrayList<>();
        quickSkillButtons = new ActionButton[Hero.QUICK_SKILL_SLOT_COUNT];
        reloadVisualAssets();
    }

    public void reloadVisualAssets() {
        mercHpBar = new GameSprite("images/misc/hp_bar.png", MERC_HUD_HP_BAR_WIDTH, MERC_HUD_HP_BAR_HEIGHT);
        mercWarriorPortrait = new GameSprite("images/units/warrior/portrait.png", MERC_HUD_PORTRAIT_SIZE, MERC_HUD_PORTRAIT_SIZE);
        mercRoguePortrait = new GameSprite("images/units/rogue/portrait.png", MERC_HUD_PORTRAIT_SIZE, MERC_HUD_PORTRAIT_SIZE);
        mercWizardPortrait = new GameSprite("images/units/wizard/portrait.png", MERC_HUD_PORTRAIT_SIZE, MERC_HUD_PORTRAIT_SIZE);
        mercHuntressPortrait = new GameSprite("images/units/huntress/portrait.png", MERC_HUD_PORTRAIT_SIZE, MERC_HUD_PORTRAIT_SIZE);
    }

    public void addButton(Button button){
        buttons.add(button);
    }

    public void clearButtons(){
        buttons.clear();
        mercenaryHudButtons.clear();
        jumpButton = null;
        meleeButton = null;
        rangedButton = null;
        healthPotionButton = null;
        manaPotionButton = null;
        foodButton = null;
        exitButton = null;
        for (int slotIndex = 0; slotIndex < quickSkillButtons.length; slotIndex++) {
            quickSkillButtons[slotIndex] = null;
        }
        mainMenuButton = null;
        bossSlainBanner = null;
        bossSlainBannerTimer = 0f;
        achievementBannerBackground = null;
        achievementBannerIcon = null;
        achievementBannerName = null;
        achievementBannerTimer = 0f;
        depthTransitionBannerBackground = null;
        depthTransitionBannerText = null;
        depthTransitionBannerFrame = 0f;
        depthTransitionBannerTimer = 0f;
        depthTransitionBannerCompleteAction = null;
        gameOverBanner = null;
        desktopFoodIcon = null;
        desktopHealthPotionIcon = null;
        desktopManaPotionIcon = null;
        desktopDepthIcon = null;
        desktopKeyIcon = null;
        clearPressedButton();
    }

    public void basicButtons(){
        clearButtons();
        boolean touchHudEnabled = SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled();

        heroPortrait = UnitHelper.getInstance().getHero().getHeroClass().getClassPortrait().clone();
        heroPortrait.setPosition(800, 75);
        float meleeButtonX = touchHudEnabled ? touchPrimaryCombatButtonX() : 2075f;
        float meleeButtonY = touchHudEnabled ? touchPrimaryCombatButtonY() : 325f;
        float meleeButtonSize = touchHudEnabled ? TOUCH_RIGHT_PRIMARY_BUTTON_SIZE : TOUCH_RIGHT_SECONDARY_BUTTON_SIZE;

        float jumpButtonX = touchHudEnabled ? touchOrbitButtonX(0) : 1825f;
        float jumpButtonY = touchHudEnabled ? touchOrbitButtonY(0) : 150f;
        jumpButton = new ActionButton(jumpButtonX, jumpButtonY, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, "images/buttons/blank-button.png", "images/buttons/blank-button-pressed.png"){
            @Override
            public void click(){
                UnitHelper.getInstance().getHero().jump();
            }
        };

        meleeButton = new ActionButton(meleeButtonX, meleeButtonY, meleeButtonSize, meleeButtonSize, "images/buttons/blank-button.png", "images/buttons/blank-button-pressed.png"){
            @Override
            public void click(){
                UnitHelper.getInstance().getHero().attack();
            }
        };

        float rangedButtonX = touchHudEnabled ? touchOrbitButtonX(1) : 2075f;
        float rangedButtonY = touchHudEnabled ? touchOrbitButtonY(1) : 600f;
        rangedButton = new ActionButton(rangedButtonX, rangedButtonY, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, "images/buttons/blank-button.png", "images/buttons/blank-button-pressed.png"){
            @Override
            public void click(){
                UnitHelper.getInstance().getHero().rangedAttack();
            }
        };
        rangedButton.setUseItemCountForAvailability(true);
        rangedButton.setItemCount(0);

        for (int slotIndex = 0; slotIndex < quickSkillButtons.length; slotIndex++) {
            final int quickSkillSlotIndex = slotIndex;
            float quickSkillX = 0f;
            float quickSkillY = 0f;
            if (touchHudEnabled) {
                if (slotIndex == 0) {
                    quickSkillX = touchOrbitButtonX(2);
                    quickSkillY = touchOrbitButtonY(2);
                } else if (slotIndex == 1) {
                    quickSkillX = touchOrbitButtonX(3);
                    quickSkillY = touchOrbitButtonY(3);
                }
            }

            quickSkillButtons[slotIndex] = new ActionButton(quickSkillX, quickSkillY, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, "images/buttons/blank-button.png", "images/buttons/blank-button-pressed.png"){
                @Override
                public void click(){
                    UnitHelper.getInstance().getHero().useQuickSkillSlot(quickSkillSlotIndex);
                }
            };

            quickSkillButtons[slotIndex].setActionAvailable(false);
        }

        healthPotionButton = new ActionButton(touchOrbitButtonX(4), touchOrbitButtonY(4), TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, "images/buttons/blank-button.png", "images/buttons/blank-button-pressed.png") {
            @Override
            public void click() {
                performHealthPotionAction();
            }
        };
        healthPotionButton.setUseItemCountForAvailability(true);

        manaPotionButton = new ActionButton(touchOrbitButtonX(5), touchOrbitButtonY(5), TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, "images/buttons/blank-button.png", "images/buttons/blank-button-pressed.png") {
            @Override
            public void click() {
                performManaPotionAction();
            }
        };
        manaPotionButton.setUseItemCountForAvailability(true);

        foodButton = new ActionButton(touchOrbitButtonX(6), touchOrbitButtonY(6), TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, "images/buttons/blank-button.png", "images/buttons/blank-button-pressed.png") {
            @Override
            public void click() {
                performEatFoodAction();
            }
        };
        foodButton.setUseItemCountForAvailability(true);

        doorButton = new ActionButton(
                TOUCH_CONTEXT_BUTTON_X + TOUCH_CONTEXT_BUTTON_SIZE + TOUCH_CONTEXT_BUTTON_GAP,
                TOUCH_CONTEXT_BUTTON_Y,
                TOUCH_CONTEXT_BUTTON_SIZE,
                TOUCH_CONTEXT_BUTTON_SIZE,
                "images/buttons/blank-button.png",
                "images/buttons/blank-button-pressed.png"){
            @Override
            public void click(){
                MapHelper.getInstance().enterDoor();
            }
        };

        GameSprite gs = new GameSprite("images/buttons/button-door.png", ConstantsHelper.TILE /2, ConstantsHelper.TILE /2);
        gs.translate(65, 65);
        doorButton.addGameSprite(gs);
        doorButton.disable();

        signButton = new ActionButton(
                TOUCH_CONTEXT_BUTTON_X,
                TOUCH_CONTEXT_BUTTON_Y,
                TOUCH_CONTEXT_BUTTON_SIZE,
                TOUCH_CONTEXT_BUTTON_SIZE,
                "images/buttons/blank-button.png",
                "images/buttons/blank-button-pressed.png"){
            @Override
            public void click(){
                MapHelper.getInstance().readSign();
            }
        };

        gs = new GameSprite("images/buttons/button-sign.png", ConstantsHelper.TILE /2, ConstantsHelper.TILE /2);
        gs.translate(65, 65);
        signButton.addGameSprite(gs);
        signButton.disable();

        pickupButton = new ActionButton(
                TOUCH_CONTEXT_BUTTON_X,
                TOUCH_CONTEXT_BUTTON_Y,
                TOUCH_CONTEXT_BUTTON_SIZE,
                TOUCH_CONTEXT_BUTTON_SIZE,
                "images/buttons/blank-button.png",
                "images/buttons/blank-button-pressed.png"){
            @Override
            public void click(){
                MapHelper.getInstance().openItemWindow();
            }
        };

        pickupButton.disable();

        interactButton = new ActionButton(
                TOUCH_CONTEXT_BUTTON_X,
                TOUCH_CONTEXT_BUTTON_Y,
                TOUCH_CONTEXT_BUTTON_SIZE,
                TOUCH_CONTEXT_BUTTON_SIZE,
                "images/buttons/blank-button.png",
                "images/buttons/blank-button-pressed.png"){
            @Override
            public void click(){
                MapHelper.getInstance().interact();
            }
        };

        interactButton.disable();

        backPackButton = new ActionButton(550, 75, 200, 200, "images/buttons/back-pack.png", "images/buttons/back-pack.png"){
            @Override
            public void click(){
                WindowHelper.getInstance().addWindow(new InventoryWindow(UnitHelper.getInstance().getHero().getHeroClass(),2000, 1000).build());
            }
        };

        exitButton = new ActionButton(
                ConstantsHelper.SCREEN_WIDTH - GAME_EXIT_BUTTON_MARGIN - GAME_EXIT_BUTTON_SIZE,
                ConstantsHelper.SCREEN_HEIGHT - GAME_EXIT_BUTTON_MARGIN - GAME_EXIT_BUTTON_SIZE,
                GAME_EXIT_BUTTON_SIZE,
                GAME_EXIT_BUTTON_SIZE,
                "images/menu/exit.png",
                "images/menu/exit.png") {
            {
                enableUiPressFeedback();
            }

            @Override
            public void click() {
                WindowHelper.getInstance().addWindow(new PauseMenuWindow().build());
            }
        };

        if (touchHudEnabled) {
            applyTouchHudButtonStyle(jumpButton);
            applyTouchHudButtonStyle(meleeButton);
            applyTouchHudButtonStyle(rangedButton);
            applyTouchHudButtonStyle(healthPotionButton);
            applyTouchHudButtonStyle(manaPotionButton);
            applyTouchHudButtonStyle(foodButton);
            applyTouchHudButtonStyle(doorButton);
            applyTouchHudButtonStyle(signButton);
            applyTouchHudButtonStyle(pickupButton);
            applyTouchHudButtonStyle(interactButton);
            applyTouchHudButtonStyle(backPackButton);
            applyTouchHudButtonStyle(exitButton);
            for (ActionButton quickSkillButton : quickSkillButtons) {
                applyTouchHudButtonStyle(quickSkillButton);
            }
        }

        gs = new GameSprite("images/units/" + UnitHelper.getInstance().getHero().getHeroClass().getAssetFolderName() + "/button-jump.png", TOUCH_RIGHT_SECONDARY_ICON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);
        centerButtonSprite(jumpButton, gs, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);

        gs = new GameSprite("skill.png", touchHudEnabled ? TOUCH_RIGHT_PRIMARY_ICON_SIZE : TOUCH_RIGHT_SECONDARY_ICON_SIZE, touchHudEnabled ? TOUCH_RIGHT_PRIMARY_ICON_SIZE : TOUCH_RIGHT_SECONDARY_ICON_SIZE);
        centerButtonSprite(meleeButton, gs, meleeButtonSize, touchHudEnabled ? TOUCH_RIGHT_PRIMARY_ICON_SIZE : TOUCH_RIGHT_SECONDARY_ICON_SIZE);

        setDefaultRangedButtonIcon();

        gs = new GameSprite("images/items/health-potion.png", TOUCH_RIGHT_SECONDARY_ICON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);
        centerButtonSprite(healthPotionButton, gs, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);

        gs = new GameSprite("images/items/mana-potion.png", TOUCH_RIGHT_SECONDARY_ICON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);
        centerButtonSprite(manaPotionButton, gs, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);

        gs = new GameSprite("images/items/food.png", TOUCH_RIGHT_SECONDARY_ICON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);
        centerButtonSprite(foodButton, gs, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);

        layoutTouchOrbitButtons();

        if (touchHudEnabled) {
            buttons.add(meleeButton);
            buttons.add(jumpButton);
            buttons.add(rangedButton);
            buttons.add(quickSkillButtons[0]);
            buttons.add(quickSkillButtons[1]);
            buttons.add(healthPotionButton);
            buttons.add(manaPotionButton);
            buttons.add(foodButton);
            buttons.add(doorButton);
            buttons.add(signButton);
            buttons.add(pickupButton);
            buttons.add(interactButton);
        }

        buttons.add(backPackButton);
        buttons.add(exitButton);

        float mainMenuButtonX = (ConstantsHelper.SCREEN_WIDTH - 900f) / 2f;
        float mainMenuButtonY = (ConstantsHelper.SCREEN_HEIGHT - 100f) / 2f;

        mainMenuButton = new PauseMenuRowButton(mainMenuButtonX,
                mainMenuButtonY,
                900f,
                100f) {
            @Override
            public void clicked() {
                WindowHelper.getInstance().hideAll();
                SkillfulPixelDungeonPlatformer.transition(new TitleScreen(), true);
            }
        }.setCenteredText("MAIN MENU");

        gameOverBanner = buildGameOverBanner(mainMenuButtonY);

        hpBar = new GameSprite("images/misc/hp_bar.png", 400, 30);
        hpBar.setPosition(HUD_BAR_X, HP_BAR_Y);
        mpBar = new GameSprite("images/misc/mana_bar.png", 400, 30);
        mpBar.setPosition(HUD_BAR_X, MP_BAR_Y);
        desktopFoodIcon = new GameSprite("images/items/food.png", 45, 45);
        desktopHealthPotionIcon = new GameSprite("images/items/health-potion.png", 45, 45);
        desktopManaPotionIcon = new GameSprite("images/items/mana-potion.png", 45, 45);
        desktopDepthIcon = new GameSprite("images/misc/depth.png", 45, 45);
        desktopKeyIcon = new GameSprite("images/misc/keys.png", 45, 45);
        updateTouchConsumableButtons();
        updateTouchActionButtonAvailability();
    }

    public void drawButtons(Batch batch){
        if (bossSlainBanner != null && bossSlainBannerTimer > 0f && !heroDead()) {
            bossSlainBanner.draw(batch);
            bossSlainBannerTimer = Math.max(0f, bossSlainBannerTimer - Gdx.graphics.getDeltaTime());
            if (bossSlainBannerTimer == 0f) {
                bossSlainBanner = null;
            }
        }

        if (achievementBannerBackground != null && achievementBannerIcon != null && achievementBannerTimer > 0f && !heroDead()) {
            drawAchievementBanner(batch);
        }

        if (depthTransitionBannerBackground != null && depthTransitionBannerTimer > 0f && !heroDead()) {
            drawDepthTransitionBanner(batch);
            return;
        }

        if (heroDead()) {
            if (gameOverBanner != null) {
                gameOverBanner.draw(batch);
            }
            if (mainMenuButton != null) {
                mainMenuButton.draw(batch);
            }
            return;
        }

        updateTouchActionButtonAvailability();

        for(Button button : buttons){
            button.draw(batch);
        }

        if(UnitHelper.getInstance().getHero() == null){
            return;
        }

        heroPortrait.draw(batch);

        drawMercenaryHud(batch);

        drawResourceBar(batch,
                hpBar,
                HUD_BAR_X,
                HP_BAR_Y,
                UnitHelper.getInstance().getHero().getHP(),
                UnitHelper.getInstance().getHero().getMaxHP(),
                ACTIVE_HP_FILL_COLOR);
        drawResourceBar(batch,
                mpBar,
                HUD_BAR_X,
                MP_BAR_Y,
                UnitHelper.getInstance().getHero().getMp(),
                UnitHelper.getInstance().getHero().getMmp(),
                ACTIVE_MP_FILL_COLOR);

        if (SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()) {
            drawTouchStatusBadges(batch);
        }

        int offsetX = 0;
        int offsetY = 0;
        for(Buff buff : UnitHelper.getInstance().getHero().getBuffs()){
            if(!shouldDrawHudBuff(buff)){
                continue;
            }

            GameSprite gs = buff.getGameSprite();
            gs.setHeight(50);
            gs.setWidth(50);
            gs.setPosition(1075 + offsetX, 125 + offsetY);
            gs.draw(batch);

            offsetX += 75;

            if(offsetX == 75 * 7){
                offsetX = 0;
                offsetY -= 75;
            }
        }

        for (Skill skill : getPersistentHudSkills(UnitHelper.getInstance().getHero())) {
            GameSprite gs = skill.getGameSprite();
            gs.setHeight(50);
            gs.setWidth(50);
            gs.setPosition(1075 + offsetX, 125 + offsetY);
            gs.draw(batch);

            offsetX += 75;
            if(offsetX == 75 * 7){
                offsetX = 0;
                offsetY -= 75;
            }
        }

        drawDesktopActionBar(batch);
        drawDesktopConsumables(batch);

        FontHelper.getSingleton().write(Color.WHITE, batch, 3f, 800 + (int)expStringXOffset, 55, expString);
        FontHelper.getSingleton().write(Color.WHITE, batch, 3f, 545 + (int)inventoryStringXOffset, 55, inventoryString);
    }

    private void drawResourceBar(Batch batch,
                                 GameSprite barSprite,
                                 float x,
                                 float y,
                                 int currentValue,
                                 int maxValue,
                                 Color fillColor) {
        float fillFraction = maxValue <= 0 ? 0f : MathUtils.clamp(currentValue / (float) maxValue, 0f, 1f);
        int filledBarWidth = Math.round(HUD_BAR_WIDTH * fillFraction);

        barSprite.setWidth((int) HUD_BAR_WIDTH);
        barSprite.setColor(Color.WHITE);
        barSprite.setAlpha(0.8f);
        barSprite.draw(batch);

        barSprite.setWidth(filledBarWidth);
        barSprite.setColor(getResourceFillColor(fillColor, fillFraction));
        barSprite.setAlpha(1f);
        barSprite.draw(batch);
        barSprite.setColor(Color.WHITE);

        drawResourceValue(batch, x, y, filledBarWidth, currentValue);
    }

    private void drawResourceValue(Batch batch, float barX, float barY, int filledBarWidth, int currentValue) {
        String valueText = Integer.toString(Math.max(0, currentValue));
        float textSize = fitHudBarTextSize(valueText);
        GlyphLayout layout = FontHelper.getSingleton().measure(Color.WHITE, textSize, valueText);
        float textX = barX + filledBarWidth - layout.width - HUD_BAR_VALUE_RIGHT_PADDING;
        if (textX < barX + HUD_BAR_VALUE_PADDING) {
            textX = barX + HUD_BAR_VALUE_PADDING;
        }

        float textY = barY + (HUD_BAR_HEIGHT + layout.height) * 0.5f - 2f;
        FontHelper.getSingleton().writeWhite(batch, textSize, textX, textY, valueText);
    }

    private float fitHudBarTextSize(String text) {
        float textSize = HUD_BAR_VALUE_MAX_SIZE;
        while (textSize > HUD_BAR_VALUE_MIN_SIZE) {
            GlyphLayout layout = FontHelper.getSingleton().measure(Color.WHITE, textSize, text);
            if (layout.height <= HUD_BAR_HEIGHT - HUD_BAR_VALUE_PADDING * 0.4f
                    && layout.width <= HUD_BAR_WIDTH - HUD_BAR_VALUE_PADDING - HUD_BAR_VALUE_RIGHT_PADDING) {
                return textSize;
            }
            textSize -= 0.1f;
        }

        return HUD_BAR_VALUE_MIN_SIZE;
    }

    private Color getResourceFillColor(Color baseColor, float fillFraction) {
        Color pulsedColor = new Color(baseColor);
        if (fillFraction >= LOW_RESOURCE_THRESHOLD) {
            return pulsedColor;
        }

        float pulse = 0.5f + 0.5f * MathUtils.sin((System.currentTimeMillis() % 100000L) * LOW_RESOURCE_PULSE_SPEED);
        float brightness = MathUtils.lerp(LOW_RESOURCE_PULSE_MIN_BRIGHTNESS, 1f, pulse);
        pulsedColor.r *= brightness;
        pulsedColor.g *= brightness;
        pulsedColor.b *= brightness;
        return pulsedColor;
    }

    public void showDoorButton(){
        doorButton.setEnabled();
    }

    public void disableDoorButton(){
        doorButton.disable();
    }

    public void showSignButton(){
        signButton.setEnabled();
    }

    public void disableSignButton(){
        signButton.disable();
    }

    public void showPickupButton(ItemOnScreen item){
        pickupButton.clearSprites();
        GameSprite gs = item.getItem().getGameSprite().clone();
        gs.setWidth((int) ConstantsHelper.TILE / 2);
        gs.setHeight((int)ConstantsHelper.TILE / 2);
        gs.setPosition(65, 65);
        pickupButton.addGameSprite(gs);
        pickupButton.setEnabled();
    }

    public void disablePickupButton(){ pickupButton.disable(); }

    public void showInteractButton(GameSprite person){
        interactButton.clearSprites();
        GameSprite gs = person.clone();
        gs.setWidth((int) ConstantsHelper.TILE / 2);
        gs.setHeight((int)ConstantsHelper.TILE / 2);
        gs.setPosition(65, 65);
        interactButton.addGameSprite(gs);
        interactButton.setEnabled();
    }

    public void disableInteractButton(){ interactButton.disable(); }

    public boolean performContextAction() {
        if (heroDead()) {
            return false;
        }
        if (MapHelper.getInstance().performContextAction()) {
            return true;
        }

        if (pickupButton != null && pickupButton.canClick()) {
            pickupButton.click();
            return true;
        }

        if (signButton != null && signButton.canClick()) {
            signButton.click();
            return true;
        }

        if (interactButton != null && interactButton.canClick()) {
            interactButton.click();
            return true;
        }

        return false;
    }

    public boolean performPrimaryAction() {
        if (heroDead()) {
            return false;
        }

        if (meleeButton != null && meleeButton.canClick()) {
            meleeButton.click();
            return true;
        }

        return false;
    }

    public boolean performSecondaryAction() {
        if (heroDead()) {
            return false;
        }
        if (rangedButton != null && rangedButton.canClick()) {
            rangedButton.click();
            return true;
        }

        return false;
    }

    public boolean performQuickSkillAction() {
        return performQuickSkillAction(0);
    }

    public boolean performQuickSkill2Action() {
        return performQuickSkillAction(1);
    }

    public boolean performQuickSkill3Action() {
        return performQuickSkillAction(2);
    }

    public boolean performQuickSkill4Action() {
        return performQuickSkillAction(3);
    }

    public boolean performQuickSkill5Action() {
        return performQuickSkillAction(4);
    }

    public boolean performQuickSkill6Action() {
        return performQuickSkillAction(5);
    }

    public boolean performQuickSkill7Action() {
        return performQuickSkillAction(6);
    }

    public boolean performQuickSkillAction(int slotIndex) {
        if (heroDead()) {
            return false;
        }
        ActionButton quickSkillButton = getQuickSkillButton(slotIndex);
        if (quickSkillButton != null && quickSkillButton.canClick()) {
            quickSkillButton.click();
            return true;
        }

        return false;
    }

    public boolean performHealthPotionAction() {
        return !heroDead() && InventoryHelper.getInstance().consumeHealthPotion();
    }

    public boolean performManaPotionAction() {
        return !heroDead() && InventoryHelper.getInstance().consumeManaPotion();
    }

    public boolean performEatFoodAction() {
        return !heroDead() && InventoryHelper.getInstance().consumeRations();
    }

    public void updateTouchConsumableButtons() {
        InventoryHelper inventoryHelper = InventoryHelper.getInstance();
        if (inventoryHelper == null) {
            return;
        }

        updateTouchConsumableButtons(
                inventoryHelper.getHealthPotionCount(),
                inventoryHelper.getManaPotionCount(),
                inventoryHelper.getRationsCount());
    }

    public void updateTouchConsumableButtons(int healthPotionCount, int manaPotionCount, int rationsCount) {
        if (healthPotionButton != null) {
            healthPotionButton.setItemCount(healthPotionCount);
        }

        if (manaPotionButton != null) {
            manaPotionButton.setItemCount(manaPotionCount);
        }

        if (foodButton != null) {
            foodButton.setItemCount(rationsCount);
        }

        updateTouchActionButtonAvailability();
    }

    public boolean performInventoryAction() {
        if (heroDead() || WindowHelper.getInstance().windowOpen()) {
            return false;
        }

        WindowHelper.getInstance().addWindow(new InventoryWindow(UnitHelper.getInstance().getHero().getHeroClass(),2000, 1000).build());
        return true;
    }


    public void equiped(MeleeWeapon weapon){
        if(meleeButton == null){
            return;
        }

        if(weapon instanceof Wand){
            meleeButton.setManaCost(((Wand)weapon).getManaCost());
            meleeButton.setHaveMana(UnitHelper.getInstance().getHero().getMp() >= ((Wand)weapon).getManaCost());
        }
        else{
            meleeButton.setManaCost(0);
        }
        meleeButton.clearSprites();
        GameSprite gs = weapon.getGameSprite().clone();
        float iconSize = SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()
            ? TOUCH_RIGHT_PRIMARY_ICON_SIZE
            : TOUCH_RIGHT_SECONDARY_ICON_SIZE;
        float buttonSize = SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()
            ? TOUCH_RIGHT_PRIMARY_BUTTON_SIZE
            : TOUCH_RIGHT_SECONDARY_BUTTON_SIZE;
        gs.setWidth((int) iconSize);
        gs.setHeight((int) iconSize);
        centerButtonSprite(meleeButton, gs, buttonSize, iconSize);
        gs.setRotation(0f);
        EnhancementVisualHelper.applyWeaponEnhancementPulse(gs, weapon);
    }

    public void equiped(RangedWeapon weapon){
        if(rangedButton == null){
            return;
        }

        if(weapon == null){
            rangedButton.setEnabled();
            setDefaultRangedButtonIcon();
            rangedButton.setItemCount(0);
            return;
        }

        rangedButton.clearSprites();
        GameSprite gs = weapon.getGameSprite().clone();
        gs.setWidth((int) TOUCH_RIGHT_SECONDARY_ICON_SIZE);
        gs.setHeight((int) TOUCH_RIGHT_SECONDARY_ICON_SIZE);
        centerButtonSprite(rangedButton, gs, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);
        gs.setRotation(0f);
        EnhancementVisualHelper.applyWeaponEnhancementPulse(gs, weapon);
        rangedButton.setEnabled();

        rangedButton.setItemCount(weapon.getAmmo());
    }

    public InputGestureListener inputGestureListener(){
        return new InputGestureListener(){
            @Override
            public boolean touchDown(float x,
                                     float y,
                                     int pointer,
                                     int button) {
                return getInstance().touchDown(x, y,
                        pointer,
                        button);
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                return getInstance().tap(x, y, button);
            }

            @Override
            public boolean longPress(float x, float y) {
                return getInstance().longpress(x, y);
            }
        };
    }

    public InputAdapter touchInputProcessor() {
        return new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return getInstance().touchPointerDown(screenX, screenY, pointer);
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return getInstance().touchPointerUp(screenX, screenY, pointer);
            }
        };
    }

    private Button getButtonAt(float x, float y) {
        Vector3 t = GameHelper.GetSingleton().getUICamera().unproject(new Vector3(x, y, 0));

        if (heroDead()) {
            if (mainMenuButton != null && mainMenuButton.isHitProjected(t.x, t.y)) {
                return mainMenuButton;
            }
            return null;
        }

        for (Button button : buttons) {
            if (button.isHitProjected(t.x, t.y)) {
                return button;
            }
        }

        syncMercenaryHudButtons(getActiveMercenaries());
        for (ActionButton mercenaryHudButton : mercenaryHudButtons) {
            if (mercenaryHudButton.isHitProjected(t.x, t.y)) {
                return mercenaryHudButton;
            }
        }

        return null;
    }

    public boolean isButtonHit(float x, float y) {
        return getButtonAt(x, y) != null;
    }

    private boolean touchPointerDown(float x, float y, int pointer) {
        if (WindowHelper.getInstance().windowOpen()) {
            return false;
        }

        clearPressedButton(pointer);
        Button hitButton = getButtonAt(x, y);
        if (hitButton != null) {
            pressedButtonsByPointer.put(pointer, hitButton);
            if (hitButton instanceof ActionButton) {
                ((ActionButton) hitButton).pressDown();
            }
            return true;
        }

        return false;
    }

    private boolean touchPointerUp(float x, float y, int pointer) {
        Button tappedButton = pressedButtonsByPointer.remove(pointer);
        if (tappedButton == null) {
            return false;
        }

        if (tappedButton instanceof ActionButton) {
            ((ActionButton) tappedButton).releasePress();
        }

        if (!WindowHelper.getInstance().windowOpen() && getButtonAt(x, y) == tappedButton) {
            tappedButton.click();
            return true;
        }

        if (tappedButton instanceof ActionButton) {
            ((ActionButton) tappedButton).cancelPress();
        }

        return true;
    }

    private boolean touchDown(float x, float y, int pointer, int button) {
        clearPressedButton();
        Button hitButton = getButtonAt(x, y);
        if (hitButton != null) {
            pressedButton = hitButton;
            if (hitButton instanceof ActionButton) {
                ((ActionButton) hitButton).pressDown();
            }
            return true;
        }

        return false;
    }

    public boolean tap(float x, float y, int button) {
        if (pressedButton != null) {
            Button tappedButton = pressedButton;
            pressedButton = null;

            if (tappedButton instanceof ActionButton) {
                ((ActionButton) tappedButton).releasePress();
            }

            if (getButtonAt(x, y) == tappedButton) {
                tappedButton.click();
                return true;
            }

            if (tappedButton instanceof ActionButton) {
                ((ActionButton) tappedButton).cancelPress();
            }

            return true;
        }

        if (isButtonHit(x, y)) {
            return true;
        }

        boolean desktopMouseAttack = SkillfulPixelDungeonPlatformer.getPlatformProfile().keyboardControlsEnabled()
                && !SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled();
        if (desktopMouseAttack
            && GameSettingsHelper.getInstance().getAttackBinding().matchesMouse(button)
            && meleeButton != null
            && meleeButton.canClick()) {
            meleeButton.click();
            return true;
        }

        return false;
    }

    private boolean longpress(float x, float y) {
        clearPressedButton();

        Vector3 t = GameHelper.GetSingleton().getUICamera().unproject(new Vector3(x, y, 0));

        boolean consumed = false;

        if (heroDead()) {
            return false;
        }

        for(Button button : buttons){
            if(button.isHitProjected(t.x, t.y)){
                consumed = true;
                button.longClick();
                break;
            }
        }

        return consumed;
    }

    private void clearPressedButton() {
        if (pressedButton instanceof ActionButton) {
            ((ActionButton) pressedButton).cancelPress();
        }

        pressedButton = null;

        for (Button button : pressedButtonsByPointer.values()) {
            if (button instanceof ActionButton) {
                ((ActionButton) button).cancelPress();
            }
        }
        pressedButtonsByPointer.clear();
    }

    private void clearPressedButton(int pointer) {
        Button pointerButton = pressedButtonsByPointer.remove(pointer);
        if (pointerButton instanceof ActionButton) {
            ((ActionButton) pointerButton).cancelPress();
        }
    }

    public void setExpString(String expString){
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 3), expString);

        this.expStringXOffset = 100 - glyphLayout.width / 2;
        this.expString = expString;
    }

    public void setInventoryString(String inventoryString){
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 3), inventoryString);

        this.inventoryStringXOffset = 100 - glyphLayout.width / 2;
        this.inventoryString = inventoryString;
    }

    public void updateRangedButton(){
        if (rangedButton == null) {
            return;
        }

        if (UnitHelper.getInstance().getHero().getRangedWeapon() != null) {
            rangedButton.setItemCount(UnitHelper.getInstance().getHero().getRangedWeapon().getAmmo());
        } else {
            rangedButton.setItemCount(0);
        }

        updateTouchActionButtonAvailability();
    }

    private float touchPrimaryCombatButtonX() {
        return touchPrimaryCombatButtonCenterX() - TOUCH_RIGHT_PRIMARY_BUTTON_SIZE / 2f;
    }

    private float touchPrimaryCombatButtonY() {
        return TOUCH_RIGHT_BUTTON_MARGIN + TOUCH_RIGHT_BUTTON_VERTICAL_OFFSET;
    }

    private float touchPrimaryCombatButtonCenterX() {
        float orbitRadius = touchOrbitRadius();
        float startRadians = TOUCH_RIGHT_ORBIT_START_ANGLE_DEGREES * MathUtils.degreesToRadians;
        float endRadians = TOUCH_RIGHT_ORBIT_END_ANGLE_DEGREES * MathUtils.degreesToRadians;
        float maxPositiveCosine = Math.max(0f, Math.max(MathUtils.cos(startRadians), MathUtils.cos(endRadians)));
        float orbitRightExtent = orbitRadius * maxPositiveCosine + TOUCH_RIGHT_SECONDARY_BUTTON_SIZE / 2f;
        return ConstantsHelper.SCREEN_WIDTH - TOUCH_RIGHT_BUTTON_MARGIN - Math.max(TOUCH_RIGHT_PRIMARY_BUTTON_SIZE / 2f, orbitRightExtent);
    }

    private float touchPrimaryCombatButtonCenterY() {
        return touchPrimaryCombatButtonY() + TOUCH_RIGHT_PRIMARY_BUTTON_SIZE / 2f;
    }

    private float touchOrbitRadius() {
        return touchOrbitMinimumRadius(TOUCH_RIGHT_ORBIT_BUTTON_COUNT);
    }

    private float touchOrbitMinimumRadius(int orbitButtonCount) {
        float primaryClearanceRadius = TOUCH_RIGHT_PRIMARY_BUTTON_SIZE / 2f + TOUCH_RIGHT_SECONDARY_BUTTON_SIZE / 2f + TOUCH_RIGHT_BUTTON_GAP;
        if (orbitButtonCount < 2) {
            return primaryClearanceRadius;
        }

        float angleStepDegrees = Math.abs(TOUCH_RIGHT_ORBIT_END_ANGLE_DEGREES - TOUCH_RIGHT_ORBIT_START_ANGLE_DEGREES)
                / (orbitButtonCount - 1f);
        float halfStepRadians = angleStepDegrees * MathUtils.degreesToRadians / 2f;
        if (halfStepRadians <= 0f) {
            return primaryClearanceRadius;
        }

        float spacingRadius = (TOUCH_RIGHT_SECONDARY_BUTTON_SIZE + TOUCH_RIGHT_ORBIT_BUTTON_SPACING)
                / (2f * MathUtils.sin(halfStepRadians));
        return Math.max(primaryClearanceRadius, spacingRadius);
    }

    private float touchVisibleOrbitRadius(int orbitButtonCount) {
        return Math.max(touchOrbitMinimumRadius(orbitButtonCount), touchOrbitRadius() * TOUCH_RIGHT_VISIBLE_ORBIT_RADIUS_SCALE);
    }

    private float touchOrbitAngleDegrees(int orbitIndex) {
        return touchOrbitAngleDegrees(orbitIndex, TOUCH_RIGHT_ORBIT_BUTTON_COUNT);
    }

    private float touchOrbitAngleDegrees(int orbitIndex, int orbitButtonCount) {
        if (orbitButtonCount < 2) {
            return TOUCH_RIGHT_ORBIT_START_ANGLE_DEGREES;
        }

        return TOUCH_RIGHT_ORBIT_START_ANGLE_DEGREES + orbitIndex
                * ((TOUCH_RIGHT_ORBIT_END_ANGLE_DEGREES - TOUCH_RIGHT_ORBIT_START_ANGLE_DEGREES)
                / (orbitButtonCount - 1f));
    }

    private float touchOrbitButtonX(int orbitIndex) {
        return touchOrbitButtonX(orbitIndex, TOUCH_RIGHT_ORBIT_BUTTON_COUNT, touchOrbitRadius());
    }

    private float touchOrbitButtonY(int orbitIndex) {
        return touchOrbitButtonY(orbitIndex, TOUCH_RIGHT_ORBIT_BUTTON_COUNT, touchOrbitRadius());
    }

    private float touchOrbitButtonX(int orbitIndex, int orbitButtonCount, float orbitRadius) {
        float radians = touchOrbitAngleDegrees(orbitIndex, orbitButtonCount) * MathUtils.degreesToRadians;
        return touchPrimaryCombatButtonCenterX() + MathUtils.cos(radians) * orbitRadius - TOUCH_RIGHT_SECONDARY_BUTTON_SIZE / 2f;
    }

    private float touchOrbitButtonY(int orbitIndex, int orbitButtonCount, float orbitRadius) {
        float radians = touchOrbitAngleDegrees(orbitIndex, orbitButtonCount) * MathUtils.degreesToRadians;
        return touchPrimaryCombatButtonCenterY() + MathUtils.sin(radians) * orbitRadius - TOUCH_RIGHT_SECONDARY_BUTTON_SIZE / 2f;
    }

    private void centerButtonSprite(ActionButton button, GameSprite sprite, float buttonSize, float iconSize) {
        if (button == null || sprite == null) {
            return;
        }

        sprite.setPosition((buttonSize - iconSize) / 2f, (buttonSize - iconSize) / 2f);
        button.addGameSprite(sprite);
    }

    private void applyTouchHudButtonStyle(ActionButton button) {
        if (button == null) {
            return;
        }

        button.setVisualAlpha(TOUCH_HUD_AVAILABLE_ALPHA, TOUCH_HUD_PRESSED_ALPHA, TOUCH_HUD_UNAVAILABLE_ALPHA);
    }

    private void setDefaultRangedButtonIcon() {
        if (rangedButton == null) {
            return;
        }

        rangedButton.clearSprites();
        GameSprite projectileIcon = new GameSprite("images/weapons/projectile.png", TOUCH_RIGHT_SECONDARY_ICON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);
        centerButtonSprite(rangedButton, projectileIcon, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);
    }

    private void updateTouchActionButtonAvailability() {
        if (!SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()) {
            return;
        }

        if (UnitHelper.getInstance().getHero() == null) {
            return;
        }

        if (jumpButton != null) {
            jumpButton.setActionAvailable(UnitHelper.getInstance().getHero().canJumpNow());
        }
    }

    private void layoutTouchOrbitButtons() {
        if (!SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()) {
            return;
        }

        ArrayList<ActionButton> visibleOrbitButtons = getVisibleTouchOrbitButtons();
        if (visibleOrbitButtons.isEmpty()) {
            return;
        }

        float orbitRadius = touchVisibleOrbitRadius(visibleOrbitButtons.size());
        for (int buttonIndex = 0; buttonIndex < visibleOrbitButtons.size(); buttonIndex++) {
            ActionButton orbitButton = visibleOrbitButtons.get(buttonIndex);
            orbitButton.setPosition(
                    touchOrbitButtonX(buttonIndex, visibleOrbitButtons.size(), orbitRadius),
                    touchOrbitButtonY(buttonIndex, visibleOrbitButtons.size(), orbitRadius));
        }
    }

    private ArrayList<ActionButton> getVisibleTouchOrbitButtons() {
        ArrayList<ActionButton> visibleOrbitButtons = new ArrayList<>();
        addVisibleTouchOrbitButton(visibleOrbitButtons, jumpButton);
        addVisibleTouchOrbitButton(visibleOrbitButtons, rangedButton);
        addVisibleTouchOrbitButton(visibleOrbitButtons, getQuickSkillButton(0));
        addVisibleTouchOrbitButton(visibleOrbitButtons, getQuickSkillButton(1));
        addVisibleTouchOrbitButton(visibleOrbitButtons, healthPotionButton);
        addVisibleTouchOrbitButton(visibleOrbitButtons, manaPotionButton);
        addVisibleTouchOrbitButton(visibleOrbitButtons, foodButton);
        return visibleOrbitButtons;
    }

    private void addVisibleTouchOrbitButton(ArrayList<ActionButton> visibleOrbitButtons, ActionButton orbitButton) {
        if (orbitButton != null && orbitButton.isEnabled()) {
            visibleOrbitButtons.add(orbitButton);
        }
    }

    public void setQuickSkill(ActiveSkill activeSkill){
        setQuickSkill(0, activeSkill);
    }

    public void setQuickSkillCostCheck(boolean canCast){
        setQuickSkillCostCheck(0, canCast);
    }

    public void setMeleeButtonCostCheck(boolean canCast){
        if (meleeButton != null) {
            meleeButton.setHaveMana(canCast);
        }
    }

    public void setQuickSkill2(ActiveSkill activeSkill){
        setQuickSkill(1, activeSkill);
    }

    public void setQuickSkill2CostCheck(boolean canCast){
        setQuickSkillCostCheck(1, canCast);
    }

    public void setQuickSkill3(ActiveSkill activeSkill){
        setQuickSkill(2, activeSkill);
    }

    public void setQuickSkill3CostCheck(boolean canCast){
        setQuickSkillCostCheck(2, canCast);
    }

    public void setQuickSkill(int slotIndex, ActiveSkill activeSkill){
        ActionButton quickSkillButton = getQuickSkillButton(slotIndex);
        if(quickSkillButton == null){
            return;
        }

        quickSkillButton.clearSprites();
        quickSkillButton.setItemCount(0);
        quickSkillButton.setManaCost(0);
        quickSkillButton.setHaveMana(true);
        quickSkillButton.setActionAvailable(false);
        quickSkillButton.setEnabled();

        if(activeSkill == null){
            layoutTouchOrbitButtons();
            return;
        }

        GameSprite gs = activeSkill.getGameSprite().clone();
        gs.setWidth((int) TOUCH_RIGHT_SECONDARY_ICON_SIZE);
        gs.setHeight((int) TOUCH_RIGHT_SECONDARY_ICON_SIZE);
        centerButtonSprite(quickSkillButton, gs, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);
        quickSkillButton.setManaCost(activeSkill.getManaCost());
        quickSkillButton.setHaveMana(UnitHelper.getInstance().getHero().getMp() >= activeSkill.getManaCost());
        quickSkillButton.setActionAvailable(true);
        quickSkillButton.setEnabled();
        layoutTouchOrbitButtons();
    }

    public void setQuickSkillCostCheck(int slotIndex, boolean canCast){
        ActionButton quickSkillButton = getQuickSkillButton(slotIndex);
        if(quickSkillButton != null){
            quickSkillButton.setHaveMana(canCast);
        }
    }

    public void showBossSlainBanner() {
        bossSlainBanner = buildBossSlainBanner();
        bossSlainBannerTimer = BOSS_SLAIN_BANNER_DURATION;
    }

    public void showAchievementBanner(Achievement achievement) {
        if (achievement == null) {
            return;
        }

        achievementBannerBackground = new GameSprite("images/misc/black.png", ACHIEVEMENT_BANNER_WIDTH, ACHIEVEMENT_BANNER_HEIGHT, 0.78f);
        achievementBannerBackground.setPosition((ConstantsHelper.SCREEN_WIDTH - ACHIEVEMENT_BANNER_WIDTH) / 2f, ACHIEVEMENT_BANNER_Y);

        achievementBannerIcon = new GameSprite(achievement.getAchievedIconPath(), ACHIEVEMENT_BANNER_ICON_SIZE, ACHIEVEMENT_BANNER_ICON_SIZE, 1f);
        achievementBannerIcon.setPosition(achievementBannerBackground.getX() + 22f,
                achievementBannerBackground.getY() + (ACHIEVEMENT_BANNER_HEIGHT - ACHIEVEMENT_BANNER_ICON_SIZE) / 2f);

        achievementBannerName = UtilsHelper.multiLine(achievement.getName(), 2, ACHIEVEMENT_BANNER_WIDTH - 180f);
        achievementBannerTimer = ACHIEVEMENT_BANNER_DURATION;
    }

    public void showDepthTransitionBanner(String text) {
        showDepthTransitionBanner(text, null);
    }

    public void showDepthTransitionBanner(String text, Runnable onComplete) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        depthTransitionBannerBackground = new GameSprite("images/misc/black.png", ConstantsHelper.SCREEN_WIDTH, ConstantsHelper.SCREEN_HEIGHT, 1f);
        depthTransitionBannerBackground.setPosition(0f, 0f);
        depthTransitionBannerText = text.trim();
        depthTransitionBannerFrame = 0f;
        depthTransitionBannerTimer = DEPTH_TRANSITION_BANNER_DURATION;
        depthTransitionBannerCompleteAction = onComplete;
    }

    private boolean heroDead() {
        return UnitHelper.getInstance().getHero() != null && UnitHelper.getInstance().getHero().isDead();
    }

    private void drawAchievementBanner(Batch batch) {
        float backgroundAlpha = achievementBannerBackground.getAlpha();
        float iconAlpha = achievementBannerIcon.getAlpha();

        achievementBannerBackground.setAlpha(backgroundAlpha);
        achievementBannerBackground.draw(batch);
        achievementBannerIcon.setAlpha(iconAlpha);
        achievementBannerIcon.draw(batch);

        float textX = achievementBannerBackground.getX() + 148f;
        float headerY = achievementBannerBackground.getY() + ACHIEVEMENT_BANNER_HEIGHT - 28f;
        float nameY = achievementBannerBackground.getY() + 56f;
        FontHelper.getSingleton().write(Color.GOLDENROD, batch, 2f, textX, headerY, Messages.maybeTranslate("BADGE UNLOCKED"));
        FontHelper.getSingleton().writeWhiteRaw(batch, 2f, textX, nameY, achievementBannerName);

        achievementBannerBackground.setAlpha(backgroundAlpha);
        achievementBannerIcon.setAlpha(iconAlpha);

        achievementBannerTimer = Math.max(0f, achievementBannerTimer - Gdx.graphics.getDeltaTime());
        if (achievementBannerTimer == 0f) {
            achievementBannerBackground = null;
            achievementBannerIcon = null;
            achievementBannerName = null;
        }
    }

    private void drawDepthTransitionBanner(Batch batch) {
        if (depthTransitionBannerBackground == null || depthTransitionBannerText == null) {
            return;
        }

        depthTransitionBannerBackground.draw(batch);

        depthTransitionBannerFrame += Gdx.graphics.getDeltaTime();

        GlyphLayout bannerLayout = new GlyphLayout();
        bannerLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 3f), depthTransitionBannerText);
        float textX = (ConstantsHelper.SCREEN_WIDTH - bannerLayout.width) / 2f;
        float textY = (ConstantsHelper.SCREEN_HEIGHT + bannerLayout.height) / 2f;
        float phase = depthTransitionBannerFrame / DEPTH_TRANSITION_FLASH_PERIOD_SECONDS;
        float flashAlpha = 0.35f + 0.65f * (0.5f + 0.5f * MathUtils.sin(phase * MathUtils.PI2));
        Color flashColor = new Color(Color.WHITE);
        flashColor.a = flashAlpha;
        FontHelper.getSingleton().write(flashColor, batch, 3f, textX, textY, depthTransitionBannerText);

        depthTransitionBannerTimer = Math.max(0f, depthTransitionBannerTimer - Gdx.graphics.getDeltaTime());
        if (depthTransitionBannerTimer == 0f) {
            Runnable completeAction = depthTransitionBannerCompleteAction;
            depthTransitionBannerBackground = null;
            depthTransitionBannerText = null;
            depthTransitionBannerFrame = 0f;
            depthTransitionBannerCompleteAction = null;
            if (completeAction != null) {
                completeAction.run();
            }
        }
    }

    private void drawDesktopActionBar(Batch batch) {
        if (!desktopActionBarEnabled()) {
            return;
        }

        ArrayList<DesktopActionSlot> actions = new ArrayList<DesktopActionSlot>();
        ArrayList<DesktopActionSlot> unlockedSkillActions = new ArrayList<DesktopActionSlot>();
        Hero hero = UnitHelper.getInstance().getHero();
        Weapon weapon = hero.getWeapon();
        int attackManaCost = weapon instanceof Wand ? ((Wand) weapon).getManaCost() : 0;
        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        boolean attackOnCooldown = !hero.canAttack() || (weapon instanceof Wand && ((Wand) weapon).isOnCooldown());
        GameSprite attackIcon = snapshotDesktopActionIcon(weapon.getGameSprite());
        EnhancementVisualHelper.applyWeaponEnhancementPulse(attackIcon, weapon);
        actions.add(new DesktopActionSlot(attackIcon, attackManaCost, attackManaCost == 0 || hero.getMp() >= attackManaCost, settings.bindingLabel(settings.getAttackBinding()), attackOnCooldown));
        if (hero.getRangedWeapon() != null) {
            GameSprite rangedIcon = snapshotDesktopActionIcon(hero.getRangedWeapon().getGameSprite());
            EnhancementVisualHelper.applyWeaponEnhancementPulse(rangedIcon, hero.getRangedWeapon());
            actions.add(new DesktopActionSlot(rangedIcon,
                    String.valueOf(hero.getRangedWeapon().getAmmo()),
                    Color.WHITE,
                    hero.getRangedWeapon().getAmmo() > 0,
                    settings.bindingLabel(settings.getRangedBinding()),
                    !hero.canAttack()));
        }

        for (int slotIndex = 0; slotIndex < hero.getQuickSkillSlotCount(); slotIndex++) {
            addDesktopSkillAction(unlockedSkillActions,
                    hero.getQuickSkill(slotIndex),
                    quickSkillBindingLabel(settings, slotIndex));
        }

        Collections.sort(unlockedSkillActions, new Comparator<DesktopActionSlot>() {
            @Override
            public int compare(DesktopActionSlot first, DesktopActionSlot second) {
                return Integer.compare(first.sortOrder, second.sortOrder);
            }
        });
        actions.addAll(unlockedSkillActions);

        if (actions.isEmpty()) {
            return;
        }

        float startX = hpBar.getX() + HUD_BAR_WIDTH + ConstantsHelper.TILE;
        float firstRowY = hpBar.getY() - DESKTOP_ACTION_ROW_OFFSET;
        float rowStep = DESKTOP_ACTION_SLOT_SIZE + DESKTOP_ACTION_ROW_GAP - DESKTOP_ACTION_ADDITIONAL_ROW_DROP;
        for (int index = 0; index < actions.size(); index++) {
            int column = index % DESKTOP_ACTIONS_PER_ROW;
            int row = index / DESKTOP_ACTIONS_PER_ROW;
            int visualRow = row;
            if (row == 0) {
                visualRow = 1;
            }
            else if (row == 1) {
                visualRow = 0;
            }

            float slotX = startX + column * (DESKTOP_ACTION_SLOT_SIZE + DESKTOP_ACTION_SLOT_GAP);
            float slotY = firstRowY + visualRow * rowStep;
            slotY += visualRow == 0 ? ConstantsHelper.TILE * 0.5f : ConstantsHelper.TILE * 0.75f;
            drawDesktopActionSlot(batch, actions.get(index), slotX, slotY);
        }
    }

    private void drawDesktopConsumables(Batch batch) {
        if (!desktopActionBarEnabled()
                || desktopFoodIcon == null
                || desktopHealthPotionIcon == null
                || desktopManaPotionIcon == null
                || desktopDepthIcon == null
                || desktopKeyIcon == null) {
            return;
        }

        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        DesktopActionSlot[] consumables = new DesktopActionSlot[]{
                new DesktopActionSlot(desktopFoodIcon,
                        String.valueOf(InventoryHelper.getInstance().getRationsCount()),
                        Color.WHITE,
                        InventoryHelper.getInstance().getRationsCount() > 0,
                        settings.bindingLabel(settings.getEatFoodBinding()),
                        false),
                new DesktopActionSlot(desktopHealthPotionIcon,
                        String.valueOf(InventoryHelper.getInstance().getHealthPotionCount()),
                        Color.WHITE,
                        InventoryHelper.getInstance().getHealthPotionCount() > 0,
                        settings.bindingLabel(settings.getHealthPotionBinding()),
                        false),
                new DesktopActionSlot(desktopManaPotionIcon,
                        String.valueOf(InventoryHelper.getInstance().getManaPotionCount()),
                        Color.WHITE,
                        InventoryHelper.getInstance().getManaPotionCount() > 0,
                        settings.bindingLabel(settings.getManaPotionBinding()),
                        false)
        };

        float inventoryButtonX = backPackButton != null ? backPackButton.x : 550f;
        float inventoryButtonY = backPackButton != null ? backPackButton.y : 75f;
        float totalHeight = consumables.length * DESKTOP_ACTION_SLOT_SIZE + (consumables.length - 1) * DESKTOP_CONSUMABLE_SLOT_GAP;
        float startY = inventoryButtonY + (200f - totalHeight) / 2f;
        float consumableSlotX = getDesktopConsumableSlotX(inventoryButtonX, consumables);

        drawDesktopStatusBadges(batch,
                consumableSlotX - DESKTOP_CONSUMABLE_LABEL_GAP - DESKTOP_ACTION_SLOT_SIZE,
                inventoryButtonY);

        for (int index = 0; index < consumables.length; index++) {
            float slotY = startY + (consumables.length - 1 - index) * (DESKTOP_ACTION_SLOT_SIZE + DESKTOP_CONSUMABLE_SLOT_GAP);
            drawDesktopConsumableSlot(batch, consumables[index], inventoryButtonX, slotY);
        }
    }

    private float getDesktopConsumableSlotX(float inventoryButtonX, DesktopActionSlot[] consumables) {
        float leftMostSlotX = inventoryButtonX;

        for (DesktopActionSlot consumable : consumables) {
            GlyphLayout bindingLayout = new GlyphLayout();
            bindingLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 1.8f), consumable.bindingLabel);

            float bindingX = inventoryButtonX - DESKTOP_CONSUMABLE_INVENTORY_GAP - bindingLayout.width;
            float slotX = bindingX - DESKTOP_CONSUMABLE_LABEL_GAP - DESKTOP_ACTION_SLOT_SIZE;
            leftMostSlotX = Math.min(leftMostSlotX, slotX);
        }

        return leftMostSlotX;
    }

    private void drawDesktopStatusBadges(Batch batch, float x, float inventoryButtonY) {
        float totalHeight = DESKTOP_ACTION_SLOT_SIZE * 2f + DESKTOP_CONSUMABLE_SLOT_GAP;
        float startY = inventoryButtonY + (200f - totalHeight) / 2f;
        float keyY = startY;
        float depthY = startY + DESKTOP_ACTION_SLOT_SIZE + DESKTOP_CONSUMABLE_SLOT_GAP;

        drawDesktopKeyBadge(batch, x, keyY);
        drawDesktopDepthBadge(batch, x, depthY);
    }

    private void drawTouchStatusBadges(Batch batch) {
        if (backPackButton == null || desktopDepthIcon == null || desktopKeyIcon == null) {
            return;
        }

        float badgeX = backPackButton.x - DESKTOP_ACTION_SLOT_SIZE - DESKTOP_CONSUMABLE_LABEL_GAP;
        drawDesktopStatusBadges(batch, badgeX, backPackButton.y);
    }

    private void drawMercenaryHud(Batch batch) {
        ArrayList<MercenaryAlly> mercenaries = getActiveMercenaries();
        if (mercenaries.isEmpty()) {
            mercenaryHudButtons.clear();
            return;
        }

        syncMercenaryHudButtons(mercenaries);

        float margin = heroPortrait.getWidth() * MERC_HUD_MARGIN_RATIO;
        float panelX = margin;
        float panelY = ConstantsHelper.SCREEN_HEIGHT - margin - MERC_HUD_PANEL_HEIGHT;

        for (MercenaryAlly mercenary : mercenaries) {
            if (panelY < 0f) {
                break;
            }

            drawMercenaryHudEntry(batch, mercenary, panelX, panelY);
            panelY -= MERC_HUD_PANEL_HEIGHT + MERC_HUD_PANEL_GAP;
        }
    }

    private void drawMercenaryHudEntry(Batch batch, MercenaryAlly mercenary, float panelX, float panelY) {
        Color previousColor = new Color(batch.getColor());
        batch.setColor(previousColor.r, previousColor.g, previousColor.b, MERC_HUD_OVERLAY_ALPHA);
        batch.draw(TextureHelper.GetSingleton().getTexture("images/misc/black.png"),
            panelX,
            panelY - MERC_HUD_OVERLAY_EXTRA_HEIGHT,
            MERC_HUD_PANEL_WIDTH,
            MERC_HUD_PANEL_HEIGHT + MERC_HUD_OVERLAY_EXTRA_HEIGHT);
        batch.setColor(previousColor);

        GameSprite portrait = getMercenaryPortrait(mercenary);
        float portraitX = panelX + MERC_HUD_PANEL_PADDING;
        float portraitY = panelY + (MERC_HUD_PANEL_HEIGHT - MERC_HUD_PORTRAIT_SIZE) / 2f;
        portrait.setPosition(portraitX, portraitY);
        portrait.draw(batch);

        float textX = portraitX + MERC_HUD_PORTRAIT_SIZE + MERC_HUD_TEXT_GAP;
        String displayName = MercenaryHelper.getDisplayName(mercenary.getMercenaryType(), mercenary.getMercenaryName());
        String levelText = Messages.get("custom.generated.level_arg_4466275219", new Object[]{mercenary.getMercenaryLevel()}) + " " + displayName;
        float levelY = panelY + MERC_HUD_PANEL_HEIGHT - MERC_HUD_PANEL_PADDING - 2f;
        FontHelper.getSingleton().write(Color.WHITE, batch, MERC_HUD_LEVEL_TEXT_SIZE, textX, levelY, levelText);

        float barX = textX;
        float hpBarY = panelY + MERC_HUD_PANEL_PADDING + 20f;
        drawMercenaryHudBar(batch,
            mercHpBar,
            barX,
            hpBarY,
            MERC_HUD_HP_BAR_WIDTH,
            MERC_HUD_HP_BAR_HEIGHT,
            Math.max(0, mercenary.getHP()),
            Math.max(1, mercenary.getMaxHP()));

        int maxHp = Math.max(1, mercenary.getMaxHP());
        String healthText = mercenary.getHP() + " / " + maxHp;
        GlyphLayout healthLayout = new GlyphLayout();
        healthLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, MERC_HUD_HP_TEXT_SIZE), healthText);
        float healthTextX = barX + (MERC_HUD_HP_BAR_WIDTH - healthLayout.width) / 2f;
        float healthTextY = hpBarY + (MERC_HUD_HP_BAR_HEIGHT + healthLayout.height) / 2f;
        FontHelper.getSingleton().write(Color.WHITE, batch, MERC_HUD_HP_TEXT_SIZE, healthTextX, healthTextY, healthText);

        String expText = "EXP " + mercenary.getMercenaryExperienceText();
        GlyphLayout expLayout = new GlyphLayout();
        expLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, MERC_HUD_HP_TEXT_SIZE), expText);
        float expTextX = barX + (MERC_HUD_HP_BAR_WIDTH - expLayout.width) / 2f;
        float expTextY = hpBarY - MERC_HUD_EXP_TEXT_GAP - expLayout.height;
        FontHelper.getSingleton().write(Color.WHITE, batch, MERC_HUD_HP_TEXT_SIZE, expTextX, expTextY, expText);
    }

        private void drawMercenaryHudBar(Batch batch,
                         GameSprite barSprite,
                         float x,
                         float y,
                         float width,
                         float height,
                         int value,
                         int maxValue) {
        barSprite.setPosition(x, y);
        barSprite.setWidth(Math.round(width));
        barSprite.setHeight(Math.round(height));
        barSprite.setAlpha(0.35f);
        barSprite.draw(batch);

        int filledBarWidth = Math.round(width * Math.max(0, value) / (float) Math.max(1, maxValue));
        barSprite.setWidth(filledBarWidth);
        barSprite.setAlpha(1f);
        barSprite.draw(batch);
        }

    private ArrayList<MercenaryAlly> getActiveMercenaries() {
        ArrayList<MercenaryAlly> mercenaries = new ArrayList<MercenaryAlly>();
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit instanceof MercenaryAlly && !unit.isDead()) {
                mercenaries.add((MercenaryAlly) unit);
            }
        }

        return mercenaries;
    }

    private void syncMercenaryHudButtons(ArrayList<MercenaryAlly> mercenaries) {
        if (mercenaryHudButtons.size() == mercenaries.size()) {
            boolean matches = true;
            for (int i = 0; i < mercenaryHudButtons.size(); i++) {
                if (!(((MercenaryHudButton) mercenaryHudButtons.get(i)).getMercenary() == mercenaries.get(i))) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                return;
            }
        }

        mercenaryHudButtons.clear();
        float margin = heroPortrait.getWidth() * MERC_HUD_MARGIN_RATIO;
        float panelX = margin;
        float panelY = ConstantsHelper.SCREEN_HEIGHT - margin - MERC_HUD_PANEL_HEIGHT;

        for (MercenaryAlly mercenary : mercenaries) {
            if (panelY < 0f) {
                break;
            }

            mercenaryHudButtons.add(new MercenaryHudButton(panelX, panelY, mercenary));
            panelY -= MERC_HUD_PANEL_HEIGHT + MERC_HUD_PANEL_GAP;
        }
    }

    private GameSprite getMercenaryPortrait(MercenaryAlly mercenary) {
        switch (mercenary.getMercenaryType()) {
            case ROGUE:
                return mercRoguePortrait;
            case WIZARD:
                return mercWizardPortrait;
            case HUNTRESS:
                return mercHuntressPortrait;
            case BRUTE:
            default:
                return mercWarriorPortrait;
        }
    }

    private static final class MercenaryHudButton extends ActionButton {
        private final MercenaryAlly mercenary;

        private MercenaryHudButton(float x, float y, MercenaryAlly mercenary) {
            super(x, y, MERC_HUD_PANEL_WIDTH, MERC_HUD_PANEL_HEIGHT, "images/misc/transparent.png", "images/misc/transparent.png");
            this.mercenary = mercenary;
        }

        private MercenaryAlly getMercenary() {
            return mercenary;
        }

        @Override
        public void clicked() {
            if (mercenary == null || mercenary.isDead()) {
                return;
            }

            WindowHelper.getInstance().addWindow(new MercenaryWindow(mercenary).build());
        }
    }

    private void drawDesktopDepthBadge(Batch batch, float x, float y) {
        GameSprite icon = desktopDepthIcon.clone();
        int iconSize = Math.round(DESKTOP_ACTION_SLOT_SIZE * 0.72f);
        icon.setWidth(iconSize);
        icon.setHeight(iconSize);
        icon.setPosition(x + (DESKTOP_ACTION_SLOT_SIZE - iconSize) / 2f,
            y + DESKTOP_ACTION_SLOT_SIZE - iconSize + iconSize * DESKTOP_DEPTH_ICON_RAISE_RATIO);
        icon.draw(batch);

        String depthText = String.valueOf(Math.max(1, MapHelper.getInstance().getDepth()));
        GlyphLayout depthLayout = new GlyphLayout();
        depthLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 2f), depthText);
        FontHelper.getSingleton().write(Color.WHITE,
                batch,
                2f,
                x + (DESKTOP_ACTION_SLOT_SIZE - depthLayout.width) / 2f,
                y + depthLayout.height + 2f,
                depthText);
    }

    private void drawDesktopKeyBadge(Batch batch, float x, float y) {
        GameSprite icon = desktopKeyIcon.clone();
        int iconSize = Math.round(DESKTOP_ACTION_SLOT_SIZE * 0.9f);
        icon.setWidth(iconSize);
        icon.setHeight(iconSize);
        icon.setPosition(x + (DESKTOP_ACTION_SLOT_SIZE - iconSize) / 2f, y + (DESKTOP_ACTION_SLOT_SIZE - iconSize) / 2f);
        icon.draw(batch);

        String keyCountText = String.valueOf(MapHelper.getInstance().getCurrentDepthKeyCount());
        GlyphLayout keyLayout = new GlyphLayout();
        keyLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 2f), keyCountText);
        FontHelper.getSingleton().write(Color.WHITE,
                batch,
                2f,
                x + (DESKTOP_ACTION_SLOT_SIZE - keyLayout.width) / 2f,
            icon.getY() + iconSize * DESKTOP_KEY_TEXT_HEIGHT_RATIO + keyLayout.height / 2f,
                keyCountText);
    }

    private void drawDesktopConsumableSlot(Batch batch, DesktopActionSlot action, float inventoryButtonX, float y) {
        GlyphLayout bindingLayout = new GlyphLayout();
        bindingLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 1.8f), action.bindingLabel);

        float bindingX = inventoryButtonX - DESKTOP_CONSUMABLE_INVENTORY_GAP - bindingLayout.width;
        float slotX = bindingX - DESKTOP_CONSUMABLE_LABEL_GAP - DESKTOP_ACTION_SLOT_SIZE;

        drawDesktopActionSlot(batch, action, slotX, y, false);
        FontHelper.getSingleton().write(Color.WHITE,
                batch,
                1.8f,
                bindingX,
                y + DESKTOP_ACTION_SLOT_SIZE / 2f + bindingLayout.height / 2f,
                action.bindingLabel);
    }

    private void addDesktopSkillAction(ArrayList<DesktopActionSlot> actions, ActiveSkill activeSkill, String bindingLabel) {
        if (activeSkill == null) {
            return;
        }

        actions.add(new DesktopActionSlot(activeSkill.getGameSprite(),
                activeSkill.getManaCost(),
                UnitHelper.getInstance().getHero().getMp() >= activeSkill.getManaCost(),
                bindingLabel,
            !UnitHelper.getInstance().getHero().canAttack() || activeSkill.isOnCooldown(),
                getUnlockOrder(activeSkill)));
    }

    private GameSprite snapshotDesktopActionIcon(GameSprite icon) {
        if (icon == null) {
            return null;
        }

        if (icon.spriteString != null) {
            return new GameSprite(icon.spriteString, icon.getWidth(), icon.getHeight(), 1f);
        }

        GameSprite snapshot = icon.clone();
        snapshot.setRotation(0f);
        snapshot.setPosition(0f, 0f);
        snapshot.setScale(Math.abs(snapshot.getScaleX()), Math.abs(snapshot.getScaleY()));
        snapshot.setAlpha(1f);
        return snapshot;
    }

    private int getUnlockOrder(ActiveSkill activeSkill) {
        if (activeSkill == null) {
            return Integer.MAX_VALUE;
        }

        ArrayList<Integer> unlockedSkills = UnitHelper.getInstance().getHero().getUnlockedSkills();
        int unlockOrder = unlockedSkills.indexOf(activeSkill.getId());
        return unlockOrder < 0 ? Integer.MAX_VALUE : unlockOrder;
    }

    private void drawDesktopActionSlot(Batch batch, DesktopActionSlot action, float x, float y) {
        drawDesktopActionSlot(batch, action, x, y, true);
    }

    private void drawDesktopActionSlot(Batch batch, DesktopActionSlot action, float x, float y, boolean drawBindingBelow) {
        if (action.icon == null) {
            return;
        }

        float imageAlpha = action.cooldownActive || !action.isAvailable ? DESKTOP_ACTION_COOLDOWN_ALPHA : 1f;

        GameSprite icon = action.icon.clone();
        int iconSize = (int) (DESKTOP_ACTION_SLOT_SIZE * DESKTOP_ACTION_ICON_SCALE);
        icon.setWidth(iconSize);
        icon.setHeight(iconSize);
        icon.setPosition(x + (DESKTOP_ACTION_SLOT_SIZE - iconSize) / 2f, y + (DESKTOP_ACTION_SLOT_SIZE - iconSize) / 2f);
        icon.setAlpha(icon.getAlpha() * imageAlpha);
        icon.draw(batch);

        if (action.badgeText != null) {
            GlyphLayout manaLayout = new GlyphLayout();
            manaLayout.setText(FontHelper.getSingleton().getFont(action.badgeColor, 1.8f), action.badgeText);
            float badgeWidth = manaLayout.width + DESKTOP_ACTION_MANA_BADGE_PADDING_X * 2f;
            float badgeHeight = manaLayout.height + DESKTOP_ACTION_MANA_BADGE_PADDING_Y * 2f;
            float badgeX = x + DESKTOP_ACTION_SLOT_SIZE - badgeWidth;
            float badgeY = y + DESKTOP_ACTION_SLOT_SIZE - badgeHeight;
            Color previousColor = new Color(batch.getColor());
            batch.setColor(previousColor.r, previousColor.g, previousColor.b, DESKTOP_ACTION_MANA_BADGE_ALPHA);
            batch.draw(TextureHelper.GetSingleton().getTexture("images/misc/black.png"), badgeX, badgeY, badgeWidth, badgeHeight);
            batch.setColor(previousColor);
                FontHelper.getSingleton().write(action.badgeColor,
                    batch,
                    1.8f,
                badgeX + DESKTOP_ACTION_MANA_BADGE_PADDING_X,
                badgeY + badgeHeight - DESKTOP_ACTION_MANA_BADGE_PADDING_Y,
                    action.badgeText);
        }

        if (drawBindingBelow) {
            GlyphLayout bindingLayout = new GlyphLayout();
            bindingLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 1.8f), action.bindingLabel);
            FontHelper.getSingleton().write(Color.WHITE,
                batch,
                1.8f,
                x + (DESKTOP_ACTION_SLOT_SIZE - bindingLayout.width) / 2f,
                y - DESKTOP_ACTION_LABEL_OFFSET,
                action.bindingLabel);
        }
    }

    private String quickSkillBindingLabel(GameSettingsHelper settings, int slotIndex) {
        switch (slotIndex) {
            case 0:
                return settings.bindingLabel(settings.getQuickSkillBinding());
            case 1:
                return settings.bindingLabel(settings.getQuickSkill2Binding());
            case 2:
                return settings.bindingLabel(settings.getQuickSkill3Binding());
            case 3:
                return settings.bindingLabel(settings.getQuickSkill4Binding());
            case 4:
                return settings.bindingLabel(settings.getQuickSkill5Binding());
            case 5:
                return settings.bindingLabel(settings.getQuickSkill6Binding());
            case 6:
                return settings.bindingLabel(settings.getQuickSkill7Binding());
            default:
                return "?";
        }
    }

    private ActionButton getQuickSkillButton(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= quickSkillButtons.length) {
            return null;
        }

        return quickSkillButtons[slotIndex];
    }

    private ArrayList<Skill> getPersistentHudSkills(Hero hero) {
        ArrayList<Skill> persistentHudSkills = new ArrayList<Skill>();
        if (hero == null) {
            return persistentHudSkills;
        }

        for (int skillId : hero.getUnlockedSkills()) {
            Skill skill = SkillsHelper.getInstance().getSkill(skillId);
            if (skill == null || skill instanceof ActiveSkill) {
                continue;
            }

            persistentHudSkills.add(skill);
        }

        return persistentHudSkills;
    }

    private boolean shouldDrawHudBuff(Buff buff) {
        return buff != null && buff.active() && !isPassiveSkillBuff(buff);
    }

    private boolean isPassiveSkillBuff(Buff buff) {
        return buff instanceof Health
                || buff instanceof Regeneration
                || buff instanceof Toughness
                || buff instanceof Mastery
                || buff instanceof Aggression
                || buff instanceof GladiatorBuff
                || buff instanceof BerserkerBuff
                || buff instanceof Mana
                || buff instanceof ManaRegeneration
                || buff instanceof FireMastery
                || buff instanceof BattleMage
                || buff instanceof WandMaster
                || buff instanceof GrandMaster
                || buff instanceof Warlock;
    }

    private boolean desktopActionBarEnabled() {
        return SkillfulPixelDungeonPlatformer.getPlatformProfile().keyboardControlsEnabled()
                && !SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()
                && UnitHelper.getInstance().getHero() != null;
    }

    private GameSprite buildBossSlainBanner() {
        Texture bannersTexture = TextureHelper.GetSingleton().getTexture("images/misc/banners.png");
        float bannerWidth = BOSS_SLAIN_BANNER_WIDTH * BOSS_SLAIN_BANNER_SCALE;
        float bannerHeight = BOSS_SLAIN_BANNER_HEIGHT * BOSS_SLAIN_BANNER_SCALE;
        GameSprite banner = new GameSprite(new Sprite(bannersTexture,
                BOSS_SLAIN_BANNER_X,
                BOSS_SLAIN_BANNER_Y,
                BOSS_SLAIN_BANNER_WIDTH,
                BOSS_SLAIN_BANNER_HEIGHT),
                bannerWidth,
                bannerHeight);
        banner.setPosition((ConstantsHelper.SCREEN_WIDTH - bannerWidth) / 2f, ConstantsHelper.SCREEN_HEIGHT - bannerHeight - 40f);
        return banner;
    }

    private GameSprite buildGameOverBanner(float mainMenuButtonY) {
        Texture bannersTexture = TextureHelper.GetSingleton().getTexture("images/misc/banners.png");
        float bannerWidth = GAME_OVER_BANNER_WIDTH * GAME_OVER_BANNER_SCALE;
        float bannerHeight = GAME_OVER_BANNER_HEIGHT * GAME_OVER_BANNER_SCALE;
        GameSprite banner = new GameSprite(new Sprite(bannersTexture,
                GAME_OVER_BANNER_X,
                GAME_OVER_BANNER_Y,
                GAME_OVER_BANNER_WIDTH,
                GAME_OVER_BANNER_HEIGHT),
                bannerWidth,
                bannerHeight);
        banner.setPosition((ConstantsHelper.SCREEN_WIDTH - bannerWidth) / 2f, mainMenuButtonY + 230f);
        return banner;
    }

    private static final class DesktopActionSlot {
        private final GameSprite icon;
        private final String badgeText;
        private final Color badgeColor;
        private final boolean isAvailable;
        private final String bindingLabel;
        private final boolean cooldownActive;
        private final int sortOrder;

        private DesktopActionSlot(GameSprite icon, int manaCost, boolean hasEnoughMana, String bindingLabel, boolean cooldownActive) {
            this(icon,
                    manaCost > 0 ? String.valueOf(manaCost) : null,
                    manaCost > 0 ? (hasEnoughMana ? Color.ROYAL : Color.FIREBRICK) : null,
                    hasEnoughMana,
                    bindingLabel,
                    cooldownActive,
                    Integer.MIN_VALUE);
        }

        private DesktopActionSlot(GameSprite icon, int manaCost, boolean hasEnoughMana, String bindingLabel, boolean cooldownActive, int sortOrder) {
            this(icon,
                    manaCost > 0 ? String.valueOf(manaCost) : null,
                    manaCost > 0 ? (hasEnoughMana ? Color.ROYAL : Color.FIREBRICK) : null,
                    hasEnoughMana,
                    bindingLabel,
                    cooldownActive,
                    sortOrder);
        }

        private DesktopActionSlot(GameSprite icon, String badgeText, Color badgeColor, boolean isAvailable, String bindingLabel, boolean cooldownActive) {
            this(icon, badgeText, badgeColor, isAvailable, bindingLabel, cooldownActive, Integer.MIN_VALUE);
        }

        private DesktopActionSlot(GameSprite icon, String badgeText, Color badgeColor, boolean isAvailable, String bindingLabel, boolean cooldownActive, int sortOrder) {
            this.icon = icon;
            this.badgeText = badgeText;
            this.badgeColor = badgeColor != null ? badgeColor : Color.WHITE;
            this.isAvailable = isAvailable;
            this.bindingLabel = bindingLabel;
            this.cooldownActive = cooldownActive;
            this.sortOrder = sortOrder;
        }
    }
}
