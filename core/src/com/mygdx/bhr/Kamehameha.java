package com.mygdx.bhr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Iterator;

public class Kamehameha {
    private Animation<TextureRegion> texture;
    private Vector2 position;
    private Vector2 direction;
    private Vector2 tempVector;
    private float width;
    private float height;
    private Polygon polygon;
    private int damage;
    private bhr game;
    private boolean active;
    private float cooldownTimer;
    private float COOLDOWN_DURATION = 6.0f;
    private float ACTIVE_DURATION = 0.5f;
    private float activeTimer;
    private ShapeRenderer shapeRenderer =new ShapeRenderer();
    private float stateTime;

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setCOOLDOWN_DURATION(float COOLDOWN_DURATION) {
        this.COOLDOWN_DURATION = COOLDOWN_DURATION;
    }

    public void setACTIVE_DURATION(float ACTIVE_DURATION) {
        this.ACTIVE_DURATION = ACTIVE_DURATION;
    }

    public Kamehameha(Animation<TextureRegion> texture, bhr game) {
        this.texture = texture;
        this.position = new Vector2();
        this.direction = new Vector2();
        this.tempVector = new Vector2(); // Temporary vector for scaling operations
        this.width = 150;
        this.height = 15;
        this.polygon = new Polygon();
        this.damage = 100;
        this.game = game;
        this.active = false;
        this.cooldownTimer = 0; // Start on cooldown
        this.activeTimer = 0;
        this.stateTime = 0;
    }

    public void update(float deltaTime, Vector2 heroPosition) {
        cooldownTimer -= deltaTime;

        if (active) {
            activeTimer -= deltaTime;
            stateTime +=deltaTime;
            if (activeTimer <= 0) {
                active = false;
                stateTime = 0;
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
        stateTime = 0;
        Vector3 mousePosition3 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        game.getCamera().unproject(mousePosition3);
        Vector2 mousePosition = new Vector2(mousePosition3.x, mousePosition3.y);

        // Calculate direction from hero to mouse
        direction.set(mousePosition).sub(heroPosition).nor();

        // Use temporary vector for scaling
        tempVector.set(direction).scl(width / 2f);

        // Set initial position of the kamehameha
        position.set(heroPosition).add(tempVector);

        // Update the polygon
        updatePolygon();
    }

    private void updatePolygon() {
        float originX = position.x;
        float originY = position.y;

        // Calculate the corners of the rectangle based on the direction
        float halfWidth = width / 2;
        float halfHeight = height / 2;

        // Rotate corners around the origin based on the direction angle
        Vector2 corner1 = new Vector2(-halfWidth, -halfHeight);
        Vector2 corner2 = new Vector2(halfWidth, -halfHeight);
        Vector2 corner3 = new Vector2(halfWidth, halfHeight);
        Vector2 corner4 = new Vector2(-halfWidth, halfHeight);

        float[] vertices = {
                originX + corner1.x, originY + corner1.y,
                originX + corner2.x, originY + corner2.y,
                originX + corner3.x, originY + corner3.y,
                originX + corner4.x, originY + corner4.y
        };

        polygon.setVertices(vertices);
        polygon.setOrigin(originX, originY);
        polygon.setRotation(direction.angleDeg());
    }


    public void draw(SpriteBatch batch) {
        if (active) {
            // Calculate the center of the rectangle for rotation
            float originX = width / 2;
            float originY = height / 2;
            TextureRegion currentFrame = texture.getKeyFrame(stateTime,true);
            // Draw the texture with rotation around the center of the rectangle
            batch.draw(currentFrame.getTexture(),
                    position.x - originX, position.y - originY, // Position
                    originX, originY, // Origin for rotation
                    width, height, // Width and height
                    1.3f, 3, // Scale
                    direction.angleDeg(), // Rotation
                    0, 0, // Texture coordinates
                    currentFrame.getRegionWidth(), currentFrame.getRegionHeight(), // Texture size
                    false, false); // Flip horizontally and vertically

            // Debugging: Confirm drawing
            //System.out.println("Drawing Kamehameha at: " + (position.x - originX) + ", " + (position.y - originY));

            // Draw polygon for debugging
//            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//            shapeRenderer.setColor(Color.RED);
//            shapeRenderer.polygon(polygon.getTransformedVertices());
//            shapeRenderer.end();
        }
    }

    public void checkCollisions(Iterator<Enemies> enemiesIterator) {
        if (active) {
            while (enemiesIterator.hasNext()) {
                Enemies enemy = enemiesIterator.next();
                if (enemy.polygon != null && Intersector.overlapConvexPolygons(polygon, enemy.polygon)) {
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
