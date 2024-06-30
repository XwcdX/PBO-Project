package com.mygdx.bhr;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Bomber_Enemy extends Enemies {
    private static final float STOP_DISTANCE = 100f;
    private static final float BOMB_PLANT_INTERVAL = 5f;
    private float timeSinceLastBomb = 5f;

    public Bomber_Enemy(Polygon polygon, int worldWidth, int worldHeight) {
        super(polygon, worldWidth, worldHeight);
    }

    @Override
    public void update(float deltaTime, Polygon heroPolygon) {
        Vector2 direction = shortestDirection(polygon, heroPolygon);
        float distance = direction.len();

        if (distance > STOP_DISTANCE) {
            direction.nor();
            velocity.set(direction.scl(50));
            polygon.translate(velocity.x * deltaTime, velocity.y * deltaTime);
        }

        wrapAroundWorld();

        timeSinceLastBomb += deltaTime;
        if (distance <= STOP_DISTANCE && timeSinceLastBomb >= BOMB_PLANT_INTERVAL) {
            plantBomb(heroPolygon);
            timeSinceLastBomb = 0f;
        }
    }

    private void plantBomb(Polygon heroPolygon) {
        Vector2 bombPosition = getPolygonCenter(heroPolygon);
        Enemy_Bomb bomb = new Enemy_Bomb(bombPosition);
        bhr.enemyBombs.add(bomb);
    }

    private Vector2 getPolygonCenter(Polygon heroPolygon) {
        float[] vertices = heroPolygon.getTransformedVertices();
        float xSum = 0, ySum = 0;
        for (int i = 0; i < vertices.length; i += 2) {
            xSum += vertices[i];
            ySum += vertices[i + 1];
        }
        float centerX = xSum / ((float) vertices.length / 2);
        float centerY = ySum / ((float) vertices.length / 2);
        return new Vector2(centerX, centerY);
    }
}
