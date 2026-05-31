package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ItemIdentityHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.ConsumableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

import java.util.ArrayList;

public class Scroll extends ConsumableItem {
    {
        name = "Scroll";
        description = "A scroll.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        quantity = 1;
        goldCost = 50;
        setIdentityFamily(ItemIdentityHelper.Family.SCROLL);
        setUnknownDescription("An unidentified scroll. Its magic is unknown until it is read.");
    }

    @Override
    public void consume(){
        identify();
        SoundHelper.GetSingleton().play(Sounds.READ, 0, 1f);
        quantity--;

        if(quantity < 1){
            InventoryHelper.getInstance().removeItem(this);
        }
    }

    @Override
    public String getBigDescription() {
        if (!isIdentified()) {
            return getDescription();
        }

        return super.getBigDescription();
    }

    protected Hero getHero() {
        return UnitHelper.getInstance().getHero();
    }

    protected ArrayList<Unit> getActiveRoomUnits() {
        ArrayList<Unit> units = new ArrayList<Unit>();
        String activeRoomIdentifier = MapHelper.getInstance().getActiveRoomIdentifier();
        if (activeRoomIdentifier == null) {
            return units;
        }

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit == null || unit.showOnly() || unit.getRoom() == null || unit.isDead() || unit.getHP() < 1) {
                continue;
            }

            if (activeRoomIdentifier.equals(unit.getRoom())) {
                units.add(unit);
            }
        }

        return units;
    }

    protected ArrayList<Mob> getActiveRoomMobs(boolean includeFriendly) {
        ArrayList<Mob> mobs = new ArrayList<Mob>();
        for (Unit unit : getActiveRoomUnits()) {
            if (!(unit instanceof Mob)) {
                continue;
            }

            if (!includeFriendly && unit.isFriendly) {
                continue;
            }

            mobs.add((Mob) unit);
        }

        return mobs;
    }

}

