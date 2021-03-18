package ru.alex9127.app.interfaces;

import ru.alex9127.app.classes.Block;
import ru.alex9127.app.classes.Terrain;

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
    void revealBlock(int x, int y);
}
