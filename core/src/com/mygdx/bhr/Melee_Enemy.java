package com.mygdx.bhr;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Melee_Enemy extends Enemies {
    private static final float ATTACK_DISTANCE = 45f;
    private static final float ATTACK_COOLDOWN = 3f;
    private Rectangle attackRange;
    private float lastAttackTime;
    ShapeRenderer sr = new ShapeRenderer();


    public Melee_Enemy(Polygon polygon, int worldWidth, int worldHeight) {
        super(polygon, worldWidth, worldHeight);
        this.attackRange = new Rectangle();
        this.lastAttackTime = 0;
        super.ATTACK_DAMAGE = 10;
    }

    boolean attack = true;
    @Override
    public void update(float deltaTime, Polygon heroPolygon) {
        Vector2 direction = shortestDirection(polygon, heroPolygon);
        direction.nor();

        Vector2 enemyCenter = getPolygonCenter(polygon);
        Vector2 heroCenter = getPolygonCenter(heroPolygon);

        float distance = enemyCenter.dst(heroCenter);

        if (distance > ATTACK_DISTANCE) {
            velocity.set(direction.scl(100));
            polygon.translate(velocity.x * deltaTime, velocity.y * deltaTime);
            wrapAroundWorld();
        } else {
            velocity.set(0, 0);
            if (stateTime-lastAttackTime >= 2.5f){
                attack = true;
            }
            if (checkAttackRange(heroPolygon) && stateTime - lastAttackTime >= ATTACK_COOLDOWN && attack) {
                attackHero();
                attack = false;
                lastAttackTime = stateTime;
            }
        }

        stateTime += deltaTime;

        if (doneCollision) {
            lastCollision += deltaTime;
            checkCollision();
        }

        updateAttackRange(direction);
    }

    private void updateAttackRange(Vector2 direction) {
        Vector2 enemyCenter = getPolygonCenter(polygon);
        float attackRangeWidth = 64;
        float attackRangeHeight = 64;

        attackRange.setPosition(enemyCenter.x + direction.x * 32, enemyCenter.y + direction.y * 32);
        attackRange.setSize(attackRangeWidth, attackRangeHeight);
    }

    private Vector2 getPolygonCenter(Polygon polygon) {
        return new Vector2(polygon.getBoundingRectangle().x + polygon.getBoundingRectangle().width / 2,
                polygon.getBoundingRectangle().y + polygon.getBoundingRectangle().height / 2);
    }

    private boolean checkAttackRange(Polygon heroPolygon) {
        return attackRange.overlaps(heroPolygon.getBoundingRectangle());
    }

    private void attackHero() {
        attack = true;
    }
}
