package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.PauseMenuRowButton;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.StoreService;

import java.util.ArrayList;

public class RatKingDonationWindow extends Window {
    private static final float WINDOW_WIDTH = 980f;
    private static final float WINDOW_HEIGHT = 420f;

    private final ArrayList<ActionButton> buttons = new ArrayList<>();
    private ActionButton pressedButton;
    private String wrappedMessage;

    public RatKingDonationWindow() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    @Override
    public Window build() {
        super.build();
        buttons.clear();
        pressedButton = null;
        wrappedMessage = UtilsHelper.multiLine(Messages.get("custom.generated.rat_king_cosmetics_notice_94a8d4e2bf"), 2, width - 180f);

        buttons.add(new PauseMenuRowButton(x + 110f, y + 70f, width - 220f, 92f) {
            @Override
            public void clicked() {
                StoreService storeService = SkillfulPixelDungeonPlatformer.getStoreService();
                if (storeService != null && storeService.supportsRatKingDonation()) {
                    storeService.purchaseRatKingDonation();
                }
                else {
                    WindowHelper.getInstance().addWindow(760f, 220f,
                            Messages.get("custom.ui.rat_king.billing_unavailable"));
                }
                WindowHelper.getInstance().closeWindow(RatKingDonationWindow.this);
            }
        }.setCenteredText(Messages.get("custom.generated.i_am_ok_with_that_17341ab7b9")));

        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        FontHelper.getSingleton().writeWhite(batch, 3.4f, x + 90f, y + height - 95f, Messages.get("custom.generated.donation_required_e289a7f07f"));
        FontHelper.getSingleton().writeWhiteRaw(batch, 2f, x + 90f, y + height - 165f, wrappedMessage);

        for (ActionButton button : buttons) {
            button.draw(batch);
        }
    }

    @Override
    public boolean pointerDown(float x, float y, int button) {
        clearPressedButton();
        for (ActionButton actionButton : buttons) {
            if (actionButton.isHitProjected(x, y)) {
                pressedButton = actionButton;
                actionButton.pressDown();
                return true;
            }
        }

        return true;
    }

    @Override
    public boolean tap(float x, float y) {
        if (pressedButton != null) {
            ActionButton tappedButton = pressedButton;
            pressedButton = null;
            tappedButton.releasePress();
            if (tappedButton.isHitProjected(x, y)) {
                tappedButton.click();
                return true;
            }

            tappedButton.cancelPress();
            return true;
        }

        if (x < this.x || x > this.x + this.width || y < this.y || y > this.y + this.height) {
            WindowHelper.getInstance().closeWindow(this);
        }

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        WindowHelper.getInstance().closeWindow(this);
        return true;
    }

    private void clearPressedButton() {
        if (pressedButton != null) {
            pressedButton.cancelPress();
            pressedButton = null;
        }
    }
}