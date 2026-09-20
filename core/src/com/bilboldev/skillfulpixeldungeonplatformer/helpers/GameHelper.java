package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.OrthographicCamera;



public class GameHelper {


    private OrthographicCamera camera, uiCamera;
    private float impactTime, impactStrength, impactDirection;
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



    }

    public OrthographicCamera getCamera(){
        return camera;
    }

    public void setCamera(OrthographicCamera c){
        camera = c;
        clearHitImpulse();
    }

    public void showHitImpulse(float damageFraction, float direction) {
        if (GameSettingsHelper.getInstance().isReducedCameraMotion()) return;
        impactStrength = Math.max(impactStrength, Math.min(5f, 2f + damageFraction * 8f));
        impactDirection = direction;
        impactTime = 0.14f;
    }

    public void updateHitImpulse(float delta) {
        if (GameSettingsHelper.getInstance().isReducedCameraMotion()) { clearHitImpulse(); return; }
        impactTime = Math.max(0f, impactTime - delta);
        if (impactTime == 0f) impactStrength = 0f;
    }

    public float getHitImpulseX() {
        if (GameSettingsHelper.getInstance().isReducedCameraMotion() || impactTime <= 0f) return 0f;
        float remaining = impactTime / 0.14f;
        return impactDirection * impactStrength * remaining * remaining * (float) Math.cos((1f - remaining) * Math.PI * 3f);
    }

    public void clearHitImpulse() {
        impactTime = impactStrength = 0f;
    }

    public OrthographicCamera getUICamera(){
        return uiCamera;
    }

    public void setUICamera(OrthographicCamera c){
        uiCamera = c;
    }
}

