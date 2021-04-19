package ru.alex9127.app.classes;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import ru.alex9127.app.saving.CompactTerrain;
import ru.alex9127.app.saving.Save;
import ru.alex9127.app.terrain.Dungeon;
import ru.alex9127.app.terrain.Terrain;

public class GameLogic {
    public Unit unit;
    public int level;
    public int floor;
    public final Dungeon dungeon;
    public String path = "";

    public GameLogic(String name) {
        level = 1;
        floor = 1;
        Terrain terrain = new Terrain(128, level, name, 100, 10, 0, 5, "common");
        unit = terrain.getUnit();
        dungeon = new Dungeon(terrain);
    }

    public GameLogic(Save save) {
        level = save.level;
        floor = save.floor;
        unit = save.unit;
        dungeon = new Dungeon(save.dungeon);
        path = save.path;
    }

    public void checkNextLevel() {
        if (getTerrain().getPortalPoint(unit.getX(), unit.getY()) != null
                /*&& getTerrain().getEnemies().isEmpty()*/) {
            char c;
            floor++;
            if (floor > 5) {
                floor = 1;
                path = "";
            } else {
                c = getTerrain().getBlockConfig(unit.getX(), unit.getY()).charAt(6);
                getTerrain().setLastPortal(Integer.parseInt(String.valueOf(c)));
                if (dungeon.find(path + c) == null) {
                    level++;
                    Terrain terrain;
                    if (floor % 5 == 0) {
                        terrain = new Terrain(128, level, unit, "boss");
                    } else {
                        terrain = new Terrain(128, level, unit, "common");
                    }
                    unit = terrain.getUnit();
                    dungeon.addByPath(path, c, terrain);
                }
                path += c;
            }
            Log.v("LOG", level + " " + floor);
            unit.setX(getTerrain().getSpawnPoint().getX() + 1);
            unit.setY(getTerrain().getSpawnPoint().getY());
        }
    }

    public void checkGoingBack() {
        if (getTerrain().getSpawnPoint().getX() == unit.getX() &&
                getTerrain().getSpawnPoint().getY() == unit.getY() && path.length() > 0) {
            floor--;
            path = path.substring(0, path.length() - 1);
            unit.setX(getTerrain().getLastPortal().getX() - 1);
            unit.setY(getTerrain().getLastPortal().getY());
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
        for (Enemy e : getTerrain().getEnemies()) {
            e.decide(unit, getTerrain());
            if (Pathfinder.distance(unit.getX(), unit.getY(), e.getX(), e.getY()) == 0 && e.alive()) {
                int dmg = -1 * (e.getAttackPower() - unit.getDefensePower());
                hurt += Math.min(dmg, 0);
            }
        }
        return hurt;
    }

    public void checkAllEnemiesKilled() {
        if (getTerrain().enemies.isEmpty() && !getTerrain().enemyRewardGotten) {
            getTerrain().setBlockConfig(unit.getX(), unit.getY(), "chest");
            getTerrain().enemyRewardGotten = true;
        }
    }

    public Terrain getTerrain() {
        return dungeon.find(path);
    }
}
