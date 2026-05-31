package com.bilboldev.skillfulpixeldungeonplatformer.units.classes;

import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class StartingBonus {
    protected String title;
    protected String description;
    protected GameSprite gs;

    public StartingBonus(String title, String description, String sprite){
        this.title = title;
        this.description = description;
        this.gs = new GameSprite(sprite, 50, 50);
    }

    public String getTitle(){
        return Messages.capitalizeForDisplay(Messages.maybeTranslate(title));
    }

    public String getDescription(){
        return Messages.maybeTranslate(description);
    }

    public GameSprite getGameSprite(){
        return gs;
    }
}

