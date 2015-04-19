package net.yupol.transmissionremote.app;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.widget.TextView;

import com.google.common.base.Strings;

import net.yupol.transmissionremote.app.utils.MetricsUtils;
import net.yupol.transmissionremote.app.utils.SizeUtils;

public abstract class SpeedTextView extends TextView {

    private static final int PADDING_LEFT = 5; // dp

    private Direction direction = Direction.NONE;

    public SpeedTextView(Context context, Direction direction) {
        super(context);
        this.direction = direction;
        int horPadding = (int) MetricsUtils.dp2px(context, PADDING_LEFT);
        setPadding(horPadding, 0, horPadding, 0);
        setTypeface(null, Typeface.BOLD);
        setSpeed(0);
    }

    public void setSpeed(long speedInBytesPerSec) {
        String speedText = speedText(speedInBytesPerSec);

        if (direction != Direction.NONE) {
            speedText = direction.symbol + " " + speedText;
        }
        setText(Html.fromHtml(speedText));
    }

    private String speedText(long bytes) {
        return Strings.padStart(SizeUtils.displayableSize(bytes), 5, ' ') + "/s";
    }

    private static enum Direction {
        DOWNLOAD("<font color=#009933>\u2b07</font>"),
        UPLOAD("<font color=#ff0000>\u2b06</font>"),//UPLOAD("<font color=#ff0000 style=bold>\u2191</font>"),
        NONE("");


        private String symbol;

        private Direction(String symbol) {
            this.symbol = symbol;
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
