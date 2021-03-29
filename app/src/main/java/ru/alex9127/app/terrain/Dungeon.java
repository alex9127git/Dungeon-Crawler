package ru.alex9127.app.terrain;

import ru.alex9127.app.interfaces.TerrainLike;

public class Dungeon {
    private final Tree<TerrainLike> tree;
    public TerrainLike currentTerrain;

    public Dungeon(TerrainLike t) {
        tree = new Tree<>(t);
        setCurrentTerrain(t);
    }

    public void addByPath(String path, char index, TerrainLike terrain) {
        tree.addByPath(path, Integer.parseInt(String.valueOf(index)), terrain);
    }

    public void goTo(String path) {
        setCurrentTerrain(tree.findTerrain(path));
    }

    public void setCurrentTerrain(TerrainLike currentTerrain) {
        this.currentTerrain = currentTerrain;
    }

    public TerrainLike find(String path) {
        return tree.findTerrain(path);
    }
}
