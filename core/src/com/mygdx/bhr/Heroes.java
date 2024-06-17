package com.mygdx.bhr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
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
    private Texture tinyCircleTexture;
    public Heroes(int worldWidth, int worldHeight, Camera camera) {
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;
        this.polygon = createPolygon((float) WORLD_WIDTH / 2 - 32, (float) WORLD_HEIGHT / 2 - 32, 64, 64);
        this.hp = 100;
        this.exp = 0;
        this.level = 1;
        this.ExpNeeded = 200;
        this.bullets = new Array<>();
        this.direction = new Vector2(0, 0);
        this.lastDirection = new Vector2(1, 0);
        this.lastAttackTime = TimeUtils.nanoTime();
        this.lvlUps =  Gdx.audio.newSound(Gdx.files.internal("Audio/achievement.wav"));
        this.camera = camera;
        this.skills = new HashSet<>();
        this.tinyCircleTexture = new Texture(Gdx.files.internal("skull_icon.png"));
    }

    private Polygon createPolygon(float x, float y, float width, float height) {
        float[] vertices = {0, 0, width, 0, width, height, 0, height};
        Polygon polygon = new Polygon(vertices);
        polygon.setPosition(x, y);
        return polygon;
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

        // Auto attack every 0.5 seconds (500,000,000 nanoseconds)
        long attackInterval = 500000000;
        if (TimeUtils.nanoTime() - lastAttackTime > attackInterval) {
            shoot();
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

        if (guardianSkill != null) {
            guardianSkill.update(deltaTime, new Vector2(getX() + polygon.getBoundingRectangle().width / 2, getY() + polygon.getBoundingRectangle().height / 2));
        }

        checkAndAddSkills();
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
        return skills.contains(skill);
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

    public void checkLevelUp(){
        if (exp >= ExpNeeded) {
            levelUp();
        }
    }
    private void levelUp() {
        level++;
        exp -= ExpNeeded;
        ExpNeeded += 15;
        lvlUps.play();
    }

    public int getLevel() {
        return level;
    }

    public void addGuardianSkill() {
        if (guardianSkill == null) {
            guardianSkill = new Guardian_Skill(tinyCircleTexture);
        }
    }


    public void drawSkills(SpriteBatch batch) {
        if (guardianSkill != null) {
            guardianSkill.draw(batch);
        }
    }

    public void checkAndAddSkills() {
        if (level == 2 && !hasSkill(Skill.GUARDIAN)) {
            addSkill(Skill.GUARDIAN);
            addGuardianSkill();
            System.out.println("Skill acquired: Guardian");
        }
        if (level == 10 && !hasSkill(Skill.KAMEHAMEHA)) {
            addSkill(Skill.KAMEHAMEHA);
            System.out.println("Skill acquired: Kamehameha");
        }
        if (level == 15 && !hasSkill(Skill.MINE_BOMB)) {
            addSkill(Skill.MINE_BOMB);
            System.out.println("Skill acquired: Mine Bomb");
        }
    }

    public void checkCollisionsWithGuardianSkill(Iterator<Enemies> enemiesIterator) {
        if (guardianSkill != null) {
            guardianSkill.checkCollisions(enemiesIterator);
        }
    }
}
