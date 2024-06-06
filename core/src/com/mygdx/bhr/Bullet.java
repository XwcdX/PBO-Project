package com.mygdx.bhr;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    Circle circle;
    private final Vector2 initialPosition;
    private final Vector2 direction;
    private final int WORLD_WIDTH;
    private final int WORLD_HEIGHT;

    public Bullet(float x, float y, Vector2 direction, int worldWidth, int worldHeight) {
        this.circle = new Circle(x, y, 5); // Radius of the bullet is 5
        this.initialPosition = new Vector2(x, y);
        this.direction = new Vector2(direction).nor(); // Ensure direction is normalized
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;
    }

    public void update(float deltaTime) {
        float speed = 500f;
        circle.x += direction.x * speed * deltaTime;
        circle.y += direction.y * speed * deltaTime;
        wrapAroundWorld();
    }

    private void wrapAroundWorld() {
        if (circle.x < 0) circle.x += WORLD_WIDTH;
        if (circle.x >= WORLD_WIDTH) circle.x -= WORLD_WIDTH;
        if (circle.y < 0) circle.y += WORLD_HEIGHT;
        if (circle.y >= WORLD_HEIGHT) circle.y -= WORLD_HEIGHT;
    }

    public boolean hasExceededRange() {
        float maxRange = 400f;
        return initialPosition.dst(circle.x, circle.y) > maxRange;
    }

    public float getRotation() {
        return direction.angleDeg();
    }
}
