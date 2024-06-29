package com.mygdx.bhr;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Circle;

public class Enemy_Bullet {
    private final Vector2 position;
    private final Vector2 velocity;
    private static final float RADIUS = 4f;
    public static final float LIFESPAN = 3.0f;
    private float timeAlive;

    public Enemy_Bullet(Vector2 position, Vector2 velocity) {
        this.position = position;
        this.velocity = velocity;
        this.timeAlive = 0f;
    }

    public void update(float deltaTime, Polygon heroPolygon) {
        timeAlive += deltaTime;
        if (timeAlive > LIFESPAN) {
            return;
        }
        Vector2 heroCenter = getPolygonCenter(heroPolygon);
        System.out.println(heroCenter.x + " " + heroCenter.y);

        Vector2 directionToHero = new Vector2(
                heroCenter.x - position.x,
                heroCenter.y - position.y
        ).nor();

        velocity.set(directionToHero.scl(150));
        position.add(velocity.scl(deltaTime));
    }

    private Vector2 getPolygonCenter(Polygon polygon) {
        float[] vertices = polygon.getTransformedVertices();
        float xSum = 0, ySum = 0;
        for (int i = 0; i < vertices.length; i += 2) {
            xSum += vertices[i];
            ySum += vertices[i + 1];
        }
        float centerX = xSum / ((float) vertices.length / 2);
        float centerY = ySum / ((float) vertices.length / 2);
        return new Vector2(centerX, centerY);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Circle getBoundingCircle() {
        return new Circle(position.x, position.y, RADIUS);
    }
    public float getTimeAlive() {
        return timeAlive;
    }
}
