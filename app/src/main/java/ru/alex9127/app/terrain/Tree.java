package ru.alex9127.app.terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.alex9127.app.saving.CompactTerrain;

public class Tree<T> {
    private T value;
    private final String path;
    private final HashMap<Integer, Tree<T>> children = new HashMap<>();

    public Tree(T value, String path) {
        this.value = value;
        this.path = path;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Tree(T value) {
        this(value, "");
    }

    public void addChild(int index, T child) {
        children.put(index, new Tree<>(child));
    }

    private void addTree(int index, Tree<T> tree) {
        children.put(index, tree);
    }

    public Tree<T> getChildTree(int index) {
        return children.get(index);
    }

    public T getValue() {
        return value;
    }

    public T findValue(String path) {
        Tree<T> t = findTree(path);
        return t == null ? null : t.getValue();
    }

    public Tree<T> findTree(String path) {
        ArrayList<Integer> p = new ArrayList<>();
        for (char c:path.toCharArray()) {
            p.add(Integer.parseInt(String.valueOf(c)));
        }
        Tree<T> t = this;
        for (int i:p) {
            t = t.getChildTree(i);
            if (t == null) break;
        }
        return t;
    }

    public void addByPath(String path, int index, T child) {
        Tree<T> t = findTree(path);
        if (t != null) {
            t.addChild(index, child);
        }
    }

    public static Tree<CompactTerrain> compact(Tree<Terrain> tree) {
        Tree<CompactTerrain> result = new Tree<>(new CompactTerrain((Terrain) tree.getValue()));
        for (Map.Entry<Integer, Tree<Terrain>> child : tree.children.entrySet()) {
            result.addTree(child.getKey(), Tree.compact(child.getValue()));
        }
        return result;
    }

    public static Tree<Terrain> restore(Tree<CompactTerrain> tree) {
        Tree<Terrain> result = new Tree<>(new Terrain((CompactTerrain) tree.getValue()));
        for (Map.Entry<Integer, Tree<CompactTerrain>> child : tree.children.entrySet()) {
            result.addTree(child.getKey(), Tree.restore(child.getValue()));
        }
        return result;
    }
}
