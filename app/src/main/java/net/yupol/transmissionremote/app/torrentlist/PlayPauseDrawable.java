package net.yupol.transmissionremote.app.torrentlist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Property;

public class PlayPauseDrawable extends Drawable {

    private static final String TAG = PlayPauseDrawable.class.getSimpleName();

    private Paint paint = new Paint();
    private Paint borderPaint = new Paint();
    private Path leftPauseBar = new Path();
    private Path rightPauseBar = new Path();
    private RectF bounds = new RectF();
    private RectF circleBounds = new RectF();
    private PointF leftBarTopLeftStart = new PointF();
    private PointF leftBarTopLeftEnd = new PointF();
    private PointF leftBarTopRightStart = new PointF();
    private PointF leftBarTopRightEnd = new PointF();
    private PointF leftBarBottomRightStart = new PointF();
    private PointF leftBarBottomRightEnd = new PointF();
    private PointF leftBarBottomLeftStart = new PointF();
    private PointF leftBarBottomLeftEnd = new PointF();
    private PointF rightBarTopLeftStart = new PointF();
    private PointF rightBarTopLeftEnd = new PointF();
    private PointF rightBarTopRightStart = new PointF();
    private PointF rightBarTopRightEnd = new PointF();
    private PointF rightBarBottomRightStart = new PointF();
    private PointF rightBarBottomRightEnd = new PointF();
    private PointF rightBarBottomLeftStart = new PointF();
    private PointF rightBarBottomLeftEnd = new PointF();

    private float progress = 0f;
    private boolean paused = false;

    private static final Property<PlayPauseDrawable, Float> PROGRESS_PROPERTY = new Property<PlayPauseDrawable, Float>(Float.class, "progress") {
        @Override
        public Float get(PlayPauseDrawable d) {
            return d.progress;
        }

        @Override
        public void set(PlayPauseDrawable d, Float value) {
            d.progress = value;
            d.invalidateSelf();
            Log.d(TAG, "progress: " + d.progress);
        }
    };

    public PlayPauseDrawable(Context context) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);

        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.DKGRAY);
        borderPaint.setStrokeWidth(2);
    }

    @Override
    public void draw(Canvas canvas) {
        float w = bounds.width();
        float h = bounds.height();
        circleBounds.set(3, 3, w - 6, h - 6);

        float leftBarTopLeftX = interpolate(leftBarTopLeftStart.x, leftBarTopLeftEnd.x, progress);
        float leftBarTopLeftY = interpolate(leftBarTopLeftStart.y, leftBarTopLeftEnd.y, progress);
        float leftBarTopRightX = interpolate(leftBarTopRightStart.x, leftBarTopRightEnd.x, progress);
        float leftBarTopRightY = interpolate(leftBarTopRightStart.y, leftBarTopRightEnd.y, progress);
        float leftBarBottomRightX = interpolate(leftBarBottomRightStart.x, leftBarBottomRightEnd.x, progress);
        float leftBarBottomRightY = interpolate(leftBarBottomRightStart.y, leftBarBottomRightEnd.y, progress);
        float leftBarBottomLeftX = interpolate(leftBarBottomLeftStart.x, leftBarBottomLeftEnd.x, progress);
        float leftBarBottomLeftY = interpolate(leftBarBottomLeftStart.y, leftBarBottomLeftEnd.y, progress);

        leftPauseBar.rewind();
        leftPauseBar.moveTo(leftBarTopLeftX, leftBarTopLeftY);
        leftPauseBar.lineTo(leftBarTopRightX, leftBarTopRightY);
        leftPauseBar.lineTo(leftBarBottomRightX, leftBarBottomRightY);
        leftPauseBar.lineTo(leftBarBottomLeftX, leftBarBottomLeftY);
        leftPauseBar.close();
        canvas.drawPath(leftPauseBar, paint);

        float rightBarTopLeftX = interpolate(rightBarTopLeftStart.x, rightBarTopLeftEnd.x, progress);
        float rightBarTopLeftY = interpolate(rightBarTopLeftStart.y, rightBarTopLeftEnd.y, progress);
        float rightBarTopRightX = interpolate(rightBarTopRightStart.x, rightBarTopRightEnd.x, progress);
        float rightBarTopRightY = interpolate(rightBarTopRightStart.y, rightBarTopRightEnd.y, progress);
        float rightBarBottomRightX = interpolate(rightBarBottomRightStart.x, rightBarBottomRightEnd.x, progress);
        float rightBarBottomRightY = interpolate(rightBarBottomRightStart.y, rightBarBottomRightEnd.y, progress);
        float rightBarBottomLeftX = interpolate(rightBarBottomLeftStart.x, rightBarBottomLeftEnd.x, progress);
        float rightBarBottomLeftY = interpolate(rightBarBottomLeftStart.y, rightBarBottomLeftEnd.y, progress);

        rightPauseBar.rewind();
        rightPauseBar.moveTo(rightBarTopLeftX, rightBarTopLeftY);
        rightPauseBar.lineTo(rightBarTopRightX, rightBarTopRightY);
        rightPauseBar.lineTo(rightBarBottomRightX, rightBarBottomRightY);
        rightPauseBar.lineTo(rightBarBottomLeftX, rightBarBottomLeftY);
        rightPauseBar.close();
        canvas.drawPath(rightPauseBar, paint);

        canvas.drawOval(circleBounds, borderPaint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.bounds.set(bounds);

        float w = bounds.width();
        float h = bounds.height();

        leftBarTopLeftStart.set(0.3f * w, 0.3f * h);
        leftBarTopLeftEnd.set(leftBarTopLeftStart);
        leftBarTopRightStart.set(leftBarTopLeftStart);
        leftBarTopRightStart.offset(0.15f * w, 0);
        leftBarTopRightEnd.set(0.5f * w, 0.4f * h);
        leftBarBottomLeftStart.set(leftBarTopLeftStart.x, leftBarTopLeftStart.y + 0.4f * h);
        leftBarBottomLeftEnd.set(leftBarBottomLeftStart);
        leftBarBottomRightStart.set(leftBarBottomLeftStart.x + 0.15f * w, leftBarBottomLeftStart.y);
        leftBarBottomRightEnd.set(0.5f * w, 0.6f * h);

        rightBarTopLeftStart.set(0.55f * w, 0.3f * h);
        rightBarTopLeftEnd.set(leftBarTopRightEnd);
        rightBarTopRightStart.set(rightBarTopLeftStart.x + 0.15f * w, rightBarTopLeftStart.y);
        rightBarTopRightEnd.set(rightBarTopRightStart.x, 0.5f * h);
        rightBarBottomRightStart.set(rightBarTopRightStart.x, rightBarTopRightStart.y + 0.4f * h);
        rightBarBottomRightEnd.set(rightBarTopRightEnd);
        rightBarBottomLeftStart.set(rightBarTopLeftStart.x, rightBarBottomRightStart.y);
        rightBarBottomLeftEnd.set(leftBarBottomRightEnd);

        float offsetX = 0.05f * w;
        leftBarTopLeftEnd.offset(offsetX, 0);
        leftBarTopRightEnd.offset(offsetX, 0);
        leftBarBottomRightEnd.offset(offsetX, 0);
        leftBarBottomLeftEnd.offset(offsetX, 0);
        rightBarTopLeftEnd.offset(offsetX, 0);
        rightBarTopRightEnd.offset(offsetX, 0);
        rightBarBottomRightEnd.offset(offsetX, 0);
        rightBarBottomLeftEnd.offset(offsetX, 0);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        progress = paused ? 0f : 1f;
        invalidateSelf();
    }

    public Animator getAnimator() {
        Animator animator = ObjectAnimator.ofFloat(this, PROGRESS_PROPERTY, paused ? 0 : 1, paused ? 1 : 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                paused = !paused;
            }
        });
        return animator;
    }

    private static float interpolate(float a, float b, float progress) {
        return a + (b - a) * progress;
    }
}
