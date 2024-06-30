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
    private float[] tinyCircleAngles; // Angles of the tiny circles
    private Circle[] tinyCircles; // Circles for collision detection
    private int damage; // Damage dealt by the tiny circles
    private bhr game; // Reference to the game instance
    private final Animation<TextureRegion> circleAnimation;
    private int numberOfCircles;

    public void setNumberOfCircles(int numberOfCircles) {
        this.numberOfCircles = numberOfCircles;
        this.tinyCircleAngles = new float[numberOfCircles];
        for (int i = 0; i < numberOfCircles; i++) {
            this.tinyCircleAngles[i] = i * (360.0f / numberOfCircles);
        }
        this.tinyCircles = new Circle[numberOfCircles];
        for (int i = 0; i < tinyCircles.length; i++) {
            tinyCircles[i] = new Circle();
        }
//        System.out.println("Number of circles set to " + numberOfCircles);
//        for (int i = 0; i < numberOfCircles; i++) {
//            System.out.println("Initialized circle " + i + " at (" + tinyCircles[i].x + ", " + tinyCircles[i].y + ")");
//        }
    }

    public Guardian_Skill(Animation<TextureRegion> tinyCircleTexture, bhr game, int initialNumberOfCircles) {
        this.center = new Vector2();
        this.bigCircleRadius = 80;
        this.tinyCircleRadius = 20;
        this.speed = .1f; // Rotation speed
        this.angleOffset = 0; // Initial angle
        this.numberOfCircles = initialNumberOfCircles;
        this.tinyCircleAngles = new float[initialNumberOfCircles];
        for (int i = 0; i < initialNumberOfCircles; i++) {
            this.tinyCircleAngles[i] = i * (360.0f / initialNumberOfCircles);
        }
        this.tinyCircles = new Circle[initialNumberOfCircles];
        for (int i = 0; i < tinyCircles.length; i++) {
            tinyCircles[i] = new Circle();
        }
        this.damage = 3;
        this.game = game;
        this.circleAnimation = tinyCircleTexture;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    // Update the position and angles of the tiny circles
    public void update(float deltaTime, Vector2 heroPosition) {
        center.set(heroPosition); // Set the center to the hero's position
        angleOffset += speed * deltaTime * 360; // Increment angle offset
//        System.out.println("Updating circles with heroPosition: (" + heroPosition.x + ", " + heroPosition.y + ")");

        // Update the positions and sizes of the tiny circles
        for (int i = 0; i < tinyCircleAngles.length; i++) {
            tinyCircleAngles[i] += speed * deltaTime * 360;
            float angleInRadians = (angleOffset + tinyCircleAngles[i]) * (float) Math.PI / 180;
            float x = center.x + bigCircleRadius * (float) Math.cos(angleInRadians);
            float y = center.y + bigCircleRadius * (float) Math.sin(angleInRadians);
            tinyCircles[i].set(x, y, tinyCircleRadius); // Update tiny circle position and size
//            System.out.println("Updated circle " + i + " to (" + x + ", " + y + ")");
//            System.out.println("Circle " + i + " state: (" + tinyCircles[i].x + ", " + tinyCircles[i].y + ")");
        }
    }

    // Draw the tiny circles on the screen
    public void draw(SpriteBatch batch, float animationTime) {
        TextureRegion currentFrame = circleAnimation.getKeyFrame(animationTime, true);
        for (int i = 0; i < tinyCircles.length; i++) {
            Circle circle = tinyCircles[i];
            if (circle.x != 0.0 && circle.y != 0.0) { // Check if coordinates are valid
                System.out.println("Drawing circle " + i + " at (" + circle.x + ", " + circle.y + ")");
                batch.draw(currentFrame, circle.x - circle.radius, circle.y - circle.radius, circle.radius * 2, circle.radius * 2);
            } else {
                System.out.println("Skipping circle " + i + " at (" + circle.x + ", " + circle.y + ")");
            }
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

    public Animation<TextureRegion> getCircleAnimation() {
        return circleAnimation;
    }
}
