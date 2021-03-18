package ru.alex9127.app.classes;

import java.util.Random;

public class EnemyGenerator {
    private static final Random random = new Random();
    public static Enemy getEnemy(String name, int level, int startHp, int varyHp, int startAtk,
                          int varyAtk, int startDef, int varyDef, int startXp, int varyXp,
                          int enemyX, int enemyY) {
        return new Enemy(name,
                (int) (startHp + random.nextInt(varyHp) * (1 + level * 0.5)),
                (int) (startAtk + random.nextInt(varyAtk) * (1 + level * 0.5)),
                (int) (startDef + random.nextInt(varyDef) * (1 + level * 0.5)), 0,
                enemyX, enemyY, (startXp + random.nextInt(varyXp) * level));
    }
}
