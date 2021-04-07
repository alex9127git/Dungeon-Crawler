package ru.alex9127.app.classes;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import ru.alex9127.app.terrain.Dungeon;
import ru.alex9127.app.terrain.Terrain;

public class GameLogic {
    public final Unit unit;
    public int level;
    public int floor;
    public Dungeon dungeon;
    public String path = "";

    public GameLogic(String name) {
        level = 1;
        floor = 1;
        Terrain terrain = new Terrain(128, level, null, "common");
        this.unit = new Unit(name, 100, 10, 0, 5,
                terrain.getSpawnPoint().getX() + 1, terrain.getSpawnPoint().getY());
        terrain.addBlockEntity(unit.getX(), unit.getY(), unit);
        dungeon = new Dungeon(terrain);
    }

    public void checkNextLevel() {
        if (dungeon.currentTerrain.getPortalPoint(unit.getX(), unit.getY()) != null
                /*&& dungeon.currentTerrain.getEnemies().isEmpty()*/) {
            char c;
            floor++;
            if (floor > 5) {
                floor = 1;
                path = "";
            } else {
                c = dungeon.currentTerrain.getBlockConfig(unit.getX(), unit.getY()).charAt(6);
                dungeon.currentTerrain.setLastPortal(Integer.parseInt(String.valueOf(c)));
                if (dungeon.find(path + c) == null) {
                    level++;
                    Terrain terrain;
                    if (floor % 5 == 0) {
                        terrain = new Terrain(128, level, unit, "boss");
                    } else {
                        terrain = new Terrain(128, level, unit, "common");
                    }
                    dungeon.addByPath(path, c, terrain);
                }
                path += c;
            }
            dungeon.goTo(path);
            Log.v("LOG", level + " " + floor);
            unit.setX(dungeon.currentTerrain.getSpawnPoint().getX() + 1);
            unit.setY(dungeon.currentTerrain.getSpawnPoint().getY());
        }
    }

    public void checkGoingBack() {
        if (dungeon.currentTerrain.getSpawnPoint().getX() == unit.getX() &&
                dungeon.currentTerrain.getSpawnPoint().getY() == unit.getY() && path.length() > 0) {
            floor--;
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

    public void checkAllEnemiesKilled() {
        if (dungeon.currentTerrain.enemies.isEmpty() && !dungeon.currentTerrain.enemyRewardGotten) {
            dungeon.currentTerrain.setBlockConfig(unit.getX(), unit.getY(), "chest");
            dungeon.currentTerrain.enemyRewardGotten = true;
        }
    }
}
