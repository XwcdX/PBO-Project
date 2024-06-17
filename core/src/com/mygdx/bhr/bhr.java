package com.mygdx.bhr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import org.w3c.dom.Text;

public class bhr extends ApplicationAdapter {
	private BitmapFont HP;
	private BitmapFont EXP;
	private BitmapFont LVL;
	private Texture enemyImage;
	private Sound enemyS;
	private Sound crystalCollectS;
	private Sound lvlUps;
	private Texture heroImage;
	private Texture bulletImage;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Heroes hero;
	private Array<Enemies> enemies;
	private long lastSpawnTime;

	private final int WORLD_WIDTH = 3200;
	private final int WORLD_HEIGHT = 1920;
	// Adding gems
	private Texture redImg1,redImg2,redImg3,redImg4;
	private Texture blueImg1,blueImg2,blueImg3,blueImg4;
	private Texture greenImg1,greenImg2,greenImg3,greenImg4;
	private Texture pinkImg1,pinkImg2,pinkImg3,pinkImg4;
	private Texture purpleImg1,purpleImg2,purpleImg3,purpleImg4;
	public Animation<TextureRegion> crystalAnimationRed;
	public Animation<TextureRegion> crystalAnimationBlue;
	public Animation<TextureRegion> crystalAnimationPurple;
	public Animation<TextureRegion> crystalAnimationPink;
	public Animation<TextureRegion> crystalAnimationGreen;
	public Array<Crystal>crystals;

	private Map<Enemies, Float> collisionTimes;

	///
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

	private void spawnCrystals(Enemies enemy) {
		Random rand = new Random();
		int exp = rand.nextInt(51);
		int byk50 = exp / 50;
		exp %= 50;
		int byk20 = exp / 20;
		exp %= 20;
		int byk10 = exp / 10;
		exp %= 10;
		int byk5 = exp / 5;
		exp %= 5;
		int byk1 = exp;
		for (int i = 0; i < byk50; i++) {
			crystals.add(new Crystal(enemy.polygon.getX(), enemy.polygon.getY(), 52, 52, crystalAnimationPink, 52));
		}
		for (int i = 0; i < byk20; i++) {
			crystals.add(new Crystal(enemy.polygon.getX(), enemy.polygon.getY(), 46, 46, crystalAnimationPurple, 46));
		}
		for (int i = 0; i < byk10; i++) {
			crystals.add(new Crystal(enemy.polygon.getX(), enemy.polygon.getY(), 40, 40, crystalAnimationRed, 40));
		}
		for (int i = 0; i < byk5; i++) {
			crystals.add(new Crystal(enemy.polygon.getX(), enemy.polygon.getY(), 34, 34, crystalAnimationBlue, 34));
		}
		for (int i = 0; i < byk1; i++) {
			crystals.add(new Crystal(enemy.polygon.getX(), enemy.polygon.getY(), 28, 28, crystalAnimationGreen, 28));
		}
	}

	@Override
	public void create() {
		enemyImage = new Texture(Gdx.files.internal("enemyTest1.png"));
		enemyImage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		bulletImage = new Texture(Gdx.files.internal("fireballtest1.png"));
		bulletImage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		heroImage = new Texture(Gdx.files.internal("charaTest1.png"));
		heroImage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		enemyS = Gdx.audio.newSound(Gdx.files.internal("attack.wav"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		hero = new Heroes(WORLD_WIDTH, WORLD_HEIGHT, camera);
		enemies = new Array<>();
		crystals = new Array<>();
		spawnEnemies();
		/*
		Make Texture Image
		 */
		redImg1 =  new Texture(Gdx.files.internal("Red/red_crystal_0000T.png"));
		redImg2 =  new Texture(Gdx.files.internal("Red/red_crystal_0001T.png"));
		redImg3 =  new Texture(Gdx.files.internal("Red/red_crystal_0002T.png"));
		redImg4 =  new Texture(Gdx.files.internal("Red/red_crystal_0003T.png"));

		blueImg1  =  new Texture(Gdx.files.internal("Blue/blue_crystal_0000T.png"));
		blueImg2  =  new Texture(Gdx.files.internal("Blue/blue_crystal_0001T.png"));
		blueImg3  =  new Texture(Gdx.files.internal("Blue/blue_crystal_0002T.png"));
		blueImg4  =  new Texture(Gdx.files.internal("Blue/blue_crystal_0003T.png"));

		pinkImg1 =  new Texture(Gdx.files.internal("Pink/pink_crystal_0000T.png"));
		pinkImg2 =  new Texture(Gdx.files.internal("Pink/pink_crystal_0001T.png"));
		pinkImg3 =  new Texture(Gdx.files.internal("Pink/pink_crystal_0002T.png"));
		pinkImg4 =  new Texture(Gdx.files.internal("Pink/pink_crystal_0003T.png"));

		greenImg1 = new Texture(Gdx.files.internal("Green/green_crystal_0000T.png"));
		greenImg2 = new Texture(Gdx.files.internal("Green/green_crystal_0001T.png"));
		greenImg3 = new Texture(Gdx.files.internal("Green/green_crystal_0002T.png"));
		greenImg4 = new Texture(Gdx.files.internal("Green/green_crystal_0003T.png"));

		purpleImg1 = new Texture(Gdx.files.internal("Purple/purple_crystal_0000T.png"));
		purpleImg2 = new Texture(Gdx.files.internal("Purple/purple_crystal_0001T.png"));
		purpleImg3 = new Texture(Gdx.files.internal("Purple/purple_crystal_0002T.png"));
		purpleImg4 = new Texture(Gdx.files.internal("Purple/purple_crystal_0003T.png"));

		Array<TextureRegion> frameRed = new Array<>();
		frameRed.add(new TextureRegion(redImg1));
		frameRed.add(new TextureRegion(redImg2));
		frameRed.add(new TextureRegion(redImg3));
		frameRed.add(new TextureRegion(redImg4));

		Array<TextureRegion> frameBlue = new Array<>();
		frameBlue.add(new TextureRegion(blueImg1));
		frameBlue.add(new TextureRegion(blueImg2));
		frameBlue.add(new TextureRegion(blueImg3));
		frameBlue.add(new TextureRegion(blueImg4));

		Array<TextureRegion> frameGreen = new Array<>();
		frameGreen.add(new TextureRegion(greenImg1));
		frameGreen.add(new TextureRegion(greenImg2));
		frameGreen.add(new TextureRegion(greenImg3));
		frameGreen.add(new TextureRegion(greenImg4));

		Array<TextureRegion> framePurple= new Array<>();
		framePurple.add(new TextureRegion(purpleImg1));
		framePurple.add(new TextureRegion(purpleImg2));
		framePurple.add(new TextureRegion(purpleImg3));
		framePurple.add(new TextureRegion(purpleImg4));

		Array<TextureRegion> framePink= new Array<>();
		framePink.add(new TextureRegion(pinkImg1));
		framePink.add(new TextureRegion(pinkImg2));
		framePink.add(new TextureRegion(pinkImg3));
		framePink.add(new TextureRegion(pinkImg4));
		// Create the animation (0.25f is the duration of each frame)
		crystalAnimationRed = new Animation<>(0.25f, frameRed, Animation.PlayMode.LOOP);
		crystalAnimationBlue = new Animation<>(0.25f, frameBlue, Animation.PlayMode.LOOP);
		crystalAnimationGreen = new Animation<>(0.25f, frameGreen, Animation.PlayMode.LOOP);
		crystalAnimationPurple= new Animation<>(0.25f, framePurple, Animation.PlayMode.LOOP);
		crystalAnimationPink = new Animation<>(0.25f, framePink, Animation.PlayMode.LOOP);

		//Audio Claim Crystal
		crystalCollectS = Gdx.audio.newSound(Gdx.files.internal("Audio/object_collect.wav"));
		//Audio Exp Lvl Up
//		lvlUps = Gdx.audio.newSound(Gdx.files.internal("Audio/achievement.wav"));
		HP = new BitmapFont();
		EXP = new BitmapFont();
		LVL = new BitmapFont();
		LVL.getData().setScale(1);
		LVL.setColor(1,1,1,1);
		EXP.getData().setScale(1);
		EXP.setColor(1, 1, 1, 1);
		HP.getData().setScale(1);
		HP.setColor(1, 1, 1, 1);

		collisionTimes = new HashMap<>();
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
		for (Bullet bullet : hero.getBullets()) {
			float rotation = bullet.getRotation() - 180;
			batch.draw(bulletImage,
					bullet.circle.x - bullet.circle.radius, bullet.circle.y - bullet.circle.radius,
					bullet.circle.radius, bullet.circle.radius,
					bulletImage.getWidth(), bulletImage.getHeight(),
					1f, 1f,
					rotation,
					0, 0,
					bulletImage.getWidth(), bulletImage.getHeight(),
					false, false);
		}

		for (Crystal crystal : crystals) {
			crystal.updateStateTime(Gdx.graphics.getDeltaTime());
			if (!crystal.collected) {
				Texture texture = crystal.getTexture();
				drawWrapped(texture, crystal.polygon);
			}
		}

		hero.drawSkills(batch);

		HP.draw(batch, "HP: " + hero.getHP(), camera.position.x - camera.viewportWidth / 2 + 10, camera.position.y + camera.viewportHeight / 2 - 10);
		EXP.draw(batch, "EXP: " + hero.getExp(), camera.position.x - camera.viewportWidth / 2 + 10, camera.position.y + camera.viewportHeight / 2 - 30);
		LVL.draw(batch,"LVL: "+hero.getLevel(),camera.position.x - camera.viewportWidth / 2 + 10, camera.position.y + camera.viewportHeight / 2 - 50);
		batch.end();

		if (TimeUtils.nanoTime() - lastSpawnTime > 1000000000) spawnEnemies();

		for (Iterator<Enemies> iter = enemies.iterator(); iter.hasNext(); ) {
			Enemies enemy = iter.next();
			enemy.update(Gdx.graphics.getDeltaTime(), hero.polygon);

			if (Intersector.overlapConvexPolygons(enemy.polygon, hero.polygon)) {
				if (!collisionTimes.containsKey(enemy)) {
					collisionTimes.put(enemy, 0f);
				}
				float collisionTime = collisionTimes.get(enemy) + Gdx.graphics.getDeltaTime();
				collisionTimes.put(enemy, collisionTime);

				if (collisionTime >= 2f) {
					enemyS.play();
					hero.takeDamage(10);
					if (!hero.isAlive()) {
						dispose();
					}
					iter.remove();
					collisionTimes.remove(enemy);
				}
			} else {
				collisionTimes.remove(enemy);
			}
		}

		// Check collisions between bullets and enemies
		for (Iterator<Bullet> iterBullet = hero.getBullets().iterator(); iterBullet.hasNext(); ) {
			Bullet bullet = iterBullet.next();
			for (Iterator<Enemies> iterEnemy = enemies.iterator(); iterEnemy.hasNext(); ) {
				Enemies enemy = iterEnemy.next();
				if (Intersector.overlaps(bullet.circle, enemy.polygon.getBoundingRectangle())) {
					enemy.takeDamage(50);
					if (!enemy.isAlive()) {
						spawnCrystals(enemy); // Function to handle crystal spawning
						iterEnemy.remove();
					}
					iterBullet.remove();
					break;
				}
			}
			bullet.update(Gdx.graphics.getDeltaTime()); // Update the bullet after collision check
			if (bullet.hasExceededRange()) {
				iterBullet.remove(); // Remove bullets that exceed range
			}
		}

		// Check collisions between guardian skill's tiny circles and enemies
		for (Iterator<Enemies> iterEnemy = enemies.iterator(); iterEnemy.hasNext(); ) {
			Enemies enemy = iterEnemy.next();
			hero.checkCollisionsWithGuardianSkill(iterEnemy);
			if (!enemy.isAlive()){
				System.out.println("X-X");
			}
		}

		for (Crystal crystal : crystals) {
			if (!crystal.collected && Intersector.overlapConvexPolygons(crystal.polygon, hero.polygon)) {
				crystal.collected = true;
				crystalCollectS.play();
				switch (crystal.getSize()) {
					case 52:
						hero.addExp(50);
						hero.checkLevelUp();
						break;
					case 46:
						hero.addExp(20);
						hero.checkLevelUp();
						break;
					case 40:
						hero.addExp(10);
						hero.checkLevelUp();
						break;
					case 34:
						hero.addExp(5);
						hero.checkLevelUp();
						break;
					case 28:
						hero.addExp(1);
						hero.checkLevelUp();
						break;
					default:
						break;
				}
			}
		}
	}

	@Override
	public void dispose() {
		enemyImage.dispose();
		bulletImage.dispose();
		heroImage.dispose();
		enemyS.dispose();
		batch.dispose();
		HP.dispose();
	}
}
