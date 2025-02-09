package com.mygdx.bhr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.rmi.ConnectIOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Heroes implements hasHP, canShoot, hasExp {
    Polygon polygon;
    private final int WORLD_WIDTH;
    private final int WORLD_HEIGHT;
    private int hp;
    private int exp;
    private final Array<Bullet> bullets;
    private final Vector2 direction;
    private final Vector2 lastDirection;
    private long lastAttackTime;
    public int  ExpNeeded;
    private int level;
    private final Sound lvlUps;
    private final Camera camera;
    private Set<Skill> skills;
    private Guardian_Skill guardianSkill;
    private Kamehameha kamehamehaSkill;
    private Mine_Bomb minebombSkill;
    private final Animation<TextureRegion> tinyCircleTexture;
    private final Animation<TextureRegion> kamehamehaTexture;
    private final Texture minebombTexture;
    private final bhr game;
    private float animationTime;
    private Modal modal;
    private boolean level2bomb;
    private boolean level2guardian;
    private boolean level2kamehameha;
    private boolean level3bomb;
    private boolean level3guardian;
    private boolean level3kamehameha;
    boolean shoot;

    public Sound fireball_sfx;

    public Heroes(int worldWidth, int worldHeight, Camera camera, bhr game) {
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;
//        this.playerAnimation = playerAnimation;
        this.polygon = createPolygon((float) WORLD_WIDTH / 2 - 16, (float) WORLD_HEIGHT / 2 - 24, 32, 48);
        this.hp = 100;
        this.exp = 0;
        this.level = 1;
        this.ExpNeeded = 50;
        this.bullets = new Array<>();
        this.direction = new Vector2(0, 0);
        this.lastDirection = new Vector2(1, 0);
        this.lastAttackTime = TimeUtils.nanoTime();
        this.lvlUps = Gdx.audio.newSound(Gdx.files.internal("Audio/achievement.wav"));
        this.camera = camera;
        this.skills = new HashSet<>();
        Texture[] generateCircle = new Texture[60];
        TextureRegion[] textureRegions = new TextureRegion[60];
        for(int i=0;i<60;i++){
            String filename = String.format("electric_shield_60fps/Effect_ElectricShield_1_%03d.png", i);
            generateCircle[i] = new Texture(Gdx.files.internal(filename));
            textureRegions[i] = new TextureRegion(generateCircle[i]);
        }
        this.tinyCircleTexture= new Animation<>(0.16f,textureRegions);
        this.tinyCircleTexture.setPlayMode(Animation.PlayMode.LOOP);
        animationTime = 0;
        Texture[] generateKameha= new Texture[12];
        TextureRegion[] textureRegions1 = new TextureRegion[12];
        for(int i=0;i<12;i++){
            String filename = String.format("Kameha_Animation/tile%03d.png", i);
            generateKameha[i] = new Texture(Gdx.files.internal(filename));
            textureRegions1[i] = new TextureRegion(generateKameha[i]);
        }
        this.kamehamehaTexture = new Animation<>(0.20f,textureRegions1);
        this.kamehamehaTexture.setPlayMode(Animation.PlayMode.LOOP);
        this.minebombTexture = new Texture(Gdx.files.internal("restart_button.png"));
        this.game = game;
        this.modal = new Modal(camera);
        this.level2bomb = false;
        this.level2guardian = false;
        this.level2kamehameha = false;
        this.level3bomb = false;
        this.level3guardian = false;
        this.level3kamehameha = false;
        this.shoot = false;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    private Polygon createPolygon(float x, float y, float width, float height) {
        float[] vertices = {0, 0, width, 0, width, height, 0, height};
        Polygon polygon = new Polygon(vertices);
        polygon.setPosition(x, y);
        return polygon;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void update(float deltaTime) {
        direction.set(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.A)) direction.add(-1, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) direction.add(1, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) direction.add(0, -1);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) direction.add(0, 1);

        if (!direction.isZero()) {
            lastDirection.set(direction).nor(); // Update last direction if there's movement
        }

        direction.nor(); // Normalize the direction vector to ensure consistent speed

        polygon.translate(direction.x * 300 * deltaTime, direction.y * 300 * deltaTime);
        long attackInterval = 3000000000L;
        long animationInterval = 2500000000L;
        if (TimeUtils.nanoTime() - lastAttackTime > animationInterval) {
            shoot = TimeUtils.nanoTime() - lastAttackTime <= attackInterval;
        }
        if (TimeUtils.nanoTime() - lastAttackTime > attackInterval) {
            shoot();

            fireball_sfx = Gdx.audio.newSound(Gdx.files.internal("fireball_sfx.mp3"));
            fireball_sfx.play();

            lastAttackTime = TimeUtils.nanoTime();
        }

        float[] vertices = polygon.getTransformedVertices();
        float x = vertices[0];
        float y = vertices[1];

        if (x < 0) polygon.translate(WORLD_WIDTH, 0);
        if (x >= WORLD_WIDTH) polygon.translate(-WORLD_WIDTH, 0);
        if (y < 0) polygon.translate(0, WORLD_HEIGHT);
        if (y >= WORLD_HEIGHT) polygon.translate(0, -WORLD_HEIGHT);

        for (Iterator<Bullet> iter = bullets.iterator(); iter.hasNext(); ) {
            Bullet bullet = iter.next();
            bullet.update(deltaTime);
            if (bullet.hasExceededRange()) {
                iter.remove();
            }
        }
        checkAndAddSkills();
        animationTime += deltaTime;
        if (guardianSkill != null) {
            guardianSkill.update(deltaTime, new Vector2(getX() + polygon.getBoundingRectangle().width / 2, getY() + polygon.getBoundingRectangle().height / 2));
        }

        if (kamehamehaSkill != null) {
            kamehamehaSkill.update(deltaTime, new Vector2(getX() + polygon.getBoundingRectangle().width / 2, getY() + polygon.getBoundingRectangle().height / 2));
        }

        if (minebombSkill != null) {
            minebombSkill.update(deltaTime);
        }
        modal.update(deltaTime);
    }

    public float getX() {
        return polygon.getTransformedVertices()[0];
    }

    public float getY() {
        return polygon.getTransformedVertices()[1];
    }

    @Override
    public void takeDamage(int damage) {
        hp -= damage;
    }

    @Override
    public int getHP() {
        return hp;
    }

    @Override
    public boolean isAlive() {
        return hp > 0;
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public boolean hasSkill(Skill skill) {
        return !skills.contains(skill);
    }

    @Override
    public void shoot() {
        float[] vertices = polygon.getTransformedVertices();
        float x = vertices[0] + polygon.getBoundingRectangle().width / 2;
        float y = vertices[1] + polygon.getBoundingRectangle().height / 2;

        Vector3 mousePosition3 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePosition3);
        Vector2 mousePosition = new Vector2(mousePosition3.x, mousePosition3.y);

        // Calculate direction from hero to mouse
        Vector2 direction = mousePosition.sub(x, y).nor();

        bullets.add(new Bullet(x, y, direction, WORLD_WIDTH, WORLD_HEIGHT));
    }

    @Override
    public Array<Bullet> getBullets() {
        return bullets;
    }

    @Override
    public void addExp(int exp) {
        this.exp += exp;
    }

    @Override
    public int getExp() {
        return exp;
    }

    public void checkLevelUp() {
        if (exp >= ExpNeeded) {
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        exp -= ExpNeeded;
        ExpNeeded += 100;
        lvlUps.play();
    }

    public int getLevel() {
        return level;
    }

    public void addGuardianSkill() {
        if (guardianSkill == null) {
            guardianSkill = new Guardian_Skill(tinyCircleTexture, game, 3);
        }
    }

    public void addKamehamehaSkill() {
        if (kamehamehaSkill == null) {
            kamehamehaSkill = new Kamehameha(kamehamehaTexture, game);
        }
    }

    public void addMineBombSkill(){
        if (minebombSkill == null) {
            minebombSkill = new Mine_Bomb(minebombTexture, game);
        }
    }

    public void drawSkills(SpriteBatch batch) {
        if (guardianSkill != null) {
            guardianSkill.draw(batch,animationTime);
        }
        if (kamehamehaSkill != null) {
            kamehamehaSkill.draw(batch);
        }
        if (minebombSkill != null) {
            minebombSkill.draw(batch);
            //minebombSkill.debugDraw();
        }
        modal.draw(batch);
    }

    public void checkAndAddSkills() {
        if (level == 2 && hasSkill(Skill.MINE_BOMB)) {
            addSkill(Skill.MINE_BOMB);
            addMineBombSkill();
            modal.show("Skill Acquired : Mine Bomb", 2f);
        }
        if (level == 3 && hasSkill(Skill.GUARDIAN)) {
            addSkill(Skill.GUARDIAN);
            addGuardianSkill();
            modal.show("Skill Acquired : Guardian", 2f);
        }
        if (level == 4 && hasSkill(Skill.KAMEHAMEHA)) {
            addSkill(Skill.KAMEHAMEHA);
            addKamehamehaSkill();
            modal.show("Skill Acquired : Flame Thrower", 2f);
        }
        if (level == 5 && !level2bomb) {
            minebombSkill.setInitialRadius(50);
            minebombSkill.setExplosionRadius(100);
            modal.show("Skill Upgraded : Big Bomb", 2f);
            level2bomb = true;
        }
        if (level == 6 && !level2guardian) {
            guardianSkill.setNumberOfCircles(4);
            modal.show("Skill Upgraded : Protector", 2f);
            level2guardian = true;
        }
        if (level == 7 && !level2kamehameha) {
            kamehamehaSkill.setWidth(300);
            kamehamehaSkill.setHeight(30);
            modal.show("Skill Upgraded : Flame Prince", 2f);
            level2kamehameha = true;
        }
        if (level == 8 && !level3bomb) {
            minebombSkill.setInitialRadius(60);
            minebombSkill.setExplosionRadius(110);
            minebombSkill.setCooldownDuration(5f);
            minebombSkill.setExplosionDelay(3000000000L);
            minebombSkill.setExplosionDamage(50);
            modal.show("Skill Upgraded : The True Bomber", 2f);
            level3bomb = true;
        }
        if (level == 9 && !level3guardian) {
            guardianSkill.setNumberOfCircles(5);
            guardianSkill.setSpeed(.4f);
            modal.show("Skill Upgraded : Ancient Guard", 2f);
            level3guardian = true;
        }
        if (level == 10 && !level3kamehameha) {
            kamehamehaSkill.setHeight(50);
            kamehamehaSkill.setWidth(350);
            kamehamehaSkill.setACTIVE_DURATION(1.5f);
            kamehamehaSkill.setCOOLDOWN_DURATION(5f);
            modal.show("Skill Upgraded : Flame King", 2f);
            level3kamehameha = true;
        }

    }


    public void checkCollisionsWithGuardianSkill(Iterator<Enemies> enemiesIterator) {
        if (guardianSkill != null) {
            guardianSkill.checkCollisions(enemiesIterator);
        }
    }

    public void checkCollisionsWithKamehamehaSkill(Iterator<Enemies> enemiesIterator) {
        if (kamehamehaSkill != null) {
            kamehamehaSkill.checkCollisions(enemiesIterator);
        }
    }

    public void checkCollisionsWithMineBombSkill(Iterator<Enemies> enemiesIterator) {
        if (minebombSkill != null) {
            minebombSkill.checkCollisions(enemiesIterator);
        }
    }
}
