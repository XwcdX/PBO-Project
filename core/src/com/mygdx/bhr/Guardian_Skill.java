package com.mygdx.bhr;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Intersector;
import java.util.Iterator;

public class Guardian_Skill {
    private Vector2 center;
    private float bigCircleRadius;
    private float tinyCircleRadius;
    private float speed;
    private float angleOffset;
    private Texture tinyCircleTexture;
    private float[] tinyCircleAngles;
    private Circle[] tinyCircles;  // Add circles for collision detection
    private int damage;
    private bhr game;

    public Guardian_Skill(Texture tinyCircleTexture, bhr game) {
        this.center = new Vector2();
        this.bigCircleRadius = 80;
        this.tinyCircleRadius = 20;
        this.speed = .4f; // Rotation speed
        this.angleOffset = 0; // Initial angle
        this.tinyCircleTexture = tinyCircleTexture;
        this.tinyCircleAngles = new float[]{0, 120, 240}; // Initial angles for the tiny circles
        this.tinyCircles = new Circle[3]; // Initialize tiny circles
        for (int i = 0; i < tinyCircles.length; i++) {
            tinyCircles[i] = new Circle();
        }
        this.damage = 100;
        this.game=game;
    }

    public void update(float deltaTime, Vector2 heroPosition) {
        center.set(heroPosition);
        angleOffset += speed * deltaTime * 360; // Increment angle offset

        for (int i = 0; i < tinyCircleAngles.length; i++) {
            tinyCircleAngles[i] += speed * deltaTime * 360;
            float angleInRadians = (angleOffset + tinyCircleAngles[i]) * (float) Math.PI / 180;
            float x = center.x + bigCircleRadius * (float) Math.cos(angleInRadians);
            float y = center.y + bigCircleRadius * (float) Math.sin(angleInRadians);
            tinyCircles[i].set(x, y, tinyCircleRadius); // Update tiny circle position and size
        }
    }

    public void draw(SpriteBatch batch) {
        for (Circle circle : tinyCircles) {
            batch.draw(tinyCircleTexture, circle.x - circle.radius, circle.y - circle.radius, circle.radius * 2, circle.radius * 2);
        }
    }

    public void checkCollisions(Iterator<Enemies> enemiesIterator) {
        while (enemiesIterator.hasNext()) {
            Enemies enemy = enemiesIterator.next();
            for (Circle circle : tinyCircles) {
                if (Intersector.overlaps(circle, enemy.polygon.getBoundingRectangle())) {
                    enemy.takeDamage(damage);
                    if (!enemy.isAlive()) {
                        enemiesIterator.remove();
                        game.spawnCrystals(enemy);
                    }
                }
            }
        }
    }
}
