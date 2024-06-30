package com.mygdx.bhr;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class BossSpawner_Enemy extends Enemies {
    private static final float STOP_DISTANCE = 300f;
    private final Array<Enemies> summonedEnemies;

    public BossSpawner_Enemy(Polygon polygon, int worldWidth, int worldHeight, Array<Enemies> summonedEnemies) {
        super(polygon, worldWidth, worldHeight);
        this.summonedEnemies = summonedEnemies;
    }

    public void summonEnemies(Polygon heroPolygon) {
        int numberOfEnemies = 8; // Number of enemies to summon
        float radius = 200f; // Radius of the circle around the hero

        Vector2 heroCenter = new Vector2(heroPolygon.getBoundingRectangle().x + heroPolygon.getBoundingRectangle().width / 2,
                heroPolygon.getBoundingRectangle().y + heroPolygon.getBoundingRectangle().height / 2);

        for (int i = 0; i < numberOfEnemies; i++) {
            float angle = (float) (i * 2 * Math.PI / numberOfEnemies);
            float x = heroCenter.x + radius * (float) Math.cos(angle);
            float y = heroCenter.y + radius * (float) Math.sin(angle);

            Polygon enemyPolygon = new Polygon(new float[]{0, 0, 64, 0, 64, 64, 0, 64});
            enemyPolygon.setPosition(x, y);

            Enemies enemy;
            if (i % 2 == 0) {
                enemy = new Long_Enemy(enemyPolygon, WORLD_WIDTH, WORLD_HEIGHT);
            } else {
                enemy = new Bomber_Enemy(enemyPolygon, WORLD_WIDTH, WORLD_HEIGHT);
            }

            summonedEnemies.add(enemy);
        }
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

        if (distance <= STOP_DISTANCE && getStateTime() >= 30f) {
            summonEnemies(heroPolygon);
            resetStateTime();
        }
    }
}
