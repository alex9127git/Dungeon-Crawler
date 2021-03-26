package ru.alex9127.app.classes;

import ru.alex9127.app.interfaces.TerrainLike;

public class Enemy extends Entity {
    private final int xpReward;

    public Enemy(String name, int hp, int atk, int def, int mana, int x, int y, int xpReward) {
        super(name, hp, atk, def, mana, x, y);
        this.xpReward = xpReward;
    }

    public void decide(Unit unit, TerrainLike terrain) {
        if (alive()) {
            if (distanceTo(unit) < 10) {
                int[][] path = Pathfinder.findPath(terrain, this, unit);
                terrain.removeBlockEntity(getX(), getY(), this);
                if (path.length > 1 && (int) (Math.random() * 10) < 9) {
                    if (getName().equals("BLUE SLIME") && path.length > 2) {
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
}
