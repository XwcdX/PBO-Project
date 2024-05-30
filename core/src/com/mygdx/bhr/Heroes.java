package com.mygdx.bhr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Polygon;

public class Heroes {
    Polygon polygon;
    private final int WORLD_WIDTH;
    private final int WORLD_HEIGHT;

    public Heroes(int worldWidth, int worldHeight) {
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;
        this.polygon = createPolygon((float) WORLD_WIDTH / 2 - 32, (float) WORLD_HEIGHT / 2 - 32, 64, 64);
    }

    private Polygon createPolygon(float x, float y, float width, float height) {
        float[] vertices = {0, 0, width, 0, width, height, 0, height};
        Polygon polygon = new Polygon(vertices);
        polygon.setPosition(x, y);
        return polygon;
    }

    public void update(float deltaTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) polygon.translate(-200 * deltaTime, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) polygon.translate(200 * deltaTime, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) polygon.translate(0, -200 * deltaTime);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) polygon.translate(0, 200 * deltaTime);

        float[] vertices = polygon.getTransformedVertices();
        float x = vertices[0];
        float y = vertices[1];

        if (x < 0) polygon.translate(WORLD_WIDTH, 0);
        if (x >= WORLD_WIDTH) polygon.translate(-WORLD_WIDTH, 0);
        if (y < 0) polygon.translate(0, WORLD_HEIGHT);
        if (y >= WORLD_HEIGHT) polygon.translate(0, -WORLD_HEIGHT);
    }

    public float getX() {
        return polygon.getTransformedVertices()[0];
    }

    public float getY() {
        return polygon.getTransformedVertices()[1];
    }
}
