package net.yupol.transmissionremote.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class PauseResumeButton extends ImageButton {

    private State state;

    public PauseResumeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

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
        setImageDrawable(getResources().getDrawable(state.resId));
    }

    public State getState() {
        return state;
    }

    public void toggleState() {
        setState(state == State.PAUSE ? State.RESUME : State.PAUSE);
    }

    public static enum State {
        PAUSE, RESUME;

        private int resId;
    }
}
