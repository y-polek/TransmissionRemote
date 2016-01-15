package net.yupol.transmissionremote.app.torrentlist;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import net.yupol.transmissionremote.app.R;

public class PlayPauseButton extends ImageButton {


    private static final long ANIMATION_DURATION = 150;

    private boolean isPaused;
    private PlayPauseDrawable drawable;
    private Animator animator;

    public PlayPauseButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        int backgroundColor, foregroundColor, borderColor;
        int paddingLeft, paddingTop, paddingRight, paddingBottom;
        TypedArray customAttrs = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlayPauseButton, 0, 0);
        TypedArray paddingAttrs = context.obtainStyledAttributes(attrs, new int[] {
                android.R.attr.paddingLeft,
                android.R.attr.paddingTop,
                android.R.attr.paddingRight,
                android.R.attr.paddingBottom
        });
        try {
            backgroundColor = customAttrs.getColor(R.styleable.PlayPauseButton_background_color, Color.WHITE);
            foregroundColor = customAttrs.getColor(R.styleable.PlayPauseButton_foreground_color, Color.GRAY);
            borderColor = customAttrs.getColor(R.styleable.PlayPauseButton_border_color, Color.DKGRAY);
            paddingLeft = paddingAttrs.getDimensionPixelOffset(0, 0);
            paddingTop = paddingAttrs.getDimensionPixelOffset(1, 0);
            paddingRight = paddingAttrs.getDimensionPixelOffset(2, 0);
            paddingBottom = paddingAttrs.getDimensionPixelOffset(3, 0);
        } finally {
            customAttrs.recycle();
            paddingAttrs.recycle();
        }

        drawable = new PlayPauseDrawable(backgroundColor, foregroundColor, borderColor);
        drawable.setCallback(this);
        setImageDrawable(drawable);
        setBackgroundDrawable(null);
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        drawable.setArmed(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        drawable.setArmed(false);
                        break;
                }
                return false;
            }
        });

        setPaused(true);
    }

    public void setPaused(boolean isPaused) {
        if (this.isPaused == isPaused) return;

        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        this.isPaused = isPaused;
        drawable.setPaused(isPaused);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void toggle() {
        isPaused = !isPaused;

        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        animator = drawable.getAnimator(isPaused);
        animator.setDuration(ANIMATION_DURATION);
        animator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawable.setBounds(0, 0, w, h);
    }
}
