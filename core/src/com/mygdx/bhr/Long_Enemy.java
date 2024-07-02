package com.mygdx.bhr;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Long_Enemy extends Enemies {
    private static final float STOP_DISTANCE = 300f;
    private static final float SHOOT_INTERVAL = 10f;
    private float timeSinceLastShot = 10f;

    public Long_Enemy(Polygon polygon, int worldWidth, int worldHeight) {
        super(polygon, worldWidth, worldHeight);
        this.hp = 70;
        super.ATTACK_DAMAGE = 30;
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

        timeSinceLastShot += deltaTime;
        if (distance <= STOP_DISTANCE && timeSinceLastShot >= SHOOT_INTERVAL) {
            shootBullet(heroPolygon);
            timeSinceLastShot = 0f;
        }

        stateTime += deltaTime;

        if (doneCollision) {
            lastCollision += deltaTime;
            checkCollision();
        }
    }

    private void shootBullet(Polygon heroPolygon) {
        Vector2 bulletDirection = shortestDirection(polygon, heroPolygon).nor();
        Vector2 bulletPosition = new Vector2(polygon.getBoundingRectangle().x, polygon.getBoundingRectangle().y);
        float BULLET_SPEED = 150f;
        Enemy_Bullet bullet = new Enemy_Bullet(bulletPosition, bulletDirection.scl(BULLET_SPEED));
        // Add bullet to a global or class-level list to manage its updates and rendering
        bhr.enemyBullets.add(bullet);
    }
}
