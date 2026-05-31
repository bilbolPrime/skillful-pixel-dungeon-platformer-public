package com.bilboldev.skillfulpixeldungeonplatformer.units.skills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SkillsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

import java.util.ArrayList;

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
        return Messages.capitalizeForDisplay(Messages.maybeTranslate(name));
    }

    public String getSourceName(){
        return Messages.capitalize(name);
    }

    public String getQuickDescription(){
        return Messages.maybeTranslate(quickDescription);
    }

    public String getDescription(){
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
                    SkillsHelper.getInstance().getSkill(requires.get(0)).getName());
        }

        if(requires.size() > 1){
            String requiredString = "";
            for(Integer required : requires){
                if(!requiredString.equals("")){
                    requiredString += ", ";
                } else{
                    requiredString = requiresLabel + ": ";
                }

                requiredString += SkillsHelper.getInstance().getSkill(required).getName();
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

