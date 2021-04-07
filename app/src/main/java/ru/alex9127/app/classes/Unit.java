package ru.alex9127.app.classes;

import ru.alex9127.app.exceptions.SerializationException;
import ru.alex9127.app.interfaces.DatabaseSerializable;
import ru.alex9127.app.terrain.Terrain;

public class Unit extends Entity {
    private int level, xp, xpNeeded;
    private final Inventory inventory;

    public Unit(String name, int hp, int atk, int def, int mana, int x, int y) {
        super(name, hp, atk, def, mana, x, y);
        this.inventory = new Inventory(10);
        this.level = 1;
        this.xp = 0;
        this.xpNeeded = 10;
    }

    public void addXp(int xp) {
        this.xp += xp;
        if (this.xp >= xpNeeded) {
            while (this.xp >= xpNeeded) {
                this.xp -= xpNeeded;
                xpNeeded *= 1.5;
                levelUp();
            }
        }
    }

    public void levelUp() {
        this.level += 1;
        int r = (int) (Math.random() * 4);
        switch (r) {
            case 0:
                upgradeHp(20);
                break;
            case 1:
                upgradeAtk(3);
                break;
            case 2:
                upgradeDef(3);
                break;
            case 3:
                upgradeMana(1);
                break;
        }
    }

    public void pickUp(InventoryItem item) {
        this.inventory.add(item);
    }

    public void useItem(InventoryItem item) {
        this.inventory.remove(item);
    }

    public void throwItem(InventoryItem item) {
        this.inventory.remove(item);
    }

    public String checkMove(int dx, int dy, Terrain terrain) {
        terrain.removeBlockEntity(getX(), getY(), this);
        if (terrain.getBlockWalkable(this.getX() + dx, this.getY() + dy)) {
            String config = terrain.getBlockConfig(this.getX() + dx, this.getY() + dy);
            move(dx, dy);
            if (config.equals("chest")) {
                int r = (int) (Math.random() * 2);
                if (r == 0) {
                    upgradeAtk((int) (3 + (int) (Math.random() * 3) * (1 + (level * 0.2))));
                    return "atk";
                } else if (r == 1) {
                    upgradeDef((int) (1 + (int) (Math.random() * 3) * (1 + (level * 0.2))));
                    return "def";
                }
            }
            if (config.equals("spikes")) {
                terrain.revealTrap(getX(), getY());
                changeHp(-10);
                return "moved onto trap";
            }
            return "moved";
        }
        terrain.removeBlockEntity(getX(), getY(), this);
        return "notMoved";
    }

    @Override
    public String stats() {
        return super.stats() + "\nLEVEL: " + level + "\nXP: " + xp + "/" + xpNeeded;
    }

    @Override
    public String serialize() {
        return super.serialize() + "," + level + "," + xp + "," + xpNeeded;
    }

    @Override
    public DatabaseSerializable deserialize(String serialized) throws SerializationException {
        String[] data = serialized.split(",");
        if (data.length != 12) {
            throw new SerializationException("Object " + getClass() + " couldn't be deserialized: needed 12 pieces of data, got " + data.length);
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
            int level = Integer.parseInt(data[9]);
            int xp = Integer.parseInt(data[10]);
            int xpNeeded = Integer.parseInt(data[11]);
            Unit u = new Unit(name, maxHp, atk, def, maxMana, x, y);
            u.level = level;
            u.xp = xp;
            u.xpNeeded = xpNeeded;
            u.changeHp(-1 * (maxHp - hp));
            u.changeMana(-1 * (maxMana - mana));
            return u;
        }
    }
}
