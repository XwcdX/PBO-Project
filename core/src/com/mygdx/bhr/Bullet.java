package com.mygdx.bhr;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    Circle circle;
    private Vector2 realPosition;
    private final Vector2 initialPosition;
    private final Vector2 direction;
    private final int WORLD_WIDTH;
    private final int WORLD_HEIGHT;
    private float stateTime;
    public Bullet(float x, float y, Vector2 direction, int worldWidth, int worldHeight) {
        this.circle = new Circle(x, y, 5); // Radius of the bullet is 5
        this.initialPosition = new Vector2(x, y);
        this.realPosition = new Vector2(x, y);
        this.direction = new Vector2(direction).nor(); // Ensure direction is normalized
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;
        this.stateTime = 0f;
    }

    public void update(float deltaTime) {
        float speed = 1000f;
        realPosition.x += direction.x * speed * deltaTime;
        realPosition.y += direction.y * speed * deltaTime;

        // Wrap around the world
        if (realPosition.x < 0) realPosition.x += WORLD_WIDTH;
        if (realPosition.x >= WORLD_WIDTH) realPosition.x -= WORLD_WIDTH;
        if (realPosition.y < 0) realPosition.y += WORLD_HEIGHT;
        if (realPosition.y >= WORLD_HEIGHT) realPosition.y -= WORLD_HEIGHT;

        // Update the circle position for rendering
        circle.x = realPosition.x;
        circle.y = realPosition.y;
    }

    public boolean hasExceededRange() {
        float maxRange = 400f;

        // Calculate the shortest distance considering world wrapping
        float dx = Math.abs(realPosition.x - initialPosition.x);
        float dy = Math.abs(realPosition.y - initialPosition.y);

        if (dx > WORLD_WIDTH / 2f) dx = WORLD_WIDTH - dx;
        if (dy > WORLD_HEIGHT / 2f) dy = WORLD_HEIGHT - dy;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance > maxRange;
    }

    public float getRotation() {
        return direction.angleDeg();
    }
    public float getStateTime() {
        return stateTime;
    }
    public void updates(float deltaTime){
        stateTime += deltaTime;
    }
}
