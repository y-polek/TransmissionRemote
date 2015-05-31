package net.yupol.transmissionremote.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class TurtleModeButton extends ImageButton implements View.OnClickListener {

    private boolean isEnabled;
    private int enabledRes;
    private int disabledRes;
    private OnEnableChangedListener enableListener;

    public TurtleModeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TurtleModeButton, 0, 0);

        try {
            enabledRes = a.getResourceId(R.styleable.TurtleModeButton_src_enabled, 0);
            disabledRes = a.getResourceId(R.styleable.TurtleModeButton_src_disabled, 0);
            isEnabled = a.getBoolean(R.styleable.TurtleModeButton_enabled, false);
        } finally {
            a.recycle();
        }

        updateImage();

        setOnClickListener(this);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        if (enableListener != null) {
            enableListener.onEnableChanged(isEnabled);
        }
        updateImage();
        invalidate();
        requestLayout();
    }

    public void setEnableChangedListener(OnEnableChangedListener listener) {
        enableListener = listener;
    }

    @Override
    public void onClick(View v) {
        setEnabled(!isEnabled);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l != this) {
            throw new UnsupportedOperationException("Use setEnableChangedListener(OnEnableChangedListener) instead");
        }
        super.setOnClickListener(l);
    }

    private void updateImage() {
        setImageResource(isEnabled ? enabledRes : disabledRes);
    }

    public interface OnEnableChangedListener {
        void onEnableChanged(boolean isEnabled);
    }
}
