package com.bilboldev.skillfulpixeldungeonplatformer.party;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfIdentify;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.MercenaryRecruit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.*;
import java.io.IOException;
import java.util.*;

final class PartyMenuViews {
    private static final int MERCHANT = 0, CHOICE = 1, ITEMS = 2, IDENTIFY = 3, MERCENARY = 4, HIRE = 5, TEXT = 6, DESCRIPTION = 7;
    public static final class Data {
        public int kind;
        public float width, height, portraitOffset, padding, extension;
        public PartySprite.Data portrait;
        public String actor;
        public ArrayList<SaveHelper.ItemSaveData> items;
    }

    static boolean supports(Window window) {
        return window instanceof MerchantWindow || window instanceof DescriptionWindow || window instanceof TextWindow
                || window instanceof MercenaryWindow || window instanceof MercenaryHireWindow;
    }

    static String capture(Window window) {
        Data data = new Data(); data.width = window.partyWidth(); data.height = window.partyHeight();
        if (window instanceof DescriptionWindow) {
            data.portrait = PartySprite.capture(((DescriptionWindow)window).partyPortrait());
            data.portraitOffset = ((DescriptionWindow)window).partyPortraitOffset();
        }
        if (window instanceof MerchantWindow) {
            data.kind = MERCHANT; data.portrait = PartySprite.capture(((MerchantWindow)window).partyPortrait());
        } else if (window instanceof ChoiceDialogWindow) {
            data.kind = CHOICE;
            data.padding = ((ChoiceDialogWindow)window).partyButtonPadding();
            data.extension = ((ChoiceDialogWindow)window).partyBottomExtension();
        } else if (window instanceof InventoryItemChoiceWindow) {
            data.kind = ITEMS; data.items = items(((InventoryItemChoiceWindow)window).partyItems());
        } else if (window instanceof IdentifyScrollWindow) {
            data.kind = IDENTIFY; data.items = items(((IdentifyScrollWindow)window).partyItems());
        } else if (window instanceof MercenaryWindow) {
            data.kind = MERCENARY; data.actor = ((MercenaryWindow)window).partyMercenary().getPersistentId();
        } else if (window instanceof MercenaryHireWindow) {
            data.kind = HIRE; data.actor = ((MercenaryHireWindow)window).partyRecruit().getPersistentId();
        } else if (window instanceof TextWindow) data.kind = TEXT;
        else data.kind = DESCRIPTION;
        return PartyCodec.encode(data);
    }

    private static ArrayList<SaveHelper.ItemSaveData> items(List<Item> items) {
        ArrayList<SaveHelper.ItemSaveData> result = new ArrayList<>();
        for (Item item : items) result.add(SaveHelper.getInstance().snapshotItem(item, UnitHelper.getInstance().getHero()));
        return result;
    }

    static Window restore(String encoded, String description, List<String> labels, List<Boolean> enabled) throws IOException {
        Data data = PartyCodec.decode(Data.class, encoded);
        if (data == null || data.kind < MERCHANT || data.kind > DESCRIPTION || !bounded(data.width, 1, 2500)
                || !bounded(data.height, 1, 3000) || !bounded(data.portraitOffset, -2000, 2000)
                || !bounded(data.padding, 0, 2000) || !bounded(data.extension, 0, 2000)) throw new IOException("menu_view");
        GameSprite portrait = PartySprite.restore(data.portrait);
        Window view;
        switch (data.kind) {
            case MERCHANT:
                view = new MerchantWindow(UnitHelper.getInstance().getHero().getHeroClass(), data.width, data.height, portrait, description); break;
            case CHOICE:
                ChoiceDialogWindow dialog = new ChoiceDialogWindow(portrait, description, data.width, data.height)
                        .setButtonBottomPadding(data.padding).extendBottom(data.extension);
                for (int i = 0; i < labels.size(); i++) dialog.addChoice(labels.get(i), null, enabled.get(i));
                view = dialog; break;
            case ITEMS: case IDENTIFY:
                if (data.items == null || data.items.size() > 64) throw new IOException("menu_items");
                ArrayList<Item> items = new ArrayList<>();
                for (SaveHelper.ItemSaveData saved : data.items) {
                    Item item = SaveHelper.getInstance().createPreviewItem(saved);
                    if (item == null) throw new IOException("menu_item");
                    items.add(item);
                }
                view = data.kind == ITEMS ? new InventoryItemChoiceWindow(portrait, description, items, null)
                        : new IdentifyScrollWindow(new ScrollOfIdentify(), items); break;
            case MERCENARY:
                Unit mercenary = actor(data.actor);
                if (!(mercenary instanceof MercenaryAlly)) throw new IOException("menu_mercenary");
                view = new MercenaryWindow((MercenaryAlly)mercenary); break;
            case HIRE:
                Unit recruit = actor(data.actor);
                if (!(recruit instanceof MercenaryRecruit)) throw new IOException("menu_recruit");
                view = new MercenaryHireWindow((MercenaryRecruit)recruit); break;
            case TEXT: view = new TextWindow(data.width, data.height, description); break;
            default: view = new DescriptionWindow(portrait, description, data.width, data.height); break;
        }
        if (view instanceof DescriptionWindow) ((DescriptionWindow)view).setDescriptionSpriteYOffset(data.portraitOffset);
        return view;
    }

    static Unit actor(String id) throws IOException {
        if (id == null || id.length() > 160) throw new IOException("menu_actor");
        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) if (id.equals(unit.getPersistentId())) return unit;
        return null;
    }
    private static boolean bounded(float value, float min, float max) { return !Float.isNaN(value) && value >= min && value <= max; }
    private PartyMenuViews() { }
}
