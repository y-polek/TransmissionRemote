package net.yupol.transmissionremote.app.torrentlist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.Property;

public class PlayPauseDrawable extends Drawable {

    private static final float SHADOW_PADDING_RATIO = 0.1f;

    private int borderColor;
    private Paint paint = new Paint();
    private Paint backgroundPaint = new Paint();
    private Paint borderPaint = new Paint();
    private Paint shadowPaint = new Paint();
    private Path leftPauseBar = new Path();
    private Path rightPauseBar = new Path();
    private Path playPath = new Path();
    private RectF bounds = new RectF();
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
    private boolean isPaused = false;
    private boolean isArmed = false;

    private static final Property<PlayPauseDrawable, Float> PROGRESS_PROPERTY = new Property<PlayPauseDrawable, Float>(Float.class, "progress") {
        @Override
        public Float get(PlayPauseDrawable d) {
            return d.progress;
        }

        @Override
        public void set(PlayPauseDrawable d, Float value) {
            d.progress = value;
            d.invalidateSelf();
        }
    };

    public PlayPauseDrawable(int backgroundColor, int foregroundColor, int borderColor) {
        this.borderColor = borderColor;

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(foregroundColor);

        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);

        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(2);

        shadowPaint.setAntiAlias(true);
        shadowPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        float centerX = bounds.centerX();
        float centerY = bounds.centerY();
        float radius = bounds.width() / 2;
        if (isArmed) {
            canvas.drawCircle(centerX, centerY, radius, shadowPaint);
        }
        float borderRadius = (1f - SHADOW_PADDING_RATIO) * radius;
        canvas.drawCircle(centerX, centerY, borderRadius, backgroundPaint);
        canvas.drawCircle(centerX, centerY, borderRadius, borderPaint);

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

        if (progress < 1.0f) {
            canvas.drawPath(leftPauseBar, paint);
            canvas.drawPath(rightPauseBar, paint);
        } else {
            playPath.rewind();
            playPath.moveTo(leftBarTopLeftX, leftBarTopLeftY);
            playPath.lineTo(rightBarTopRightX, rightBarTopRightY);
            playPath.lineTo(leftBarBottomLeftX, leftBarBottomLeftY);
            playPath.close();
            canvas.drawPath(playPath, paint);
        }
    }

    @Override
    protected void onBoundsChange(Rect canvasBounds) {
        super.onBoundsChange(canvasBounds);
        float diameter = Math.min(canvasBounds.width(), canvasBounds.height());
        float radius = diameter/2;
        float centerX = canvasBounds.exactCenterX();
        float centerY = canvasBounds.exactCenterY();
        this.bounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        leftBarTopLeftStart.set(bounds.left + 0.3f * diameter, bounds.top + 0.3f * diameter);
        leftBarTopLeftEnd.set(leftBarTopLeftStart);
        leftBarTopRightStart.set(leftBarTopLeftStart);
        leftBarTopRightStart.offset(0.15f * diameter, 0);
        leftBarTopRightEnd.set(bounds.left + 0.5f * diameter, bounds.top + 0.4f * diameter);
        leftBarBottomLeftStart.set(leftBarTopLeftStart.x, leftBarTopLeftStart.y + 0.4f * diameter);
        leftBarBottomLeftEnd.set(leftBarBottomLeftStart);
        leftBarBottomRightStart.set(leftBarBottomLeftStart.x + 0.15f * diameter, leftBarBottomLeftStart.y);
        leftBarBottomRightEnd.set(bounds.left + 0.5f * diameter, bounds.top + 0.6f * diameter);

        rightBarTopLeftStart.set(bounds.left + 0.55f * diameter, bounds.top + 0.3f * diameter);
        rightBarTopLeftEnd.set(leftBarTopRightEnd);
        rightBarTopRightStart.set(rightBarTopLeftStart.x + 0.15f * diameter, rightBarTopLeftStart.y);
        rightBarTopRightEnd.set(rightBarTopRightStart.x, bounds.top + 0.5f * diameter);
        rightBarBottomRightStart.set(rightBarTopRightStart.x, rightBarTopRightStart.y + 0.4f * diameter);
        rightBarBottomRightEnd.set(rightBarTopRightEnd);
        rightBarBottomLeftStart.set(rightBarTopLeftStart.x, rightBarBottomRightStart.y);
        rightBarBottomLeftEnd.set(leftBarBottomRightEnd);

        float offsetX = 0.05f * diameter;
        leftBarTopLeftEnd.offset(offsetX, 0);
        leftBarTopRightEnd.offset(offsetX, 0);
        leftBarBottomRightEnd.offset(offsetX, 0);
        leftBarBottomLeftEnd.offset(offsetX, 0);
        rightBarTopLeftEnd.offset(offsetX, 0);
        rightBarTopRightEnd.offset(offsetX, 0);
        rightBarBottomRightEnd.offset(offsetX, 0);
        rightBarBottomLeftEnd.offset(offsetX, 0);

        shadowPaint.setShader(new RadialGradient(centerX, centerY, radius,
                new int[]{ borderColor, Color.TRANSPARENT },
                new float[]{ 1f - 2 * SHADOW_PADDING_RATIO, 1f },
                Shader.TileMode.MIRROR));
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
        this.isPaused = paused;
        progress = paused ? 1f : 0f;
        invalidateSelf();
    }

    public void setArmed(boolean isArmed) {
        this.isArmed = isArmed;
        invalidateSelf();
    }

    public Animator getAnimator(final boolean toPaused) {
        Animator animator = ObjectAnimator.ofFloat(this, PROGRESS_PROPERTY, toPaused ? 0 : 1, toPaused ? 1 : 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isPaused = toPaused;
            }
        });
        return animator;
    }

    private static float interpolate(float a, float b, float progress) {
        return a + (b - a) * progress;
    }
}
