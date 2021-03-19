package ru.alex9127.app.classes;

import java.util.ArrayList;

import ru.alex9127.app.interfaces.TerrainLike;

public class BossArena implements TerrainLike {
    private final Block[][] terrain;
    private final int size;
    public int spawnX;
    public int spawnY;
    public int portalX;
    public int portalY;
    public final ArrayList<Enemy> enemies = new ArrayList<>();
    public final int level;

    public BossArena(int size, int level) {
        this.size = size;
        this.terrain = new Block[size][size];
        this.level = level;
        createTerrain();
    }

    public void createTerrain() {
        createWalls();
        createArena();
        spawnX = spawnY = size / 4;
        portalX = portalY = size / 4 * 3;
        setBlockConfig(spawnX, spawnY, "spawn");
        setBlockConfig(portalX, portalY, "portal");
    }

    public void generateEnemies() {
        enemies.clear();
        boolean enemyPlaced;
        for (int i = 0; i < (level % 6 == 0 ? 1 : level); i++) {
            enemyPlaced = false;
            do {
                int enemyX = (int) (Math.random() * 128);
                int enemyY = (int) (Math.random() * 128);
                if (getBlockWalkable(enemyX, enemyY)) {
                    Enemy e;
                    if (level % 6 != 0) {
                        if (level <= 6) {
                            e = EnemyGenerator.getEnemy("SLIME", level, 20, 10,
                                    4, 2, 2, 1, 30,
                                    20, enemyX, enemyY);

                        } else {
                            e = EnemyGenerator.getEnemy("ZOMBIE", level, 50, 30,
                                    10, 5, 5, 3, 70,
                                    50, enemyX, enemyY);
                        }
                    } else {
                        e = EnemyGenerator.getEnemy("KING SLIME", level, 1000, 500,
                                20, 10, 0, 0, 1000,
                                500, enemyX, enemyY);
                    }
                    enemies.add(e);
                    setBlockEnemy(enemyX, enemyY, e);
                    enemyPlaced = true;
                }
            } while (!enemyPlaced);
        }
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
                terrain[y][x] = new Block(x, y, false, "stone", "none");
            }
        }
    }

    private void createArena() {
        for (int y = size / 4; y < size / 4 * 3; y++) {
            for (int x = size / 4; x < size / 4 * 3; x++) {
                setBlockWalkable(x, y, true);
            }
        }
    }

    public boolean getBlockWalkable(int x, int y) {
        return terrain[y][x].isWalkable();
    }

    public String getBlockMaterial(int x, int y) {
        return terrain[y][x].getMaterial();
    }

    public String getBlockConfig(int x, int y) {
        return terrain[y][x].getConfig();
    }

    public Enemy getBlockEnemy(int x, int y) {
        return terrain[y][x].getEnemy();
    }

    public boolean isBlockRevealed(int x, int y) {
        return terrain[y][x].isShown();
    }

    public void setBlockWalkable(int x, int y, boolean isWalkable) {
        terrain[y][x].setWalkable(isWalkable);
    }

    public void setBlockMaterial(int x, int y, String material) {
        terrain[y][x].setMaterial(material);
    }

    public void setBlockConfig(int x, int y, String config) {
        terrain[y][x].setConfig(config);
    }

    public void setBlockEnemy(int x, int y, Enemy e) {
        terrain[y][x].giveEnemy(e);
    }

    public void revealBlock(int x, int y) {
        terrain[y][x].reveal();
    }

    public int getSize() {
        return size;
    }

    @Override
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
}
