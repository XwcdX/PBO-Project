package com.mygdx.bhr;

import java.util.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class bhr extends ApplicationAdapter {
	private BitmapFont HP;
	private BitmapFont EXP;
	private BitmapFont LVL;
	private Texture enemyImage;
	private Texture longEnemyImage;
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

	//stats
	private int spawntime = 1000000000;
	private int hero_atk = 30;
	private int enemy_atk = 15;

	private final int WORLD_WIDTH = 3200;
	private final int WORLD_HEIGHT = 1920;
	// Adding gems
	private Texture redImg1,redImg2,redImg3,redImg4;
	private Texture blueImg1,blueImg2,blueImg3,blueImg4;
	private Texture greenImg1,greenImg2,greenImg3,greenImg4;
	private Texture pinkImg1,pinkImg2,pinkImg3,pinkImg4;
	private Texture purpleImg1,purpleImg2,purpleImg3,purpleImg4;
	private Texture[] generateFireballImg;
	private Texture texture_pause;
	private Sprite pause;
	public Animation<TextureRegion> crystalAnimationRed;
	public Animation<TextureRegion> crystalAnimationBlue;
	public Animation<TextureRegion> crystalAnimationPurple;
	public Animation<TextureRegion> crystalAnimationPink;
	public Animation<TextureRegion> crystalAnimationGreen;
	public Animation<TextureRegion> firebalAnimation;
	public Animation <TextureRegion> undeadAnimation;
	public Animation <TextureRegion> wizzardAnimation;
	public Animation <TextureRegion> bomberAnimation;
	public Animation <TextureRegion> bossAnimation;
	public Array<Crystal>crystals;

	private Map<Enemies, Float> collisionTimes;
	public static Array<Enemy_Bullet> enemyBullets = new Array<>();
	public static Array<Enemy_Bomb> enemyBombs = new Array<>();
	private long lastBoundChangeTime = TimeUtils.nanoTime();
	private int currentRandomBound = 1;
	private int minute = 0;
	Array<Enemies> summonedEnemies = new Array<>();
	private float stateTime = 0;
	private void spawnEnemies() {
		Polygon enemyPolygon;
		Polygon bossPolygon;
		boolean isOverlapping;
		int safetyCounter = 0;
		final int MAX_TRIES = 100;

		// Create a new enemy polygon, ensuring it does not overlap with existing enemies
		do {
			enemyPolygon = createPolygon(MathUtils.random(0, WORLD_WIDTH - 64), MathUtils.random(0, WORLD_HEIGHT - 64), 64, 64);
			bossPolygon = createPolygon(MathUtils.random(0, WORLD_WIDTH - 96), MathUtils.random(0, WORLD_HEIGHT - 96), 96, 96);
			isOverlapping = false;

			for (Enemies enemy : enemies) {
				if (Intersector.overlapConvexPolygons(enemyPolygon, enemy.polygon)) {
					isOverlapping = true;
					break;
				}
			}

			safetyCounter++;
			if (safetyCounter > MAX_TRIES) {
				// Exit the loop to avoid infinite loop if no suitable position is found
				break;
			}
		} while (isOverlapping);

		// Randomly determine the type of enemy to spawn
		Random random = new Random();
		int randomValue = random.nextInt(currentRandomBound);

		int totalBoss=0;
		for (Enemies cekBoss : enemies){
			if (cekBoss instanceof  BossSpawner_Enemy){
				totalBoss++;
			}
		}

		if (minute < 3) {
			enemies.add(new Enemies(enemyPolygon, WORLD_WIDTH, WORLD_HEIGHT));
		} else if (minute < 4) {
			// 25% chance for Long_Enemy, 75% chance for regular Enemies
			if (randomValue == 3) {
				enemies.add(new Long_Enemy(enemyPolygon, WORLD_WIDTH, WORLD_HEIGHT));
			} else {
				enemies.add(new Enemies(enemyPolygon, WORLD_WIDTH, WORLD_HEIGHT));
			}
		} else if (minute < 6) {
			// 16.6% chance for Bomber_Enemy, 33.3% chance for Long_Enemy, 50% chance for regular Enemies
			if (randomValue == 5) {
				enemies.add(new Bomber_Enemy(enemyPolygon, WORLD_WIDTH, WORLD_HEIGHT));
			} else if (randomValue >= 3 && randomValue <= 4) {
				enemies.add(new Long_Enemy(enemyPolygon, WORLD_WIDTH, WORLD_HEIGHT));
			} else {
				enemies.add(new Enemies(enemyPolygon, WORLD_WIDTH, WORLD_HEIGHT));
			}
		} else {
			// 5% chance for Boss 20% chance for Bomber_Enemy, 30% chance for Long_Enemy, 45% chance for regular Enemies
			randomValue = random.nextInt(100);
			if (randomValue >= 95 && totalBoss<=8){
				enemies.add(new BossSpawner_Enemy(bossPolygon, WORLD_WIDTH, WORLD_HEIGHT, summonedEnemies));
			} else if (randomValue >= 75) {
				enemies.add(new Bomber_Enemy(enemyPolygon, WORLD_WIDTH, WORLD_HEIGHT));
			} else if (randomValue >= 45) {
				enemies.add(new Long_Enemy(enemyPolygon, WORLD_WIDTH, WORLD_HEIGHT));
			} else {
				enemies.add(new Enemies(enemyPolygon, WORLD_WIDTH, WORLD_HEIGHT));
			}
		}

		lastSpawnTime = TimeUtils.nanoTime();
	}

	public OrthographicCamera getCamera() {
		return camera;
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


	private void drawWrapped(Animation<TextureRegion> animation, Polygon polygon, float stateTime) {
		TextureRegion texture = animation.getKeyFrame(stateTime, true);
		float[] vertices = polygon.getTransformedVertices();
		float width = texture.getRegionWidth();
		float height = texture.getRegionHeight();

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
	void spawnCrystals(Enemies enemy) {
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

		longEnemyImage = new Texture(Gdx.files.internal("charaTest1.png"));
		longEnemyImage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		bulletImage = new Texture(Gdx.files.internal("fireballtest1.png"));
		bulletImage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		heroImage = new Texture(Gdx.files.internal("charaTest1.png"));
		heroImage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		texture_pause = new Texture(Gdx.files.internal("Pause screen.jpg"));
		pause = new Sprite(texture_pause);

		enemyS = Gdx.audio.newSound(Gdx.files.internal("attack.wav"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		hero = new Heroes(WORLD_WIDTH, WORLD_HEIGHT, camera,this);
		enemies = new Array<>();
		crystals = new Array<>();
		Texture []generateFireballImg = new Texture[60];
		TextureRegion[] texture_region = new TextureRegion[60];
		for(int i = 0;i<60;i++){
			String filename = String.format("Fireball_Frames/Effect_FastPixelFire_1_%03d.png",i);
			generateFireballImg[i] = new Texture(Gdx.files.internal(filename));
			texture_region[i] = new TextureRegion(generateFireballImg[i]);
		}

		firebalAnimation = new Animation<>(0.1f,texture_region);
		firebalAnimation.setPlayMode(Animation.PlayMode.LOOP);

		//Enemy Load Here
		Texture[] generateUndeadMelee = new Texture[20];
		TextureRegion[] texture_region_enemy_melee = new TextureRegion[20];
		for(int i = 0;i<20;i++){
			String filename = String.format("Enemy_Melee_Run/tile%03d.png",i);
			generateUndeadMelee[i] = new Texture(Gdx.files.internal(filename));
			texture_region_enemy_melee[i] = new TextureRegion(generateUndeadMelee[i]);
		}
		undeadAnimation = new Animation<>(0.20f,texture_region_enemy_melee);
		undeadAnimation.setPlayMode(Animation.PlayMode.LOOP);

		Texture[] generateEnemyWizard = new Texture[8];
		TextureRegion[] texture_region_enemy_wizard = new TextureRegion[8];
		for(int i = 0;i<8;i++){
			String filename = String.format("Run_Enemy_Wizzard/tile%03d.png",i);
			generateEnemyWizard[i] = new Texture(Gdx.files.internal(filename));
			texture_region_enemy_wizard[i] = new TextureRegion(generateEnemyWizard[i]);
		}
		wizzardAnimation = new Animation<>(0.20f,texture_region_enemy_wizard);
		wizzardAnimation.setPlayMode(Animation.PlayMode.LOOP);
		Texture[] generateEnemyBomber = new Texture[9];
		TextureRegion[] texture_region_enemy_bomber = new TextureRegion[9];
		for(int i =0;i<9;i++){
			String filename = String.format("Bomber Run/tile%03d.png",i);
			generateEnemyBomber[i] = new Texture(Gdx.files.internal(filename));
			texture_region_enemy_bomber[i] = new TextureRegion(generateEnemyBomber[i]);
		}
		bomberAnimation = new Animation<>(0.20f,texture_region_enemy_bomber);
		bomberAnimation.setPlayMode(Animation.PlayMode.LOOP);

		Texture[] generateBoss = new Texture[8];
		TextureRegion[] texture_region_enemy_boss = new TextureRegion[8];
		for(int i =0;i<8;i++){
			String filename = String.format("Boss Walk/tile%03d.png",i);
			generateBoss[i] = new Texture(Gdx.files.internal(filename));
			texture_region_enemy_boss[i] = new TextureRegion(generateBoss[i]);
		}
		bossAnimation = new Animation<>(0.20f,texture_region_enemy_boss);
		bossAnimation.setPlayMode(Animation.PlayMode.LOOP);

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

	boolean paused;
	@Override
	public void render() {
		stateTime += Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0, 0, 0.2f, 1);

        if (paused) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isTouched()) {
                paused = false;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            GeneralUpdate();
            hero.update(Gdx.graphics.getDeltaTime());
        }

        // Update camera position
        camera.position.set(hero.getX() + 32, hero.getY() + 32, 0);
        camera.update();

        // Ensure batch uses the camera's projection matrix
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Draw hero and other entities
        drawWrapped(heroImage, hero.polygon);

		for (Enemies enemy : enemies) {
			if (enemy instanceof Long_Enemy){
				drawWrapped(wizzardAnimation, enemy.polygon, stateTime);
			} else if (enemy instanceof Bomber_Enemy) {
				drawWrapped(bossAnimation, enemy.polygon, stateTime);
			} else if(enemy instanceof BossSpawner_Enemy){
				drawWrapped(bossAnimation, enemy.polygon, stateTime);
			} else {
				drawWrapped(undeadAnimation, enemy.polygon, stateTime);
			}
		}

		for (Enemy_Bullet bullet : enemyBullets) {
			batch.draw(bulletImage, bullet.getPosition().x, bullet.getPosition().y);
		}

		for (Enemy_Bomb bomb : enemyBombs){
			bomb.draw(batch);
		}

		float deltaTime = Gdx.graphics.getDeltaTime();
        for (Bullet bullet : hero.getBullets()) {
			bullet.updates(deltaTime);
            float rotation = bullet.getRotation() - 180;
			TextureRegion currentFrame = firebalAnimation.getKeyFrame(bullet.getStateTime(), true);
            batch.draw(currentFrame.getTexture(),
                    bullet.circle.x - bullet.circle.radius, bullet.circle.y - bullet.circle.radius,
                    bullet.circle.radius, bullet.circle.radius,
                    currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),
                    0.09f, 0.09f,
                    rotation,
                    0, 0,
                    currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),
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
        LVL.draw(batch, "LVL: " + hero.getLevel(), camera.position.x - camera.viewportWidth / 2 + 10, camera.position.y + camera.viewportHeight / 2 - 50);

        // Draw pause texture if paused
        if (paused) {
            // Set color with alpha value for transparency (e.g., 0.5f for 50% transparency)
            batch.setColor(1, 1, 1, 0.5f);
            // Draw pause texture to cover the entire screen
            float pauseX = camera.position.x - camera.viewportWidth / 2;
            float pauseY = camera.position.y - camera.viewportHeight / 2;
            batch.draw(pause, pauseX, pauseY, camera.viewportWidth, camera.viewportHeight);
            // Reset color to white (fully opaque) for subsequent drawings
            batch.setColor(1, 1, 1, 1);
        }

        batch.end();
    }


	public void GeneralUpdate(){

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
			paused = true;
			try{
				Thread.sleep(100);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}

		// klo level hero dibawah tertentu monster spawn normal
		if (hero.getLevel() % 10 == 0){
			spawntime-=100000;
		}
		// Check if it's time to spawn enemies
		if (TimeUtils.nanoTime() - lastSpawnTime > spawntime) {
			spawnEnemies();
		}

		// Check if a minute has passed to update bounds
		if (TimeUtils.nanoTime() - lastBoundChangeTime > 60000000000L) { // 1 minute in nanoseconds
			currentRandomBound += 1;
			minute += 1;
			lastBoundChangeTime = TimeUtils.nanoTime(); // Reset the last bound change time
		}

		// Handle enemy and hero collision
        for (Enemies enemy : enemies) {
            enemy.update(Gdx.graphics.getDeltaTime(), hero.polygon);
            if (Intersector.overlapConvexPolygons(enemy.polygon, hero.polygon)) {
                if (!collisionTimes.containsKey(enemy)) {
                    collisionTimes.put(enemy, 0f);
                }
                float collisionTime = collisionTimes.get(enemy) + Gdx.graphics.getDeltaTime();
                collisionTimes.put(enemy, collisionTime);

                if (collisionTime >= 1f) {
                    enemyS.play();
                    hero.takeDamage(enemy_atk); // biar enemy atk bisa tambah sakit makin late game
                }
            }
			if (enemy instanceof BossSpawner_Enemy){
				enemies.addAll(summonedEnemies);
				summonedEnemies.clear();
			}
        }

		if (hero.getLevel() % 10 == 0){
			enemy_atk+=10;
		}

		// Handle bullet and enemy collision (Heroes)
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
		}

		for (Iterator<Enemy_Bullet> iter = enemyBullets.iterator(); iter.hasNext(); ) {
			Enemy_Bullet bullet = iter.next();
			bullet.update(Gdx.graphics.getDeltaTime(),hero.polygon);
			if (Intersector.overlaps(bullet.getBoundingCircle(), hero.polygon.getBoundingRectangle())) {
				hero.takeDamage(10);
				iter.remove();
			}
			if (bullet.getTimeAlive() > Enemy_Bullet.LIFESPAN) {
				iter.remove();
			}
		}

		for (Iterator<Enemy_Bomb> iter = enemyBombs.iterator(); iter.hasNext(); ) {
			Enemy_Bomb bomb = iter.next();
			bomb.update();
			if (bomb.isExploded()) {
				if (!bomb.hasHeroBeenDamaged() && Intersector.overlaps(bomb.getCircle(), hero.getPolygon().getBoundingRectangle())) {
					hero.takeDamage(25);
					bomb.setHeroDamaged(true);
				}

				if (bomb.shouldRemove()) {
					iter.remove();
				}
			}
		}

		// Handle skill-specific collisions
		Iterator<Enemies> enemiesIterator = enemies.iterator();
		hero.checkCollisionsWithGuardianSkill(enemiesIterator);
		hero.checkCollisionsWithKamehamehaSkill(enemiesIterator);
		hero.checkCollisionsWithMineBombSkill(enemiesIterator);

		// Handle crystal collection
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
		if (!hero.isAlive()) {
			dispose();
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
