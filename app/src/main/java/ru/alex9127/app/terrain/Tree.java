package ru.alex9127.app.terrain;

import java.util.ArrayList;
import java.util.HashMap;

public class Tree<T> {
    private final T value;
    private final HashMap<Integer, Tree<T>> children = new HashMap<>();

    public Tree(T value) {
        this.value = value;
    }

    public void addChild(int index, T child) {
        children.put(index, new Tree<>(child));
    }

    public Tree<T> getChildTree(int index) {
        return children.get(index);
    }

    public T getValue() {
        return value;
    }

    public T findTerrain(String path) {
        ArrayList<Integer> p = new ArrayList<>();
        for (char c:path.toCharArray()) {
            p.add(Integer.parseInt(String.valueOf(c)));
        }
        Tree<T> t = this;
        for (int i:p) {
            t = t.getChildTree(i);
            if (t == null) break;
        }
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

    public void addByPath(String path, int index, T terrain) {
        Tree<T> t = findTree(path);
        if (t != null) {
            t.addChild(index, terrain);
        }
    }
}
