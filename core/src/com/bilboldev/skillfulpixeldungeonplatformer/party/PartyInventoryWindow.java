package com.bilboldev.skillfulpixeldungeonplatformer.party;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.InventoryWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public final class PartyInventoryWindow extends InventoryWindow {
    private final int tab;
    private final List<String> labels;
    private final List<Boolean> enabled;
    private final IntConsumer choose;
    private ArrayList<Button> remoteButtons;
    private boolean dismissed;

    PartyInventoryWindow(HeroClass heroClass, int tab, List<String> labels, List<Boolean> enabled, IntConsumer choose) {
        super(heroClass, 2000, 1000);
        this.tab = tab;
        this.labels = labels;
        this.enabled = enabled;
        this.choose = choose;
    }

    @Override public Window build() {
        remoteButtons = null;
        super.build();
        selectPartyTab(tab);
        ArrayList<Button> original = super.getKeyboardChoices();
        if (original.size() != labels.size()) throw new IllegalArgumentException("inventory_choices");
        remoteButtons = new ArrayList<>();
        for (int i = 0; i < original.size(); i++) {
            final int index = i;
            final Button button = original.get(i);
            remoteButtons.add(new Button(button.x, button.y, button.getWidth(), button.getHeight(),
                    "images/misc/transparent.png", "images/misc/transparent.png") {
                @Override public String partyLabel() { return labels.get(index); }
                @Override public boolean canClick() { return enabled.get(index); }
                @Override public boolean isHitProjected(float x, float y) { return canClick() && button.isHitProjected(x, y); }
                @Override public void click() { if (canClick()) choose.accept(index); }
            });
        }
        return this;
    }

    @Override protected ArrayList<Button> getKeyboardChoices() {
        return remoteButtons == null ? super.getKeyboardChoices() : remoteButtons;
    }

    @Override public boolean click(float x, float y) {
        for (Button button : getKeyboardChoices()) {
            if (button.isHitProjected(x, y)) { button.click(); return true; }
        }
        if (!contains(x, y)) hide();
        return true;
    }

    @Override public void hide() {
        if (dismissed) return;
        dismissed = true;
        choose.accept(-1);
        super.hide();
    }
}
