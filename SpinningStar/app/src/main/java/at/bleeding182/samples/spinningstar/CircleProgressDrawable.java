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
 *         <p/>
 *         Proof of concept for http://stackoverflow.com/questions/34536075/modifying-the-resource-image-of-progress-bar/34590765#34590765
 */
public class CircleProgressDrawable extends Drawable implements Runnable, Animatable {
    private static final long FRAME_DELAY = 1000 / 60;
    private static final String TAG = "RectProgressDrawable";
    private boolean mRunning = false;
    private long mStartTime;
    private int mDuration = 1000;

    private Paint mPaint;

    private float[] posX;
    private float[] posY;
    private float mSize;
    private int mPoints = 5;

    /**
     * The padding in px.
     */
    private int mPadding = 4;
    private int mAnimatedPoints = 5;

    public void setPoints(int points) {
        if (points != mPoints) {
            mPoints = points;
            init();
        }
    }

    private void init() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
        }

        int count = (mPoints - 1) * 4;
        posX = new float[count];
        posY = new float[count];

        Rect bounds = new Rect();
        bounds.set(getBounds());
        bounds.inset(mPadding, mPadding);

        // circumference
        float circ = (float) (Math.PI * (float) Math.min(bounds.width(), bounds.height()));
        float arcLength = circ / count;

        mSize = (float) ((arcLength ) / (2*Math.PI));

        double degrees = Math.toRadians((float) 360 / (float) count);
        for (int i = 0; i < count; i++) {
            posX[i] = (float) (bounds.exactCenterX() - (bounds.width() / 2) * Math.sin(degrees * i));
            posY[i] = (float) (bounds.exactCenterY() + (bounds.height() / 2) * Math.cos(degrees * i));
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (isRunning()) {
            // animation in progress
            final int save = canvas.save();

            long timeDiff = SystemClock.uptimeMillis() - mStartTime;

            float progress = ((float) timeDiff) / ((float) mDuration); // 0..1
            int level = ((int) (progress * posX.length)) % posX.length; // current value 0..posX.length

            for (int i = 0; i < posX.length; i++) {
                if ((i >= level && i < level + mAnimatedPoints) || level + mAnimatedPoints > posX.length && i < (level + mAnimatedPoints) % posX.length) {
                    float num = (i - level + posX.length) % posX.length; // 0..5
                    float size = mSize * (1 + (num * (1f / mAnimatedPoints)));
                    float sizeNext = mSize * (1 + ((num + 1) * (1f / mAnimatedPoints)));

                    float levelProgress = progress * posX.length - (int) (progress * posX.length);
                    float currentSize;
                    if (num == (mAnimatedPoints - 1)) {
                        // grow to next size
                        currentSize = mSize + (size - mSize) * levelProgress;
                    } else {
                        // shrink
                        currentSize = size + (sizeNext - size) * (1 - levelProgress);
                    }

                    canvas.drawCircle(posX[i], posY[i], currentSize, mPaint);
                } else {
                    canvas.drawCircle(posX[i], posY[i], mSize, mPaint);
                }
            }

            canvas.restoreToCount(save);
        } else {
            // draw normal
            for (int i = 0; i < posX.length; i++) {
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

    public void setAnimatedPoints(int animatedPoints) {
        mAnimatedPoints = animatedPoints;
    }
}
