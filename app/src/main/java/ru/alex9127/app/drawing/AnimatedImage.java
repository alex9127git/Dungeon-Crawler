package ru.alex9127.app.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class AnimatedImage extends Image {
    private final Bitmap bitmap;
    private final int width;
    private final int height;
    private final int framesNumber;
    private final int frameTime;
    private int currentFrame;
    private int animationCycles;
    private final Rect[] frames;
    private static long time = System.currentTimeMillis();
    private int timeFromStart = 0;

    public AnimatedImage(Bitmap bitmap, int frameWidth, int frameHeight, int framesNumber, int frameTime) {
        this.bitmap = bitmap;
        this.width = frameWidth;
        this.height = frameHeight;
        this.framesNumber = framesNumber;
        this.frameTime = frameTime;
        this.frames = new Rect[framesNumber];
        for (int i = 0; i < framesNumber; i++) {
            frames[i] = new Rect(bitmap.getWidth() / framesNumber * i, 0, bitmap.getWidth() / framesNumber * (i + 1), bitmap.getHeight());
        }
    }

    public void draw(Canvas canvas, int x, int y) {
        update();
        canvas.drawBitmap(this.bitmap, frames[currentFrame],
                new Rect(x - width / 2, y - height / 2,
                        x + width / 2, y + height / 2), new Paint());
    }

    public void update() {
        timeFromStart += System.currentTimeMillis() - time;
        time = System.currentTimeMillis();
        currentFrame = (timeFromStart % (frameTime * framesNumber)) / frameTime;
        animationCycles = timeFromStart / (frameTime * framesNumber);
    }

    public int getAnimationCycles() {
        return animationCycles;
    }

    public void rewind() {
        timeFromStart = 0;
        time = System.currentTimeMillis();
        currentFrame = 0;
        animationCycles = 0;
    }

    public AnimatedImage clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException ignored) {}
        return new AnimatedImage(this.bitmap, this.width, this.height, this.framesNumber, this.frameTime);
    }
}
