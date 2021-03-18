package ru.alex9127.app.drawing;

import android.graphics.*;

public class DefaultImage extends Image {
    private final Bitmap bitmap;
    private final int width;
    private final int height;

    public DefaultImage(Bitmap bitmap, int width, int height) {
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
    }

    public void draw(Canvas canvas, int x, int y) {
        canvas.drawBitmap(this.bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new Rect(x - width / 2, y - height / 2,
                        x + width / 2, y + height / 2), new Paint());
    }
}
