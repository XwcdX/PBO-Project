//package com.mygdx.bhr;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//
//public class MainMenuScreen implements Screen {
//    private bhr game;
//    private Texture playButtonActive;
//    private Texture playButtonInactive;
//    private SpriteBatch batch;
//    private static final int BUTTON_WIDTH = 300;
//    private static final int BUTTON_HEIGHT = 100;
//
//    public MainMenuScreen(bhr game) {
//        this.game = game;
//        playButtonActive = new Texture("play_button_active.png");
//        playButtonInactive = new Texture("play_button_inactive.png");
//        batch = new SpriteBatch();
//    }
//
//    @Override
//    public void show() {
//    }
//
//    @Override
//    public void render(float delta) {
//        Gdx.gl.glClearColor(0, 0, 0, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        batch.begin();
//
//        int x = (Gdx.graphics.getWidth() - BUTTON_WIDTH) / 2;
//        int y = (Gdx.graphics.getHeight() - BUTTON_HEIGHT) / 2;
//
//        if (Gdx.input.getX() < x + BUTTON_WIDTH && Gdx.input.getX() > x &&
//                Gdx.input.getY() < y + BUTTON_HEIGHT && Gdx.input.getY() > y) {
//            batch.draw(playButtonActive, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
//            if (Gdx.input.isTouched()) {
//                game.setScreen(new GameScreen(game));
//            }
//        } else {
//            batch.draw(playButtonInactive, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
//        }
//
//        batch.end();
//    }
//
//    @Override
//    public void resize(int width, int height) {
//    }
//
//    @Override
//    public void pause() {
//    }
//
//    @Override
//    public void resume() {
//    }
//
//    @Override
//    public void hide() {
//    }
//
//    @Override
//    public void dispose() {
//        playButtonActive.dispose();
//        playButtonInactive.dispose();
//        batch.dispose();
//    }
//}
