package at.bleeding182.samples.spinningstar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * @author David Medenjak on 1/6/2016.
 */
public class PathProgressDrawable extends Drawable implements Runnable, Animatable {
    private static final long FRAME_DELAY = 1000 / 60;
    private static final String TAG = "RectProgressDrawable";
    private boolean mRunning = false;
    private long mStartTime;
    private int mDuration = 1000 * 3;

    private Paint mPaint;

    private float mSize;
    private int mPoints = 4 * 4;

    float[] mFactor = new float[]{
            1f,
            1.2f,
            1.4f,
            1.6f,
            1.8f,
            2f,
            1f
    };

    int mColorBase = Color.WHITE;
    int mColorAccent = Color.BLUE;

    /**
     * The padding in px.
     */
    private int mPadding = 12;
    private PathMeasure mMeasure;
    private float mLength;
    private float mDistance;
    private float[] pos = new float[2];
    private float[] tan = new float[2];

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

        Rect bounds = new Rect();
        bounds.set(getBounds());
        bounds.inset(mPadding, mPadding);

        Path path = new Path(); // rectangle
        path.moveTo(bounds.left, bounds.top);
        path.lineTo(bounds.right, bounds.top);
        path.lineTo(bounds.right, bounds.bottom);
        path.lineTo(bounds.left, bounds.bottom);
        path.close();

        path = new Path();
        path.addOval(new RectF(bounds), Path.Direction.CW);

        mMeasure = new PathMeasure(path, true);
        float[] pos1 = new float[2];
        float[] pos2 = new float[2];
        mLength = mMeasure.getLength();
        mDistance = mLength / (float) mPoints;

        mMeasure.getPosTan(0, pos1, tan);
        mMeasure.getPosTan(mDistance, pos2, tan);

        float diffX = pos2[0] - pos1[0];
        float diffY = pos2[1] - pos1[1];

        mSize = ((float) Math.sqrt(diffX * diffX + diffY * diffY) / 4) * 0.9f;
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        if (isRunning()) {
            // animation in progress
            final int save = canvas.save();

            long timeDiff = SystemClock.uptimeMillis() - mStartTime;

            float progress = ((float) timeDiff) / ((float) mDuration); // 0..1
            int level = ((int) (progress * mPoints)) % mPoints;
            float levelProgress = progress * mPoints - (int) (progress * mPoints);

            float interpolatedDistance = mDistance * progress;

            int endLevelAnimation = level + mFactor.length - 1;

            for (int i = 0; i < mPoints; i++) {
                mMeasure.getPosTan(((mDistance * i - interpolatedDistance) + mLength) % mLength, pos, tan);

                if ((i >= level && i < endLevelAnimation)
                        || endLevelAnimation > mPoints && i + mPoints < endLevelAnimation) {
                    int num = (i - level + mPoints) % mPoints; // 0..5

                    float size = mSize * mFactor[num + 1] * (1 - levelProgress) + mSize * mFactor[num] * levelProgress;

                    canvas.drawCircle(pos[0], pos[1], size, mPaint);
                } else {
                    mPaint.setColor(mColorBase);
                    canvas.drawCircle(pos[0], pos[1], mSize, mPaint);
                }
            }

            canvas.restoreToCount(save);
        } else {
            // draw normal
            float distance = mLength / (float) mPoints;
            float[] pos = new float[2];
            float[] tan = new float[2];
            for (int i = 0; i < mPoints; i++) {
                mMeasure.getPosTan(distance * i, pos, tan);
                canvas.drawCircle(pos[0], pos[1], mSize, mPaint);
            }
        }
        mPaint.setStyle(Paint.Style.STROKE);
        if (BuildConfig.DEBUG) {
            canvas.drawRect(getBounds(), mPaint);
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
        float dist = 1f / (float) (animatedPoints - 2);
//        Interpolator interpolator = new AccelerateInterpolator();
//        Interpolator interpolator = new DecelerateInterpolator();
        Interpolator interpolator = new AnticipateInterpolator();

        mFactor = new float[animatedPoints];
        for (int i = 0; i < animatedPoints - 1; i++) {
            mFactor[i] = 1f + interpolator.getInterpolation(dist * i);
        }
        mFactor[animatedPoints - 1] = 1f;
    }
}
