package com.bilboldev.skillfulpixeldungeonplatformer.units.skills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SkillsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassSkillTree;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

import java.util.ArrayList;
import java.util.Locale;

public class Skill {
    protected int id;
    protected ArrayList<Integer> requires;
    protected int locksOut;
    protected int tier, xOffset, yOffset;
    protected HeroClass skillClass;
    protected String name;
    protected String quickDescription;
    protected String description;
    protected GameSprite gs;
    protected String gsString;


    public Skill(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite){
        this.id = id;
        this.skillClass = skillClass;
        this.tier = tier;
        this.name = name;
        this.quickDescription = quickDescription;
        this.description = description;
        this.gs = new GameSprite(sprite, 50, 50);
        this.gsString = sprite;
        requires = new ArrayList<>();
    }

    public Skill setRequires(int requires){
        this.requires.add(requires);
        return this;
    }

    public ArrayList<Integer> getRequires(){
        return requires;
    }

    public Skill setTreeOffsets(int xOffset, int yOffset){
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        return this;
    }

    public GameSprite getGameSprite(){
        return gs;
    }

    public String getName(){
        String localized = NewClassSkillTree.isReserved(id)
                ? Messages.get("custom.newskills." + id + ".name") : Messages.maybeTranslate(name);
        if (Messages.lang() != Languages.ENGLISH) return Messages.capitalizeForDisplay(localized);

        StringBuilder title = new StringBuilder(localized.toLowerCase(Locale.ENGLISH));
        boolean wordStart = true;
        for (int i = 0; i < title.length(); i++) {
            char letter = title.charAt(i);
            if (Character.isLetterOrDigit(letter)) {
                if (wordStart) title.setCharAt(i, Character.toUpperCase(letter));
                wordStart = false;
            } else if (Character.isWhitespace(letter) || letter == '-') wordStart = true;
        }
        return title.toString();
    }

    public String getSourceName(){
        return Messages.capitalize(name);
    }

    public String getQuickDescription(){
        if (NewClassSkillTree.isReserved(id)) return Messages.get("custom.newskills." + id + ".quick");
        return Messages.maybeTranslate(quickDescription);
    }

    public String getDescription(){
        if (NewClassSkillTree.isReserved(id)) return Messages.get("custom.newskills." + id + ".description");
        return Messages.maybeTranslate(description);
    }

    public String getBigDescription(){
        String toReturn = Messages.maybeTranslate("%s: %s", getName(), getDescription());
        String requiresLabel = Messages.maybeTranslate("Requires");

        if(requires.size() == 1){
            return Messages.maybeTranslate(
                    "%s\n%s: %s",
                    toReturn,
                    requiresLabel,
                    SkillsHelper.getInstance().getSkillName(requires.get(0)));
        }

        if(requires.size() > 1){
            String requiredString = "";
            for(Integer required : requires){
                if(!requiredString.equals("")){
                    requiredString += ", ";
                } else{
                    requiredString = requiresLabel + ": ";
                }

                requiredString += SkillsHelper.getInstance().getSkillName(required);
            }

            return Messages.maybeTranslate("%s\n%s", toReturn, requiredString);
        }
        return toReturn;
    }

    public int getId(){
        return id;
    }

    public int getLocksOut(){
        return locksOut;
    }

    public Skill setLocksOut(int locksOut){
        this.locksOut = locksOut;
        return this;
    }

    public int getTier(){
        return tier;
    }

    public String getGameSpriteString(){
        return gsString;
    }

    public int getXOffset(){
        return xOffset;
    }

    public int getYOffset(){
        return yOffset;
    }

    public void affect(Unit owner){

    }

    public HeroClass getSkillClass(){
        return skillClass;
    }
}

