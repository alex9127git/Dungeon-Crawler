package ru.alex9127.app.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class StaticImage extends Image {
    private final Bitmap bitmap;
    private final int width;
    private final int height;
    private int defaultX;
    private int defaultY;
    private Rect boundaryRect;

    public StaticImage(Bitmap bitmap, int width, int height, int defaultX, int defaultY) {
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
        this.defaultX = defaultX;
        this.defaultY = defaultY;
        updateBoundaryRect();
    }

    private void updateBoundaryRect() {
        this.boundaryRect = new Rect(defaultX - width / 2, defaultY - height / 2,
                defaultX + width / 2, defaultY + height / 2);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new Rect(defaultX - width / 2, defaultY - height / 2,
                        defaultX + width / 2, defaultY + height / 2), new Paint());
    }

    public Rect getBoundaryRect() {
        return boundaryRect;
    }

    public void setDefaultX(int defaultX) {
        this.defaultX = defaultX;
        updateBoundaryRect();

    }

    public void setDefaultY(int defaultY) {
        this.defaultY = defaultY;
        updateBoundaryRect();
    }

    public StaticImage clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException ignored) {}
        return new StaticImage(this.bitmap, this.width, this.height, this.defaultX, this.defaultY);
    }
}
