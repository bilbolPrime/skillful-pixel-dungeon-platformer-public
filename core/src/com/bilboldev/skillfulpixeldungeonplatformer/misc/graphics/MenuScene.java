package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;


public interface MenuScene {
    void act(float delta);
    void draw(Batch batch, OrthographicCamera camera);
}
