package com.mygdx.bhr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Intersector;

import java.util.Iterator;

public class Kamehameha {
    private Texture texture;
    private Vector2 position;
    private Vector2 direction;
    private float width;
    private float height;
    private Rectangle rectangle;
    private int damage;
    private bhr game;
    private boolean active;
    private float cooldownTimer;
    private static final float COOLDOWN_DURATION = 1.0f;
    private ShapeRenderer shapeRenderer;

    public Kamehameha(Texture texture, bhr game) {
        this.texture = texture;
        this.position = new Vector2();
        this.direction = new Vector2();
        this.width = 500;
        this.height = 50;
        this.rectangle = new Rectangle();
        this.damage = 200;
        this.game = game;
        this.active = false;
        this.cooldownTimer = 0;
//        this.shapeRenderer = new ShapeRenderer();
    }

    public void update(Vector2 heroPosition) {
        Vector3 mousePosition3 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        game.getCamera().unproject(mousePosition3);
        Vector2 mousePosition = new Vector2(mousePosition3.x, mousePosition3.y);

        // Calculate direction from hero to mouse
        direction.set(mousePosition).sub(heroPosition).nor();

        // Update the rectangle position based on direction
        position.set(heroPosition).add(direction.scl(width/2));

        // Set rectangle position
        rectangle.set(position.x, position.y, width, height);
    }

    public void draw(SpriteBatch batch) {
        // Calculate the center of the rectangle for rotation
        float originX = rectangle.width / 2;
        float originY = rectangle.height / 2;

        // Draw the texture with rotation around the center of the rectangle
        batch.draw(texture,
                rectangle.x - originX, rectangle.y - originY, // Position
                originX, originY, // Origin for rotation
                rectangle.width, rectangle.height, // Width and height
                1, 1, // Scale
                direction.angleDeg(), // Rotation
                0, 0, // Texture coordinates
                texture.getWidth(), texture.getHeight(), // Texture size
                false, false); // Flip horizontally and vertically

//        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(1, 0, 0, 1); // Set the color to red
//        shapeRenderer.rect(rectangle.x - originX, rectangle.y - originY, originX, originY, rectangle.width, rectangle.height, 1, 1, direction.angleDeg());
//        shapeRenderer.end();
    }

    public void checkCollisions(Iterator<Enemies> enemiesIterator) {
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
