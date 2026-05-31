package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.OrthographicCamera;



public class GameHelper {


    private OrthographicCamera camera, uiCamera;
    private static GameHelper m_instance;


    int mode = 0;


    public static GameHelper GetSingleton(){
        if(m_instance == null){
            m_instance = new GameHelper();
        }

        return m_instance;
    }

    public static  void reset(){
        m_instance = null;
    }

    private GameHelper()
    {
        camera = new OrthographicCamera(100,100); uiCamera = new OrthographicCamera(100,100);
    }

    public int mode()
    {
        return mode;
    }

    public boolean paused()
    {
        return mode == 1;
    }

    public void pause()
    {
       mode = 1;
    }

    public void unpause()
    {
        mode = 0;
    }

    public void openInventory()
    {

    }

    public void openSettings()
    {

    }

    public void closeInventory()
    {
        mode = 0;
    }

    public boolean inventoryOpened()
    {
        return mode == 2;
    }


    public void goHome(){
        //TextureHelper.GetSingleton().dispose();


    }

    public OrthographicCamera getCamera(){
        return camera;
    }

    public void setCamera(OrthographicCamera c){
        camera = c;
    }

    public OrthographicCamera getUICamera(){
        return uiCamera;
    }

    public void setUICamera(OrthographicCamera c){
        uiCamera = c;
    }
}

