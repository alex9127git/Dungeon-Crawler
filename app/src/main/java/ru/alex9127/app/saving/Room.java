package ru.alex9127.app.saving;

public class Room {
    public final int top;
    public final int left;
    public final int bottom;
    public final int right;
    public final int width;
    public final int height;
    public final int centerX;
    public final int centerY;
    public final String material;

    public Room(int top, int left, int bottom, int right, int centerX, int centerY, String material) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.width = right - left + 1;
        this.height = bottom - top + 1;
        this.centerX = centerX;
        this.centerY = centerY;
        this.material = material;
    }
}
