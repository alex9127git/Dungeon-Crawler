package ru.alex9127.app.classes;

import android.util.Log;

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
                if (getName().equals("KingSlime")) {
                    int random = (int) (Math.random() * 10);
                    Log.v("LOG", String.valueOf(random));
                    if ((int) (Math.random() * 10) == 9) {
                        for (int i = 0; i < 3; i++) {
                            int x = getX() + (int) (Math.random() * 20 - 10);
                            int y = getY() + (int) (Math.random() * 20 - 10);
                            if (terrain.getBlockWalkable(x, y)) {
                                Enemy e = EnemyGenerator.getGreenSlime(terrain.level, x, y);
                                terrain.addBlockEntity(x, y, e);
                                terrain.enemies.add(e);
                            }
                        }
                    }
                }
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
}
