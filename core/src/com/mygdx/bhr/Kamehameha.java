package com.mygdx.bhr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Intersector;

import java.util.Iterator;

public class Kamehameha {
    private Texture texture;
    private Vector2 position;
    private Vector2 direction;
    private Vector2 tempVector;
    private float width;
    private float height;
    private Rectangle rectangle;
    private int damage;
    private bhr game;
    private boolean active;
    private float cooldownTimer;
    private static final float COOLDOWN_DURATION = 5.0f;
    private static final float ACTIVE_DURATION = 0.5f; // 0.5 seconds
    private float activeTimer;

    public Kamehameha(Texture texture, bhr game) {
        this.texture = texture;
        this.position = new Vector2();
        this.direction = new Vector2();
        this.tempVector = new Vector2(); // Temporary vector for scaling operations
        this.width = 300;
        this.height = 50;
        this.rectangle = new Rectangle();
        this.damage = 200;
        this.game = game;
        this.active = false;
        this.cooldownTimer = 0; // Start on cooldown
        this.activeTimer = 0;
    }

    public void update(float deltaTime, Vector2 heroPosition) {
        cooldownTimer -= deltaTime;

        if (active) {
            activeTimer -= deltaTime;
            if (activeTimer <= 0) {
                active = false;
            }
        }

        if (!active && cooldownTimer <= 0) {
            activate(heroPosition);
            cooldownTimer = COOLDOWN_DURATION;
            activeTimer = ACTIVE_DURATION;
        }
    }

    private void activate(Vector2 heroPosition) {
        active = true;
        Vector3 mousePosition3 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        game.getCamera().unproject(mousePosition3);
        Vector2 mousePosition = new Vector2(mousePosition3.x, mousePosition3.y);

        // Calculate direction from hero to mouse
        direction.set(mousePosition).sub(heroPosition).nor();

        // Debugging: Print out direction and position
//        System.out.println("Kamehameha activated!");
//        System.out.println("Mouse Position: " + mousePosition);
//        System.out.println("Hero Position: " + heroPosition);
//        System.out.println("Direction: " + direction);

        // Use temporary vector for scaling
        tempVector.set(direction).scl(width / 2f);

        // Set initial position of the rectangle
        position.set(heroPosition).add(tempVector);
        rectangle.set(position.x, position.y, width, height);

        // Debugging: Print out initial rectangle position
//        System.out.println("Initial Rectangle Position: " + rectangle.x + ", " + rectangle.y);
    }

    public void draw(SpriteBatch batch) {
        if (active) {
            // Calculate the center of the rectangle for rotation
            float originX = width / 2;
            float originY = height / 2;

            // Draw the texture with rotation around the center of the rectangle
            batch.draw(texture,
                    position.x - originX, position.y - originY, // Position
                    originX, originY, // Origin for rotation
                    width, height, // Width and height
                    1, 1, // Scale
                    direction.angleDeg(), // Rotation
                    0, 0, // Texture coordinates
                    texture.getWidth(), texture.getHeight(), // Texture size
                    false, false); // Flip horizontally and vertically

            // Debugging: Confirm drawing
//            System.out.println("Drawing Kamehameha at: " + (position.x - originX) + ", " + (position.y - originY));
        }
    }

    public void checkCollisions(Iterator<Enemies> enemiesIterator) {
        if (active) {
            while (enemiesIterator.hasNext()) {
                Enemies enemy = enemiesIterator.next();
                if (enemy.polygon != null && Intersector.overlaps(rectangle, enemy.polygon.getBoundingRectangle())) {
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
