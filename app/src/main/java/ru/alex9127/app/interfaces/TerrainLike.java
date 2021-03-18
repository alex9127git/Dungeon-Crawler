package ru.alex9127.app.interfaces;

import ru.alex9127.app.classes.Block;
import ru.alex9127.app.classes.Terrain;

public interface TerrainLike {
    void createTerrain();
    Terrain.Point getSpawnPoint();
    Terrain.Point getPortalPoint();
    Block getBlock(int x, int y);
    int getSize();
    public boolean getBlockWalkable(int x, int y);
    public String getBlockMaterial(int x, int y);
    public String getBlockConfig(int x, int y);
    public void setBlockWalkable(int x, int y, boolean isWalkable);
    public void setBlockMaterial(int x, int y, String material);
    public void setBlockConfig(int x, int y, String config);
}
