package com.mygdx.bhr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class Enemy_Bomb {
    private Texture texture;
    private Vector2 position;
    private Circle circle;
    private static final float INITIAL_RADIUS = 10f;
    private static final float EXPLOSION_RADIUS = 50f;
    private static final long EXPLOSION_DELAY = 1000000000L; // 1 second in nanoseconds
    private boolean exploded;
    private long activationTime;
    private long explosionTime;
    private boolean shouldRemove;
    private boolean heroDamaged;

    public Enemy_Bomb(Vector2 position) {
        this.texture = new Texture(Gdx.files.internal("skull_icon.png"));
        this.position = position;
        this.circle = new Circle(position.x, position.y, INITIAL_RADIUS);
        this.exploded = false;
        this.activationTime = TimeUtils.nanoTime();
        this.explosionTime = 0;
        this.shouldRemove = false;
        this.heroDamaged = false;
    }

    public void update() {
        if (!exploded && TimeUtils.nanoTime() - activationTime > EXPLOSION_DELAY) {
            explode();
        }
        if (exploded && !shouldRemove && TimeUtils.nanoTime() - explosionTime > 100000000L) {
            shouldRemove = true;
        }
    }

    private void explode() {
        exploded = true;
        circle.setRadius(EXPLOSION_RADIUS);
        explosionTime = TimeUtils.nanoTime();
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x - circle.radius, position.y - circle.radius, circle.radius * 2, circle.radius * 2);
    }

    public boolean shouldRemove() {
        return shouldRemove;
    }

    public boolean isExploded() {
        return exploded;
    }

    public Circle getCircle() {
        return circle;
    }
    public boolean hasHeroBeenDamaged() {
        return heroDamaged;
    }
    public void setHeroDamaged(boolean heroDamaged) {
        this.heroDamaged = heroDamaged;
    }
}
