package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.IntroHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.InputGestureListener;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Kingdom;

import java.util.ArrayList;

public class LoadingScreen extends BaseScreen {

    private float frame = 0f;

    private BaseScreen preparing, disposing;
    private boolean disposed, prepared;

    public LoadingScreen prepare(BaseScreen preparing, BaseScreen disposing){
        this.preparing = preparing;
        this.disposing = disposing;
        return this;
    }

    @Override
    public void create() {

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void init() {

    }

    @Override
    public void act(float delta) {
        frame += 10f * delta;

        if(frame > 3f && disposing != null && !disposed){
            disposed = true;
            disposing.dispose();
            WindowHelper.getInstance().hideAll();
        }

        if(frame > 10f && !prepared){
            prepared = true;
            SkillfulPixelDungeonPlatformer.transition(this.preparing, true);
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.position.x = 1250f;
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        String loading = Messages.get("scenes.interlevelscene$mode.continue");
        for(int i = 0; i < frame % 4; i++){
            loading += ".";
        }
        FontHelper.getSingleton().writeWhite(batch, 3, 1100, 600, loading);
        batch.end();

        act(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    public LoadingScreen getInstance(){
        return this;
    }
}

