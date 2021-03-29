package ru.alex9127.app.classes;

import android.app.Activity;
import android.content.Intent;

import ru.alex9127.app.interfaces.TerrainLike;
import ru.alex9127.app.terrain.BossArena;
import ru.alex9127.app.terrain.Dungeon;
import ru.alex9127.app.terrain.Terrain;

public class GameLogic {
    public final Unit unit;
    public int level;
    public Dungeon dungeon;
    public String path = "";

    public GameLogic(String name) {
        level = 1;
        TerrainLike terrain = new Terrain(128, level, null);
        this.unit = new Unit(name, 100, 10, 0, 15,
                terrain.getSpawnPoint().getX() + 1, terrain.getSpawnPoint().getY());
        terrain.addBlockEntity(unit.getX(), unit.getY(), unit);
        dungeon = new Dungeon(terrain);
    }

    public void checkNextLevel() {
        if (dungeon.currentTerrain.getPortalPoint(unit.getX(), unit.getY()) != null
                /*&& dungeon.currentTerrain.getEnemies().isEmpty()*/) {
            level++;
            char c = dungeon.currentTerrain.getBlockConfig(unit.getX(), unit.getY()).charAt(6);
            dungeon.currentTerrain.setLastPortal(Integer.parseInt(String.valueOf(c)));
            if (dungeon.find(path + c) == null) {
                TerrainLike terrain;
                if (level % 5 == 0) {
                    terrain = new BossArena(128, level, unit);
                } else {
                    terrain = new Terrain(128, level, unit);
                }
                dungeon.addByPath(path, c, terrain);
            }
            path += c;
            dungeon.goTo(path);
            unit.setX(dungeon.currentTerrain.getSpawnPoint().getX() + 1);
            unit.setY(dungeon.currentTerrain.getSpawnPoint().getY());
        }
    }

    public void checkGoingBack() {
        if (dungeon.currentTerrain.getSpawnPoint().getX() == unit.getX() &&
                dungeon.currentTerrain.getSpawnPoint().getY() == unit.getY() && path.length() > 0) {
            level--;
            path = path.substring(0, path.length() - 1);
            dungeon.goTo(path);
            unit.setX(dungeon.currentTerrain.getLastPortal().getX() - 1);
            unit.setY(dungeon.currentTerrain.getLastPortal().getY());
        }
    }

    public void checkUnitAlive(Activity a) {
        if (!unit.alive()) {
            Intent i = new Intent();
            a.setResult(Activity.RESULT_OK, i);
            a.finish();
        }
    }

    public int enemyAI() {
        int hurt = 0;
        for (Enemy e : dungeon.currentTerrain.getEnemies()) {
            e.decide(unit, dungeon.currentTerrain);
            if (Pathfinder.distance(unit.getX(), unit.getY(), e.getX(), e.getY()) == 0 && e.alive()) {
                int dmg = -1 * (e.getAttackPower() - unit.getDefensePower());
                hurt += Math.min(dmg, 0);
            }
        }
        return hurt;
    }
}
