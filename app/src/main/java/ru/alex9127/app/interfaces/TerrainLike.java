package ru.alex9127.app.interfaces;

import ru.alex9127.app.classes.Block;
import ru.alex9127.app.classes.Terrain;

public interface TerrainLike {
    void createTerrain();
    Terrain.Point getSpawnPoint();
    Terrain.Point getPortalPoint();
    Block getBlock(int x, int y);
    void setBlock(int x, int y, String floor);
    int getSize();
}
