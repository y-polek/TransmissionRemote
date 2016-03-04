package net.yupol.transmissionremote.app.torrentlist;

import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Property;
import android.view.animation.OvershootInterpolator;

public class RotateAnimationDrawable extends Drawable {

    private static final int ANIMATION_DURATION = 300;

    private static final Property<RotateAnimationDrawable, Integer> ANGLE_PROPERTY = new Property<RotateAnimationDrawable, Integer>(Integer.class, "angle") {
        @Override
        public Integer get(RotateAnimationDrawable d) {
            return d.angle;
        }

        @Override
        public void set(RotateAnimationDrawable d, Integer angle) {
            d.angle = angle;
            d.invalidateSelf();
        }
    };

    private Drawable drawable;
    private Paint paint = new Paint();
    private Rect bounds;

    private int angle;

    public RotateAnimationDrawable(@NonNull Drawable wrappedDrawable) {
        drawable = wrappedDrawable;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(angle, bounds.centerX(), bounds.centerY());
        drawable.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.bounds = bounds;
        drawable.setBounds(bounds);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void animate(int from, int to) {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, ANGLE_PROPERTY, from, to);
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new OvershootInterpolator());
        animator.start();
    }
}
