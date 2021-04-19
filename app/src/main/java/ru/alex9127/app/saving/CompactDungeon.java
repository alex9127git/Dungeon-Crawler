package ru.alex9127.app.saving;

import java.util.Map;

import ru.alex9127.app.terrain.*;

public class CompactDungeon {
    public final Tree<CompactTerrain> tree;

    public CompactDungeon(Dungeon d) {
        Tree<Terrain> t = d.getTree();
        tree = Tree.compact(t);
    }
}
