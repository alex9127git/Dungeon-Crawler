package ru.alex9127.app.classes;

import ru.alex9127.app.interfaces.Movable;

@SuppressWarnings("unused")
public class Thing implements Movable {
    private int x;
    private int y;
    public Thing(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
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
}
