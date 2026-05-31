package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.BuildHeroHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

public class CharacterSelectScreen extends MenuScreenBase {

    private static final float UNSELECTED_HERO_ALPHA = 0.2f;

    private GameSprite banner;

    @Override
    protected void createMenuContent() {
        RatKingSupportHelper.getInstance().applyConfiguredTitleTheme();
        addTitleBackButton();

        banner = new GameSprite("images/intro/select-hero.png", 803, 114);
        banner.setPosition(850, 900);

        float buttonY = 350f;
        float buttonWidth = 200f;
        float buttonHeight = 200f;
        float buttonSpacing = 300f;
        float startX = (2500f - (buttonWidth * 4f + (buttonSpacing - buttonWidth) * 3f)) / 2f;

        buttons.add(createHeroButton("WARRIOR", startX, buttonY, buttonWidth, buttonHeight,
                "images/intro/warrior.png", HeroClass.WARRIOR));
        buttons.add(createHeroButton("WIZARD", startX + buttonSpacing, buttonY, buttonWidth, buttonHeight,
                "images/intro/wizard.png", HeroClass.WIZARD));
        buttons.add(createHeroButton("ROGUE", startX + buttonSpacing * 2f, buttonY, buttonWidth, buttonHeight,
                "images/intro/rogue.png", HeroClass.ROGUE));
        buttons.add(createHeroButton("HUNTRESS", startX + buttonSpacing * 3f, buttonY, buttonWidth, buttonHeight,
                "images/intro/huntress.png", HeroClass.ARCHER));
    }

    private IconButton createHeroButton(String text, float x, float y, float width, float height, String spritePath, HeroClass heroClass) {
        return new IconButton(text, x, y, width, height, spritePath, spritePath)
                .setAction(() -> BuildHeroHelper.getSingleton().buildHeroWindow(heroClass))
                .setDisplayAlpha(1f);
    }

    @Override
    protected void drawMenu(Batch batch) {
        banner.draw(batch);
        drawButtons(batch);
    }

    @Override
    protected boolean handleBackAction() {
        if (WindowHelper.getInstance().windowOpen()) {
            WindowHelper.getInstance().closeWindow();
            return true;
        }

        SkillfulPixelDungeonPlatformer.transition(new TitleScreen(), true);
        return true;
    }
}
