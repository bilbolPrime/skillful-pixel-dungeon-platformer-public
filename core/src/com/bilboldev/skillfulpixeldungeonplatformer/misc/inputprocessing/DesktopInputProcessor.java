package com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.IntSet;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;

public class DesktopInputProcessor extends InputAdapter {
    private final UnitHelper unitHelper;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean controllerLeft, controllerRight, controllerJump;
    private final IntSet modalKeysHeld = new IntSet();
    private final IntSet jumpKeysHeld = new IntSet();
    private final IntSet jumpMouseHeld = new IntSet();
    private final IntSet worldKeysHeld = new IntSet();
    private final IntSet worldMouseHeld = new IntSet();
    private int jumpBindingType, jumpBindingCode;

    public DesktopInputProcessor(UnitHelper unitHelper) {
        this.unitHelper = unitHelper;
        jumpBindingType = GameSettingsHelper.getInstance().getJumpBinding().getType();
        jumpBindingCode = GameSettingsHelper.getInstance().getJumpBinding().getCode();
        suppressHeldActions();
    }

    public void clearModalKeyPresses() {
        modalKeysHeld.clear();
        suppressHeldActions();
        if (unitHelper.getHero() != null) clearMovementState(unitHelper.getHero());
    }

    private void suppressHeldActions() {
        GameSettingsHelper s = GameSettingsHelper.getInstance();
        GameSettingsHelper.InputBinding[] actions = {s.getJumpBinding(), s.getAttackBinding(), s.getRangedBinding(),
                s.getQuickSkillBinding(), s.getQuickSkill2Binding(), s.getQuickSkill3Binding(), s.getQuickSkill4Binding(),
                s.getQuickSkill5Binding(), s.getQuickSkill6Binding(), s.getQuickSkill7Binding(), s.getEnterDoorBinding(),
                s.getInteractBinding(), s.getInventoryBinding(), s.getHealthPotionBinding(), s.getManaPotionBinding(), s.getEatFoodBinding()};
        for (GameSettingsHelper.InputBinding action : actions) {
            if (action.getType() == GameSettingsHelper.INPUT_TYPE_KEY && Gdx.input.isKeyPressed(action.getCode())) worldKeysHeld.add(action.getCode());
            else if (action.getType() == GameSettingsHelper.INPUT_TYPE_MOUSE && Gdx.input.isButtonPressed(action.getCode())) worldMouseHeld.add(action.getCode());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) worldKeysHeld.add(Input.Keys.UP);
    }

    public void refreshMovementState() {
        Hero hero = unitHelper.getHero();
        if (hero == null) {
            return;
        }

        discardReleasedSources(worldKeysHeld, false);
        discardReleasedSources(worldMouseHeld, true);
        discardReleasedSources(jumpKeysHeld, false);
        discardReleasedSources(jumpMouseHeld, true);
        if (jumpKeysHeld.size == 0 && jumpMouseHeld.size == 0 && !controllerJump) hero.releaseJump();
        refreshJumpBinding(hero, -1, -1);
        if (WindowHelper.getInstance().windowOpen()) clearMovementState(hero);
        else syncHorizontalMovement(hero);
    }

    private void refreshJumpBinding(Hero hero, int freshKey, int freshMouse) {
        GameSettingsHelper.InputBinding jump = GameSettingsHelper.getInstance().getJumpBinding();
        if (jump.getType() != jumpBindingType || jump.getCode() != jumpBindingCode) {
            hero.clearControlIntent();
            jumpBindingType = jump.getType(); jumpBindingCode = jump.getCode();

            if (jumpBindingType == GameSettingsHelper.INPUT_TYPE_KEY && jumpBindingCode != freshKey && Gdx.input.isKeyPressed(jumpBindingCode)) {
                jumpKeysHeld.add(jumpBindingCode); worldKeysHeld.add(jumpBindingCode);
            } else if (jumpBindingType == GameSettingsHelper.INPUT_TYPE_MOUSE && jumpBindingCode != freshMouse && Gdx.input.isButtonPressed(jumpBindingCode)) {
                jumpMouseHeld.add(jumpBindingCode); worldMouseHeld.add(jumpBindingCode);
            }
        }
    }

    private void discardReleasedSources(IntSet sources, boolean mouse) {
        IntSet.IntSetIterator iterator = sources.iterator();
        while (iterator.hasNext) {
            int code = iterator.next();
            if (!(mouse ? Gdx.input.isButtonPressed(code) : Gdx.input.isKeyPressed(code))) iterator.remove();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        ControllerInput.getInstance().keyboardUsed();
        if (modalKeysHeld.contains(keycode) || worldKeysHeld.contains(keycode)) return true;
        if (WindowHelper.getInstance().windowOpen()) {
            modalKeysHeld.add(keycode);
            if (WindowHelper.getInstance().handleKeyDown(keycode)) return true;
        }

        Hero hero = unitHelper.getHero();
        if (hero == null) {
            return false;
        }

        if (hero.isDead()) {
            clearMovementState(hero);
            return false;
        }

        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        refreshJumpBinding(hero, keycode, -1);

        if (WindowHelper.getInstance().windowOpen()) {
            if (settings.getInteractBinding().matchesKey(keycode)) {
                WindowHelper.getInstance().topWindow().hide();
                return true;
            }

            return true;
        }

        if (settings.getMoveLeftBinding().matchesKey(keycode)) {
            leftPressed = true;
            syncHorizontalMovement(hero);
            return true;
        }

        if (settings.getMoveRightBinding().matchesKey(keycode)) {
            rightPressed = true;
            syncHorizontalMovement(hero);
            return true;
        }

        if (!worldKeysHeld.add(keycode)) return true;

        if (settings.getEnterDoorBinding().matchesKey(keycode)) {
            MapHelper.getInstance().enterDoor();
            return true;
        }

        if (settings.getInventoryBinding().matchesKey(keycode)) {
            return UIHelper.getInstance().performInventoryAction();
        }

        if (settings.getHealthPotionBinding().matchesKey(keycode)) {
            return UIHelper.getInstance().performHealthPotionAction();
        }

        if (settings.getManaPotionBinding().matchesKey(keycode)) {
            return UIHelper.getInstance().performManaPotionAction();
        }

        if (settings.getEatFoodBinding().matchesKey(keycode)) {
            return UIHelper.getInstance().performEatFoodAction();
        }

        if (settings.getAttackBinding().matchesKey(keycode)) {
            return UIHelper.getInstance().performPrimaryAction();
        }

        if (settings.getRangedBinding().matchesKey(keycode)) {
            return UIHelper.getInstance().performSecondaryAction();
        }

        if (settings.getQuickSkillBinding().matchesKey(keycode)) {
            return UIHelper.getInstance().performQuickSkillAction();
        }

        if (settings.getQuickSkill2Binding().matchesKey(keycode)) {
            return UIHelper.getInstance().performQuickSkill2Action();
        }

        if (settings.getQuickSkill3Binding().matchesKey(keycode)) {
            return UIHelper.getInstance().performQuickSkill3Action();
        }

        if (settings.getQuickSkill4Binding().matchesKey(keycode)) {
            return UIHelper.getInstance().performQuickSkill4Action();
        }

        if (settings.getQuickSkill5Binding().matchesKey(keycode)) {
            return UIHelper.getInstance().performQuickSkill5Action();
        }

        if (settings.getQuickSkill6Binding().matchesKey(keycode)) {
            return UIHelper.getInstance().performQuickSkill6Action();
        }

        if (settings.getQuickSkill7Binding().matchesKey(keycode)) {
            return UIHelper.getInstance().performQuickSkill7Action();
        }

        if (settings.getJumpBinding().matchesKey(keycode) || keycode == Input.Keys.UP) {
            if (jumpKeysHeld.add(keycode) && !controllerJump) hero.pressJump();
            return true;
        }

        if (settings.getInteractBinding().matchesKey(keycode)) {
            return UIHelper.getInstance().performContextAction();
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        worldKeysHeld.remove(keycode);
        boolean consumedByWindow = modalKeysHeld.remove(keycode);
        Hero hero = unitHelper.getHero();
        if (hero == null) {
            return false;
        }

        if (jumpKeysHeld.remove(keycode)) {
            if (jumpKeysHeld.size == 0 && jumpMouseHeld.size == 0 && !controllerJump) hero.releaseJump();
            return true;
        }

        if (hero.isDead()) {
            clearMovementState(hero);
            return false;
        }

        GameSettingsHelper settings = GameSettingsHelper.getInstance();

        if (settings.getMoveLeftBinding().matchesKey(keycode)) {
            leftPressed = false;
            syncHorizontalMovement(hero);
            return true;
        }

        if (settings.getMoveRightBinding().matchesKey(keycode)) {
            rightPressed = false;
            syncHorizontalMovement(hero);
            return true;
        }

        return consumedByWindow || WindowHelper.getInstance().windowOpen();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        ControllerInput.getInstance().keyboardUsed();
        if (WindowHelper.getInstance().windowOpen() || UIHelper.getInstance().isButtonHit(screenX, screenY)) {
            return false;
        }

        Hero hero = unitHelper.getHero();
        if (hero != null && hero.isDead()) {
            clearMovementState(hero);
            return false;
        }

        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        if (hero != null) refreshJumpBinding(hero, -1, button);
        if (!worldMouseHeld.add(button)) return true;
        if (settings.getAttackBinding().matchesMouse(button)) {
            return UIHelper.getInstance().tap(screenX, screenY, button);
        }

        if (settings.getRangedBinding().matchesMouse(button)) {
            return UIHelper.getInstance().performSecondaryAction();
        }

        if (settings.getJumpBinding().matchesMouse(button)) {
            if (hero != null) {
                if (jumpMouseHeld.add(button) && !controllerJump) hero.pressJump();
                return true;
            }
        }

        if (settings.getEnterDoorBinding().matchesMouse(button)) {
            MapHelper.getInstance().enterDoor();
            return true;
        }

        if (settings.getInventoryBinding().matchesMouse(button)) {
            return UIHelper.getInstance().performInventoryAction();
        }

        if (settings.getHealthPotionBinding().matchesMouse(button)) {
            return UIHelper.getInstance().performHealthPotionAction();
        }

        if (settings.getManaPotionBinding().matchesMouse(button)) {
            return UIHelper.getInstance().performManaPotionAction();
        }

        if (settings.getEatFoodBinding().matchesMouse(button)) {
            return UIHelper.getInstance().performEatFoodAction();
        }

        if (settings.getInteractBinding().matchesMouse(button)) {
            return UIHelper.getInstance().performContextAction();
        }

        if (settings.getQuickSkillBinding().matchesMouse(button)) {
            return UIHelper.getInstance().performQuickSkillAction();
        }

        if (settings.getQuickSkill2Binding().matchesMouse(button)) {
            return UIHelper.getInstance().performQuickSkill2Action();
        }

        if (settings.getQuickSkill3Binding().matchesMouse(button)) {
            return UIHelper.getInstance().performQuickSkill3Action();
        }

        if (settings.getQuickSkill4Binding().matchesMouse(button)) {
            return UIHelper.getInstance().performQuickSkill4Action();
        }

        if (settings.getQuickSkill5Binding().matchesMouse(button)) {
            return UIHelper.getInstance().performQuickSkill5Action();
        }

        if (settings.getQuickSkill6Binding().matchesMouse(button)) {
            return UIHelper.getInstance().performQuickSkill6Action();
        }

        if (settings.getQuickSkill7Binding().matchesMouse(button)) {
            return UIHelper.getInstance().performQuickSkill7Action();
        }

        if (settings.getMoveLeftBinding().matchesMouse(button)) {
            leftPressed = true;
            syncHorizontalMovement(unitHelper.getHero());
            return true;
        }

        if (settings.getMoveRightBinding().matchesMouse(button)) {
            rightPressed = true;
            syncHorizontalMovement(unitHelper.getHero());
            return true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        worldMouseHeld.remove(button);
        Hero hero = unitHelper.getHero();
        if (hero == null) {
            return false;
        }

        if (jumpMouseHeld.remove(button)) {
            if (jumpKeysHeld.size == 0 && jumpMouseHeld.size == 0 && !controllerJump) hero.releaseJump();
            return true;
        }

        if (hero.isDead()) {
            clearMovementState(hero);
            return false;
        }

        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        if (settings.getMoveLeftBinding().matchesMouse(button)) {
            leftPressed = false;
            syncHorizontalMovement(hero);
            return true;
        }

        if (settings.getMoveRightBinding().matchesMouse(button)) {
            rightPressed = false;
            syncHorizontalMovement(hero);
            return true;
        }

        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (WindowHelper.getInstance().windowOpen()) {
            return WindowHelper.getInstance().handleScroll(amountY);
        }

        return false;
    }

    public void setControllerMovement(boolean left, boolean right) {
        controllerLeft = left; controllerRight = right;
    }

    public void setControllerJump(boolean pressed) {
        if (controllerJump == pressed) return;
        boolean wasHeld = controllerJump || jumpKeysHeld.size > 0 || jumpMouseHeld.size > 0;
        controllerJump = pressed;
        Hero hero = unitHelper.getHero();
        if (hero == null) return;
        if (pressed && !wasHeld && !hero.isDead()) hero.pressJump();
        else if (!pressed && jumpKeysHeld.size == 0 && jumpMouseHeld.size == 0) hero.releaseJump();
    }

    private void syncHorizontalMovement(Hero hero) {
        if (hero == null) return;
        refreshHorizontalBindings();

        if (hero.isDead()) {
            clearMovementState(hero);
            return;
        }

        if (leftPressed == rightPressed) {
            hero.movingLeft = false;
            hero.movingRight = false;
            return;
        }

        if (leftPressed) {
            hero.movingLeft = true;
            hero.movingRight = false;
            if (!hero.isAttacking()) {
                hero.facingRight = false;
                hero.changeState(UnitState.RUNNING);
            }
            return;
        }

        hero.movingLeft = false;
        hero.movingRight = true;
        if (!hero.isAttacking()) {
            hero.facingRight = true;
            hero.changeState(UnitState.RUNNING);
        }
    }

    private void refreshHorizontalBindings() {
        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        leftPressed = controllerLeft || isBindingPressed(settings.getMoveLeftBinding());
        rightPressed = controllerRight || isBindingPressed(settings.getMoveRightBinding());
    }

    private boolean isBindingPressed(GameSettingsHelper.InputBinding binding) {
        if (binding.getType() == GameSettingsHelper.INPUT_TYPE_KEY) {
            return !modalKeysHeld.contains(binding.getCode()) && Gdx.input.isKeyPressed(binding.getCode());
        }

        if (binding.getType() == GameSettingsHelper.INPUT_TYPE_MOUSE) {
            return Gdx.input.isButtonPressed(binding.getCode());
        }

        return false;
    }

    private void clearMovementState(Hero hero) {
        leftPressed = false;
        rightPressed = false;
        hero.movingLeft = false;
        hero.movingRight = false;
    }
}
