package ru.alex9127.app.terrain;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

import ru.alex9127.app.classes.Enemy;
import ru.alex9127.app.classes.EnemyGenerator;
import ru.alex9127.app.classes.Entity;
import ru.alex9127.app.classes.Unit;
import ru.alex9127.app.interfaces.TerrainLike;

public class Terrain implements TerrainLike {
    private final Block[][] terrain;
    private final int size;
    private final Room[][] rooms;
    private int spawnX;
    private int spawnY;
    private final ArrayList<Point> portals = new ArrayList<>();
    private final Trap[] traps;
    public final ArrayList<Enemy> enemies = new ArrayList<>();
    public final int level;
    private int lastPortal;

    public Terrain(int size, int level, Unit unit) {
        this.size = size;
        this.terrain = new Block[size][size];
        this.rooms = new Room[4][4];
        this.traps = new Trap[5 + (int) (Math.random() * 5)];
        this.level = level;
        createTerrain();
        addBlockEntity(spawnX, spawnY, unit);
    }

    static class Room {
        int centerX;
        int centerY;

        public Room(int centerX, int centerY) {
            this.centerX = centerX;
            this.centerY = centerY;
        }
    }

    public void generateEnemies() {
        enemies.clear();
        boolean enemyPlaced;
        for (int i = 0; i < (level % 6 == 0 ? 1 : (int) (level * (10 + Math.random() * 10)));
             i++) {
            enemyPlaced = false;
            do {
                int enemyX = (int) (Math.random() * 128);
                int enemyY = (int) (Math.random() * 128);
                if (getBlockWalkable(enemyX, enemyY)) {
                    Enemy e;
                    if (level % 5 != 0) {
                        if (level < 3) {
                            e = EnemyGenerator.getGreenSlime(level, enemyX, enemyY);

                        } else if (level < 4) {
                            if (new Random().nextInt(3) == 2) {
                                e = EnemyGenerator.getBlueSlime(level, enemyX, enemyY);
                            } else {
                                e = EnemyGenerator.getGreenSlime(level, enemyX, enemyY);
                            }
                        } else {
                            if (new Random().nextInt(5) < 2) {
                                e = EnemyGenerator.getGreenSlime(level, enemyX, enemyY);
                            } else if (new Random().nextInt(5) < 4) {
                                e = EnemyGenerator.getBlueSlime(level, enemyX, enemyY);

                            } else {
                                e = EnemyGenerator.getZombie(level, enemyX, enemyY);
                            }
                        }
                    } else {
                        e = EnemyGenerator.getKingSlime(level, enemyX, enemyY);
                    }
                    enemies.add(e);
                    addBlockEntity(enemyX, enemyY, e);
                    enemyPlaced = true;
                }
            } while (!enemyPlaced);
        }
    }

    public static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static class Trap {
        int x;
        int y;
        boolean isRevealed;

        public Trap(int x, int y) {
            this.x = x;
            this.y = y;
            isRevealed = false;
        }

        public boolean isNotRevealed() {
            return !isRevealed;
        }
    }

    public void createTerrain() {
        createWalls();
        generateRooms(4, 4, size / 8 ,size / 8 * 6);
        generatePaths();
        generateSpawn();
        generatePortals();
        generateChests();
        generateTraps();
        generateEnemies();
    }

    private void generateTraps() {
        int trapX, trapY;
        for (int i = 0; i < traps.length; i++) {
            boolean trapPlaced = false;
            do {
                trapX = generateRandom(0, 128);
                trapY = generateRandom(0, 128);
                if (getBlockWalkable(trapX, trapY)) trapPlaced = true;
            } while (!trapPlaced);
            setBlockConfig(trapX, trapY, "spikes");
            traps[i] = new Trap(trapX, trapY);
        }
    }

    private void generateSpawn() {
        int spawnX, spawnY;
        boolean spawnPlaced = false;
        do {
            spawnX = generateRandom(0, 128);
            spawnY = generateRandom(0, 128);
            if (getBlockWalkable(spawnX, spawnY) && getBlockWalkable(spawnX + 1, spawnY)) spawnPlaced = true;
        } while (!spawnPlaced);
        setBlockConfig(spawnX, spawnY, "spawn");
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    private void generatePortals() {
        int portalX, portalY;
        int numberOfPortals = generateRandom(2, 3);
        for (int i = 0; i < numberOfPortals; i++) {
            boolean portalPlaced = false;
            do {
                portalX = generateRandom(0, 128);
                portalY = generateRandom(0, 128);
                if (getBlockWalkable(portalX, portalY) && getBlockWalkable(portalX - 1, portalY) && getPortalPoint(portalX, portalY) == null)
                    portalPlaced = true;
            } while (!portalPlaced);
            setBlockConfig(portalX, portalY, "portal" + i);
            portals.add(new Point(portalX, portalY));
        }
    }

    private void generatePaths() {
        for (Room[] roomsList : rooms) {
            for (int x = 0; x < rooms[0].length - 1; x++) {
                fillRow(roomsList[x].centerY, roomsList[x].centerX, roomsList[x + 1].centerX);
                fillColumn(roomsList[x + 1].centerX, roomsList[x].centerY, roomsList[x + 1].centerY);
            }
        }
        for (int y = 0; y < rooms.length - 1; y++) {
            for (int x = 0; x < rooms[0].length; x++) {
                fillRow(rooms[y][x].centerY, rooms[y][x].centerX, rooms[y + 1][x].centerX);
                fillColumn(rooms[y + 1][x].centerX, rooms[y][x].centerY, rooms[y + 1][x].centerY);
            }
        }
    }

    private void fillColumn(int column, int startRow, int endRow) {
        if (startRow > endRow) {
            int t = startRow;
            startRow = endRow;
            endRow = t;
        }
        for (int y = startRow; y <= endRow; y++) {
            setBlockWalkable(column, y, true);
        }
    }

    private void fillRow(int row, int startColumn, int endColumn) {
        if (startColumn > endColumn) {
            int t = startColumn;
            startColumn = endColumn;
            endColumn = t;
        }
        for (int x = startColumn; x <= endColumn; x++) {
            setBlockWalkable(x, row, true);
        }
    }

    private void generateChests() {
        int chestX, chestY;
        boolean chestPlaced = false;
        do {
            chestX = generateRandom(0, 128);
            chestY = generateRandom(0, 128);
            if (getBlockWalkable(chestX, chestY)) chestPlaced = true;
        } while (!chestPlaced);
        setBlockConfig(chestX, chestY, "chest");
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (getBlockWalkable(x, y) && generateRandom(0, 10000) == 0) {
                    setBlockConfig(x, y, "chest");
                }
            }
        }
    }
    
    private void generateRooms(int roomsX, int roomsY, int start, int end) {
        for (int y = 0; y < roomsY; y++) {
            for (int x = 0; x < roomsX; x++) {
                generateRoom(start + ((end - start) / (roomsX - 1) * x), start + ((end - start) / (roomsY - 1) * y), x, y);
            }
        }
    }

    private void generateRoom(int startX, int startY, int roomX, int roomY) {
        int centerX = generateRandom(startX, 16);
        int centerY = generateRandom(startY, 16);
        int width = generateRandom(3, 4);
        int height = generateRandom(3, 4);
        rooms[roomY][roomX] = new Room(centerX, centerY);
        String material;
        if (Math.random() * 5 < 4) {
            material = "stone";
        } else {
            material = "wooden";
        }
        for (int y = centerY - height - 3; y <= centerY + height + 3; y++) {
            for (int x = centerX - width - 3; x <= centerX + width + 3; x++) {
                setBlockWalkable(x, y, y >= centerY - height && y <= centerY + height &&
                        x >= centerX - width && x <= centerX + width);
                setBlockMaterial(x, y, material);
            }
        }
    }

    private int generateRandom(int start, int variation) {
        return new Random().nextInt(variation) + start;
    }

    private void createWalls() {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                terrain[y][x] = new Block(x, y, false, "stone", "none");
            }
        }
    }

    public Point getSpawnPoint() {
        return new Point(spawnX, spawnY);
    }

    public Point getPortalPoint(int x, int y) {
        for (Point p:portals) {
            if (p.getX() == x && p.getY() == y) {
                return p;
            }
        }
        return null;
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

    public void addBlockEntity(int x, int y, Entity e) {
        terrain[y][x].addEntity(e);
    }

    public void removeBlockEntity(int x, int y, Entity e) {
        terrain[y][x].removeEntity(e);
    }

    public void revealBlock(int x, int y) {
        terrain[y][x].reveal();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                s.append(terrain[y][x]);
            }
            s.append("\n");
        }
        return s.toString();
    }

    public int getSize() {
        return size;
    }

    @Override
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void revealTrap(int x, int y) {
        for (Trap t:traps) {
            if (t.x == x && t.y == y) {
                t.isRevealed = true;
            }
        }
    }

    public Trap getTrap(int x, int y) {
        for (Trap t:traps) {
            if (t.x == x && t.y == y) {
                return t;
            }
        }
        return null;
    }

    public void setLastPortal(int lastPortal) {
        this.lastPortal = lastPortal;
    }

    public Terrain.Point getLastPortal() {
        return portals.get(lastPortal);
    }
}
