package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class InteractiveTabbedWindow extends InteractiveWindow {

    protected ArrayList<Tab> tabs;
    protected Tab selectedTab;

    public InteractiveTabbedWindow(float width, float height) {
        super(width, height);

        tabs = new ArrayList<>();
    }


    @Override
    public void draw(Batch batch){
        for(Tab tab : tabs){
            if(tab.selected){
                continue;
            }

            tab.draw(batch);
        }

        super.draw(batch);

        selectedTab.draw(batch);
    }

    @Override
    public boolean contains(float x, float y) {
        if (super.contains(x, y)) return true;
        for (Tab tab : tabs) if (tab.isHitProjected(x, y)) return true;
        return false;
    }

    @Override
    public boolean click(float x, float y){
        for(Tab tab : tabs){
            if(tab.isHitProjected(x, y)){
                tab.click();
                return true;
            }
        }

        for(ActionButton actionButton : actionButtons){
            if(actionButton.isHitProjected(x, y)){
                actionButton.click();
                return true;
            }
        }

        if(x < this.x || x > this.x + this.width){
            hide();
        }

        if(y < this.y || y > this.y + this.height){
            hide();
        }

        return true;
    }

    public void addTab(Tab tab){
        this.tabs.add(tab);
        if(this.selectedTab == null){
            this.selectedTab = tab;
            this.selectedTab.selected = true;
        }
    }

    protected void hideTabs(){
        for(Tab tab : tabs){
            tab.selected = false;
        }
    }

    protected class Tab extends Button{
        protected String name;
        protected GameSprite gs, background;
        protected float x, y;
        protected boolean selected;

        public Tab(String name, float x, float y, String sprite){
            super(x, y, 220, 180, "images/misc/transparent.png", "images/misc/transparent.png");
            this.name = name;
            this.x = x;
            this.y = y;
            this.gs = new GameSprite(sprite, 100, 100);
            this.gs.setPosition(x + 75, y +  45);
            this.background = new GameSprite("images/window/tab.png", 220, 180);
            this.background.setPosition(x, y);
            selected = false;
        }


        public void draw(Batch batch){
            if(selected){
                this.background.setPosition(x - 60, y);
                this.gs.setPosition(x - 0, y +  45);
                background.draw(batch);
                gs.draw(batch);
            }
            else {
                this.background.setPosition(x , y);
                this.gs.setPosition(x + 30, y +  45);
                background.draw(batch);
                gs.draw(batch);
            }
        }

        @Override
        public boolean isHitProjected(float x, float y) {
            if (!canClick()) {
                return false;
            }

            float hitX = selected ? this.x - 60f : this.x;
            float hitY = this.y;
            float hitMargin = 20f;
            return x >= hitX - hitMargin
                    && x <= hitX + 220f + hitMargin
                    && y >= hitY - hitMargin
                    && y <= hitY + 180f + hitMargin;
        }

        @Override
        public void click(){
            selectedTab = this;
            hideTabs();
            selected = true;
        }
    }

    @Override
    public void refresh(){
        tabs.clear();
        Tab selectedTabMemory = selectedTab;
        selectedTab = null;
        super.refresh();
        for(Tab tab : tabs){
            if(selectedTabMemory.name.equals(tab.name)){
                tab.click();
                return;
            }
        }
    }
}

