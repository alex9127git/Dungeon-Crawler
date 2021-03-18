package ru.alex9127.app.classes;

public abstract class Entity extends Thing {
    private int hp, maxHp, atk, def, mana, maxMana;
    private final String name;

    public String getName() {
        return name;
    }

    public Entity(String name, int hp, int atk, int def, int mana, int x, int y) {
        super(x, y);
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.atk = atk;
        this.def = def;
        this.mana = mana;
        this.maxMana = mana;
    }

    public String stats() {
        return name.toUpperCase() + "\nHP: " + hp + "/" + maxHp + "\nMANA: " + mana + "/" + maxMana + "\nATK: " + atk + "\nDEF: " + def;
    }

    public double distanceTo(Entity entity) {
        return Math.sqrt(Math.abs(entity.getX() - this.getX()) * Math.abs(entity.getX() - this.getX()) +
                Math.abs(entity.getY() - this.getY()) * Math.abs(entity.getY() - this.getY()));
    }

    public int getDefensePower() {
        return def;
    }

    public int getAttackPower() {
        return atk;
    }

    public double getHpPercentage() {
        return (double) hp / maxHp;
    }

    public double getManaPercentage() {
        return (double) mana / maxMana;
    }

    public boolean alive() {
        return hp > 0;
    }

    public void changeHp(int diff) {
        hp += diff;
    }

    public void changeMana(int diff) {
        mana += diff;
    }

    public void upgradeHp(int up) {
        hp += up;
        maxHp += up;
    }

    public void upgradeAtk(int up) {
        atk += up;
    }

    public void upgradeDef(int up) {
        def += up;
    }

    public void upgradeMana(int up) {
        mana += up;
        maxMana += up;
    }
}
