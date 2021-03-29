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

    public static Enemy getGreenSlime(int level, int enemyX, int enemyY) {
        return EnemyGenerator.getEnemy("GREEN SLIME", level, 40, 20,
                5, 3, 4, 2, 50,
                30, enemyX, enemyY);
    }

    public static Enemy getBlueSlime(int level, int enemyX, int enemyY) {
        return EnemyGenerator.getEnemy("BLUE SLIME", level, 30, 15,
                4, 6, 3, 1, 70,
                30, enemyX, enemyY);
    }

    public static Enemy getZombie(int level, int enemyX, int enemyY) {
        return EnemyGenerator.getEnemy("ZOMBIE", level, 100, 50,
                10, 10, 8, 5, 100,
                100, enemyX, enemyY);
    }

    public static Enemy getKingSlime(int level, int enemyX, int enemyY) {
        return EnemyGenerator.getEnemy("KING SLIME", level, 1000, 1000,
                20, 10, 10, 10, 1000,
                500, enemyX, enemyY);
    }
}
