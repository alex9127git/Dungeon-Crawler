package ru.alex9127.app.classes;

import android.app.Activity;
import android.content.Intent;

import ru.alex9127.app.saving.Save;
import ru.alex9127.app.terrain.Dungeon;
import ru.alex9127.app.terrain.Terrain;

public class GameLogic {
    public Unit unit;
    public int level;
    public int floor;
    public final Dungeon dungeon;
    public String path = "";
    public int bossesDefeated = 0;
    public int coinsGotten = 0;

    public GameLogic(String name, double startHp, double startMana) {
        level = 1;
        floor = 1;
        Terrain terrain = new Terrain(128, level, name, (int) (100 + startHp),
                10, 0,  (int) (5 + startMana), "common");
        unit = terrain.getUnit();
        dungeon = new Dungeon(terrain);
    }

    public GameLogic(Save save) {
        level = save.level;
        floor = save.floor;
        unit = save.unit;
        dungeon = new Dungeon(save.dungeon);
        path = save.path;
        coinsGotten = save.coinsGotten;
        bossesDefeated = save.bossesDefeated;
    }

    public void checkNextLevel() {
        if (getTerrain().getPortalPoint(unit.getX(), unit.getY()) != null
                && (getTerrain().getEnemies().isEmpty() || getTerrain().type.equals("common"))) {
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
                    coinsGotten += 20 * (floor - 1);
                }
                path += c;
            }
            unit.setX(getTerrain().getSpawnPoint().getX() + 1);
            unit.setY(getTerrain().getSpawnPoint().getY());
        }
    }

    public void checkGoingBack() {
        if (getTerrain().getSpawnPoint().getX() == unit.getX() &&
                getTerrain().getSpawnPoint().getY() == unit.getY() && path.length() > 0 &&
                getTerrain().type.equals("common")) {
            floor--;
            path = path.substring(0, path.length() - 1);
            unit.setX(getTerrain().getLastPortal().getX() - 1);
            unit.setY(getTerrain().getLastPortal().getY());
        }
    }

    public void checkUnitAlive(Activity a) {
        if (!unit.alive()) {
            Intent i = new Intent();
            i.putExtra("Result", "Lost");
            i.putExtra("Coins gotten", coinsGotten);
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

    public void checkAllEnemiesKilled(Activity a) {
        if (getTerrain().type.equals("common") && getTerrain().enemies.isEmpty() && !getTerrain().enemyRewardGotten) {
            getTerrain().setBlockConfig(unit.getX(), unit.getY(), "chest");
            getTerrain().enemyRewardGotten = true;
        }
        if (getTerrain().type.equals("boss") && !getTerrain().hasBoss() && !getTerrain().enemyRewardGotten) {
            getTerrain().setBlockConfig(unit.getX(), unit.getY(), "chest");
            getTerrain().enemyRewardGotten = true;
            getTerrain().enemies.clear();
            bossesDefeated++;
            coinsGotten += 100 * bossesDefeated;
            if (bossesDefeated > 2) {
                Intent i = new Intent();
                i.putExtra("Result", "Won");
                i.putExtra("Coins gotten", coinsGotten);
                a.setResult(Activity.RESULT_OK, i);
                a.finish();
            }
        }
    }

    public Terrain getTerrain() {
        return dungeon.find(path);
    }
}
