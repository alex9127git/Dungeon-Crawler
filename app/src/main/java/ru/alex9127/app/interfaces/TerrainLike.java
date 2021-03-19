package ru.alex9127.app.interfaces;

import java.util.ArrayList;

import ru.alex9127.app.classes.*;

public interface TerrainLike {
    void createTerrain();
    Terrain.Point getSpawnPoint();
    Terrain.Point getPortalPoint();
    int getSize();
    boolean getBlockWalkable(int x, int y);
    String getBlockMaterial(int x, int y);
    String getBlockConfig(int x, int y);
    boolean isBlockRevealed(int x, int y);
    void setBlockWalkable(int x, int y, boolean isWalkable);
    void setBlockMaterial(int x, int y, String material);
    void setBlockConfig(int x, int y, String config);
    void addBlockEntity(int x, int y, Entity e);
    void removeBlockEntity(int x, int y, Entity e);
    void revealBlock(int x, int y);
    ArrayList<Enemy> getEnemies();
    void generateEnemies();
}
