package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SkillsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.ConsumableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.RedButton;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ActiveSkill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.BuffSkill;

import java.util.ArrayList;

public class SkillWindow extends DescriptionWindow {

    private static final float ACTION_BUTTON_WIDTH = 400f;
    private static final float ACTION_BUTTON_HEIGHT = 100f;
    private static final float ACTION_BUTTON_RIGHT_MARGIN = 100f;
    private static final float ACTION_BUTTON_BOTTOM_MARGIN = 100f;
    private static final float ACTION_BUTTON_GAP = 15f;

    ArrayList<ActionButton> actionButtons;
    Skill skill;
    private boolean showUnlockButton;
    private boolean showSetQuickSkillButton;

    public SkillWindow(Skill skill) {
        super(skill.getGameSprite().spriteString, buildDescription(skill), 1500, 400);
        this.skill = skill;
        actionButtons = new ArrayList<>();
        height = Math.max(400, 200 + (UtilsHelper.multiLine(getHeroClass().getSkillBigDescription(skill.getId()), 3, getDescriptionWrapWidth(width)).split("\n").length + 1) * 75);
        y = ConstantsHelper.SCREEN_HEIGHT / 2 - height / 2;
        x = ConstantsHelper.SCREEN_WIDTH / 2 - width / 2;
    }

    private static String buildDescription(Skill skill) {
        HeroClass heroClass = getHeroClass();
        String description = heroClass.getSkillBigDescription(skill.getId());

        if (UnitHelper.getInstance().getHero().hasSkill(skill.getId())) {
            return description + "\n\n" + Messages.maybeTranslate("You have unlocked this skill!");
        }

        if (UnitHelper.getInstance().getHero().skillLockedOut(skill.getId())) {
            return description + "\n\n" + Messages.maybeTranslate("This skill is locked out.");
        }

        if (SkillsHelper.getInstance().isSubclassSkill(skill.getId())) {
            return description + "\n\n" + Messages.maybeTranslate("This subclass is unlocked by the Tome of Mastery dropped by Tengu.");
        }

        return description + "\n\n" + Messages.maybeTranslate(
                "It takes %d skill points to learn this skill.",
                SkillsHelper.getInstance().skillCost(skill));
    }

    private static HeroClass getHeroClass() {
        return UnitHelper.getInstance().getHero().getHeroClass();
    }

    @Override
    public Window build(){
        setReservedBottomHeight(getReservedActionButtonHeight());
        super.build();
        actionButtons.clear();

        float buttonX = x + width - ACTION_BUTTON_WIDTH - ACTION_BUTTON_RIGHT_MARGIN;
        float buttonY = y + ACTION_BUTTON_BOTTOM_MARGIN;
        if (showUnlockButton) {
            actionButtons.add(createActionButton(buttonX, buttonY, Messages.get("custom.ui.skill.learn"), new Runnable() {
                @Override
                public void run() {
                    SkillsHelper.getInstance().learnSkill(skill);
                    setQuickSkillIfApplicable();
                    hide();
                    WindowHelper.getInstance().refresh();
                }
            }));
            buttonY += ACTION_BUTTON_HEIGHT + ACTION_BUTTON_GAP;
        }

        if (showSetQuickSkillButton) {
            if (mobileQuickSkillButtonsEnabled()) {
                actionButtons.add(createActionButton(buttonX, buttonY, "QS2", new Runnable() {
                    @Override
                    public void run() {
                        setQuickSkill(1);
                        hide();
                        WindowHelper.getInstance().refresh();
                    }
                }));
                buttonY += ACTION_BUTTON_HEIGHT + ACTION_BUTTON_GAP;
                actionButtons.add(createActionButton(buttonX, buttonY, "QS1", new Runnable() {
                    @Override
                    public void run() {
                        setQuickSkill(0);
                        hide();
                        WindowHelper.getInstance().refresh();
                    }
                }));
            } else {
                actionButtons.add(createActionButton(buttonX, buttonY, Messages.get("custom.ui.skill.set_quick_skill"), new Runnable() {
                    @Override
                    public void run() {
                        setQuickSkillIfApplicable();
                        hide();
                        WindowHelper.getInstance().refresh();
                    }
                }));
            }
        }
        return this;
    }


    public Window addUnlock(){
        showUnlockButton = true;
        return this;
    }

    public Window addSetQuickSkill(){
        showSetQuickSkillButton = true;
        return this;
    }

    @Override
    public void draw(Batch batch){
        super.draw(batch);

        for (ActionButton actionButton : actionButtons){
            actionButton.draw(batch);
        }
    }


    @Override
    public boolean click(float x, float y){
        for(ActionButton actionButton : actionButtons){
            if(actionButton.isHitProjected(x, y)){
                actionButton.click();
                return true;
            }
        }

        if(x < this.x || x > this.x + this.width){
            hide();
        }

        if(y < this.y || y > this.y + this.height){
            hide();
        }

        return true;
    }

    private void setQuickSkillIfApplicable() {
        if(!(skill instanceof ActiveSkill)){
            return;
        }

        ActiveSkill activeSkill = (ActiveSkill) skill;
        for (int slotIndex = 0; slotIndex < UnitHelper.getInstance().getHero().getQuickSkillSlotCount(); slotIndex++) {
            if (sameSkill(UnitHelper.getInstance().getHero().getQuickSkill(slotIndex), activeSkill)) {
                return;
            }
        }

        int[] preferredSlots = preferredQuickSkillSlots(skill instanceof BuffSkill);

        for (int slotIndex : preferredSlots) {
            if (UnitHelper.getInstance().getHero().getQuickSkill(slotIndex) == null) {
                UnitHelper.getInstance().getHero().setQuickSkill(slotIndex, activeSkill);
                return;
            }
        }

        UnitHelper.getInstance().getHero().setQuickSkill(preferredSlots[0], activeSkill);
    }

    private boolean sameSkill(ActiveSkill first, ActiveSkill second) {
        return first != null && second != null && first.getId() == second.getId();
    }

    private void setQuickSkill(int slotIndex) {
        if (!(skill instanceof ActiveSkill)) {
            return;
        }

        UnitHelper.getInstance().getHero().setQuickSkill(slotIndex, (ActiveSkill) skill);
    }

    private int[] preferredQuickSkillSlots(boolean buffSkill) {
        if (mobileQuickSkillButtonsEnabled()) {
            return buffSkill ? new int[]{1, 0} : new int[]{0, 1};
        }

        return buffSkill
                ? new int[]{1, 2, 3, 4, 5, 6, 0}
                : new int[]{0, 2, 3, 4, 5, 6, 1};
    }

    private boolean mobileQuickSkillButtonsEnabled() {
        return SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()
                && UnitHelper.getInstance().getHero().getQuickSkillSlotCount() == 2;
    }

    private float getReservedActionButtonHeight() {
        int actionCount = 0;
        if (showUnlockButton) {
            actionCount++;
        }
        if (showSetQuickSkillButton) {
            actionCount += mobileQuickSkillButtonsEnabled() ? 2 : 1;
        }

        if (actionCount == 0) {
            return 0f;
        }

        return ACTION_BUTTON_BOTTOM_MARGIN
                + actionCount * ACTION_BUTTON_HEIGHT
                + Math.max(0, actionCount - 1) * ACTION_BUTTON_GAP;
    }

    private RedButton createActionButton(float buttonX, float buttonY, String label, final Runnable action) {
        return new RedButton(buttonX, buttonY, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT) {
            @Override
            public void click() {
                if (action != null) {
                    action.run();
                }
            }
        }.setText(label);
    }
}


