package at.bleeding182.samples.spinningstar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

/**
 * @author David Medenjak on 24.12.2015.
 *
 * Proof of concept for http://stackoverflow.com/questions/34536075/modifying-the-resource-image-of-progress-bar/34590765#34590765
 */
public class RectProgressDrawable extends Drawable implements Runnable, Animatable {
    private static final long FRAME_DELAY = 1000 / 60;
    private boolean mRunning = false;
    private long mStartTime;
    private int mDuration = 1000;

    private Paint mPaint;

    private float[] posX = new float[16];
    private float[] posY = new float[16];
    private float mSize;

    private void init() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
        }
        Rect bounds = getBounds();
        float cellWidth = ((float) bounds.width()) / 5f;
        float cellHeight = ((float) bounds.height()) / 5f;

        float min = Math.min(cellWidth, cellHeight);
        mSize = (min * 0.9f) / 4;

        for (int i = 0; i < 5; i++) { // top row
            posX[i] = bounds.left + cellWidth * (float) i + cellWidth / 2;
            posY[i] = cellHeight / 2;
        }
        for (int i = 0; i < 3; i++) { // sides
            // right side top bottom
            posX[5 + i] = cellWidth * 4 + cellWidth / 2;
            posY[5 + i] = cellHeight * (i + 1) + cellHeight / 2;
            //left side bottom top
            posX[13 + i] = cellWidth / 2;
            posY[13 + i] = cellHeight * (3 - i) + cellHeight / 2;
        }
        for (int i = 0; i < 5; i++) { // bottom from right to left
            posX[8 + i] = cellWidth * (4 - i) + cellWidth / 2;
            posY[8 + i] = cellHeight * 4 + cellHeight / 2;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();

        if (isRunning()) {
            // animation in progress
            final int save = canvas.save();
            for (int i = 0; i < 16; i++) {
                long timeDiff = SystemClock.uptimeMillis() - mStartTime;
                float progress = ((float) timeDiff) / ((float) mDuration) * 16;
                int level = ((int) (progress + 0.5)) % 16;
                if (i >= level && i < level + 5) {
                    float num = i - level; // 0..5
                    float size = mSize * (1 + (num * 0.2f));
                    canvas.drawCircle(posX[i], posY[i], size, mPaint);
                } else {
                    canvas.drawCircle(posX[i], posY[i], mSize, mPaint);
                }
            }

            canvas.restoreToCount(save);
        } else {
            // draw normal
            for (int i = 0; i < 16; i++) {
                canvas.drawCircle(posX[i], posY[i], mSize, mPaint);
            }
        }
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        init();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void start() {
        if (mRunning) stop();
        mRunning = true;
        mStartTime = SystemClock.uptimeMillis();
        invalidateSelf();
        scheduleSelf(this, SystemClock.uptimeMillis() + FRAME_DELAY);
    }

    @Override
    public void stop() {
        unscheduleSelf(this);
        mRunning = false;
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void run() {
        invalidateSelf();
        long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis + FRAME_DELAY < mStartTime + mDuration) {
            scheduleSelf(this, uptimeMillis + FRAME_DELAY);
        } else {
            mRunning = false;
            start();
        }
    }
}
