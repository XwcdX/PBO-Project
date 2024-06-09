package com.mygdx.bhr;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;

public class Crystal {
    public Polygon polygon;
    public boolean collected;
    public Animation<TextureRegion>animation;
    private float stateTime;
    public Crystal(float x,float y,float width,float height,Animation<TextureRegion> animation){
        float[] vertex = {0,0,width,0,width,height,0,height};
        polygon = new Polygon(vertex);
        polygon.setPosition(x,y);
        collected = false;
        this.animation = animation;
    }
    public void update(float deltaTime) {
        if (!collected) {
            stateTime += deltaTime;
        }
    }

    public Texture getTexture() {
        return animation.getKeyFrame(stateTime).getTexture();
    }
    public void updateStateTime(float delta) {
        stateTime += delta;
    }
}
