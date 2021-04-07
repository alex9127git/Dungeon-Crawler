package ru.alex9127.app.terrain;

public class Dungeon {
    private final Tree<Terrain> tree;
    public Terrain currentTerrain;

    public Dungeon(Terrain t) {
        tree = new Tree<>(t);
        setCurrentTerrain(t);
    }

    public void addByPath(String path, char index, Terrain terrain) {
        tree.addByPath(path, Integer.parseInt(String.valueOf(index)), terrain);
    }

    public void goTo(String path) {
        setCurrentTerrain(tree.findTerrain(path));
    }

    public void setCurrentTerrain(Terrain currentTerrain) {
        this.currentTerrain = currentTerrain;
    }

    public Terrain find(String path) {
        return tree.findTerrain(path);
    }
}
