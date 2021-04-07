package ru.alex9127.app.terrain;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import ru.alex9127.app.classes.Enemy;
import ru.alex9127.app.classes.Entity;
import ru.alex9127.app.classes.Unit;
import ru.alex9127.app.exceptions.SerializationException;
import ru.alex9127.app.interfaces.DatabaseSerializable;
import ru.alex9127.app.interfaces.Locatable;

public class Block implements Locatable, DatabaseSerializable {
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

    @Override
    public String serialize() {
        StringBuilder s = new StringBuilder(x + "," + y + "," + isWalkable + "," + material + "," + config + "," + isShown + ",");
        if (entities.isEmpty()) {
            s.append("none");
        } else {
            for (Entity e : entities) {
                s.append(e.serialize()).append("_");
            }
        }
        return s.toString();
    }

    @Override
    public DatabaseSerializable deserialize(String serialized) throws SerializationException {
        String[] data = serialized.split(",");
        if (data.length != 7) {
            throw new SerializationException("Object " + getClass() + " couldn't be deserialized: needed 7 pieces of data, got " + data.length);
        } else {
            int x = Integer.parseInt(data[0]);
            int y = Integer.parseInt(data[1]);
            boolean isWalkable = Boolean.parseBoolean(data[2]);
            String material = data[3];
            String config = data[4];
            boolean isShown = Boolean.parseBoolean(data[5]);
            String entities = data[6];
            Block b = new Block(x, y, isWalkable, material, config);
            if (isShown) b.reveal();
            if (!entities.equals("none")) {
                for (String s : entities.split("_")) {
                    if (s.split(" ").length == 12) {
                        b.addEntity((Entity) new Unit("", 0, 0, 0, 0, 0, 0).deserialize(s));
                    } else if (s.split(" ").length == 10) {
                        b.addEntity((Entity) new Enemy("", 0, 0, 0, 0, 0, 0, 0).deserialize(s));
                    }
                }
            }
            return b;
        }
    }
}
