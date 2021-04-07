package ru.alex9127.app.classes;

import ru.alex9127.app.exceptions.SerializationException;
import ru.alex9127.app.interfaces.DatabaseSerializable;
import ru.alex9127.app.terrain.Terrain;

public class Enemy extends Entity {
    private final int xpReward;

    public Enemy(String name, int hp, int atk, int def, int mana, int x, int y, int xpReward) {
        super(name, hp, atk, def, mana, x, y);
        this.xpReward = xpReward;
    }

    public void decide(Unit unit, Terrain terrain) {
        if (alive()) {
            if (distanceTo(unit) < 10) {
                int[][] path = Pathfinder.findPath(terrain, this, unit);
                terrain.removeBlockEntity(getX(), getY(), this);
                if (path.length > 1 && (int) (Math.random() * 10) < 9) {
                    if (getName().equals("BlueSlime") && path.length > 2) {
                        setX(path[2][0]);
                        setY(path[2][1]);
                    } else {
                        setX(path[1][0]);
                        setY(path[1][1]);
                    }
                }
                terrain.addBlockEntity(getX(), getY(), this);
            }
        }
    }

    public int getXpReward() {
        return xpReward;
    }

    @Override
    public String serialize() {
        return super.serialize() + " " + xpReward;
    }

    @Override
    public DatabaseSerializable deserialize(String serialized) throws SerializationException {
        String[] data = serialized.split(" ");
        if (data.length != 10) {
            throw new SerializationException("Object " + getClass() + " couldn't be deserialized: needed 10 pieces of data, got " + data.length);
        } else {
            String name = data[0];
            int x = Integer.parseInt(data[1]);
            int y = Integer.parseInt(data[2]);
            int hp = Integer.parseInt(data[3]);
            int maxHp = Integer.parseInt(data[4]);
            int atk = Integer.parseInt(data[5]);
            int def = Integer.parseInt(data[6]);
            int mana = Integer.parseInt(data[7]);
            int maxMana = Integer.parseInt(data[8]);
            int xpReward = Integer.parseInt(data[9]);
            Enemy e = new Enemy(name, maxHp, atk, def, maxMana, x, y, xpReward);
            e.changeHp(-1 * (maxHp - hp));
            e.changeMana(-1 * (maxMana - mana));
            return e;
        }
    }
}
