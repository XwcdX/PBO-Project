package com.mygdx.bhr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;
import java.util.Random;

public class Mine_Bomb {
    private Texture texture;
    private Vector2 position;
    private Circle circle;
    private int damage;
    private bhr game;
    private boolean active;
    private boolean exploded;
    private long activationTime;
    private long EXPLOSION_DELAY = 4000000000L;
    private float COOLDOWN_DURATION = 7.0f;
    private float cooldownTimer;
    private Random rand;
    private ShapeRenderer shapeRenderer;
    private float INITIAL_RADIUS = 30;
    private float EXPLOSION_RADIUS = 60;
    private int EXPLOSION_DAMAGE = 25;

    public void setInitialRadius(float initialRadius) {
        INITIAL_RADIUS = initialRadius;
    }

    public void setExplosionRadius(float explosionRadius) {
        EXPLOSION_RADIUS = explosionRadius;
    }

    public void setExplosionDamage(int explosionDamage) {
        EXPLOSION_DAMAGE = explosionDamage;
    }

    public void setExplosionDelay(long explosionDelay) {
        EXPLOSION_DELAY = explosionDelay;
    }

    public void setCooldownDuration(float cooldownDuration) {
        COOLDOWN_DURATION = cooldownDuration;
    }

    public Mine_Bomb(Texture texture, bhr game) {
        this.texture = texture;
        this.position = new Vector2();
        this.circle = new Circle();
        this.damage = EXPLOSION_DAMAGE;
        this.game = game;
        this.active = false;
        this.exploded = false;
        this.activationTime = 0;
        this.cooldownTimer = 0;
        this.rand = new Random();
        this.shapeRenderer = new ShapeRenderer();
    }

    public void update(float deltaTime) {
        cooldownTimer -= deltaTime;

        if (!active && cooldownTimer <= 0) {
            activate();
            cooldownTimer = COOLDOWN_DURATION;
        }

        if (active && TimeUtils.nanoTime() - activationTime > EXPLOSION_DELAY) {
            explode();
        }
    }

    private void activate() {
        active = true;
        exploded = false;

        // Calculate a random position within the camera's viewport
        float viewportWidth = game.getCamera().viewportWidth;
        float viewportHeight = game.getCamera().viewportHeight;
        float cameraX = game.getCamera().position.x - viewportWidth / 2;
        float cameraY = game.getCamera().position.y - viewportHeight / 2;

        float randomX = cameraX + INITIAL_RADIUS + rand.nextFloat() * (viewportWidth - 2 * INITIAL_RADIUS);
        float randomY = cameraY + INITIAL_RADIUS + rand.nextFloat() * (viewportHeight - 2 * INITIAL_RADIUS);

        position.set(randomX, randomY);
        circle.set(position.x, position.y, INITIAL_RADIUS); // Set initial radius
        activationTime = TimeUtils.nanoTime();
    }

    private void explode() {
        exploded = true;
        active = false;
        circle.setRadius(EXPLOSION_RADIUS); // Increase radius on explosion

        // Reset radius back to initial after explosion for next activation
        Gdx.app.postRunnable(() -> circle.setRadius(INITIAL_RADIUS));
    }

    public void draw(SpriteBatch batch) {
        if (active || exploded) {
            batch.draw(texture, position.x - circle.radius, position.y - circle.radius, circle.radius * 2, circle.radius * 2);
        }
    }

    public void debugDraw() {
        if (active || exploded) {
            shapeRenderer.setProjectionMatrix(game.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.circle(circle.x, circle.y, circle.radius);
            shapeRenderer.end();
        }
    }

    public void checkCollisions(Iterator<Enemies> enemiesIterator) {
        if (exploded) {
            while (enemiesIterator.hasNext()) {
                Enemies enemy = enemiesIterator.next();
                if (enemy.polygon != null && Intersector.overlaps(circle, enemy.polygon.getBoundingRectangle())) {
                    enemy.takeDamage(damage);
                    if (!enemy.isAlive()) {
                        enemiesIterator.remove();
                        game.spawnCrystals(enemy);
                    }
                }
            }
            exploded = false;
        }
    }
}
