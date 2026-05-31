package com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
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

    public DesktopInputProcessor(UnitHelper unitHelper) {
        this.unitHelper = unitHelper;
    }

    public void refreshMovementState() {
        Hero hero = unitHelper.getHero();
        if (hero == null) {
            return;
        }

        syncHorizontalMovement(hero);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (WindowHelper.getInstance().windowOpen() && WindowHelper.getInstance().handleKeyDown(keycode)) {
            return true;
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

        if (WindowHelper.getInstance().windowOpen()) {
            if (settings.getInteractBinding().matchesKey(keycode)) {
                WindowHelper.getInstance().closeWindow();
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
            hero.jump();
            return true;
        }

        if (settings.getInteractBinding().matchesKey(keycode)) {
            return UIHelper.getInstance().performContextAction();
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Hero hero = unitHelper.getHero();
        if (hero == null) {
            return false;
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

        return WindowHelper.getInstance().windowOpen();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (WindowHelper.getInstance().windowOpen() || UIHelper.getInstance().isButtonHit(screenX, screenY)) {
            return false;
        }

        Hero hero = unitHelper.getHero();
        if (hero != null && hero.isDead()) {
            clearMovementState(hero);
            return false;
        }

        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        if (settings.getAttackBinding().matchesMouse(button)) {
            return UIHelper.getInstance().tap(screenX, screenY, button);
        }

        if (settings.getRangedBinding().matchesMouse(button)) {
            return UIHelper.getInstance().performSecondaryAction();
        }

        if (settings.getJumpBinding().matchesMouse(button)) {
            if (hero != null) {
                hero.jump();
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
        Hero hero = unitHelper.getHero();
        if (hero == null) {
            return false;
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

    private void syncHorizontalMovement(Hero hero) {
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
        leftPressed = isBindingPressed(settings.getMoveLeftBinding());
        rightPressed = isBindingPressed(settings.getMoveRightBinding());
    }

    private boolean isBindingPressed(GameSettingsHelper.InputBinding binding) {
        if (binding.getType() == GameSettingsHelper.INPUT_TYPE_KEY) {
            return Gdx.input.isKeyPressed(binding.getCode());
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
