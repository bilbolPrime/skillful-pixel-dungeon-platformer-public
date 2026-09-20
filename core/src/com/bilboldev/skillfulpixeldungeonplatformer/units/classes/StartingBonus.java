package com.bilboldev.skillfulpixeldungeonplatformer.units.classes;

import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class StartingBonus {
    protected String title;
    protected String description;
    protected GameSprite gs;
    private String messageKey;

    public StartingBonus(String title, String description, String sprite){
        this.title = title;
        this.description = description;
        this.gs = new GameSprite(sprite, 50, 50);
    }

    public String getTitle(){
        return Messages.capitalizeForDisplay(messageKey == null ? Messages.maybeTranslate(title) : Messages.get(messageKey + ".title"));
    }

    public String getDescription(){
        return messageKey == null ? Messages.maybeTranslate(description) : Messages.get(messageKey + ".description");
    }


    public static StartingBonus localized(String key, String sprite) {
        StartingBonus bonus = new StartingBonus(Messages.get(key + ".title"), Messages.get(key + ".description"), sprite);
        bonus.messageKey = key;
        return bonus;
    }

    public GameSprite getGameSprite(){
        return gs;
    }
}

