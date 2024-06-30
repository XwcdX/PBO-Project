package com.mygdx.bhr;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Intersector;
import java.util.Iterator;

public class Guardian_Skill {
    private Vector2 center; // Center position of the hero
    private float bigCircleRadius; // Radius of the big circle
    private float tinyCircleRadius; // Radius of the tiny circles
    private float speed; // Rotation speed of the tiny circles
    private float angleOffset; // Initial angle offset
    private Animation<TextureRegion> tinyCircleTexture; // Texture for the tiny circles
    private float[] tinyCircleAngles; // Angles of the tiny circles
    private Circle[] tinyCircles; // Circles for collision detection
    private int damage; // Damage dealt by the tiny circles
    private bhr game; // Reference to the game instance
    private Animation<TextureRegion> circleAnimation;
    public Guardian_Skill(Animation<TextureRegion> tinyCircleTexture, bhr game) {
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
        this.damage = 3;
        this.game = game;
    }

    public void setTinyCircleAngles(float[] tinyCircleAngles) {
        this.tinyCircleAngles = tinyCircleAngles;
    }

    public void setTinyCircles(Circle[] tinyCircles) {
        this.tinyCircles = tinyCircles;
    }

    // Update the position and angles of the tiny circles
    public void update(float deltaTime, Vector2 heroPosition,float animationTime) {
        center.set(heroPosition); // Set the center to the hero's position
        angleOffset += speed * deltaTime * 360; // Increment angle offset

        // Update the positions and sizes of the tiny circles
        for (int i = 0; i < tinyCircleAngles.length; i++) {
            tinyCircleAngles[i] += speed * deltaTime * 360;
            float angleInRadians = (angleOffset + tinyCircleAngles[i]) * (float) Math.PI / 180;
            float x = center.x + bigCircleRadius * (float) Math.cos(angleInRadians);
            float y = center.y + bigCircleRadius * (float) Math.sin(angleInRadians);
            tinyCircles[i].set(x, y, tinyCircleRadius); // Update tiny circle position and size
        }
    }

    // Draw the tiny circles on the screen
    public void draw(SpriteBatch batch,float animationTime) {
        TextureRegion currentFrame = tinyCircleTexture.getKeyFrame(animationTime,true);
        for (Circle circle : tinyCircles) {
            batch.draw(currentFrame, circle.x - circle.radius, circle.y - circle.radius, circle.radius * 2, circle.radius * 2);
        }
    }

    // Check for collisions between the tiny circles and enemies
    public void checkCollisions(Iterator<Enemies> enemiesIterator) {
        while (enemiesIterator.hasNext()) {
            Enemies enemy = enemiesIterator.next();
            for (Circle circle : tinyCircles) {
                // Check for collisions and handle enemy damage
                if (enemy.polygon != null && Intersector.overlaps(circle, enemy.polygon.getBoundingRectangle())) {
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
