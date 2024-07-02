package com.mygdx.bhr;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Enemies implements hasHP {
    Polygon polygon;
    Vector2 velocity;
    protected final int WORLD_WIDTH;
    protected int ATTACK_DAMAGE = 10;
    protected boolean attack = false;
    protected final int WORLD_HEIGHT;
    protected int hp;
    protected float stateTime;
    protected boolean doneCollision;
    protected float lastCollision;
    private boolean isMovingLeft; // Flag to track if enemy is moving left

    public Enemies(Polygon polygon, int worldWidth, int worldHeight) {
        this.polygon = polygon;
        this.velocity = new Vector2();
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;
        this.hp = 100;
        this.stateTime = 30f;
        this.doneCollision = false;
        this.isMovingLeft = false; // Initialize as not moving left
    }

    public boolean isDoneCollision() {
        return doneCollision;
    }

    public void setDoneCollision(boolean doneDamage) {
        this.doneCollision = doneDamage;
    }

    public void update(float deltaTime, Polygon heroPolygon) {
        Vector2 direction = shortestDirection(polygon, heroPolygon);
        direction.nor();

        velocity.set(direction.scl(100));

        // Check direction to determine flipping
        if (velocity.x < 0 && !isMovingLeft) {
            // Moving left but not currently facing left, flip
            polygon.setScale(-1, 1);
            isMovingLeft = true;
        } else if (velocity.x > 0 && isMovingLeft) {
            // Moving right but currently facing left, flip back
            polygon.setScale(1, 1);
            isMovingLeft = false;
        }

        polygon.translate(velocity.x * deltaTime, velocity.y * deltaTime);

        wrapAroundWorld();

        stateTime += deltaTime;

        if (doneCollision) {
            lastCollision += deltaTime;
            checkCollision();
        }
    }
    boolean kanan = true;

    public Vector2 shortestDirection(Polygon from, Polygon to) {
        Vector2 fromCenter = new Vector2();
        Vector2 toCenter = new Vector2();

        from.getBoundingRectangle().getCenter(fromCenter);
        to.getBoundingRectangle().getCenter(toCenter);

        if (toCenter.x - fromCenter.y < 0){
            kanan = false;
        }else {kanan = true;}
        Vector2 direction = new Vector2(toCenter.x - fromCenter.x, toCenter.y - fromCenter.y);

        if (Math.abs(direction.x) > (float) WORLD_WIDTH / 2) {
            if (direction.x > 0) {
                direction.x -= WORLD_WIDTH;
            } else {
                direction.x += WORLD_WIDTH;
            }
        }

        if (Math.abs(direction.y) > (float) WORLD_HEIGHT / 2) {
            if (direction.y > 0) {
                direction.y -= WORLD_HEIGHT;
            } else {
                direction.y += WORLD_HEIGHT;
            }
        }

        return direction;
    }

    public boolean shouldFlipHorizontal(Polygon enemyPolygon, Polygon playerPolygon) {
        Vector2 direction = shortestDirection(enemyPolygon, playerPolygon);
        return direction.x < 0; // Flip if enemy is to the left of the player
    }




    protected void wrapAroundWorld() {
        float[] vertices = polygon.getTransformedVertices();
        float x = vertices[0];
        float y = vertices[1];

        if (x < 0) polygon.translate(WORLD_WIDTH, 0);
        if (x >= WORLD_WIDTH) polygon.translate(-WORLD_WIDTH, 0);
        if (y < 0) polygon.translate(0, WORLD_HEIGHT);
        if (y >= WORLD_HEIGHT) polygon.translate(0, -WORLD_HEIGHT);
    }

    @Override
    public boolean isAlive() {
        return hp > 0;
    }

    @Override
    public int getHP() {
        return hp;
    }

    @Override
    public void takeDamage(int damage) {
        hp -= damage;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void resetStateTime() {
        stateTime = 0f;
    }

    protected void checkCollision() {
        if (lastCollision >= 1f) {
            doneCollision = false;
            lastCollision = 0;
        }
    }
}
