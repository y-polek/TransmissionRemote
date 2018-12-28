package net.yupol.transmissionremote.app.actionbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.utils.CheatSheet;

import androidx.appcompat.widget.AppCompatImageButton;

public class TurtleModeButton extends AppCompatImageButton {

    private boolean isEnabled;
    private int enabledRes;
    private int disabledRes;

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
        CheatSheet.setup(this, R.string.tooltip_turtle_mode);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        if (this.isEnabled == isEnabled) return;

        this.isEnabled = isEnabled;
        updateImage();
        invalidate();
        requestLayout();
    }

    public void toggle() {
        setEnabled(!isEnabled);
    }

    private void updateImage() {
        setImageResource(isEnabled ? enabledRes : disabledRes);
    }
}
