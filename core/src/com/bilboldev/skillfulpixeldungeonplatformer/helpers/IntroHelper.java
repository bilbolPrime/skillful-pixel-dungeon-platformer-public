package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Theme;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class IntroHelper {
    public final int MIN_FLOOR = 3;
    protected HashMap<Integer, Array<Integer>> floors;
    protected HashSet<String> platforms;
    protected Theme theme;

    private static final IntroHelper ourInstance = new IntroHelper();

    public static IntroHelper getInstance() {
        return ourInstance;
    }

    private IntroHelper() {
        floors = new HashMap<>();
        platforms = new HashSet<>();
    }

    public void generateMap(Theme theme){
        this.theme = theme;
    }


    public void draw(Batch batch){
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {

                if(j > MIN_FLOOR - 1){
                    theme.getWall().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getWall().draw(batch);
                    theme.getFader().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getFader().draw(batch);
                }

                if(j == getHeight() - 1){
                    theme.getWall().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getWall().draw(batch);
                }

                if(j == MIN_FLOOR - 1){
                    theme.getWallFading().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getWallFading().draw(batch);

                    theme.getFader().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getFader().draw(batch);

                    theme.getFloor().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getFloor().draw(batch);
                }
            }
        }
    }

    public int getWidth(){
        return 20;
    }

    public int getHeight(){
        return 10;
    }

    public Theme getTheme(){
        return theme;
    }
}

