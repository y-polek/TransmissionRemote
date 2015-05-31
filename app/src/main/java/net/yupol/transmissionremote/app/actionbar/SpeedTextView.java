package net.yupol.transmissionremote.app.actionbar;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.TextView;

import com.google.common.base.Strings;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.utils.MetricsUtils;
import net.yupol.transmissionremote.app.utils.SizeUtils;

public abstract class SpeedTextView extends TextView {

    private static final int PADDING_LEFT = 5; // dp
    private static final int PADDING_IMAGE = 8; // dp

    public SpeedTextView(Context context, Direction direction) {
        super(context);
        int horPadding = (int) MetricsUtils.dp2px(context, PADDING_LEFT);
        setPadding(horPadding, 0, horPadding, 0);

        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.speed_text_size));
        setTypeface(null, Typeface.BOLD);

        setSpeed(0);

        setCompoundDrawablePadding((int) MetricsUtils.dp2px(context, PADDING_IMAGE));
        Drawable drawable = getResources().getDrawable(direction.iconId);
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
        return Strings.padStart(SizeUtils.displayableSize(bytes), 5, ' ') + "/s";
    }

    private enum Direction {
        DOWNLOAD(R.drawable.download_icon),
        UPLOAD(R.drawable.upload_icon);


        private int iconId;

        Direction(int iconId) {
            this.iconId = iconId;
        }
    }

    public static class DownloadSpeedTextView extends SpeedTextView {
        public DownloadSpeedTextView(Context context) {
            super(context, Direction.DOWNLOAD);
        }
    }

    public static class UploadSpeedTextView extends SpeedTextView {
        public UploadSpeedTextView(Context context) {
            super(context, Direction.UPLOAD);
        }
    }
}
