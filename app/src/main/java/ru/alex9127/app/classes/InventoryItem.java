package ru.alex9127.app.classes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class InventoryItem {
    private final String itemName;

    public InventoryItem(String itemName) {
        this.itemName = itemName;
    }

    @NonNull
    @Override
    public String toString() {
        return this.itemName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof InventoryItem) && (this.itemName.equals(((InventoryItem) obj).itemName));
    }
}
