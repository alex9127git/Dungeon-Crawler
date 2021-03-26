package ru.alex9127.app.classes;

import android.app.Activity;
import android.content.Intent;

import ru.alex9127.app.interfaces.TerrainLike;
import ru.alex9127.app.terrain.BossArena;
import ru.alex9127.app.terrain.Terrain;

public class GameLogic {
    public final Unit unit;
    public int level;
    public TerrainLike terrain;

    public GameLogic(String name) {
        level = 1;
        terrain = new Terrain(128, level, null);
        this.unit = new Unit(name, 100, 10, 0, 15,
                terrain.getSpawnPoint().getX(), terrain.getSpawnPoint().getY());
        terrain.addBlockEntity(unit.getX(), unit.getY(), unit);
    }

    public void checkNextLevel() {
        if (unit.getX() == terrain.getPortalPoint().getX() &&
                unit.getY() == terrain.getPortalPoint().getY() && terrain.getEnemies().isEmpty()) {
            level++;
            if (level % 6 == 0) {
                terrain = new BossArena(128, level, unit);
            } else {
                terrain = new Terrain(128, level, unit);
            }
            unit.setX(terrain.getSpawnPoint().getX());
            unit.setY(terrain.getSpawnPoint().getY());
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
        for (Enemy e : terrain.getEnemies()) {
            e.decide(unit, terrain);
            if (Pathfinder.distance(unit.getX(), unit.getY(), e.getX(), e.getY()) == 0 && e.alive()) {
                int dmg = -1 * (e.getAttackPower() - unit.getDefensePower());
                unit.changeHp(Math.min(dmg, 0));
            }
        }
    }
}
