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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.Achievement;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SkillsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Treasure;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.Wand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.PauseMenuRowButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.InputGestureListener;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerInput;
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

public class UIHelper {
    private static final int BOSS_SLAIN_BANNER_X = 6;
    private static final int BOSS_SLAIN_BANNER_Y = 68;
    private static final int BOSS_SLAIN_BANNER_WIDTH = 122;
    private static final int BOSS_SLAIN_BANNER_HEIGHT = 39;
    private static final float BOSS_SLAIN_BANNER_SCALE = 4.5f;
    private static final float BOSS_SLAIN_BANNER_DURATION = 4f;
    private static final float ACHIEVEMENT_BANNER_WIDTH = 560f;
    private static final float ACHIEVEMENT_BANNER_HEIGHT = 140f;
    private static final float ACHIEVEMENT_BANNER_ICON_SIZE = 72f;
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
    public static final float HUD_HEIGHT = 190f;
    private static final float HUD_SLOT_WIDTH = 88f;
    private static final float HUD_SLOT_HEIGHT = 120f;

    private static final float HUD_ACTION_BOTTOM = 6f;
    private static final int HUD_STATUS_X = 688;
    private static final int HUD_STATUS_Y = 140;
    private static final int HUD_STATUS_SIZE = 36;
    private static final int HUD_STATUS_STEP = 52;
    private static final int HUD_STATUSES_PER_ROW = (int) ((ConstantsHelper.SCREEN_WIDTH - HUD_STATUS_X) / HUD_STATUS_STEP);
    private HudActionButton[] desktopHudActions;
    private final Vector3 hudPointer = new Vector3();
    private final ArrayList<PickupNotice> pickupNotices = new ArrayList<PickupNotice>();
    private static final int MAX_PICKUP_NOTICES = 3;
    private static final float PICKUP_NOTICE_DURATION = 2.2f;
    private static final float HUD_BAR_WIDTH = 330f;
    private static final float HUD_BAR_HEIGHT = 32f;
    private static final float HUD_BAR_X = 160f;
    private static final float HP_BAR_Y = 134f;
    private static final float MP_BAR_Y = 94f;
    private static final float XP_BAR_Y = 54f;
    private static final int HUD_PORTRAIT_SIZE = 104;
    private static final int HUD_BAG_SIZE = 96;
    private static final float HUD_PLAYER_PANEL_LEFT = 20f;
    private static final float HUD_PLAYER_PANEL_WIDTH = 636f;
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
    private float achievementBannerNameScale = 1.9f;
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

    private GameSprite hpBar, mpBar, xpBar;
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
        desktopHudActions = null;
        pickupNotices.clear();
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
        heroPortrait.setWidth(HUD_PORTRAIT_SIZE);
        heroPortrait.setHeight(HUD_PORTRAIT_SIZE);
        heroPortrait.setPosition(32, 54);
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
                UnitHelper.getInstance().getHero().requestPrimaryAction();
            }
        };

        float rangedButtonX = touchHudEnabled ? touchOrbitButtonX(1) : 2075f;
        float rangedButtonY = touchHudEnabled ? touchOrbitButtonY(1) : 600f;
        rangedButton = new ActionButton(rangedButtonX, rangedButtonY, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, TOUCH_RIGHT_SECONDARY_BUTTON_SIZE, "images/buttons/blank-button.png", "images/buttons/blank-button-pressed.png"){
            @Override
            public void click(){
                UnitHelper.getInstance().getHero().requestRangedAction();
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
                    UnitHelper.getInstance().getHero().requestQuickSkill(quickSkillSlotIndex);
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

        backPackButton = new ActionButton(548, 54, HUD_BAG_SIZE, HUD_BAG_SIZE, "images/buttons/back-pack.png", "images/buttons/back-pack.png"){
            { enableUiPressFeedback(); }
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
                PauseMenuWindow.openGameplay();
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

        gs = new GameSprite(UnitHelper.getInstance().getHero().getHeroClass().getJumpButtonArt(), TOUCH_RIGHT_SECONDARY_ICON_SIZE, TOUCH_RIGHT_SECONDARY_ICON_SIZE);
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
        xpBar = new GameSprite("images/misc/hp_bar.png", HUD_BAR_WIDTH, HUD_BAR_HEIGHT);
        xpBar.setPosition(HUD_BAR_X, XP_BAR_Y);
        desktopFoodIcon = new GameSprite("images/items/food.png", 45, 45);
        desktopHealthPotionIcon = new GameSprite("images/items/health-potion.png", 45, 45);
        desktopManaPotionIcon = new GameSprite("images/items/mana-potion.png", 45, 45);
        desktopDepthIcon = new GameSprite("images/misc/depth.png", 45, 45);
        desktopKeyIcon = new GameSprite("images/misc/keys.png", 45, 45);
        updateTouchConsumableButtons();
        updateTouchActionButtonAvailability();
    }

    private float hudBottom() {
        return GameHelper.GetSingleton().getUICamera().position.y
                - GameHelper.GetSingleton().getUICamera().viewportHeight / 2f;
    }

    private float hudPlayerOffsetX() {
        if (!SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()) return 0f;
        return GameHelper.GetSingleton().getUICamera().position.x
                - (HUD_PLAYER_PANEL_LEFT + HUD_PLAYER_PANEL_WIDTH / 2f);
    }


    public void layoutHud() {
        if (backPackButton == null || hpBar == null) return;
        float bottom = hudBottom();
        float offsetX = hudPlayerOffsetX();
        heroPortrait.setPosition(offsetX + 32f, bottom + 54f);
        backPackButton.setPosition(offsetX + 548f, bottom + 54f);
        hpBar.setPosition(offsetX + HUD_BAR_X, bottom + HP_BAR_Y);
        mpBar.setPosition(offsetX + HUD_BAR_X, bottom + MP_BAR_Y);
        xpBar.setPosition(offsetX + HUD_BAR_X, bottom + XP_BAR_Y);
        layoutDesktopActions();
    }

    private void hudRect(Batch batch, float x, float y, float width, float height, float r, float g, float b, float alpha) {
        float packed = batch.getPackedColor();
        batch.setColor(r, g, b, alpha);
        batch.draw(TextureHelper.GetSingleton().getSolidPixel(), x, y, width, height);
        batch.setPackedColor(packed);
    }

    private void drawHudBackdrop(Batch batch) {
        float bottom = hudBottom();
        float viewWidth = GameHelper.GetSingleton().getUICamera().viewportWidth;
        float left = GameHelper.GetSingleton().getUICamera().position.x - viewWidth / 2f;
        hudRect(batch, left, bottom, viewWidth, HUD_HEIGHT, 0.035f, 0.058f, 0.069f, 0.97f);
        hudRect(batch, left, bottom + HUD_HEIGHT - 2f, viewWidth, 2f, 0.33f, 0.41f, 0.42f, 0.8f);
        float offsetX = hudPlayerOffsetX();
        hudRect(batch, offsetX + HUD_PLAYER_PANEL_LEFT, bottom + 36f, 482f, 140f, 0.09f, 0.14f, 0.16f, 0.9f);
        hudRect(batch, offsetX + 536f, bottom + 42f, 120f, 120f, 0.09f, 0.14f, 0.16f, 0.9f);
    }

    public void drawButtons(Batch batch){
        if (!heroDead()) drawPickupNotices(batch);
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

        layoutHud();
        drawHudBackdrop(batch);
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
                hpBar.getX(),
                hpBar.getY(),
                UnitHelper.getInstance().getHero().getHP(),
                UnitHelper.getInstance().getHero().getMaxHP(),
                ACTIVE_HP_FILL_COLOR,
                "custom.hud.hp");
        drawResourceBar(batch,
                mpBar,
                mpBar.getX(),
                mpBar.getY(),
                UnitHelper.getInstance().getHero().getMp(),
                UnitHelper.getInstance().getHero().getMmp(),
                ACTIVE_MP_FILL_COLOR,
                "custom.hud.mp");
        Hero statusHero = UnitHelper.getInstance().getHero();
        drawResourceBar(batch, xpBar, xpBar.getX(), xpBar.getY(), statusHero.getExperience(),
                statusHero.getExperience() + statusHero.getExperienceToNextLevel(), new Color(0.72f, 0.58f, 0.28f, 1f),
                "custom.hud.xp");

        boolean touchHudEnabled = SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled();
        if (touchHudEnabled) {
            drawTouchStatusBadges(batch);
        }


        float statusX = touchHudEnabled ? heroPortrait.getX() : HUD_STATUS_X;
        float statusY = touchHudEnabled ? HUD_HEIGHT + 8f : HUD_STATUS_Y;
        int statusesPerRow = touchHudEnabled ? (int) (HUD_PLAYER_PANEL_WIDTH / HUD_STATUS_STEP) : HUD_STATUSES_PER_ROW;
        int offsetX = 0;
        int offsetY = 0;
        for(Buff buff : UnitHelper.getInstance().getHero().getBuffs()){
            if(!shouldDrawHudBuff(buff)){
                continue;
            }

            GameSprite gs = buff.getGameSprite();
            gs.setHeight(HUD_STATUS_SIZE);
            gs.setWidth(HUD_STATUS_SIZE);
            gs.setPosition(statusX + offsetX, hudBottom() + statusY + offsetY);
            gs.draw(batch);

            offsetX += HUD_STATUS_STEP;

            if(offsetX == HUD_STATUS_STEP * statusesPerRow){
                offsetX = 0;
                offsetY += HUD_STATUS_STEP;
            }
        }

        for (Skill skill : getPersistentHudSkills(UnitHelper.getInstance().getHero())) {
            GameSprite gs = skill.getGameSprite();
            gs.setHeight(HUD_STATUS_SIZE);
            gs.setWidth(HUD_STATUS_SIZE);
            gs.setPosition(statusX + offsetX, hudBottom() + statusY + offsetY);
            gs.draw(batch);

            offsetX += HUD_STATUS_STEP;
            if(offsetX == HUD_STATUS_STEP * statusesPerRow){
                offsetX = 0;
                offsetY += HUD_STATUS_STEP;
            }
        }

        String necromancerStatus = NecromancerFeedback.summary(UnitHelper.getInstance().getHero());
        if (necromancerStatus.isEmpty()) necromancerStatus = MercenaryFeedback.summary(UnitHelper.getInstance().getHero());
        if (!necromancerStatus.isEmpty()) {
            float statusTextX = touchHudEnabled ? statusX : Math.max(960f, HUD_STATUS_X + offsetX + 16f);
            float statusTextWidth = touchHudEnabled ? HUD_PLAYER_PANEL_WIDTH - 24f : ConstantsHelper.SCREEN_WIDTH - 32f - statusTextX;
            float statusTextY = touchHudEnabled ? statusY + offsetY + HUD_STATUS_SIZE + 30f : HUD_STATUS_Y + 27f;
            if (statusTextWidth > 0f) hudLabel(batch, necromancerStatus, statusTextX,
                    hudBottom() + statusTextY, statusTextWidth, 1.9f, Color.LIGHT_GRAY);
        }
        drawDesktopActionBar(batch);
        drawDesktopConsumables(batch);

        GlyphLayout inventoryLayout = FontHelper.getSingleton().measure(Color.WHITE, 2.2f, inventoryString);
        FontHelper.getSingleton().writeWhite(batch, 2.2f,
                backPackButton.x + (HUD_BAG_SIZE - inventoryLayout.width) / 2f, hudBottom() + 28f, inventoryString);
    }

    private void drawResourceBar(Batch batch,
                                 GameSprite barSprite,
                                 float x,
                                 float y,
                                 int currentValue,
                                 int maxValue,
                                 Color fillColor,
                                 String labelKey) {
        float fillFraction = maxValue <= 0 ? 0f : MathUtils.clamp(currentValue / (float) maxValue, 0f, 1f);
        int filledBarWidth = Math.round(HUD_BAR_WIDTH * fillFraction);

        hudRect(batch, x - 2f, y - 2f, HUD_BAR_WIDTH + 4f, HUD_BAR_HEIGHT + 4f, 0.24f, 0.31f, 0.33f, 1f);
        hudRect(batch, x, y, HUD_BAR_WIDTH, HUD_BAR_HEIGHT, 0.025f, 0.035f, 0.045f, 1f);

        barSprite.setWidth(filledBarWidth);
        barSprite.setHeight((int) HUD_BAR_HEIGHT);
        barSprite.setColor(getResourceFillColor(fillColor, fillFraction));
        barSprite.setAlpha(1f);
        if (barSprite == xpBar) {

            hudRect(batch, x, y, filledBarWidth, HUD_BAR_HEIGHT, fillColor.r, fillColor.g, fillColor.b, 1f);
        } else {
            barSprite.draw(batch);
        }
        barSprite.setColor(Color.WHITE);

        drawResourceValue(batch, x, y, Messages.get(labelKey, Math.max(0, currentValue), Math.max(0, maxValue)));
    }

    private void drawResourceValue(Batch batch, float barX, float barY, String valueText) {
        float textSize = fitHudBarTextSize(valueText);
        GlyphLayout layout = FontHelper.getSingleton().measure(Color.WHITE, textSize, valueText);
        float textX = barX + (HUD_BAR_WIDTH - layout.width) / 2f;

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
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero.getRangedWeapon() instanceof Gun) {

            hero.requestRangedAction();
            return true;
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

    public void cancelPointerInput() {
        clearPressedButton();
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


    public void showPickupNotice(String text, GameSprite icon) {
        if (text == null || text.isEmpty()) return;
        if (pickupNotices.size() == MAX_PICKUP_NOTICES) pickupNotices.remove(0);
        pickupNotices.add(new PickupNotice(text, snapshotDesktopActionIcon(icon)));
    }

    public void showItemReceipt(Item item) {

        if (item == null || item instanceof Treasure) return;
        showPickupNotice(Messages.get("custom.notice.received", new Object[]{item.getName()}), item.getGameSprite());
    }

    private static final class PickupNotice {
        private final String text;
        private final GameSprite icon;
        private float remaining = PICKUP_NOTICE_DURATION;
        private PickupNotice(String text, GameSprite icon) { this.text = text; this.icon = icon; }
    }

    private float noticeX() {
        float viewWidth = GameHelper.GetSingleton().getUICamera().viewportWidth;
        float left = GameHelper.GetSingleton().getUICamera().position.x - viewWidth / 2f;
        float x = left + viewWidth - ACHIEVEMENT_BANNER_WIDTH - 124f;
        if (exitButton != null) x = Math.min(x, exitButton.x - ACHIEVEMENT_BANNER_WIDTH - 24f);
        float top = hudBottom() + GameHelper.GetSingleton().getUICamera().viewportHeight;
        Rectangle bank = new Rectangle(x, top - 400f, ACHIEVEMENT_BANNER_WIDTH, 376f);
        MapHelper map = MapHelper.getInstance();
        for (MapHelper.InteractionPrompt prompt : new MapHelper.InteractionPrompt[]{map.getDoorPrompt(), map.getContextPrompt()}) {
            if (prompt == null) continue;
            Vector3 point = GameHelper.GetSingleton().getCamera().project(new Vector3(prompt.x, prompt.y, 0));
            point.y = Gdx.graphics.getHeight() - point.y;
            GameHelper.GetSingleton().getUICamera().unproject(point);

            float promptY = MathUtils.clamp(point.y, hudBottom() + HUD_HEIGHT + 12f, top - 52f);
            if (bank.overlaps(new Rectangle(point.x - 220f, promptY - 80f, 440f, 160f))) return left + 24f;
        }
        return x;
    }

    private void drawNoticePanel(Batch batch, float x, float y, float width, float height, float alpha) {
        hudRect(batch, x, y, width, height, 0.32f, 0.40f, 0.41f, alpha);
        hudRect(batch, x + 2f, y + 2f, width - 4f, height - 4f, 0.025f, 0.045f, 0.055f, alpha);
        hudRect(batch, x + 2f, y + 2f, 3f, height - 4f, 0.73f, 0.61f, 0.32f, alpha);
    }

    private void drawPickupNotices(Batch batch) {
        if (pickupNotices.isEmpty()) return;
        float x = noticeX();
        float top = hudBottom() + GameHelper.GetSingleton().getUICamera().viewportHeight - 24f;
        if (achievementBannerTimer > 0f) top -= ACHIEVEMENT_BANNER_HEIGHT + 10f;
        for (int i = pickupNotices.size() - 1; i >= 0; i--) {
            PickupNotice notice = pickupNotices.get(i);
            float y = top - 64f;
            float alpha = Math.min(1f, notice.remaining / 0.25f);
            drawNoticePanel(batch, x, y, ACHIEVEMENT_BANNER_WIDTH, 64f, alpha * 0.96f);
            if (notice.icon != null) {
                notice.icon.setWidth(40); notice.icon.setHeight(40);
                notice.icon.setPosition(x + 16f, y + 12f);
                notice.icon.setAlpha(alpha); notice.icon.draw(batch);
            }
            hudLabel(batch, notice.text, x + 68f, y + 41f, ACHIEVEMENT_BANNER_WIDTH - 84f, 1.9f,
                    new Color(1f, 1f, 1f, alpha));
            notice.remaining = Math.max(0f, notice.remaining - Gdx.graphics.getDeltaTime());
            if (notice.remaining == 0f) pickupNotices.remove(i);
            top = y - 8f;
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

        FontHelper.FittedTextBlock name = FontHelper.getSingleton().fitOverlayText("achievement-banner",
                achievement.getName(), achievement.getName(), 1.9f, ACHIEVEMENT_BANNER_WIDTH - 120f, 70f);
        achievementBannerName = name.text;
        achievementBannerNameScale = name.size;
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
        float x = noticeX();
        float y = hudBottom() + GameHelper.GetSingleton().getUICamera().viewportHeight - ACHIEVEMENT_BANNER_HEIGHT - 24f;
        drawNoticePanel(batch, x, y, ACHIEVEMENT_BANNER_WIDTH, ACHIEVEMENT_BANNER_HEIGHT, 0.96f);
        achievementBannerIcon.setPosition(x + 16f, y + (ACHIEVEMENT_BANNER_HEIGHT - ACHIEVEMENT_BANNER_ICON_SIZE) / 2f);
        achievementBannerIcon.draw(batch);
        hudLabel(batch, Messages.maybeTranslate("BADGE UNLOCKED"), x + 104f, y + 117f,
                ACHIEVEMENT_BANNER_WIDTH - 120f, 1.7f, Color.GOLDENROD);
        hudLabel(batch, achievementBannerName, x + 104f, y + 83f, ACHIEVEMENT_BANNER_WIDTH - 120f, achievementBannerNameScale, Color.WHITE);

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

        float viewWidth = GameHelper.GetSingleton().getUICamera().viewportWidth;
        float viewHeight = GameHelper.GetSingleton().getUICamera().viewportHeight;
        float centerX = GameHelper.GetSingleton().getUICamera().position.x;
        float centerY = GameHelper.GetSingleton().getUICamera().position.y;
        hudRect(batch, centerX - viewWidth / 2f, centerY - viewHeight / 2f, viewWidth, viewHeight, 0.015f, 0.025f, 0.03f, 1f);

        depthTransitionBannerFrame += Gdx.graphics.getDeltaTime();

        float alpha = GameSettingsHelper.getInstance().isReducedVisualEffects() ? 1f :
                Math.min(1f, Math.min(depthTransitionBannerFrame / 0.18f, depthTransitionBannerTimer / 0.18f));
        drawNoticePanel(batch, centerX - 300f, centerY - 70f, 600f, 140f, alpha);
        hudLabel(batch, Messages.get("custom.notice.depth", MapHelper.getInstance().getDepth()),
                centerX - 280f, centerY + 39f, 560f, 1.9f, new Color(0.85f, 0.73f, 0.43f, alpha));
        hudLabel(batch, depthTransitionBannerText, centerX - 280f, centerY - 5f, 560f, 2.6f,
                new Color(1f, 1f, 1f, alpha));

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
        ensureDesktopActions();
        Hero hero = UnitHelper.getInstance().getHero();
        Weapon weapon = hero.getWeapon();
        int attackManaCost = weapon instanceof Wand ? ((Wand) weapon).getManaCost() : 0;
        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        boolean recovering = hero.isAttacking() || hero.getAttackCycleRemainingSeconds() > 0f;
        boolean attackOnCooldown = recovering || (weapon instanceof Wand && ((Wand) weapon).isOnCooldown());
        GameSprite attackIcon = snapshotDesktopActionIcon(weapon.getGameSprite());
        EnhancementVisualHelper.applyWeaponEnhancementPulse(attackIcon, weapon);
        drawHudAction(batch, 3, attackIcon, weapon.getName(),
                attackManaCost > 0 ? Messages.get("custom.hud.mana_cost", attackManaCost) : null,
                settings.hudBindingLabel(settings.getAttackBinding()),
                hero.canAttack() && hero.getMp() >= attackManaCost && !attackOnCooldown,
                hero.getMp() < attackManaCost ? "custom.hud.no_mana" : null, attackOnCooldown);
        if (hero.getRangedWeapon() != null) {
            GameSprite rangedIcon = snapshotDesktopActionIcon(hero.getRangedWeapon().getGameSprite());
            EnhancementVisualHelper.applyWeaponEnhancementPulse(rangedIcon, hero.getRangedWeapon());
            boolean gunEquipped = hero.getRangedWeapon() instanceof Gun;
            boolean gunSpace = !gunEquipped || ((Gun)hero.getRangedWeapon()).hasProjectileSpace();
            String rangedReason = hero.getRangedWeapon().getAmmo() == 0 ? (gunEquipped ? "custom.guns.empty" : "custom.hud.no_ammo")
                    : (!gunSpace ? "custom.mercenary.shot_limit" : null);
            drawHudAction(batch, 4, rangedIcon, hero.getRangedWeapon().getName(),
                    String.valueOf(hero.getRangedWeapon().getAmmo()), settings.hudBindingLabel(settings.getRangedBinding()),
                    hero.getRangedWeapon().getAmmo() > 0 && hero.canAttack() && gunSpace,
                    rangedReason, recovering);
        } else {
            drawHudAction(batch, 4, null, Messages.get("custom.hud.ranged"), null,
                    settings.hudBindingLabel(settings.getRangedBinding()), false, "custom.hud.empty", false);
        }
        for (int slotIndex = 0; slotIndex < hero.getQuickSkillSlotCount(); slotIndex++) {
            ActiveSkill skill = hero.getQuickSkill(slotIndex);
            String reason = NecromancerFeedback.unavailableReasonKey(hero, skill);
            if (reason == null) reason = MercenaryFeedback.unavailableReasonKey(hero, skill);
            drawHudAction(batch, 5 + slotIndex, skill == null ? null : snapshotDesktopActionIcon(skill.getGameSprite()),
                    skill == null ? Messages.get("custom.hud.skill", slotIndex + 1) : skill.getName(),
                    skill == null || skill.getManaCost() == 0 ? null : Messages.get("custom.hud.mana_cost", skill.getManaCost()),
                    quickSkillBindingLabel(settings, slotIndex), skill != null && skill.canUse(hero),
                    skill == null ? "custom.hud.empty" : (hero.getMp() < skill.getManaCost() ? "custom.hud.no_mana" : reason),
                    skill != null && reason == null && (recovering || skill.isOnCooldown()));
        }
        drawInteractionPrompts(batch);
    }

    private void drawInteractionPrompts(Batch batch) {
        MapHelper map = MapHelper.getInstance();
        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        MapHelper.InteractionPrompt door = map.getDoorPrompt();
        MapHelper.InteractionPrompt context = map.getContextPrompt();
        Rectangle doorBounds = drawInteractionPrompt(batch, door,
                settings.hudBindingLabel(settings.getEnterDoorBinding()), null);
        drawInteractionPrompt(batch, context, settings.hudBindingLabel(settings.getInteractBinding()), doorBounds);
    }

    private Rectangle drawInteractionPrompt(Batch batch, MapHelper.InteractionPrompt prompt,
                                            String binding, Rectangle other) {
        if (prompt == null) return null;
        String text = Messages.get("custom.prompt.binding", new Object[]{binding, Messages.get(prompt.verbKey)});
        float scale = 1.9f;
        GlyphLayout label = FontHelper.getSingleton().measure(Color.WHITE, scale, text);
        if (label.width > 360f) {
            scale *= 360f / label.width;
            label = FontHelper.getSingleton().measure(Color.WHITE, scale, text);
        }
        Vector3 point = GameHelper.GetSingleton().getCamera().project(new Vector3(prompt.x, prompt.y, 0));
        point.y = Gdx.graphics.getHeight() - point.y;
        GameHelper.GetSingleton().getUICamera().unproject(point);
        float viewWidth = GameHelper.GetSingleton().getUICamera().viewportWidth;
        float left = GameHelper.GetSingleton().getUICamera().position.x - viewWidth / 2f;
        float top = hudBottom() + GameHelper.GetSingleton().getUICamera().viewportHeight;
        float width = label.width + 28f, height = 44f;
        float x = MathUtils.clamp(point.x - width / 2f, left + 8f, left + viewWidth - width - 8f);
        float y = MathUtils.clamp(point.y + 10f, hudBottom() + HUD_HEIGHT + 12f, top - height - 8f);
        Rectangle bounds = new Rectangle(x, y, width, height);
        if (other != null && bounds.overlaps(other)) {

            y = other.y - height - 8f;
            bounds.y = y;
        }
        hudRect(batch, x, y, width, height, 0.38f, 0.46f, 0.46f, 0.96f);
        hudRect(batch, x + 2f, y + 2f, width - 4f, height - 4f, 0.025f, 0.045f, 0.055f, 0.96f);
        FontHelper.getSingleton().write(Color.WHITE, batch, scale, x + 14f, y + (height + label.height) / 2f, text);

        hudRect(batch, point.x - 1f, Math.min(point.y, y), 2f, Math.max(2f, Math.abs(y - point.y)), 0.65f, 0.71f, 0.64f, 0.85f);
        return bounds;
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

        ensureDesktopActions();
        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        InventoryHelper inventory = InventoryHelper.getInstance();
        int[] counts = {inventory.getRationsCount(), inventory.getHealthPotionCount(), inventory.getManaPotionCount()};
        GameSprite[] icons = {desktopFoodIcon, desktopHealthPotionIcon, desktopManaPotionIcon};
        String[] titles = {"custom.hud.food", "custom.hud.health_potion", "custom.hud.mana_potion"};
        GameSettingsHelper.InputBinding[] bindings = {settings.getEatFoodBinding(), settings.getHealthPotionBinding(), settings.getManaPotionBinding()};

        float counterX = ConstantsHelper.SCREEN_WIDTH - 240f;
        drawHudCounter(batch, desktopDepthIcon, Math.max(1, MapHelper.getInstance().getDepth()), counterX);
        drawHudCounter(batch, desktopKeyIcon, MapHelper.getInstance().getCurrentDepthKeyCount(), counterX + 108f);
        for (int index = 0; index < counts.length; index++) {
            drawHudAction(batch, index, icons[index], Messages.get(titles[index]), String.valueOf(counts[index]),
                    settings.hudBindingLabel(bindings[index]), counts[index] > 0, counts[index] == 0 ? "custom.hud.empty" : null, false);
        }
    }

    private void drawHudCounter(Batch batch, GameSprite icon, int value, float x) {
        icon.setWidth(48);
        icon.setHeight(48);
        icon.setPosition(x, hudBottom() + HUD_ACTION_BOTTOM + 60f);
        icon.draw(batch);
        hudLabel(batch, String.valueOf(value), x + 50f, hudBottom() + HUD_ACTION_BOTTOM + 88f, 48f, 2f, Color.WHITE);
    }

    private void ensureDesktopActions() {
        if (desktopHudActions != null) return;
        desktopHudActions = new HudActionButton[5 + Hero.QUICK_SKILL_SLOT_COUNT];
        for (int i = 0; i < desktopHudActions.length; i++) {
            desktopHudActions[i] = new HudActionButton(i);
            buttons.add(desktopHudActions[i]);
        }
        layoutDesktopActions();
    }

    private void layoutDesktopActions() {
        if (desktopHudActions == null) return;
        for (int i = 0; i < desktopHudActions.length; i++) {
            float x = i < 3 ? 704f + i * 108f : 1060f + (i - 3) * 108f;
            desktopHudActions[i].setPosition(x, hudBottom() + HUD_ACTION_BOTTOM);
        }
    }



    private final class HudActionButton extends ActionButton {
        private final int slot;
        private HudActionButton(int slot) {
            super(0, 0, HUD_SLOT_WIDTH, HUD_SLOT_HEIGHT, (Texture) null, (Texture) null);
            this.slot = slot;
            enableUiPressFeedback();
        }
        @Override public boolean canClick() { return isEnabled(); }
        @Override public boolean isHitProjected(float px, float py) {
            return isEnabled() && px >= x && px < x + HUD_SLOT_WIDTH && py >= y && py < y + HUD_SLOT_HEIGHT;
        }
        @Override public void draw(Batch batch) {                                              }
        @Override public void clicked() {
            switch (slot) {
                case 0: performEatFoodAction(); break;
                case 1: performHealthPotionAction(); break;
                case 2: performManaPotionAction(); break;
                case 3: performPrimaryAction(); break;
                case 4: performSecondaryAction(); break;
                default: performQuickSkillAction(slot - 5); break;
            }
        }
        private boolean pressed() { return isShowingPressFeedback(); }
    }


    public int getHoveredQuickSkillSlot() {
        if (desktopHudActions == null || WindowHelper.getInstance().windowOpen()) return -1;
        hudPointer.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        GameHelper.GetSingleton().getUICamera().unproject(hudPointer);
        for (int i = 5; i < desktopHudActions.length; i++)
            if (desktopHudActions[i].isHitProjected(hudPointer.x, hudPointer.y)) return i - 5;
        return -1;
    }

    private void drawHudAction(Batch batch, int slot, GameSprite source, String title, String badge,
                               String binding, boolean available, String reasonKey, boolean cooling) {
        HudActionButton button = desktopHudActions[slot];
        hudPointer.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        GameHelper.GetSingleton().getUICamera().unproject(hudPointer);
        boolean hover = !WindowHelper.getInstance().windowOpen() && button.isHitProjected(hudPointer.x, hudPointer.y);
        boolean pressed = !WindowHelper.getInstance().windowOpen() && button.pressed();
        float x = button.x, y = button.y + 32f;
        float border = hover ? 3f : 1.5f;
        hudRect(batch, x, y, HUD_SLOT_WIDTH, HUD_SLOT_WIDTH, hover ? 0.75f : 0.30f, hover ? 0.77f : 0.39f, hover ? 0.68f : 0.40f, 1f);
        hudRect(batch, x + border, y + border, HUD_SLOT_WIDTH - 2 * border, HUD_SLOT_WIDTH - 2 * border,
                pressed ? 0.18f : 0.065f, pressed ? 0.24f : 0.10f, pressed ? 0.25f : 0.12f, 1f);
        if (source != null) {
            GameSprite icon = source.clone();
            icon.setWidth(64);
            icon.setHeight(64);
            icon.setPosition(x + 12f, y + 12f - (pressed ? 3f : 0f));
            icon.setAlpha(icon.getAlpha() * (available ? 1f : 0.4f));
            icon.draw(batch);
        } else {
            hudRect(batch, x + 30f, y + 44f, 28f, 3f, 0.5f, 0.57f, 0.57f, 1f);
        }
        if (badge != null) {
            hudRect(batch, x + 21f, y + 66f, 64f, 20f, 0.02f, 0.035f, 0.045f, 0.94f);
            hudLabel(batch, badge, x + 20f, y + 84f, 64f, 1.25f, Color.WHITE);
        }
        String state = cooling ? Messages.get("custom.hud.wait") :
                (reasonKey != null ? Messages.get(reasonKey) : (available ? null : Messages.get("custom.hud.unavailable")));
        if (state != null) {
            hudRect(batch, x + 3f, y + 3f, HUD_SLOT_WIDTH - 6f, 22f, 0.025f, 0.04f, 0.045f, 0.96f);

            hudLabel(batch, state, x + 5f, y + 23f, HUD_SLOT_WIDTH - 10f, 2f, Color.LIGHT_GRAY);
            if (cooling) {
                hudRect(batch, x + 6f, y + 51f, 14f, 2f, 0.8f, 0.8f, 0.7f, 1f);
                hudRect(batch, x + 10f, y + 39f, 6f, 12f, 0.8f, 0.8f, 0.7f, 1f);
                hudRect(batch, x + 6f, y + 37f, 14f, 2f, 0.8f, 0.8f, 0.7f, 1f);
            } else if (source != null) {
                hudRect(batch, x + 6f, y + 44f, 15f, 3f, 0.8f, 0.8f, 0.7f, 1f);
            }
        }
        hudBinding(batch, binding, x, y - 9f);
        if (hover) {
            hudRect(batch, x + 33f, button.y, 22f, pressed ? 5f : 2f, 0.8f, 0.8f, 0.7f, 1f);
            String necromancerHint = NecromancerFeedback.hint(reasonKey);
            if (necromancerHint == null) necromancerHint = MercenaryFeedback.hint(reasonKey);
            if (necromancerHint != null) {
                float tooltipX = MathUtils.clamp(x - 166f, 688f, 1876f);
                hudRect(batch, tooltipX, hudBottom() + HUD_HEIGHT + 6f, 420f, 112f, 0.035f, 0.058f, 0.069f, 0.97f);
                hudLabel(batch, title, tooltipX + 8f, hudBottom() + HUD_HEIGHT + 104f, 404f, 1.8f, Color.WHITE);
                FontHelper.getSingleton().writeRaw(Color.LIGHT_GRAY, batch, 1.8f, tooltipX + 12f,
                        hudBottom() + HUD_HEIGHT + 66f, UtilsHelper.multiLine(necromancerHint, 2, 396f));
                return;
            }
            float tooltipX = MathUtils.clamp(x - 106f, 688f, 1996f);
            hudRect(batch, tooltipX, hudBottom() + HUD_HEIGHT + 6f, 300f, 44f, 0.035f, 0.058f, 0.069f, 0.97f);
            hudLabel(batch, title, tooltipX + 8f, hudBottom() + HUD_HEIGHT + 38f, 284f, 1.8f, Color.WHITE);
        }
    }

    private void hudBinding(Batch batch, String binding, float x, float y) {


        ControllerInput input = ControllerInput.getInstance();
        String face = input.usesControllerLabels() && input.layout() == ControllerButton.Layout.PLAYSTATION
                ? binding.substring(binding.lastIndexOf('+') + 1) : "";
        if (!face.equals("Cross") && !face.equals("Circle") && !face.equals("Square") && !face.equals("Triangle")) {
            hudLabel(batch, binding, x, y, HUD_SLOT_WIDTH, 1.7f, Color.WHITE); return;
        }
        String prefix = binding.substring(0, binding.length() - face.length());
        float prefixWidth = prefix.isEmpty() ? 0 : new GlyphLayout(FontHelper.getSingleton().getFont(Color.WHITE, 1.7f), prefix).width + 4f;
        float size = 22f, left = x + (HUD_SLOT_WIDTH - prefixWidth - size) / 2f, bottom = y - size;
        if (!prefix.isEmpty()) FontHelper.getSingleton().write(Color.WHITE, batch, 1.7f, left, y, prefix);
        left += prefixWidth;
        float packed = batch.getPackedColor(); batch.setColor(Color.WHITE);
        if (face.equals("Cross")) {
            hudStroke(batch, left, bottom, left + size, bottom + size);
            hudStroke(batch, left, bottom + size, left + size, bottom);
        } else if (face.equals("Triangle")) {
            hudStroke(batch, left, bottom, left + size, bottom);
            hudStroke(batch, left, bottom, left + size / 2, bottom + size);
            hudStroke(batch, left + size, bottom, left + size / 2, bottom + size);
        } else {
            int sides = face.equals("Square") ? 4 : 16;
            float radius = face.equals("Square") ? size * .70710678f : size / 2;
            float offset = face.equals("Square") ? MathUtils.PI / 4 : 0;
            for (int i = 0; i < sides; i++) {
                float a = offset + i * MathUtils.PI2 / sides, b = offset + (i + 1) * MathUtils.PI2 / sides;
                hudStroke(batch, left + size / 2 + radius * MathUtils.cos(a), bottom + size / 2 + radius * MathUtils.sin(a),
                        left + size / 2 + radius * MathUtils.cos(b), bottom + size / 2 + radius * MathUtils.sin(b));
            }
        }
        batch.setPackedColor(packed);
    }

    private void hudStroke(Batch batch, float x1, float y1, float x2, float y2) {
        float dx = x2 - x1, dy = y2 - y1;
        batch.draw(TextureHelper.GetSingleton().getSolidPixel(), x1, y1 - 1.5f, 0, 1.5f,
                (float)Math.sqrt(dx * dx + dy * dy), 3, 1, 1, MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees,
                0, 0, 1, 1, false, false);
    }

    private void hudLabel(Batch batch, String text, float x, float y, float width, float scale, Color color) {
        GlyphLayout label = new GlyphLayout(FontHelper.getSingleton().getFont(color, scale), text);
        if (label.width > width) {
            scale *= width / label.width;
            label.setText(FontHelper.getSingleton().getFont(color, scale), text);
        }
        FontHelper.getSingleton().write(color, batch, scale, x + (width - label.width) / 2f, y, text);
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

        float badgeX = backPackButton.x + HUD_BAG_SIZE + DESKTOP_CONSUMABLE_LABEL_GAP;

        drawDesktopKeyBadge(batch, badgeX, hudBottom() + 28f);
        drawDesktopDepthBadge(batch, badgeX, hudBottom() + 100f);
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

    private String quickSkillBindingLabel(GameSettingsHelper settings, int slotIndex) {
        switch (slotIndex) {
            case 0:
                return settings.hudBindingLabel(settings.getQuickSkillBinding());
            case 1:
                return settings.hudBindingLabel(settings.getQuickSkill2Binding());
            case 2:
                return settings.hudBindingLabel(settings.getQuickSkill3Binding());
            case 3:
                return settings.hudBindingLabel(settings.getQuickSkill4Binding());
            case 4:
                return settings.hudBindingLabel(settings.getQuickSkill5Binding());
            case 5:
                return settings.hudBindingLabel(settings.getQuickSkill6Binding());
            case 6:
                return settings.hudBindingLabel(settings.getQuickSkill7Binding());
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

}
