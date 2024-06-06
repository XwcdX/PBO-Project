package com.mygdx.bhr;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class bhr extends ApplicationAdapter {
	private BitmapFont HP;
	private Texture enemyImage;
	private Sound enemyS;
	private Texture heroImage;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Heroes hero;
	private Array<Enemies> enemies;
	private long lastSpawnTime;

	private final int WORLD_WIDTH = 1600;
	private final int WORLD_HEIGHT = 960;

	private void spawnEnemies() {
		Polygon enemyPolygon;
		boolean isOverlapping;

		do {
			enemyPolygon = createPolygon(MathUtils.random(0, WORLD_WIDTH - 64), MathUtils.random(0, WORLD_HEIGHT - 64), 64, 64);
			isOverlapping = false;

			for (Enemies enemy : enemies) {
				if (Intersector.overlapConvexPolygons(enemyPolygon, enemy.polygon)) {
					isOverlapping = true;
					break;
				}
			}
		} while (isOverlapping);
		enemies.add(new Enemies(enemyPolygon, WORLD_WIDTH, WORLD_HEIGHT));
		lastSpawnTime = TimeUtils.nanoTime();
	}

	private Polygon createPolygon(float x, float y, float width, float height) {
		float[] vertices = {0, 0, width, 0, width, height, 0, height};
		Polygon polygon = new Polygon(vertices);
		polygon.setPosition(x, y);
		return polygon;
	}

	private void drawWrapped(Texture texture, Polygon polygon) {
		float[] vertices = polygon.getTransformedVertices();
		float width = texture.getWidth();
		float height = texture.getHeight();

		batch.draw(texture, vertices[0], vertices[1]);
		if (vertices[0] < camera.position.x - camera.viewportWidth / 2) {
			batch.draw(texture, vertices[0] + WORLD_WIDTH, vertices[1]);
		}
		if (vertices[0] + width > camera.position.x + camera.viewportWidth / 2) {
			batch.draw(texture, vertices[0] - WORLD_WIDTH, vertices[1]);
		}
		if (vertices[1] < camera.position.y - camera.viewportHeight / 2) {
			batch.draw(texture, vertices[0], vertices[1] + WORLD_HEIGHT);
		}
		if (vertices[1] + height > camera.position.y + camera.viewportHeight / 2) {
			batch.draw(texture, vertices[0], vertices[1] - WORLD_HEIGHT);
		}

		if (vertices[0] < camera.position.x - camera.viewportWidth / 2 && vertices[1] < camera.position.y - camera.viewportHeight / 2) {
			batch.draw(texture, vertices[0] + WORLD_WIDTH, vertices[1] + WORLD_HEIGHT);
		}
		if (vertices[0] < camera.position.x - camera.viewportWidth / 2 && vertices[1] + height > camera.position.y + camera.viewportHeight / 2) {
			batch.draw(texture, vertices[0] + WORLD_WIDTH, vertices[1] - WORLD_HEIGHT);
		}
		if (vertices[0] + width > camera.position.x + camera.viewportWidth / 2 && vertices[1] < camera.position.y - camera.viewportHeight / 2) {
			batch.draw(texture, vertices[0] - WORLD_WIDTH, vertices[1] + WORLD_HEIGHT);
		}
		if (vertices[0] + width > camera.position.x + camera.viewportWidth / 2 && vertices[1] + height > camera.position.y + camera.viewportHeight / 2) {
			batch.draw(texture, vertices[0] - WORLD_WIDTH, vertices[1] - WORLD_HEIGHT);
		}
	}

	@Override
	public void create() {
		enemyImage = new Texture(Gdx.files.internal("enemyTest1.png"));
		enemyImage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		heroImage = new Texture(Gdx.files.internal("charaTest1.png"));
		heroImage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		enemyS = Gdx.audio.newSound(Gdx.files.internal("attack.wav"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		hero = new Heroes(WORLD_WIDTH, WORLD_HEIGHT);
		enemies = new Array<>();
		spawnEnemies();

		HP = new BitmapFont();
		HP.getData().setScale(1);
		HP.setColor(1, 1, 1, 1);
	}

	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 0.2f, 1);

		hero.update(Gdx.graphics.getDeltaTime());

		camera.position.set(hero.getX() + 32, hero.getY() + 32, 0);
		camera.update();

		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		drawWrapped(heroImage, hero.polygon);
		for (Enemies enemy : enemies) {
			drawWrapped(enemyImage, enemy.polygon);
		}
		HP.draw(batch, "HP: " + hero.getHP(), camera.position.x - camera.viewportWidth / 2 + 10, camera.position.y + camera.viewportHeight / 2 - 10);
		batch.end();


		if (TimeUtils.nanoTime() - lastSpawnTime > 1000000000) spawnEnemies();

		for (Iterator<Enemies> iter = enemies.iterator(); iter.hasNext(); ) {
			Enemies enemy = iter.next();
			enemy.update(Gdx.graphics.getDeltaTime(), hero.polygon);
			if (Intersector.overlapConvexPolygons(enemy.polygon, hero.polygon)) {
					enemyS.play();
					hero.takeDamage(25);
					if (!hero.isAlive()) {
						dispose();
					}
				iter.remove();
			}
		}
	}

	@Override
	public void dispose() {
		enemyImage.dispose();
		heroImage.dispose();
		enemyS.dispose();
		batch.dispose();
		HP.dispose();
	}
}
