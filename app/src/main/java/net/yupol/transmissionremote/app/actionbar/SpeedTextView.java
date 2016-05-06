package net.yupol.transmissionremote.app.actionbar;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.utils.CheatSheet;
import net.yupol.transmissionremote.app.utils.MetricsUtils;
import net.yupol.transmissionremote.app.utils.TextUtils;

public abstract class SpeedTextView extends TextView {

    private static final int PADDING_LEFT = 5; // dp
    private static final int PADDING_IMAGE = 8; // dp

    public SpeedTextView(Context context, Drawable drawable) {
        super(context);
        int horPadding = (int) MetricsUtils.dp2px(context, PADDING_LEFT);
        setPadding(horPadding, 0, horPadding, 0);

        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.speed_text_size));
        setTextColor(context.getResources().getColor(R.color.text_primary_inverse));
        setTypeface(null, Typeface.BOLD);

        setSpeed(0);

        setCompoundDrawablePadding((int) MetricsUtils.dp2px(context, PADDING_IMAGE));
        if (drawable != null) {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int size = 2 * getMeasuredHeight() / 3;
            drawable.setBounds(0, 0, size, size);
        }
        setCompoundDrawables(drawable, null, null, null);
    }

    public void setSpeed(long speedInBytesPerSec) {
        setText(speedText(speedInBytesPerSec));
    }

    private String speedText(long bytes) {
        return Strings.padStart(TextUtils.displayableSize(bytes), 5, ' ') + "/s";
    }

    public static class DownloadSpeedTextView extends SpeedTextView {
        public DownloadSpeedTextView(Context context) {
            super(context, new IconicsDrawable(context)
                    .icon(FontAwesome.Icon.faw_arrow_down)
                    .colorRes(R.color.md_green_A700)
            );

            CheatSheet.setup(this, R.string.tooltip_total_download_speed);
        }
    }

    public static class UploadSpeedTextView extends SpeedTextView {
        public UploadSpeedTextView(Context context) {
            super(context, new IconicsDrawable(context)
                    .icon(FontAwesome.Icon.faw_arrow_up)
                    .colorRes(R.color.md_red_A700)
            );

            CheatSheet.setup(this, R.string.tooltip_total_upload_speed);
        }
    }
}
