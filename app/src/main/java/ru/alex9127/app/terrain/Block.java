package ru.alex9127.app.terrain;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import ru.alex9127.app.classes.Entity;
import ru.alex9127.app.interfaces.Locatable;

public class Block implements Locatable {
    private int x;
    private int y;
    private boolean isWalkable;
    private String material;
    private String config;
    private boolean isShown;
    private final ArrayList<Entity> entities;

    public Block(int x, int y, boolean isWalkable, String material, String config) {
        setX(x);
        setY(y);
        this.isWalkable = isWalkable;
        this.material = material;
        this.config = config;
        this.isShown = false;
        this.entities = new ArrayList<>();
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public boolean isShown() {
        return isShown;
    }

    @NonNull
    public String getMaterial() {
        return material;
    }

    @NonNull
    public String getConfig() {
        return config;
    }

    public void setWalkable(boolean walkable) {
        isWalkable = walkable;
    }

    public void reveal() {
        isShown = true;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public void addEntity(Entity e) {
        this.entities.add(e);
    }

    public void removeEntity(Entity e) {
        this.entities.remove(e);
    }
}
