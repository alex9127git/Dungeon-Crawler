package ru.alex9127.app.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class StaticImage extends Image {
    private final Bitmap bitmap;
    private final int width;
    private final int height;
    private final int defaultX;
    private final int defaultY;
    private final Rect boundaryRect;

    public StaticImage(Bitmap bitmap, int width, int height, int defaultX, int defaultY) {
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
        this.defaultX = defaultX;
        this.defaultY = defaultY;
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
}
