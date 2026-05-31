package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.TimeUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.RatKing;

import java.util.ArrayList;

public class RatKingHelper {
    private static final long COMMENT_COOLDOWN_MILLIS = 30000L;
    private static final long BORED_THRESHOLD_MILLIS = 60000L;
    private static final Color COMMENT_COLOR = new Color(1f, 0.9f, 0.45f, 1f);

    private static final String[] LEVEL_UP_MOCK_KEYS = new String[]{
        "custom.ui.rat_king.mock.level_up.1"
    };

    private static final String[] DEATH_MOCK_KEYS = new String[]{
        "custom.ui.rat_king.mock.death.1"
    };

    private static final String[] EAT_MOCK_KEYS = new String[]{
        "custom.ui.rat_king.mock.eat.1",
        "custom.ui.rat_king.mock.eat.2",
        "custom.ui.rat_king.mock.eat.3"
    };

    private static final String[] PICKUP_MOCK_KEYS = new String[]{
        "custom.ui.rat_king.mock.pickup.1",
        "custom.ui.rat_king.mock.pickup.2",
        "custom.ui.rat_king.mock.pickup.3"
    };

    private static final String[] BORED_MOCK_KEYS = new String[]{
        "custom.ui.rat_king.mock.bored.1",
        "custom.ui.rat_king.mock.bored.2",
        "custom.ui.rat_king.mock.bored.3"
    };

    private static final String[] TRAP_MOCK_KEYS = new String[]{
        "custom.ui.rat_king.mock.trap.1",
        "custom.ui.rat_king.mock.trap.2",
        "custom.ui.rat_king.mock.trap.3"
    };

    private static final String[] BLOCKED_MOCK_KEYS = new String[]{
        "custom.ui.rat_king.mock.blocked.1"
    };

    private static final RatKingHelper INSTANCE = new RatKingHelper();

    private long nextCommentAllowedAtMillis;
    private long lastInterestingActionAtMillis;

    public static RatKingHelper getInstance() {
        return INSTANCE;
    }

    public void resetRunState() {
        nextCommentAllowedAtMillis = 0L;
        lastInterestingActionAtMillis = TimeUtils.millis();
    }

    public void ensureCompanionPresent() {
        RatKingSupportHelper supportHelper = RatKingSupportHelper.getInstance();
        if (!supportHelper.isRatKingAvailable() || !supportHelper.isCompanionEnabled()) {
            removeCompanion();
            return;
        }

        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null || hero.getRoom() == null) {
            return;
        }

        if (getCompanion() != null) {
            return;
        }

        RatKing ratKing = new RatKing();
        ratKing.setRoom(hero.getRoom());
        ratKing.facingRight = hero.facingRight;
        ratKing.floorY = hero.floorY;
        ratKing.y = hero.y;
        ratKing.x = hero.x + (hero.facingRight ? ConstantsHelper.UNIT_DIMENSIONS : -ConstantsHelper.UNIT_DIMENSIONS);
        UnitHelper.getInstance().addUnit(ratKing);
    }

    public void removeCompanion() {
        ArrayList<Unit> units = new ArrayList<>(UnitHelper.getInstance().getUnits());
        for (Unit unit : units) {
            if (unit instanceof RatKing) {
                UnitHelper.getInstance().removeUnit(unit);
            }
        }
    }

    public void onHeroLevelUp(Hero hero) {
        ensureCompanionPresent();
        speak(LEVEL_UP_MOCK_KEYS, true);
    }

    public void onHeroDied(Hero hero) {
        speak(DEATH_MOCK_KEYS, true);
    }

    public void onHeroAte(Hero hero) {
        ensureCompanionPresent();
        speak(EAT_MOCK_KEYS, true);
    }

    public void onHeroPickedUpItem(Item item) {
        if (item == null) {
            return;
        }

        markInterestingAction();
        ensureCompanionPresent();
        speak(PICKUP_MOCK_KEYS, false);
    }

    public void onHeroKilledSomething() {
        markInterestingAction();
    }

    public void onHeroTriggeredTrap() {
        ensureCompanionPresent();
        speak(TRAP_MOCK_KEYS, true);
    }

    public void onHeroOutOfReach() {
        speak(BLOCKED_MOCK_KEYS, false);
    }

    public void maybeSayBored() {
        if (TimeUtils.millis() - lastInterestingActionAtMillis < BORED_THRESHOLD_MILLIS) {
            return;
        }

        speak(BORED_MOCK_KEYS, false);
    }

    private void markInterestingAction() {
        lastInterestingActionAtMillis = TimeUtils.millis();
    }

    private void speak(String[] messageKeys, boolean ignoreCooldown) {
        RatKing ratKing = getCompanion();
        if (ratKing == null || messageKeys == null || messageKeys.length == 0) {
            return;
        }

        long now = TimeUtils.millis();
        if (!ignoreCooldown && now < nextCommentAllowedAtMillis) {
            return;
        }

        nextCommentAllowedAtMillis = now + COMMENT_COOLDOWN_MILLIS;
        String key = messageKeys[RandomHelper.getInstance().randomInt(messageKeys.length)];
        EffectsHelper.getInstance().message(ratKing, Messages.get(key), COMMENT_COLOR, 0f);
    }

    private RatKing getCompanion() {
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit instanceof RatKing) {
                return (RatKing) unit;
            }
        }

        return null;
    }
}