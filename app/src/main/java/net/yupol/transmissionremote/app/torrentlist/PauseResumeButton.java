package net.yupol.transmissionremote.app.torrentlist;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

import net.yupol.transmissionremote.app.R;

public class PauseResumeButton extends ImageButton {

    private State state;
    private PlayPauseDrawable drawable;

    public PauseResumeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        drawable = new PlayPauseDrawable(context);
        drawable.setCallback(this);
        setImageDrawable(drawable);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PauseResumeButton, 0, 0);
        try {
            State.PAUSE.resId = a.getResourceId(R.styleable.PauseResumeButton_pause_img_src, android.R.drawable.ic_media_pause);
            State.RESUME.resId = a.getResourceId(R.styleable.PauseResumeButton_resume_img_src, android.R.drawable.ic_media_play);
        } finally {
            a.recycle();
        }

        setState(State.PAUSE);
    }

    public void setState(State state) {
        this.state = state;
        drawable.setPaused(state == State.PAUSE);
    }

    public State getState() {
        return state;
    }

    public void toggleState() {
        state = state == State.PAUSE ? State.RESUME : State.PAUSE;

        Animator animator = drawable.getAnimator();
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public enum State {
        PAUSE, RESUME;

        private int resId;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawable.setBounds(0, 0, w, h);
    }
}
