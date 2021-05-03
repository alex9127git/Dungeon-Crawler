package ru.alex9127.app.saving;

import ru.alex9127.app.classes.*;
import ru.alex9127.app.terrain.Dungeon;

public class Save {
    public Unit unit;
    public int level;
    public int floor;
    public final CompactDungeon dungeon;
    public String path;
    public int coinsGotten;
    public int bossesDefeated;

    public Save(GameLogic game) {
        this.unit = game.unit;
        this.level = game.level;
        this.floor = game.floor;
        this.dungeon = new CompactDungeon(game.dungeon);
        this.path = game.path;
        this.coinsGotten = game.coinsGotten;
        this.bossesDefeated = game.bossesDefeated;
    }
}
