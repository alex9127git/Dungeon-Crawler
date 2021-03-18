package ru.alex9127.app.drawing;

import android.graphics.*;
import android.util.Log;

public class TextImage extends Image {
    private String text;
    private final int totalTime;
    private static long time = System.currentTimeMillis();
    private int timeFromStart = 0;
    private final int x, y;

    public TextImage(String text, int time, int x, int y) {
        this.text = text;
        this.totalTime = time;
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas, Paint p) {
        update();
        if (isDrawn()) {
            float height = p.getTextSize();
            float width = p.measureText(text);
            canvas.drawText(this.text,
                    x - width / 2, y - height / 2, p);
        }
    }

    public boolean isDrawn() {
        return timeFromStart < totalTime;
    }

    public void update() {
        timeFromStart += System.currentTimeMillis() - time;
        time = System.currentTimeMillis();
    }

    public void rewind() {
        timeFromStart = 0;
        time = System.currentTimeMillis();
    }

    public void setText(String s) {
        text = s;
    }
}
