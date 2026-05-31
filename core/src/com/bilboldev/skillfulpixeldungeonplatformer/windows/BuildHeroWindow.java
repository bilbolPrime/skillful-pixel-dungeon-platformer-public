package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SkillsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.GameScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.LoadingScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.StartingBonus;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;

import java.util.ArrayList;

public class BuildHeroWindow extends InteractiveTabbedWindow {

    private static final float HERO_DESCRIPTION_X = 340f;
    private static final float HERO_DESCRIPTION_RIGHT_PADDING = 120f;

    protected enum MODE {
        HERO,
        SKILLS,
        DARK_SKILLS
    }

    private GameSprite heroSprite;
    private MODE mode = MODE.HERO;

    private ArrayList<ActionButton> skillButtons, darkSkillButtons, heroButtons;

    private HeroClass heroClass;
    private String wrappedHeroDescription;
    private float heroDescriptionFontSize = 3f;

    public BuildHeroWindow(HeroClass heroClass, float width, float height) {
        super(width, height);
        skillButtons = new ArrayList<>();
        darkSkillButtons = new ArrayList<>();
        heroButtons = new ArrayList<>();
        this.heroClass = heroClass;
    }

    @Override
    public Window build(){
        super.build();


        heroSprite = heroClass.getClassPortrait();
        heroSprite.setPosition(x + 100, y + height - 300);
        refreshHeroDescription();


        for(Integer skillId : heroClass.getSkillIds()){
            Skill skill = SkillsHelper.getInstance().getSkill(skillId);
            skillButtons.add(new SelectSkillButton(
                    x + heroClass.getSkillButtonXOffset(skillId, false),
                    y + height + heroClass.getSkillButtonYOffset(skillId, false),
                    skill));
        }

        for(Integer skillId : heroClass.getDarkSkillIds()){
            Skill skill = SkillsHelper.getInstance().getSkill(skillId);
            darkSkillButtons.add(new SelectSkillButton(
                    x + heroClass.getSkillButtonXOffset(skillId, true),
                    y + height + heroClass.getSkillButtonYOffset(skillId, true),
                    skill));
        }

        float offsetY = y + height - 400 - 50 - 30;
        float offsetX = x + 100;

        heroButtons.add(new SelectSkillButton(offsetX, offsetY, "images/stats/health.png",
            Messages.get("custom.ui.level_gain",
                new Object[]{heroClass.getHealth(1), heroClass.getHealth(2) - heroClass.getHealth(1)})){
            @Override
            public void click(){
            showSkill("images/stats/health.png",
                Messages.get("custom.generated.arg_starts_with_arg_health_429b446bac",
                    new Object[]{heroClass.getName(), heroClass.getHealth(1), heroClass.getHealth(2) - heroClass.getHealth(1)}));
            }
        });

        offsetY -= 125;
        heroButtons.add(new SelectSkillButton(offsetX, offsetY, "images/stats/mana.png",
            Messages.get("custom.ui.level_gain",
                new Object[]{heroClass.getMana(1), heroClass.getMana(2) - heroClass.getMana(1)})){
            @Override
            public void click(){
            showSkill("images/stats/mana.png",
                Messages.get("custom.generated.arg_starts_with_arg_mana_e2ffb9715d",
                    new Object[]{heroClass.getName(), heroClass.getMana(1), heroClass.getMana(2) - heroClass.getMana(1)}));
            }
        });

        offsetY -= 125;
        String heroJumpButton = "images/units/" + heroClass.getAssetFolderName() + "/button-jump.png";
        heroButtons.add(new SelectSkillButton(offsetX, offsetY, heroJumpButton, heroClass.getMoveSpeedDescription()){
            @Override
            public void click(){
                showSkill(heroJumpButton,
                        Messages.get("custom.generated.arg_s_movement_speed_is_arg_870c8f690a",
                        new Object[]{heroClass.getName(), heroClass.getMoveSpeedDescription()}));
            }
        });

        offsetY -= 125;
        heroButtons.add(new SelectSkillButton(offsetX, offsetY, "images/stats/attack.png", heroClass.getAttackSpeedDescription()){
            @Override
            public void click(){
                showSkill("images/stats/attack.png",
                        Messages.get("custom.generated.arg_s_attack_speed_is_arg_3d8f4c3596",
                        new Object[]{heroClass.getName(), heroClass.getAttackSpeedDescription()}));
            }
        });

        offsetY = y + height - 400 - 50 - 30;
        offsetX = x + 100;
        offsetX += 450;

        heroButtons.add(new SelectSkillButton(offsetX, offsetY, HeroClass.WARRIOR.getClassIcon().spriteString, heroClass.classPenaltyDescription(HeroClass.WARRIOR)){
            @Override
            public void click(){
            showSkill(HeroClass.WARRIOR.getClassIcon().spriteString,
                        Messages.get("custom.generated.arg_is_arg_with_arg_1411df0151",
                    new Object[]{
                        heroClass.getName(),
                        heroClass.classPenaltyDescription(HeroClass.WARRIOR),
                        Messages.lowerCase(HeroClass.WARRIOR.getName())
                    }));
            }
        });
        offsetY -= 125;
        heroButtons.add(new SelectSkillButton(offsetX, offsetY, HeroClass.ROGUE.getClassIcon().spriteString, heroClass.classPenaltyDescription(HeroClass.ROGUE)){
            @Override
            public void click(){
            showSkill(HeroClass.ROGUE.getClassIcon().spriteString,
                        Messages.get("custom.generated.arg_is_arg_with_arg_1411df0151",
                    new Object[]{
                        heroClass.getName(),
                        heroClass.classPenaltyDescription(HeroClass.ROGUE),
                        Messages.lowerCase(HeroClass.ROGUE.getName())
                    }));
            }
        });

        offsetY -= 125;
        heroButtons.add(new SelectSkillButton(offsetX, offsetY, HeroClass.WIZARD.getClassIcon().spriteString, heroClass.classPenaltyDescription(HeroClass.WIZARD)){
            @Override
            public void click(){
            showSkill(HeroClass.WIZARD.getClassIcon().spriteString,
                        Messages.get("custom.generated.arg_is_arg_with_arg_1411df0151",
                    new Object[]{
                        heroClass.getName(),
                        heroClass.classPenaltyDescription(HeroClass.WIZARD),
                        Messages.lowerCase(HeroClass.WIZARD.getName())
                    }));
            }
        });

        offsetY -= 125;
        heroButtons.add(new SelectSkillButton(offsetX, offsetY, HeroClass.ARCHER.getClassIcon().spriteString, heroClass.classPenaltyDescription(HeroClass.ARCHER)){
            @Override
            public void click(){
            showSkill(HeroClass.ARCHER.getClassIcon().spriteString,
                        Messages.get("custom.generated.arg_is_arg_with_arg_1411df0151",
                    new Object[]{
                        heroClass.getName(),
                        heroClass.classPenaltyDescription(HeroClass.ARCHER),
                        Messages.lowerCase(HeroClass.ARCHER.getName())
                    }));
            }
        });


        offsetY = y + height - 400 - 50 - 30;
        offsetX += 450;

        for(final StartingBonus startingBonus : heroClass.getStartingBonuses()){
            heroButtons.add(new SelectSkillButton(offsetX, offsetY, startingBonus.getGameSprite().spriteString, startingBonus.getTitle()){
                @Override
                public void click(){
                    showSkill(startingBonus.getGameSprite().spriteString, startingBonus.getDescription());
                }
            });
            offsetY -= 125;
        }

        heroButtons.add(new ActionButton(x + width - 400, y + height / 2 - 200, 200, 200, "images/intro/play.png", "images/intro/play.png"){
            @Override
            public void click(){
                if (SaveHelper.getInstance().hasSave(heroClass)) {
                    WindowHelper.getInstance().addWindow(new SavedGameWindow(heroClass).build());
                    return;
                }

                WindowHelper.getInstance().addWindow(new DifficultySelectWindow(heroClass, false).build());
            }
        });


        addTab(new Tab("Hero", x - 100, y + 500, heroClass.getClassPortrait().spriteString){
            @Override
            public void click(){

                super.click();
                mode = MODE.HERO;
            }
        });

        addTab(new Tab("Skills", x - 100, y + 300, "images/skills/light-skills.png"){
            @Override
            public void click(){
               super.click();
                mode = MODE.SKILLS;
            }
        });

        addTab(new Tab("Dark Skills", x - 100, y + 100, "images/skills/dark-skills.png"){
            @Override
            public void click(){
                super.click();
                mode = MODE.DARK_SKILLS;
            }
        });

        return this;
    }

    @Override
    public void draw(Batch batch){
        super.draw(batch);

        heroSprite.draw(batch);
        FontHelper.getSingleton().writeRaw(Color.WHITE, batch, heroDescriptionFontSize, x + HERO_DESCRIPTION_X, y + height - 150, wrappedHeroDescription);

        if(mode == MODE.SKILLS){
            heroClass.drawBranches(batch, x, y, width, height);

            for(ActionButton actionButton : skillButtons){
                actionButton.draw(batch);
            }
        }

        if(mode == MODE.DARK_SKILLS){
            heroClass.drawDarkSkillsBranches(batch, x, y, width, height);

            for(ActionButton actionButton : darkSkillButtons){
                actionButton.draw(batch);
            }
        }

        if(mode == MODE.HERO){
            for(ActionButton actionButton : heroButtons){
                actionButton.draw(batch);
            }
        }
    }

    @Override
    public boolean click(float x, float y){
        if(mode == MODE.SKILLS){
            for(ActionButton actionButton : skillButtons){
                if(actionButton.isHitProjected(x, y)){
                    actionButton.click();
                    return true;
                }
            }
        }

        if(mode == MODE.DARK_SKILLS){
            for(ActionButton actionButton : darkSkillButtons){
                if(actionButton.isHitProjected(x, y)){
                    actionButton.click();
                    return true;
                }
            }
        }

        if(mode == MODE.HERO){
            for(ActionButton actionButton : heroButtons){
                if(actionButton.isHitProjected(x, y)){
                    actionButton.click();
                    return true;
                }
            }
        }

        return super.click(x, y);
    }

    private void showSkill(String sprite, String description){
        WindowHelper.getInstance().addWindow(new DescriptionWindow(sprite, description, 1500, 400).build());
    }

    private float getHeroDescriptionWrapWidth() {
        return Math.max(100f, width - HERO_DESCRIPTION_X - HERO_DESCRIPTION_RIGHT_PADDING);
    }

    private void refreshHeroDescription() {
        FontHelper.FittedTextBlock fittedDescription = FontHelper.getSingleton().fitMultilineToEnglishFootprint(
                heroClass.getSourceDescription(),
                heroClass.getDescription(),
                3f,
                getHeroDescriptionWrapWidth());
        wrappedHeroDescription = fittedDescription.text;
        heroDescriptionFontSize = fittedDescription.size;
    }

    private class SelectSkillButton extends ActionButton {

        private int nature;
        private String text;
        private boolean selected;
        private Skill skill;
        private float fontSize = 3f;

        public SelectSkillButton(float x, float y, String gsString, String text) {
            super(x, y, 400, 100, "images/misc/grey.png", "images/misc/grey.png");

            if (text != null && !text.isEmpty()) {
                String localizedText = Messages.maybeTranslate(text);
                fontSize = FontHelper.getSingleton().fitSizeToEnglishFootprint(text, localizedText, 3f, 300f);
            }

            GameSprite gs = new GameSprite(gsString, 72, 72);
            gs.setPosition(14, 14);
            addGameSprite(gs);

            this.text = text;
        }

        public SelectSkillButton(float x, float y, Skill skill) {
            super(x, y, 400, 100, "images/misc/grey.png", "images/misc/grey.png");

            GameSprite gs = skill.getGameSprite().clone();
            gs.setWidth(72);
            gs.setHeight(72);
            gs.setPosition(14, 14);
            addGameSprite(gs);

            this.text = skill.getName();
            fontSize = FontHelper.getSingleton().fitSizeToEnglishFootprint(skill.getSourceName(), this.text, 3f, 300f);

            this.skill = skill;
        }

        @Override
        public void draw(Batch batch){
            super.draw(batch);
            FontHelper.getSingleton().writeWhite(batch, fontSize, x + 100, y + 65, Messages.maybeTranslate(text));
        }

        @Override
        public void click(){
            if(this.skill != null){
                showSkill(skill.getGameSpriteString(), heroClass.getSkillBigDescription(skill.getId()));
            }
        }
    }
}

