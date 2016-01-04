package at.bleeding182.samples.spinningstar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.Interpolator;

/**
 * @author David Medenjak on 24.12.2015.
 */
public class SpinningStarDrawable extends Drawable implements Runnable, Animatable {
    private static final long FRAME_DELAY = 1000 / 60;
    private boolean mRunning = false;
    private long mStartTime;
    private int mDuration = 1000;

    Interpolator mInterpolator = new AnticipateOvershootInterpolator();
    private Path mPath;
    private Paint mPaint;

    private void init() {
        if (mPaint == null) {
            initPaint();
        }

        mPath = new Path();
        Rect bounds = new Rect();
        bounds.set(getBounds());
        bounds.inset(10, 10); // apply some padding

        final int x = 3;
        final float angle = 360 / x;
        final double rads = Math.toRadians(angle);

        float exactCenterX = bounds.exactCenterX();
        float exactCenterY = bounds.exactCenterY();
        float widthOffset = (float) ((bounds.width() / 2) * Math.sin(rads));
        float heightOffset = (float) ((bounds.height() / 2) * Math.cos(rads));

        // move to the first point
        mPath.moveTo(exactCenterX - widthOffset, exactCenterY - heightOffset);
        for (int i = 2; i <= x; i++) {
            // draw the other 2 points
            mPath.lineTo((float) (exactCenterX - ((bounds.width() / 2) * Math.sin(rads * i))),
                    (float) (exactCenterY - ((bounds.height() / 2) * Math.cos(rads * i))));
        }

        mPath.moveTo(exactCenterX + widthOffset, exactCenterY + heightOffset);
        for (int i = 2; i <= x; i++) {
            mPath.lineTo((float) (exactCenterX + ((bounds.width() / 2) * Math.sin(rads * i))),
                    (float) (exactCenterY + ((bounds.height() / 2) * Math.cos(rads * i))));
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();

        if (isRunning()) {
            final float elapsed = SystemClock.uptimeMillis() - mStartTime;
            final float rawProgress = elapsed / (float) mDuration;
            final float progress = mInterpolator.getInterpolation(rawProgress);
            final int save = canvas.save();
            canvas.rotate(progress * 360, bounds.exactCenterX(), bounds.exactCenterY());

            canvas.drawPath(mPath, mPaint);

            canvas.restoreToCount(save);
        } else {
            canvas.drawPath(mPath, mPaint);
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
        if (isRunning()) {
            stop();
        }
        mRunning = true;
        mStartTime = SystemClock.uptimeMillis();
        invalidateSelf();
        scheduleSelf(this, mStartTime + FRAME_DELAY);
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
    public int getIntrinsicHeight() {
        return 500;
    }

    @Override
    public int getIntrinsicWidth() {
        return 500;
    }

    @Override
    public void run() {
        invalidateSelf();
        long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis + FRAME_DELAY < mStartTime + mDuration) {
            scheduleSelf(this, uptimeMillis + FRAME_DELAY);
        } else {
            mRunning = false;
        }
    }
}
