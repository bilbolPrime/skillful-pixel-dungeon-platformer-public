package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.DesktopMenuSession;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.DesktopMenuStyle;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuCameraTransition;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuUiFade;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuHeroRoster;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuHeroInfo;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerInput;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.InputGestureListener;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.DescriptionWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.FreeVersionAboutWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.PauseMenuWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.RatKingWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.TitleExitWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.Window;

import java.util.ArrayList;

public class TitleScreen extends MenuScreenBase {
    private static final float FIREWORK_BLAST_COOLDOWN_SECONDS = 0.45f;
    private static final String VERSION_LABEL = "2.0.0 (Build 14)";
    private static final String ABOUT_TEXT = "A realtime platform adaption of Skillful Pixel Dungeon by BilbolDev";
    private static final String TITLE_PATH = "images/intro/pixel-dungeon-platformer.png";
    private static final String TITLE_OVERLAY_PATH = "images/intro/pixel-dungeon-platformer-over.png";
    private static final float TITLE_SCALE = 7f;
    private static final float TITLE_CENTER_Y = 859f;
    private static final float FIREBALL_SIZE = 150f;
    private static final float FIREBALL_GAP = 24f;
    private static final float ABOUT_ICON_RAISE_RATIO = 0.15f;
    private static final float EXIT_BUTTON_SIZE = 100f;
    private static final float EXIT_BUTTON_MARGIN = 40f;
    private static final Color DESKTOP_LABEL = Color.valueOf("B9C4BF");
    private static final Color DESKTOP_LABEL_ACTIVE = Color.valueOf("E8D9A7");
    private static final Color DESKTOP_ROW = new Color(0.48f, 0.43f, 0.25f, 0.07f);
    private static final Color DESKTOP_ROW_ACTIVE = new Color(0.48f, 0.43f, 0.25f, 0.18f);

    private static boolean introFadePending = true;
    private static final String[] FIREWORK_SPRITES = new String[]{
            "images/misc/yellow-dot.png",
            "images/misc/red.png",
            "images/misc/green.png"
    };

    private GameSprite title;
    private GameSprite titleExtra;
    private GameSprite controllerIcon;
    private FontHelper.FittedTextBlock controllerCaption;
    private final Vector3 controllerPointer = new Vector3();
    private GameSprite fireballBackground;
    private GameSprite fireballFront;
    private float leftFireballX;
    private float rightFireballX;
    private float fireballY;
    private final ArrayList<FireEffect> leftBalls = new ArrayList<>();
    private final ArrayList<FireEffect> rightBalls = new ArrayList<>();
    private final ArrayList<FireworkEffect> fireworks = new ArrayList<>();
    private float titleOverlay;
    private boolean titleOverlayIncreasing = true;
    private boolean fireworksActive;
    private float fireworkBlastCooldown;
    private boolean desktopLayout;
    private boolean desktopPaused, menuDisposed;
    private final java.util.Random cosmeticRandom = new java.util.Random();
    private Languages menuLanguage;
    private DesktopMenuSession desktopSession;
    private MenuUiFade menuFade;
    private MenuHeroRoster heroRoster;
    private MenuHeroInfo heroInfo;
    private com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuRunSlots runSlots;
    private DesktopInfoMenu informationMenu;
    private Window informationPointerOwner;
    private boolean informationPointerCaptured;
    private ActionButton informationPressedNavigation;
    private final ArrayList<ActionButton> heroControls = new ArrayList<>();
    private final ArrayList<ActionButton> titleButtons = new ArrayList<>(), heroButtons = new ArrayList<>();
    private MenuCameraTransition.Section desktopSection = MenuCameraTransition.Section.TITLE;
    private GameSprite chamberLogo, chamberRunes;
    private boolean rosterOnEnter;
    public TitleScreen openRosterOnEnter() { rosterOnEnter = true; return this; }

    {
        maxTop = 20;
        maxRight = 35;
        maxPlants = 1;
        decorationDensity = 0.4f;
        isBattle = false;
        showDamage = true;
        template = "kingdom";
    }

    @Override
    protected void createMenuContent() {
        RatKingSupportHelper.getInstance().applyConfiguredTitleTheme();
        desktopLayout = !SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled();
        if (desktopLayout) {
            DesktopMenuStyle.resetHover();
            controllerIcon = new GameSprite("images/menu/game-controller.png", 64, 64);
            menuLanguage = GameSettingsHelper.getInstance().getLanguage();
            desktopSession = new DesktopMenuSession(RatKingSupportHelper.getInstance().getSelectedTitleTheme());
            menuFade = new MenuUiFade();
            heroRoster = new MenuHeroRoster();
            heroInfo = new MenuHeroInfo(this::refreshHeroControls);
            heroRoster.setSelectionListener(heroInfo::show);
            runSlots = new com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuRunSlots(heroRoster, this::refreshHeroControls);
            desktopSession.setHeroCast(heroRoster);
        }

        Texture titleTexture = TextureHelper.GetSingleton().getTexture(TITLE_PATH);
        float titleScale = desktopLayout ? 4.6f : TITLE_SCALE;
        float titleWidth = titleTexture.getWidth() * titleScale;
        float titleHeight = titleTexture.getHeight() * titleScale;
        title = new GameSprite(TITLE_PATH, titleWidth, titleHeight);
        title.setPosition(desktopLayout ? 96f : centerX(titleWidth),
                desktopLayout ? 824f : TITLE_CENTER_Y - titleHeight / 2f);
        titleExtra = new GameSprite(TITLE_OVERLAY_PATH, titleWidth, titleHeight, 0f);
        titleExtra.setPosition(title.getX(), title.getY());

        leftFireballX = title.getX() - FIREBALL_GAP - FIREBALL_SIZE;
        rightFireballX = title.getX() + titleWidth + FIREBALL_GAP;
        fireballY = TITLE_CENTER_Y - FIREBALL_SIZE / 2f;
        fireballBackground = new GameSprite("images/intro/fireball-background.png", FIREBALL_SIZE, FIREBALL_SIZE, 0.7f);
        fireballFront = new GameSprite("images/intro/fireball-front.png", FIREBALL_SIZE, FIREBALL_SIZE, 1f);

        leftBalls.clear();
        rightBalls.clear();
        for (int i = 0; i < (desktopLayout ? 0 : 10); i++) {
            leftBalls.add(new FireEffect(leftFireballX + FIREBALL_SIZE / 2f, TITLE_CENTER_Y));
            rightBalls.add(new FireEffect(rightFireballX + FIREBALL_SIZE / 2f, TITLE_CENTER_Y));
        }

        syncFireworks();

        addTitleButton("PLAY", "play", 0, this::showCharacterSelection);
        addTitleButton("LIBRARY", "library", 1,
                () -> { if (!showInformation(DesktopInfoMenu.Destination.LIBRARY))
                    SkillfulPixelDungeonPlatformer.transition(new LibraryScreen(), true); });
        addTitleButton("SETTINGS", "settings", 2,
                () -> { if (!showInformation(DesktopInfoMenu.Destination.SETTINGS))
                    WindowHelper.getInstance().addWindow(new PauseMenuWindow(false).build()); });
        addTitleButton("RANKINGS", "rankings", 3,
                () -> { if (!showInformation(DesktopInfoMenu.Destination.RANKINGS))
                    SkillfulPixelDungeonPlatformer.transition(new RankingsScreen(), true); });
        addTitleButton("BADGES", "achievements", 4,
                () -> { if (!showInformation(DesktopInfoMenu.Destination.BADGES))
                    SkillfulPixelDungeonPlatformer.transition(new AchievementsScreen(), true); });
        addTitleButton("RAT KING", "ratking", 5,
                () -> { if (!showInformation(DesktopInfoMenu.Destination.RAT_KING))
                    WindowHelper.getInstance().addWindow(new RatKingWindow().build()); });
        addTitleButton("ABOUT", "about", 6, this::showAboutWindow);


        if (desktopLayout) buttons.add(new DesktopMenuButton("Exit", "images/menu/exit.png", 7, () -> Gdx.app.exit()));
        else buttons.add(new ActionButton(
                ConstantsHelper.SCREEN_WIDTH - EXIT_BUTTON_MARGIN - EXIT_BUTTON_SIZE,
                ConstantsHelper.SCREEN_HEIGHT - EXIT_BUTTON_MARGIN - EXIT_BUTTON_SIZE,
                EXIT_BUTTON_SIZE,
                EXIT_BUTTON_SIZE,
                "images/menu/exit.png",
                "images/menu/exit.png") {
            {
                enableUiPressFeedback();
            }

            @Override
            public void click() {
                showExitWindow();
            }
        });
        if (desktopLayout) {
            titleButtons.clear(); titleButtons.addAll(buttons);
            createHeroControls(titleTexture);
            informationMenu = new DesktopInfoMenu(this::showInformation, this::refreshInformationControls);
            if (rosterOnEnter) showCharacterSelection();
        }
    }

    private void showCharacterSelection() {
        if (desktopSession != null && desktopSession.hasScene(MenuCameraTransition.Section.HEROES)) {
            desktopSession.navigation().request(MenuCameraTransition.Section.HEROES, null);
        } else SkillfulPixelDungeonPlatformer.transition(new CharacterSelectScreen(), true);
    }

    private boolean showInformation(DesktopInfoMenu.Destination destination) {
        if (desktopSession == null || informationMenu == null || !informationMenu.supports(destination)
                || !desktopSession.hasScene(MenuCameraTransition.Section.INFORMATION)
                && desktopSession.navigation().section() != MenuCameraTransition.Section.INFORMATION) return false;
        if (!desktopSession.navigation().isSettled()) return true;
        if (destination == DesktopInfoMenu.Destination.BACK) {
            leaveInformationRoom();
            return true;
        }
        cancelInformationPointer();
        WindowHelper.getInstance().hideAll();
        desktopSession.navigation().request(MenuCameraTransition.Section.INFORMATION, () -> {
            informationMenu.open(destination);
            if (destination == DesktopInfoMenu.Destination.SETTINGS)
                WindowHelper.getInstance().addWindow(new PauseMenuWindow(false).build());
            else if (destination == DesktopInfoMenu.Destination.RAT_KING)
                WindowHelper.getInstance().addWindow(new RatKingWindow().build());
            else if (destination == DesktopInfoMenu.Destination.ABOUT) openAboutWindow();
        });
        return true;
    }

    private void refreshInformationControls() {
        if (informationMenu != null && desktopSection == MenuCameraTransition.Section.INFORMATION) {
            buttons.clear(); buttons.addAll(informationMenu.controls());
        }
    }

    private boolean inInformationRoom() {
        return desktopSession != null && ownsMenuInput()
                && desktopSession.navigation().isSettled()
                && desktopSession.navigation().section() == MenuCameraTransition.Section.INFORMATION;
    }

    private void cancelInformationPointer() {
        if (informationPointerCaptured) WindowHelper.getInstance().cancelPointerInput();
        if (informationPressedNavigation != null) informationPressedNavigation.cancelPress();
        informationPointerOwner = null;
        informationPressedNavigation = null;
        informationPointerCaptured = false;
    }

    private void leaveInformationRoom() {
        cancelInformationPointer();
        clearPressedMenuButton();
        WindowHelper.getInstance().hideAll();
        desktopSession.navigation().backToTitle();
    }


    @Override protected InputGestureListener windowInputGestureListener() {
        final InputGestureListener windows = super.windowInputGestureListener();
        return new InputGestureListener() {
            @Override public boolean touchDown(float x, float y, int pointer, int button) {
                cancelInformationPointer();
                Window owner = WindowHelper.getInstance().topWindow();
                if (inInformationRoom() && owner != null) {
                    Vector3 point = projectToUi(x, y);
                    ActionButton navigation = informationMenu.navigationAt(point.x, point.y);

                    if (navigation == informationMenu.backButton() || !owner.contains(point.x, point.y)) {
                        clearPressedMenuButton();
                        WindowHelper.getInstance().cancelPointerInput();
                        informationPointerCaptured = true;
                        informationPointerOwner = owner;
                        informationPressedNavigation = navigation;
                        if (navigation != null) navigation.pressDown();
                        return true;
                    }
                }
                return windows.touchDown(x, y, pointer, button);
            }
            @Override public boolean tap(float x, float y, int count, int button) {
                Vector3 point = projectToUi(x, y);
                Window owner = WindowHelper.getInstance().topWindow();
                if (informationPointerCaptured) {
                    ActionButton navigation = informationPressedNavigation;
                    boolean sameOwner = owner != null && informationPointerOwner == owner && inInformationRoom();
                    boolean activate = navigation != null && sameOwner && navigation.isHitProjected(point.x, point.y);

                    boolean dismiss = navigation == null && sameOwner && !owner.contains(point.x, point.y)
                            && (informationMenu.hasHostedContent() || !WindowHelper.getInstance().isRootWindow(owner));
                    cancelInformationPointer();
                    if (activate) navigation.click();
                    else if (dismiss) owner.hide();
                    return true;
                }
                if (inInformationRoom() && owner != null && !owner.contains(point.x, point.y)) {
                    WindowHelper.getInstance().cancelPointerInput();
                    return true;
                }
                return windows.tap(x, y, count, button);
            }
            @Override public boolean pan(float x, float y, float deltaX, float deltaY) {
                if (informationPointerCaptured) {
                    if (informationPressedNavigation != null) informationPressedNavigation.cancelPress();
                    informationPressedNavigation = null;
                    informationPointerOwner = null;
                    return true;
                }
                return windows.pan(x, y, deltaX, deltaY);
            }
            @Override public boolean panStop(float x, float y, int pointer, int button) {
                if (informationPointerCaptured) { cancelInformationPointer(); return true; }
                return windows.panStop(x, y, pointer, button);
            }
            @Override public boolean longPress(float x, float y) {
                if (informationPointerCaptured) { cancelInformationPointer(); return true; }
                return windows.longPress(x, y);
            }
        };
    }


    public boolean usesDesktopMenuScenes() { return desktopSession != null; }

    private void rebindLanguage() {
        Languages language = GameSettingsHelper.getInstance().getLanguage();
        if (language == menuLanguage) return;
        menuLanguage = language;
        clearPressedMenuButton();
        WindowHelper.getInstance().cancelPointerInput();
        for (ActionButton button : titleButtons)
            if (button instanceof DesktopMenuButton) ((DesktopMenuButton) button).refreshLabel();
        for (ActionButton button : heroControls)
            if (button instanceof HeroChoiceButton) ((HeroChoiceButton) button).refreshLabel();
        heroInfo.rebindLanguage();
        informationMenu.rebindLanguage();
    }

    private void createHeroControls(Texture titleTexture) {
        heroControls.clear();
        for (int i = 0; i < heroRoster.size(); i++) heroControls.add(new HeroChoiceButton(i));
        chamberLogo = new GameSprite(TITLE_PATH, titleTexture.getWidth() * 2.4f, titleTexture.getHeight() * 2.4f);
        chamberLogo.setPosition(1600, 940);
        chamberRunes = new GameSprite(TITLE_OVERLAY_PATH, chamberLogo.getWidth(), chamberLogo.getHeight(), 0f);
        chamberRunes.setPosition(chamberLogo.getX(), chamberLogo.getY());
        refreshHeroControls();
    }

    private void refreshHeroControls() {
        heroButtons.clear(); heroButtons.addAll(heroControls); heroButtons.addAll(heroInfo.controls());
        if (runSlots != null) heroButtons.addAll(runSlots.controls());
        if (desktopSection == MenuCameraTransition.Section.HEROES) {
            buttons.clear(); buttons.addAll(heroButtons);
        }
    }

    private final class HeroChoiceButton extends ActionButton {
        private final HeroClass heroClass;
        private final int index;
        private final GameSprite icon;
        private FontHelper.FittedTextBlock name;
        HeroChoiceButton(int index) {
            super(heroRoster.homeX(index) - 96, 388, 192, 392,
                    "images/misc/black.png", "images/misc/black.png");
            heroClass = heroRoster.actor(index).heroClass();
            this.index = index;
            icon = heroClass.getClassPortrait();
            icon.setWidth(48); icon.setHeight(48);
            refreshLabel();
        }
        void refreshLabel() {
            name = FontHelper.getSingleton().fitLabelToBounds(heroClass.getName(), 2.6f, 184f, 56f);
        }
        @Override public void click() {
            heroRoster.select(index);
            updateHeroChoices();
        }
        void updatePosition() {
            boolean focused = heroRoster.focusedView();
            setPosition(focused ? 72 + index * 84 : heroRoster.homeX(index) - 96, focused ? 204 : 388);
        }
        @Override public float getWidth() { return heroRoster.focusedView() ? 72 : 192; }
        @Override public float getHeight() { return heroRoster.focusedView() ? 64 : 392; }
        @Override public boolean isHitProjected(float x, float y) {
            return canClick() && x >= this.x && x <= this.x + getWidth() && y >= this.y && y <= this.y + getHeight();
        }
        @Override public void draw(Batch batch) {
            boolean chosen = heroRoster.requestedHero() == heroClass;
            Color ink = chosen || selectedMenuButton() == this ? DESKTOP_LABEL_ACTIVE : DESKTOP_LABEL;
            if (heroRoster.focusedView()) {
                DesktopMenuStyle.card(batch,
                        x, y, getWidth(), getHeight(), chosen ? DESKTOP_LABEL_ACTIVE
                                : DesktopMenuStyle.EDGE,
                        chosen || selectedMenuButton() == this);
                DesktopMenuStyle.hover(batch, this, DesktopMenuStyle.GOLD);
                icon.setPosition(x + 12, y + 8); icon.draw(batch);
                if (chosen) menuFill(batch, x + 8, y, getWidth() - 16, 3, ink);
                return;
            }
            DesktopMenuStyle.hover(batch, this, DesktopMenuStyle.GOLD, x, 380, getWidth(), 68);
            FontHelper.getSingleton().writeRaw(ink, batch, name.size,
                    x + (getWidth() - name.width) / 2f, 410f, name.text);
            if (chosen) menuFill(batch, x + 24, 380, getWidth() - 48, 4, DESKTOP_LABEL_ACTIVE);
        }
    }

    private void updateHeroChoices() {
        for (ActionButton button : heroControls)
            if (button instanceof HeroChoiceButton) ((HeroChoiceButton) button).updatePosition();
    }

    private void addTitleButton(String label, String image, int index, Runnable action) {
        String path = "images/intro/" + image + ".png";
        if (desktopLayout) {
            buttons.add(new DesktopMenuButton(label, path, index, action));
        } else {
            float startX = (ConstantsHelper.SCREEN_WIDTH - (240f + 280f * 6f)) / 2f;
            buttons.add(new IconButton(label, startX + index * 280f, 300f, 240f, 244f, path, path)
                    .setPanelVisible(false).setAction(action));
        }
    }

    @Override
    protected void drawMenuBackground(Batch batch) {
        if (desktopSession != null && desktopSession.hasTitleScene()) {
            desktopSession.draw(batch, camera);
        } else {
            super.drawMenuBackground(batch);
        }
    }

    @Override
    protected boolean handleBackAction() {
        if (!ownsMenuInput()) return true;
        if (hasPriorityBackAction()) {
            leaveInformationRoom();
            return true;
        }
        if (desktopSession != null && desktopSession.navigation().isSettled()
                && desktopSession.navigation().section() == MenuCameraTransition.Section.HEROES
                && heroRoster.focusedView()) {
            heroRoster.cancelSelection();
            return true;
        }
        if (desktopSession != null && (!desktopSession.navigation().isSettled()
                || desktopSession.navigation().section() != MenuCameraTransition.Section.TITLE)) {
            heroRoster.cancelSelection();
            desktopSession.navigation().backToTitle();
            return true;
        }
        showExitWindow();
        return true;
    }

    @Override protected float getCameraX() {
        return desktopSession == null ? super.getCameraX() : desktopSession.navigation().cameraX() + heroRoster.cameraOffset();
    }

    @Override protected boolean isMenuInputEnabled() {
        if (!ownsMenuInput()) return false;
        return desktopSession == null || (desktopSession.navigation().isSettled()
                && (desktopSession.navigation().section() == MenuCameraTransition.Section.TITLE
                || desktopSession.navigation().section() == MenuCameraTransition.Section.HEROES
                && desktopSession.hasScene(MenuCameraTransition.Section.HEROES)
                || desktopSession.navigation().section() == MenuCameraTransition.Section.INFORMATION
                && informationMenu != null));
    }

    @Override protected boolean hasPriorityBackAction() {
        return desktopSession != null && (desktopSession.navigation().section() == MenuCameraTransition.Section.INFORMATION
                || desktopSession.navigation().destination() == MenuCameraTransition.Section.INFORMATION);
    }

    @Override protected boolean ownsMenuInput() {
        return !menuDisposed && (!desktopLayout || !desktopPaused && SkillfulPixelDungeonPlatformer.getActiveScreen() == this);
    }

    @Override public void act(float delta) {
        if (menuDisposed) return;
        if (desktopLayout) {
            if (desktopPaused || SkillfulPixelDungeonPlatformer.getActiveScreen() != this
                    || Float.isNaN(delta) || Float.isInfinite(delta) || delta <= 0) return;
            delta = Math.min(0.1f, delta);
        }
        super.act(delta);
    }

    @Override
    protected boolean shouldFadeInOnEnter() {
        boolean shouldFade = introFadePending;
        introFadePending = false;
        return shouldFade;
    }

    @Override
    protected void actMenu(float delta) {
        syncFireworks();
        if (desktopSession != null) {
            DesktopMenuStyle.actHover(delta);
            rebindLanguage();
            desktopSession.setTheme(RatKingSupportHelper.getInstance().getSelectedTitleTheme());
            if (desktopSession.navigation().destination() != MenuCameraTransition.Section.HEROES)
                heroRoster.cancelSelection();
            desktopSession.act(delta);
            updateHeroChoices();
            heroInfo.act(delta);
            runSlots.act(delta);
            if (informationMenu != null && desktopSession.navigation().section() == MenuCameraTransition.Section.INFORMATION)
                informationMenu.act(Math.min(0.1f, delta));
            if (desktopSection != desktopSession.navigation().section()) {
                desktopSection = desktopSession.navigation().section();
                buttons.clear();
                buttons.addAll(desktopSection == MenuCameraTransition.Section.HEROES ? heroButtons
                        : desktopSection == MenuCameraTransition.Section.INFORMATION ? informationMenu.controls() : titleButtons);
            }
        }
        fireworkBlastCooldown = Math.max(0f, fireworkBlastCooldown - delta);

        if (titleOverlayIncreasing) {
            titleOverlay = Math.min(1.5f, titleOverlay + delta);
            if (titleOverlay == 1.5f) {
                titleOverlayIncreasing = false;
            }
        } else {
            titleOverlay = Math.max(0f, titleOverlay - delta);
            if (titleOverlay == 0f) {
                titleOverlayIncreasing = true;
            }
        }

        titleExtra.setAlpha(desktopLayout && GameSettingsHelper.getInstance().isReducedVisualEffects()
                ? 0.35f : titleOverlay);
        fireballBackground.rotate(-delta * 500f);
        fireballFront.rotate(-delta * 500f);

        for (FireEffect fireEffect : leftBalls) {
            fireEffect.act(delta);
        }
        for (FireEffect fireEffect : rightBalls) {
            fireEffect.act(delta);
        }

        if (!desktopLayout || desktopSession.navigation().section() == MenuCameraTransition.Section.TITLE
                && desktopSession.navigation().isSettled())
            for (FireworkEffect firework : fireworks) firework.act(delta);
    }

    @Override
    protected void drawMenu(Batch batch) {
        if (desktopSession != null) {
            float cover = desktopSession.navigation().sceneCoverOpacity();
            if (desktopSection == MenuCameraTransition.Section.HEROES)
                cover = Math.max(cover, heroRoster.sceneCoverOpacity());
            if (cover > 0f) {
                float packed = batch.getPackedColor();
                batch.setColor(0f, 0f, 0f, cover);
                batch.draw(TextureHelper.GetSingleton().getSolidPixel(),
                        uiCamera.position.x - uiCamera.viewportWidth / 2f,
                        uiCamera.position.y - uiCamera.viewportHeight / 2f,
                        uiCamera.viewportWidth, uiCamera.viewportHeight);
                batch.setPackedColor(packed);
            }
            float opacity = desktopSession.navigation().opacity();
            menuFade.begin(batch, opacity);
            try {
                if (desktopSession.navigation().section() == MenuCameraTransition.Section.HEROES) drawHeroMenu(batch);
                else if (desktopSession.navigation().section() == MenuCameraTransition.Section.INFORMATION) {
                    informationMenu.draw(batch, selectedMenuButton(), titleOverlay);
                    drawMenuFocus(batch);
                }
                else if (desktopSession.navigation().section() == MenuCameraTransition.Section.TITLE) drawTitleMenu(batch);
            } finally { menuFade.end(batch); }
        } else drawTitleMenu(batch);
    }

    private void drawHeroMenu(Batch batch) {
        float focus = heroRoster.focusAmount();
        Texture logoTexture = TextureHelper.GetSingleton().getTexture(TITLE_PATH);
        float focusScale = Math.min(360f / logoTexture.getWidth(), 92f / logoTexture.getHeight());
        int logoWidth = Math.round(logoTexture.getWidth() * (2.4f + (focusScale - 2.4f) * focus));
        int logoHeight = Math.round(logoTexture.getHeight() * (2.4f + (focusScale - 2.4f) * focus));
        chamberLogo.setWidth(logoWidth); chamberLogo.setHeight(logoHeight);
        chamberLogo.setPosition(1600 + (2436 - logoTexture.getWidth() * focusScale - 1600) * focus, 940 + 160 * focus);
        chamberRunes.setWidth(logoWidth); chamberRunes.setHeight(logoHeight);
        chamberRunes.setPosition(chamberLogo.getX(), chamberLogo.getY());
        chamberLogo.draw(batch);
        chamberRunes.setAlpha(GameSettingsHelper.getInstance().isReducedVisualEffects() ? 0.35f : titleOverlay);
        chamberRunes.draw(batch);
        for (ActionButton control : heroControls) control.draw(batch);
        runSlots.draw(batch, selectedMenuButton());
        heroInfo.draw(batch, desktopSession.navigation().opacity(), selectedMenuButton());
        drawMenuFocus(batch);
    }

    private void drawTitleMenu(Batch batch) {
        if (desktopLayout) {

            float left = uiCamera.position.x - uiCamera.viewportWidth / 2f;
            float bottom = uiCamera.position.y - uiCamera.viewportHeight / 2f;
            float packed = batch.getPackedColor();
            for (float x = left; x < 1080f; x += 24f) {
                float opacity = 0.78f * (1f - MathUtils.clamp((x - 700f) / 380f, 0f, 1f));
                batch.setColor(0.015f, 0.022f, 0.025f, opacity);
                batch.draw(TextureHelper.GetSingleton().getSolidPixel(), x, bottom,
                        Math.min(24f, 1080f - x), uiCamera.viewportHeight);
            }
            batch.setPackedColor(packed);
        }
        for (FireworkEffect firework : fireworks) {
            firework.draw(batch);
        }

        title.draw(batch);
        titleExtra.draw(batch);

        if (!desktopLayout) {
            drawFireball(batch, leftFireballX);
            drawFireball(batch, rightFireballX);
        }

        for (FireEffect fireEffect : leftBalls) {
            fireEffect.draw(batch);
        }
        for (FireEffect fireEffect : rightBalls) {
            fireEffect.draw(batch);
        }

        drawButtons(batch);
        FontHelper.getSingleton().writeWhite(batch, desktopLayout ? 2f : 3f,
                desktopLayout ? 192f : 1050f, desktopLayout ? 54f : 50f, VERSION_LABEL);
        if (desktopLayout) drawControllerIndicator(batch);
    }

    private void drawControllerIndicator(Batch batch) {
        boolean connected = ControllerInput.getInstance().connected();
        float left = uiCamera.position.x - uiCamera.viewportWidth / 2;
        float bottom = uiCamera.position.y - uiCamera.viewportHeight / 2;
        float right = left + uiCamera.viewportWidth - 48;
        controllerIcon.setPosition(right - 64, bottom + 32);
        controllerIcon.setAlpha(connected ? 1f : .25f);
        controllerIcon.draw(batch);
        if (WindowHelper.getInstance().windowOpen() || !desktopSession.navigation().isSettled()) return;
        controllerPointer.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        uiCamera.unproject(controllerPointer);
        if (!controllerIcon.getBoundingRectangle().contains(controllerPointer.x, controllerPointer.y)) return;
        String caption = Messages.get(connected ? "custom.controls.detected" : "custom.controls.not_detected");
        if (controllerCaption == null || !controllerCaption.text.equals(caption))
            controllerCaption = FontHelper.getSingleton().fitLabelToBounds(caption, 2.4f, 520, 64);
        float width = controllerCaption.width + 40, height = controllerCaption.height + 40;
        float x = MathUtils.clamp(controllerPointer.x + 24, left + 16, left + uiCamera.viewportWidth - width - 16);
        float y = MathUtils.clamp(controllerPointer.y + 24, bottom + 16, bottom + uiCamera.viewportHeight - height - 16);
        DesktopMenuStyle.shade(batch, x - 12, y - 12, width + 24, height + 24, 20, .7f);
        DesktopMenuStyle.card(batch, x, y, width, height, DesktopMenuStyle.GOLD, false);
        FontHelper.getSingleton().writeRaw(DesktopMenuStyle.GOLD, batch, controllerCaption.size,
                x + 20, y + height - 20, controllerCaption.text);
    }

    private final class DesktopMenuButton extends ActionButton {
        private final GameSprite icon;
        private FontHelper.FittedTextBlock label;
        private final String text;
        private final Runnable action;
        private final boolean primary;

        DesktopMenuButton(String text, String path, int index, Runnable action) {
            super(168f, index == 0 ? 704f : 690f - index * 84f, 664f,
                    index == 0 ? 100f : 76f, path, path);
            enableUiPressFeedback();
            this.action = action;
            this.text = text;
            primary = index == 0;
            float size = primary ? 72f : 56f;
            Texture texture = TextureHelper.GetSingleton().getTexture(path);
            float scale = size / Math.max(texture.getWidth(), texture.getHeight());
            icon = new GameSprite(path, texture.getWidth() * scale, texture.getHeight() * scale);
            icon.setPosition(x + 28f + (72f - icon.getWidth()) / 2f, y + (getHeight() - icon.getHeight()) / 2f);
            refreshLabel();
        }

        void refreshLabel() {
            label = FontHelper.getSingleton().fitLabelToBounds(
                    Messages.maybeTranslate(text), primary ? 3.4f : 2.8f, 500f, getHeight() - 24f);
        }

        @Override
        public void click() { action.run(); }

        @Override
        public boolean isHitProjected(float atX, float atY) {

            return canClick() && atX >= x && atX <= x + getWidth()
                    && atY >= y && atY <= y + getHeight();
        }

        @Override
        public void draw(Batch batch) {
            boolean selected = selectedMenuButton() == this;
            if (selected || primary) {
                menuFill(batch, x, y, getWidth(), getHeight(), selected ? DESKTOP_ROW_ACTIVE : DESKTOP_ROW);
            }
            DesktopMenuStyle.hover(batch, this, DesktopMenuStyle.GOLD);
            icon.setAlpha(isShowingPressFeedback() ? 0.65f : 1f);
            icon.draw(batch);
            Color ink = selected || primary ? DESKTOP_LABEL_ACTIVE : DESKTOP_LABEL;
            FontHelper.getSingleton().writeRaw(ink, batch, label.size, x + 132f,
                    y + (getHeight() + label.height) / 2f, label.text);
        }
    }

    private void drawFireball(Batch batch, float x) {
        fireballBackground.setPosition(x, fireballY);
        fireballFront.setPosition(x, fireballY);
        fireballBackground.draw(batch);
        fireballFront.draw(batch);
    }

    @Override public void resize(int width, int height) {
        cancelInformationPointer();
        super.resize(width, height);
    }

    @Override public void pause() {
        cancelInformationPointer();
        super.pause();
        desktopPaused = true;
        if (desktopSession != null) desktopSession.setPaused(true);
    }

    @Override public void resume() {
        super.resume();
        desktopPaused = false;
        if (desktopSession != null) desktopSession.setPaused(false);
    }

    @Override public void dispose() {
        if (menuDisposed) return;
        cancelInformationPointer();
        menuDisposed = true;
        if (desktopSession != null) {
            desktopSession.navigation().backToTitle();
            desktopSession.setPaused(true);
            desktopSession.setHeroCast(null);
            heroRoster.setSelectionListener(null);
            heroRoster.cancelSelection();
        }
        if (menuFade != null) menuFade.dispose();
        if (heroInfo != null) heroInfo.dispose();
        desktopSession = null;
        heroRoster = null; heroInfo = null; runSlots = null; informationMenu = null;
        buttons.clear(); titleButtons.clear(); heroControls.clear(); heroButtons.clear();
        fireworks.clear(); leftBalls.clear(); rightBalls.clear();
        super.dispose();
    }

    @Override public void hide() {
        cancelInformationPointer();
        super.hide();
        clearPressedMenuButton();
        if (desktopSession != null) {
            WindowHelper.getInstance().cancelPointerInput();
            desktopSession.navigation().backToTitle();
            heroRoster.cancelSelection();
        }
    }

    private void syncFireworks() {
        boolean shouldShowFireworks = RatKingSupportHelper.getInstance().isFireworksEnabled();
        int count = shouldShowFireworks ? (desktopLayout && GameSettingsHelper.getInstance().isReducedVisualEffects() ? 1 : 6) : 0;
        if (shouldShowFireworks == fireworksActive && fireworks.size() == count) {
            return;
        }

        fireworksActive = shouldShowFireworks;
        fireworks.clear();
        if (!fireworksActive) {
            return;
        }

        for (int i = 0; i < count; i++) {
            fireworks.add(new FireworkEffect());
        }
    }

    private float fireworkRandom(float max) {
        return desktopLayout ? cosmeticRandom.nextFloat() * max : RandomHelper.getInstance().randomFloat(max);
    }

    private float centerX(float width) {
        return (ConstantsHelper.SCREEN_WIDTH - width) / 2f;
    }

    private void showExitWindow() {
        WindowHelper.getInstance().addWindow(new TitleExitWindow().build());
    }

    private void showAboutWindow() {
        if (!showInformation(DesktopInfoMenu.Destination.ABOUT)) openAboutWindow();
    }

    private void openAboutWindow() {
        if (SkillfulPixelDungeonPlatformer.getPlatformProfile().isFreeVersion()) {
            WindowHelper.getInstance().addWindow(new FreeVersionAboutWindow().build());
            return;
        }

        WindowHelper.getInstance().addWindow(new DescriptionWindow("images/intro/about.png", ABOUT_TEXT, 1500f, 360f)
                .setDescriptionSpriteYOffsetByRatio(ABOUT_ICON_RAISE_RATIO)
                .build());
    }

    private class FireEffect {

        private final float centerX;
        private final float centerY;
        private final GameSprite fire;
        private float speedX;
        private float speedY;
        private float lifeSpan = 70f;
        private float fadeIn;

        private FireEffect(float centerX, float centerY) {
            this.centerX = centerX;
            this.centerY = centerY;
            fire = new GameSprite("images/intro/fireball-effect-1.png", 38, 54, 0.7f);
            reset();
        }

        private void act(float delta) {
            fire.setPosition(fire.getX() + delta * speedX, fire.getY() + delta * speedY);
            fire.setWidth((int) (38 * (lifeSpan / 100f)));
            fire.setHeight((int) (54 * (lifeSpan / 100f)));
            fire.setAlpha(Math.min(fadeIn / 10f, lifeSpan / 100f));
            lifeSpan -= 40f * delta;
            fadeIn += delta;
            if (lifeSpan < 0f) {
                reset();
            }
        }

        private void draw(Batch batch) {
            fire.draw(batch);
        }

        private void reset() {
            speedX = 100 - RandomHelper.getInstance().randomInt(200);
            speedY = 150 + RandomHelper.getInstance().randomInt(150);
            fire.setWidth(38);
            fire.setHeight(54);
            fire.setPosition(centerX - fire.getWidth() / 2f, centerY - fire.getHeight() / 2f);
            lifeSpan = 40f + RandomHelper.getInstance().randomInt(30);
            fadeIn = 0f;
        }
    }

    private class FireworkEffect {
        private static final int SPARK_COUNT = 14;
        private static final float MIN_BURST_Y = 820f;
        private static final float BURST_Y_RANGE = 260f;
        private static final float FIREWORK_MIN_X = 300f;
        private static final float FIREWORK_X_RANGE = 1450f;
        private static final float FIREWORK_MIN_START_Y = 240f;
        private static final float FIREWORK_START_Y_RANGE = 120f;
        private static final float FIREWORK_MIN_SPEED_Y = 320f;
        private static final float FIREWORK_SPEED_Y_RANGE = 130f;
        private static final float FIREWORK_DRIFT_RANGE = 50f;
        private static final float SPARK_SPEED_MIN = 110f;
        private static final float SPARK_SPEED_RANGE = 170f;
        private static final float SPARK_GRAVITY = 180f;
        private static final float SPARK_LIFESPAN_MIN = 0.8f;
        private static final float SPARK_LIFESPAN_RANGE = 0.45f;
        private final GameSprite rocket = new GameSprite("images/misc/yellow-dot.png", 10, 10, 0.9f);
        private final ArrayList<FireworkSpark> sparks = new ArrayList<>();
        private float x;
        private float y;
        private float speedX;
        private float speedY;
        private float burstY;
        private boolean exploded;

        private FireworkEffect() {
            reset();
        }

        private void act(float delta) {
            if (!exploded) {
                x += speedX * delta;
                y += speedY * delta;
                rocket.setPosition(x, y);
                if (y >= burstY) {
                    explode();
                }
                return;
            }

            boolean hasActiveSpark = false;
            for (FireworkSpark spark : sparks) {
                spark.act(delta);
                hasActiveSpark |= spark.isActive();
            }

            if (!hasActiveSpark) {
                reset();
            }
        }

        private void draw(Batch batch) {
            if (!exploded) {
                rocket.draw(batch);
                return;
            }

            for (FireworkSpark spark : sparks) {
                spark.draw(batch);
            }
        }

        private void reset() {
            exploded = false;
            sparks.clear();
            x = FIREWORK_MIN_X + fireworkRandom(FIREWORK_X_RANGE);
            y = FIREWORK_MIN_START_Y + fireworkRandom(FIREWORK_START_Y_RANGE);
            speedX = fireworkRandom(FIREWORK_DRIFT_RANGE * 2f) - FIREWORK_DRIFT_RANGE;
            speedY = FIREWORK_MIN_SPEED_Y + fireworkRandom(FIREWORK_SPEED_Y_RANGE);
            burstY = MIN_BURST_Y + fireworkRandom(BURST_Y_RANGE);
            rocket.setPosition(x, y);
            rocket.setAlpha(0.95f);
        }

        private void explode() {
            exploded = true;
            if (fireworkBlastCooldown <= 0f) {
                if (desktopLayout) {

                    RandomHelper.getInstance().withPreviewRandom(() -> {
                        SoundHelper.GetSingleton().play(Sounds.BLAST, 0f, 0.6f);
                        return null;
                    });
                } else SoundHelper.GetSingleton().play(Sounds.BLAST, 0f, 0.6f);
                fireworkBlastCooldown = FIREWORK_BLAST_COOLDOWN_SECONDS;
            }
            float angleStep = 360f / SPARK_COUNT;
            for (int index = 0; index < SPARK_COUNT; index++) {
                float angle = (angleStep * index + fireworkRandom(18f)) * 0.017453292f;
                float speed = SPARK_SPEED_MIN + fireworkRandom(SPARK_SPEED_RANGE);
                String spritePath = FIREWORK_SPRITES[(desktopLayout ? cosmeticRandom.nextInt(FIREWORK_SPRITES.length) : RandomHelper.getInstance().randomInt(FIREWORK_SPRITES.length))];
                sparks.add(new FireworkSpark(
                        spritePath,
                        x,
                        y,
                        (float) Math.cos(angle) * speed,
                        (float) Math.sin(angle) * speed,
                        SPARK_LIFESPAN_MIN + fireworkRandom(SPARK_LIFESPAN_RANGE)));
            }
        }
    }

    private class FireworkSpark {
        private final GameSprite sprite;
        private float x;
        private float y;
        private float speedX;
        private float speedY;
        private float lifeSpan;
        private final float totalLifeSpan;

        private FireworkSpark(String spritePath, float x, float y, float speedX, float speedY, float lifeSpan) {
            sprite = new GameSprite(spritePath, 12, 12, 0.9f);
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.lifeSpan = lifeSpan;
            this.totalLifeSpan = lifeSpan;
            sprite.setPosition(x, y);
        }

        private void act(float delta) {
            if (lifeSpan <= 0f) {
                return;
            }

            lifeSpan = Math.max(0f, lifeSpan - delta);
            x += speedX * delta;
            y += speedY * delta;
            speedY -= FireworkEffect.SPARK_GRAVITY * delta;
            sprite.setPosition(x, y);
            sprite.setAlpha(totalLifeSpan <= 0f ? 0f : lifeSpan / totalLifeSpan);
        }

        private void draw(Batch batch) {
            if (lifeSpan > 0f) {
                sprite.draw(batch);
            }
        }

        private boolean isActive() {
            return lifeSpan > 0f;
        }
    }
}
