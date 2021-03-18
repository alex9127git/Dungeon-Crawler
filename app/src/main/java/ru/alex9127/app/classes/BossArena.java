package ru.alex9127.app.classes;

import ru.alex9127.app.interfaces.TerrainLike;

public class BossArena implements TerrainLike {
    private final Block[][] terrain;
    private final int size;
    public int spawnX;
    public int spawnY;
    public int portalX;
    public int portalY;

    public BossArena(int size) {
        this.size = size;
        this.terrain = new Block[size][size];
        createTerrain();
    }

    public void createTerrain() {
        createWalls();
        createArena();
        spawnX = spawnY = size / 4;
        portalX = portalY = size / 4 * 3;
        terrain[spawnX][spawnY] = new Block(spawnX, spawnY, "spawn");
        terrain[portalX][portalY] = new Block(portalX, portalY, "portal");
    }

    public Terrain.Point getSpawnPoint() {
        return new Terrain.Point(spawnX, spawnY);
    }

    public Terrain.Point getPortalPoint() {
        return new Terrain.Point(portalX, portalY);
    }

    private void createWalls() {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                terrain[y][x] = new Block(x, y, "stone wall");
            }
        }
    }

    private void createArena() {
        for (int y = size / 4; y < size / 4 * 3; y++) {
            for (int x = size / 4; x < size / 4 * 3; x++) {
                terrain[y][x] = new Block(x, y, "stone floor");
            }
        }
    }

    public Block getBlock(int x, int y) {
        return terrain[y][x];
    }
    public void setBlock(int x, int y, String type) {
        terrain[y][x] = new Block(x, y, type);
    }

    public int getSize() {
        return size;
    }
}
