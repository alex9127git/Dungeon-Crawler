package ru.alex9127.app.classes;

import android.app.Activity;
import android.content.Intent;

import java.util.*;

import ru.alex9127.app.interfaces.TerrainLike;

public class GameLogic {
    public final Unit unit;
    public final ArrayList<Enemy> enemies;
    public int level;
    public TerrainLike terrain;

    public GameLogic(String name) {
        terrain = new Terrain(128);
        this.unit = new Unit(name, 100, 10, 0, 15,
                terrain.getSpawnPoint().getX(), terrain.getSpawnPoint().getY());
        enemies = new ArrayList<>();
        level = 1;
        generateEnemies();
    }

    public void generateEnemies() {
        enemies.clear();
        boolean enemyPlaced;
        for (int i = 0; i < (level % 6 == 0 ? 1 : level); i++) {
            enemyPlaced = false;
            do {
                int enemyX = (int) (Math.random() * 128);
                int enemyY = (int) (Math.random() * 128);
                if (terrain.getBlockWalkable(enemyX, enemyY)) {
                    if (level % 6 != 0) {
                        if (level <= 6) {
                            enemies.add(new Enemy("SLIME",
                                    (int) (20 + (int) (Math.random() * 10) * (1 + level * 0.5)),
                                    (int) (4 + (int) (Math.random() * 2) * (1 + level * 0.5)),
                                    (int) (2 + (int) (Math.random() * 1) * (1 + level * 0.5)), 0,
                                    enemyX, enemyY, (30 + (int) (Math.random() * 20)) * level));
                        } else {
                            enemies.add(new Enemy("ZOMBIE",
                                    (int) (50 + (int) (Math.random() * 30) * (1 + level * 0.5)),
                                    (int) (10 + (int) (Math.random() * 5) * (1 + level * 0.5)),
                                    (int) (5 + (int) (Math.random() * 3) * (1 + level * 0.5)), 0,
                                    enemyX, enemyY, (70 + (int) (Math.random() * 50)) * level));
                        }
                    } else {
                        enemies.add(new Enemy("KING SLIME",
                                (int) (1000 + (int) (Math.random() * 500) * (1 + level * 0.5)),
                                (int) (20 + (int) (Math.random() * 10) * (1 + level * 0.5)),
                                (int) (0 + (int) (Math.random() * 0) * (1 + level * 0.5)), 0,
                                enemyX, enemyY, (1000 + (int) (Math.random() * 500)) * level));
                    }
                    enemyPlaced = true;
                }
            } while (!enemyPlaced);
        }
    }

    public void checkNextLevel() {
        if (unit.getX() == terrain.getPortalPoint().getX() &&
                unit.getY() == terrain.getPortalPoint().getY() && enemies.isEmpty()) {
            level++;
            if (level % 6 == 0) {
                terrain = new BossArena(128);
            } else {
                terrain = new Terrain(128);
            }
            unit.setX(terrain.getSpawnPoint().getX());
            unit.setY(terrain.getSpawnPoint().getY());
            generateEnemies();
        }
    }

    public void checkUnitAlive(Activity a) {
        if (!unit.alive()) {
            Intent i = new Intent();
            a.setResult(Activity.RESULT_OK, i);
            a.finish();
        }
    }

    public void enemyAI() {
        for (Enemy e : enemies) {
            e.decide(unit, terrain);
            if (Pathfinder.distance(unit.getX(), unit.getY(), e.getX(), e.getY()) == 0 && e.alive()) {
                int dmg = -1 * (e.getAttackPower() - unit.getDefensePower());
                unit.changeHp(Math.min(dmg, 0));
            }
        }
    }
}
