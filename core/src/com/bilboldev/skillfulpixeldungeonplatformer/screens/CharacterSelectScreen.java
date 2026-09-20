package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.BuildHeroHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassSkillTree;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuHeroPreview;
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

        float buttonY = 265f;
        float buttonWidth = 280f;
        float buttonHeight = 395f;
        float buttonSpacing = 336f;
        float startX = (2500f - (buttonWidth * 6f + (buttonSpacing - buttonWidth) * 5f)) / 2f;

        buttons.add(createHeroButton("WARRIOR", startX, buttonY, buttonWidth, buttonHeight,
                "images/intro/warrior.png", HeroClass.WARRIOR));
        buttons.add(createHeroButton("WIZARD", startX + buttonSpacing, buttonY, buttonWidth, buttonHeight,
                "images/intro/wizard.png", HeroClass.WIZARD));
        buttons.add(createHeroButton("ROGUE", startX + buttonSpacing * 2f, buttonY, buttonWidth, buttonHeight,
                "images/intro/rogue.png", HeroClass.ROGUE));
        buttons.add(createHeroButton("HUNTRESS", startX + buttonSpacing * 3f, buttonY, buttonWidth, buttonHeight,
                "images/intro/huntress.png", HeroClass.ARCHER));
        buttons.add(createHeroButton(HeroClass.NECROMANCER.getSourceName(), startX + buttonSpacing * 4f, buttonY, buttonWidth, buttonHeight,
                "images/intro/necromancer.png", HeroClass.NECROMANCER));
        buttons.add(createHeroButton(HeroClass.MERCENARY.getSourceName(), startX + buttonSpacing * 5f, buttonY, buttonWidth, buttonHeight,
                "images/intro/mercenary.png", HeroClass.MERCENARY));
    }

    private IconButton createHeroButton(String text, float x, float y, float width, float height, String spritePath, HeroClass heroClass) {
        float iconHeight = Math.min(width - 40f, height - 90f);
        GameSprite armorPreview = new GameSprite(new Sprite(MenuHeroPreview.maximumArmorFrame(heroClass)),
                iconHeight * 12f / 15f, iconHeight);
        return new IconButton(text, NewClassSkillTree.isNewClass(heroClass) ? heroClass.getName() : null,
                x, y, width, height, spritePath, spritePath)
                .setIcon(armorPreview)
                .setAction(() -> BuildHeroHelper.getSingleton().buildHeroWindow(heroClass))
                .setPanelVisible(false)
                .setFloorContact(ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE, 0f)
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
