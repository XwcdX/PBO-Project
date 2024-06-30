package com.mygdx.bhr;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;

public class Modal {
    private String text;
    private float duration;
    private float stateTime;
    private BitmapFont font;
    private final Camera camera;
    private Texture backgroundTexture;

    public Modal(Camera camera) {
        this.font = new BitmapFont(); // Use default font
        this.camera = camera;
        this.backgroundTexture = createBackgroundTexture();
        reset();
    }

    private Texture createBackgroundTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.59f, 0.29f, 0.0f, 1.0f)); // Set color to brown
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void show(String text, float duration) {
        this.text = text;
        this.duration = duration;
        this.stateTime = 0;
    }

    public void update(float deltaTime) {
        if (stateTime < duration) {
            stateTime += deltaTime;
        }
    }

    public void draw(SpriteBatch batch) {
        font.getData().setScale(1);
        font.setColor(1, 1, 1, 1);
        if (stateTime < duration) {
            GlyphLayout layout = new GlyphLayout(font, text);
            float textWidth = layout.width;
            float textHeight = layout.height;

            float x = camera.position.x - textWidth / 2;
            float y = camera.position.y + camera.viewportHeight / 2 - 20;

            float padding = 15;
            batch.setColor(0.59f, 0.29f, 0.0f, 1.0f); // Set color to brown for the background
            batch.draw(backgroundTexture, x - padding, y - textHeight - padding, textWidth + 2 * padding, textHeight + 2 * padding);
            batch.setColor(1, 1, 1, 1); // Reset color to white for the text

            // Draw the text
            font.draw(batch, text, x, y);
        }
    }

    private void reset() {
        this.text = "";
        this.duration = 0;
        this.stateTime = 0;
    }
}
