package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.InputGestureListener;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.TextWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.Window;

import java.util.ArrayList;

public class WindowHelper {
    protected ArrayList<Window> windows;

    private static final WindowHelper ourInstance = new WindowHelper();

    public static WindowHelper getInstance() {
        return ourInstance;
    }

    private WindowHelper() {
        windows = new ArrayList<>();
    }

    public void addWindow(Window window){
        windows.add(window);
    }

    public void replaceWindow(Window window) {
        closeWindow();
        addWindow(window);
    }
    public void addWindow(float width, float height){
        windows.add(new Window(width, height).build());
    }

    public void addWindow(float width, float height, String text){
        windows.add(new TextWindow(width, height, text).build());
    }

    public boolean windowOpen(){
        return windows != null && windows.size() > 0;
    }

    public void closeWindow(){
        if(windows == null || windows.size() == 0){
            return;
        }

        windows.remove(windows.size() - 1);
    }

    public void closeWindow(Window window){
        windows.remove(window);
    }

    public Window topWindow() {
        if (windows == null || windows.size() == 0) {
            return null;
        }

        return windows.get(windows.size() - 1);
    }

    public void draw(Batch batch){
        for(Window window : windows){
            window.draw(batch);
        }
    }

    public void hideAll(){
        windows = new ArrayList<>();
    }


    public InputGestureListener inputGestureListener(){
        return new InputGestureListener(){
            @Override
            public boolean touchDown(float x,
                                     float y,
                                     int pointer,
                                     int button) {
                return getInstance().touchDown(x, y,
                        pointer,
                        button);
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                return getInstance().tap(x, y);
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                return getInstance().pan(x, y, deltaX, deltaY);
            }

            @Override
            public boolean longPress(float x, float y) {
                return getInstance().longpress(x, y);
            }
        };
    }

    private boolean touchDown(float x, float y, int pointer, int button) {

        Vector3 t = GameHelper.GetSingleton().getUICamera().unproject(new Vector3(x, y, 0));

        if(windows != null && windows.size() > 0){
            return windows.get(windows.size() - 1).pointerDown(t.x, t.y, button);
        }

        return false;
    }

    public boolean tap(float x, float y) {
        Vector3 t = GameHelper.GetSingleton().getUICamera().unproject(new Vector3(x, y, 0));

        if (windows != null && windows.size() > 0) {
            return windows.get(windows.size() - 1).tap(t.x, t.y);
        }

        return false;
    }

    private boolean pan(float x, float y, float deltaX, float deltaY) {
        if (windows == null || windows.size() == 0) {
            return false;
        }

        Vector3 current = GameHelper.GetSingleton().getUICamera().unproject(new Vector3(x, y, 0));
        Vector3 previous = GameHelper.GetSingleton().getUICamera().unproject(new Vector3(x - deltaX, y - deltaY, 0));
        return windows.get(windows.size() - 1).pan(current.x, current.y, current.x - previous.x, current.y - previous.y);
    }

    public boolean handleScroll(float amountY) {
        if (windows == null || windows.size() == 0) {
            return false;
        }

        return windows.get(windows.size() - 1).scroll(amountY);
    }

    private boolean longpress(float x, float y) {

        Vector3 t = GameHelper.GetSingleton().getUICamera().unproject(new Vector3(x, y, 0));

        boolean consumed = false;



        return consumed;
    }

    public void refresh(){
        if(windows.size() > 0){
            windows.get(windows.size() - 1).refresh();
        }
    }

    public void refreshAll() {
        if (windows == null || windows.size() == 0) {
            return;
        }

        for (Window window : windows) {
            if (window != null) {
                window.refresh();
            }
        }
    }

    public boolean handleKeyDown(int keycode) {
        if (windows == null || windows.size() == 0) {
            return false;
        }

        return windows.get(windows.size() - 1).keyDown(keycode);
    }
}

