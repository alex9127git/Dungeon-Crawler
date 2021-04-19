package ru.alex9127.app.terrain;

import ru.alex9127.app.saving.CompactDungeon;
import ru.alex9127.app.saving.CompactTerrain;

public class Dungeon {
    private final Tree<Terrain> tree;

    public Dungeon(Terrain t) {
        tree = new Tree<>(t);
    }

    public Dungeon(CompactDungeon dungeon) {
        Tree<CompactTerrain> t = dungeon.tree;
        tree = Tree.restore(t);
    }

    public void addByPath(String path, char index, Terrain terrain) {
        tree.addByPath(path, Integer.parseInt(String.valueOf(index)), terrain);
    }

    public Terrain find(String path) {
        return tree.findValue(path);
    }

    public Tree<Terrain> getTree() {
        return tree;
    }
}
