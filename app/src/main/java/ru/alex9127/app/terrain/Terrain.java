package ru.alex9127.app.terrain;

import androidx.annotation.NonNull;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.alex9127.app.classes.*;

import ru.alex9127.app.saving.*;

public class Terrain {
    public Block[][] terrain;
    public int size;
    public Room[][] rooms;
    public ArrayList<Pathway> pathways = new ArrayList<>();
    public int spawnX;
    public int spawnY;
    public ArrayList<Point> portals = new ArrayList<>();
    public ArrayList<Point> chests = new ArrayList<>();
    public Trap[] traps;
    public CopyOnWriteArrayList<Enemy> enemies = new CopyOnWriteArrayList<>();
    public Unit unit;
    public int level;
    public int lastPortal;
    public String type;
    public boolean enemyRewardGotten = false;
    public HashSet<Integer> revealedBlocks = new HashSet<>();

    public Terrain(int size, int level, String unitName, int unitHp, int unitAtk, int unitDef,
                   int unitMana, String type) {
        this.size = size;
        this.type = type;
        this.terrain = new Block[size][size];
        this.rooms = new Room[4][4];
        this.traps = new Trap[5 + (int) (Math.random() * 5)];
        this.level = level;
        createTerrain(type);
        this.unit = new Unit(unitName, unitHp, unitAtk, unitDef, unitMana, spawnX + 1, spawnY);
        setUnit(this.unit);
    }

    public Terrain(int size, int level, Unit unit, String type) {
        this.size = size;
        this.type = type;
        this.terrain = new Block[size][size];
        this.rooms = new Room[4][4];
        this.traps = new Trap[5 + (int) (Math.random() * 5)];
        this.level = level;
        createTerrain(type);
        this.unit = unit;
        this.unit.setX(spawnX + 1);
        this.unit.setY(spawnY);
        setUnit(this.unit);
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        addBlockEntity(unit.getX(), unit.getY(), unit);
    }

    public Terrain(CompactTerrain t) {
        this.size = t.size;
        this.terrain = new Block[size][size];
        this.rooms = t.rooms;
        this.spawnX = t.spawnX;
        this.spawnY = t.spawnY;
        this.portals = t.portals;
        this.chests = t.chests;
        this.enemies = t.enemies;
        this.pathways = t.pathways;
        this.traps = t.traps;
        this.level = t.level;
        this.lastPortal = t.lastPortal;
        this.type = t.type;
        this.enemyRewardGotten = t.enemyRewardGotten;
        this.unit = t.unit;
        this.revealedBlocks.addAll(t.revealedBlocks);
        switch (type) {
            case "common":
                createWalls();
                for (Room[] row : rooms) {
                    for (Room room : row) {
                        for (int y = room.top - 3; y <= room.bottom + 3; y++) {
                            for (int x = room.left - 3; x <= room.right + 3; x++) {
                                if (!getBlockWalkable(x, y)) {
                                    setBlockWalkable(x, y, y >= room.top && y <= room.bottom &&
                                            x >= room.left && x <= room.right);
                                }
                                setBlockMaterial(x, y, room.material);
                            }
                        }
                    }
                }
                for (Pathway pathway : pathways) {
                    fillRow(pathway.y1, pathway.x1, pathway.x2);
                    fillColumn(pathway.x2, pathway.y1, pathway.y2);
                }
                setBlockConfig(spawnX, spawnY, "spawn");
                for (Point portal:portals) {
                    setBlockConfig(portal.getX(), portal.getY(), "portal" + portals.indexOf(portal));
                }
                for (Point chest:chests) {
                    setBlockConfig(chest.getX(), chest.getY(), "chest");
                }
                for (Trap trap:traps) {
                    setBlockConfig(trap.x, trap.y, "spikes");
                }
                break;
            case "boss":
                createWalls();
                createArena();
                setBlockConfig(spawnX, spawnY, "spawn");
                setBlockConfig(portals.get(0).getX(), portals.get(0).getY(), "portal0");
        }
        addBlockEntity(unit.getX(), unit.getY(), unit);
        for (Enemy enemy:enemies) {
            addBlockEntity(enemy.getX(), enemy.getY(), enemy);
        }
        for (int b : t.revealedBlocks) {
            revealBlock(b % size, b / size);
        }
    }

    public void generateEnemies() {
        enemies.clear();
        boolean enemyPlaced;
        for (int i = 0; i < (type.equals("boss") ? 1 : (int) (level * (10 + Math.random() * 10)));
             i++) {
            enemyPlaced = false;
            do {
                int enemyX = (int) (Math.random() * 128);
                int enemyY = (int) (Math.random() * 128);
                if (getBlockWalkable(enemyX, enemyY)) {
                    Enemy e;
                    if (type.equals("common")) {
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
        final int x;
        final int y;

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
        final int x;
        final int y;
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

    public void createTerrain(String type) {
        switch (type) {
            case "common":
                createWalls();
                generateRooms(4, 4, size / 8 ,size / 8 * 6);
                generatePaths();
                generateSpawn();
                generatePortals();
                generateChests();
                generateTraps();
                generateEnemies();
                break;
            case "boss":
                createWalls();
                createArena();
                spawnX = spawnY = size / 8 * 3;
                portals.add(new Terrain.Point(size / 8 * 5 - 1, size / 8 * 5 - 1));
                setBlockConfig(spawnX, spawnY, "spawn");
                setBlockConfig(portals.get(0).getX(), portals.get(0).getY(), "portal0");
                generateEnemies();
        }
    }

    private void createArena() {
        for (int y = size / 8 * 3; y < size / 8 * 5; y++) {
            for (int x = size / 8 * 3; x < size / 8 * 5; x++) {
                setBlockWalkable(x, y, true);
            }
        }
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
                pathways.add(new Pathway(roomsList[x].centerX, roomsList[x].centerY,
                        roomsList[x + 1].centerX, roomsList[x + 1].centerY));
            }
        }
        for (int y = 0; y < rooms.length - 1; y++) {
            for (int x = 0; x < rooms[0].length; x++) {
                fillRow(rooms[y][x].centerY, rooms[y][x].centerX, rooms[y + 1][x].centerX);
                fillColumn(rooms[y + 1][x].centerX, rooms[y][x].centerY, rooms[y + 1][x].centerY);
                pathways.add(new Pathway(rooms[y][x].centerX, rooms[y][x].centerY,
                        rooms[y + 1][x].centerX, rooms[y + 1][x].centerY));
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
            setBlockWalkable(column - 1, y, true);
            setBlockWalkable(column, y, true);
            setBlockWalkable(column + 1, y, true);
        }
    }

    private void fillRow(int row, int startColumn, int endColumn) {
        if (startColumn > endColumn) {
            int t = startColumn;
            startColumn = endColumn;
            endColumn = t;
        }
        for (int x = startColumn; x <= endColumn; x++) {
            setBlockWalkable(x, row - 1, true);
            setBlockWalkable(x, row, true);
            setBlockWalkable(x, row + 1, true);
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
        chests.add(new Point(chestX, chestY));
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (getBlockWalkable(x, y) && generateRandom(0, 10000) == 0) {
                    setBlockConfig(x, y, "chest");
                    chests.add(new Point(x, y));
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
        String material;
        if (Math.random() * 5 < 4) {
            material = "stone";
        } else {
            material = "wooden";
        }
        rooms[roomY][roomX] = new Room(centerY - height, centerX - width,
                centerY + height, centerX + width, centerX, centerY, material);
        for (int y = centerY - height - 3; y <= centerY + height + 3; y++) {
            for (int x = centerX - width - 3; x <= centerX + width + 3; x++) {
                if (!getBlockWalkable(x, y)) {
                    setBlockWalkable(x, y, y >= centerY - height && y <= centerY + height &&
                            x >= centerX - width && x <= centerX + width);
                }
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
        revealedBlocks.add(y * size + x);
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

    public CopyOnWriteArrayList<Enemy> getEnemies() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Terrain terrain1 = (Terrain) o;
        return size == terrain1.size &&
                spawnX == terrain1.spawnX &&
                spawnY == terrain1.spawnY &&
                level == terrain1.level &&
                lastPortal == terrain1.lastPortal &&
                enemyRewardGotten == terrain1.enemyRewardGotten &&
                Arrays.equals(rooms, terrain1.rooms) &&
                pathways.equals(terrain1.pathways) &&
                portals.equals(terrain1.portals) &&
                chests.equals(terrain1.chests) &&
                Arrays.equals(traps, terrain1.traps) &&
                enemies.equals(terrain1.enemies) &&
                type.equals(terrain1.type) &&
                Arrays.deepEquals(terrain, terrain1.terrain);
    }

    public boolean hasBoss() {
        for (Enemy e:enemies) {
            if (e.getName().equals("KingSlime")) {
                return true;
            }
        }
        return false;
    }
}
