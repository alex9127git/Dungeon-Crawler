package ru.alex9127.app.classes;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Inventory {
    private final ArrayList<InventoryItem> list;
    private final int maxSize;

    public Inventory(int maxSize) {
        this.list = new ArrayList<>();
        this.maxSize = maxSize;
    }

    public void add(InventoryItem item) {
        if (this.list.size() < maxSize) {
            list.add(item);
        }
    }

    public void remove(InventoryItem item) {
        list.remove(item);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (InventoryItem item:this.list) {
            s.append(item).append(" ");
        }
        return s.toString();
    }
}
