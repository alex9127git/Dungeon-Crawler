package ru.alex9127.app.classes;

import ru.alex9127.app.interfaces.TerrainLike;

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
                xpNeeded *= 1.1;
                levelUp();
            }
        }
    }

    public void levelUp() {
        this.level += 1;
        int r = (int) (Math.random() * 4);
        switch (r) {
            case 0:
                upgradeHp(10);
                break;
            case 1:
                upgradeAtk(1);
                break;
            case 2:
                upgradeDef(1);
                break;
            case 3:
                upgradeMana(3);
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

    public String checkMove(int dx, int dy, TerrainLike terrain) {
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
            if (config.equals("spikes") && terrain instanceof Terrain) {
                ((Terrain) terrain).revealTrap(getX(), getY());
                changeHp(-10);
                return "moved onto trap";
            }
            return "moved";
        }
        return "notMoved";
    }

    @Override
    public String stats() {
        return super.stats() + "\nLEVEL: " + level + "\nXP: " + xp + "/" + xpNeeded;
    }

    public int getLevel() {
        return level;
    }
}
