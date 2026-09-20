package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;


public final class MenuUiFade implements Disposable {
    private ShaderProgram fade, previous;
    private boolean active;

    public void begin(Batch batch, float opacity) {
        if (active) throw new IllegalStateException("Menu fade already active");
        if (opacity >= 1f) return;
        if (fade == null) {
            fade = new ShaderProgram(
                    "attribute vec4 a_position; attribute vec4 a_color; attribute vec2 a_texCoord0;\n"
                    + "uniform mat4 u_projTrans; varying vec4 v_color; varying vec2 v_texCoords;\n"
                    + "void main(){v_color=a_color;v_color.a*=255.0/254.0;v_texCoords=a_texCoord0;gl_Position=u_projTrans*a_position;}",
                    "#ifdef GL_ES\nprecision mediump float;\n#endif\n"
                    + "varying vec4 v_color; varying vec2 v_texCoords; uniform sampler2D u_texture; uniform float u_menuAlpha;\n"
                    + "void main(){gl_FragColor=v_color*texture2D(u_texture,v_texCoords);gl_FragColor.a*=u_menuAlpha;}");
            if (!fade.isCompiled()) {
                String log = fade.getLog();
                fade.dispose();
                fade = null;
                throw new IllegalStateException("Menu fade shader: " + log);
            }
        }
        previous = batch.getShader();
        batch.setShader(fade);
        fade.setUniformf("u_menuAlpha", Math.max(0f, opacity));
        active = true;
    }

    public void end(Batch batch) {
        if (!active) return;
        batch.setShader(previous);
        previous = null;
        active = false;
    }

    @Override public void dispose() { if (fade != null) { fade.dispose(); fade = null; } }
}
