package ru.alex9127.app.saving;

import java.util.ArrayList;

import ru.alex9127.app.classes.Enemy;
import ru.alex9127.app.classes.Unit;
import ru.alex9127.app.terrain.Terrain;

public class CompactTerrain {
    public final int size;
    public final Room[][] rooms;
    public final ArrayList<Pathway> pathways;
    public int spawnX;
    public int spawnY;
    public final ArrayList<Terrain.Point> portals;
    public final ArrayList<Terrain.Point> chests;
    public final Terrain.Trap[] traps;
    public final ArrayList<Enemy> enemies;
    public final int level;
    public int lastPortal;
    public final String type;
    public boolean enemyRewardGotten;
    public Unit unit;

    public CompactTerrain(Terrain terrain) {
        this.size = terrain.size;
        this.rooms = terrain.rooms;
        this.pathways = terrain.pathways;
        this.spawnX = terrain.spawnX;
        this.spawnY = terrain.spawnY;
        this.portals = terrain.portals;
        this.chests = terrain.chests;
        this.traps = terrain.traps;
        this.enemies = terrain.enemies;
        this.level = terrain.level;
        this.lastPortal = terrain.lastPortal;
        this.type = terrain.type;
        this.enemyRewardGotten = terrain.enemyRewardGotten;
        this.unit = terrain.unit;
    }
}
