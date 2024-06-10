package com.mygdx.bhr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Heroes implements hasHP, canShoot {
    Polygon polygon;
    private final int WORLD_WIDTH;
    private final int WORLD_HEIGHT;
    private int hp;
    private final Array<Bullet> bullets;
    private final Vector2 direction;
    private final Vector2 lastDirection;
    private long lastAttackTime;

    public Heroes(int worldWidth, int worldHeight) {
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;
        this.polygon = createPolygon((float) WORLD_WIDTH / 2 - 32, (float) WORLD_HEIGHT / 2 - 32, 64, 64);
        this.hp = 100;
        this.bullets = new Array<>();
        this.direction = new Vector2(0, 0);
        this.lastDirection = new Vector2(1, 0); // Default direction is to the right
        this.lastAttackTime = TimeUtils.nanoTime();
    }

    private Polygon createPolygon(float x, float y, float width, float height) {
        float[] vertices = {0, 0, width, 0, width, height, 0, height};
        Polygon polygon = new Polygon(vertices);
        polygon.setPosition(x, y);
        return polygon;
    }

    public void update(float deltaTime) {
        direction.set(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.A)) direction.add(-1, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) direction.add(1, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) direction.add(0, -1);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) direction.add(0, 1);

        if (!direction.isZero()) {
            lastDirection.set(direction).nor(); // Update last direction if there's movement
        }

        direction.nor(); // Normalize the direction vector to ensure consistent speed

        polygon.translate(direction.x * 300 * deltaTime, direction.y * 300 * deltaTime);

        // Auto attack every 0.5 seconds (500,000,000 nanoseconds)
        long attackInterval = 500000000;
        if (TimeUtils.nanoTime() - lastAttackTime > attackInterval) {
            shoot();
            lastAttackTime = TimeUtils.nanoTime();
        }

        float[] vertices = polygon.getTransformedVertices();
        float x = vertices[0];
        float y = vertices[1];

        if (x < 0) polygon.translate(WORLD_WIDTH, 0);
        if (x >= WORLD_WIDTH) polygon.translate(-WORLD_WIDTH, 0);
        if (y < 0) polygon.translate(0, WORLD_HEIGHT);
        if (y >= WORLD_HEIGHT) polygon.translate(0, -WORLD_HEIGHT);

        for (Iterator<Bullet> iter = bullets.iterator(); iter.hasNext(); ) {
            Bullet bullet = iter.next();
            bullet.update(deltaTime);
            if (bullet.hasExceededRange()) {
                iter.remove();
            }
        }
    }

    public float getX() {
        return polygon.getTransformedVertices()[0];
    }

    public float getY() {
        return polygon.getTransformedVertices()[1];
    }

    @Override
    public void takeDamage(int damage) {
        hp -= damage;
    }

    @Override
    public int getHP() {
        return hp;
    }

    @Override
    public boolean isAlive() {
        return hp > 0;
    }

    @Override
    public void shoot() {
        float[] vertices = polygon.getTransformedVertices();
        float x = vertices[0] + polygon.getBoundingRectangle().width / 2;
        float y = vertices[1] + polygon.getBoundingRectangle().height / 2;
        bullets.add(new Bullet(x, y, new Vector2(lastDirection), WORLD_WIDTH, WORLD_HEIGHT));
    }

    @Override
    public Array<Bullet> getBullets() {
        return bullets;
    }
}
