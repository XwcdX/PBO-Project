package com.mygdx.bhr;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Enemies implements hasHP {
    Polygon polygon;
    Vector2 velocity;
    protected final int WORLD_WIDTH;
    protected final int WORLD_HEIGHT;
    private int hp;
    private float stateTime;
    private boolean doneCollision;
    private float lastCollision;

    public Enemies(Polygon polygon, int worldWidth, int worldHeight) {
        this.polygon = polygon;
        this.velocity = new Vector2();
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;
        this.hp = 100;
        this.stateTime = 30f;
        this.doneCollision = false;
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

        polygon.translate(velocity.x * deltaTime, velocity.y * deltaTime);

        wrapAroundWorld();

        stateTime += deltaTime;

        if (doneCollision){
            lastCollision += deltaTime;
            checkCollision();
        }
    }

    protected Vector2 shortestDirection(Polygon from, Polygon to) {
        Vector2 fromCenter = new Vector2(from.getBoundingRectangle().x + from.getBoundingRectangle().width / 2, from.getBoundingRectangle().y + from.getBoundingRectangle().height / 2);
        Vector2 toCenter = new Vector2(to.getBoundingRectangle().x + to.getBoundingRectangle().width / 2, to.getBoundingRectangle().y + to.getBoundingRectangle().height / 2);

        Vector2 direction = new Vector2();
        direction.x = toCenter.x - fromCenter.x;
        direction.y = toCenter.y - fromCenter.y;

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

    private void checkCollision(){
        if (lastCollision >= 1f){
            doneCollision = false;
            lastCollision=0;
        }
    }

}
