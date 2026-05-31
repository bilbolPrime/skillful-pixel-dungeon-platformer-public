package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.decoration;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

import java.util.Map;

public class Library extends Decoration {
    {
        gs = MapHelper.getInstance().getTheme().getLibrary().clone();
    }
}

