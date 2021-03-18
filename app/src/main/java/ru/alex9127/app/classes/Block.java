package ru.alex9127.app.classes;

import androidx.annotation.NonNull;

import ru.alex9127.app.interfaces.Locatable;

public class Block implements Locatable {
    private int x;
    private int y;
    private final String type;

    public Block(int x, int y, String type) {
        setX(x);
        setY(y);
        this.type = type;
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

    @NonNull
    public String getType() {
        return type;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
