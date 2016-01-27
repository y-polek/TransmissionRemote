package net.yupol.transmissionremote.app.drawer;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import net.yupol.transmissionremote.app.utils.TextUtils;

public class ServerDrawable extends Drawable {

    private String text;
    private float textPadding;

    private Paint paint = new Paint();
    private Paint backgroundPaint = new Paint();
    private Rect bounds;
    private float textWidth, textHeight;

    public ServerDrawable(String name, int backgroundColor, int foregroundColor, float textPaddingPx) {
        text = TextUtils.abbreviate(name);
        textPadding = textPaddingPx;

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(foregroundColor);

        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(bounds, backgroundPaint);

        canvas.drawText(text, (bounds.width() - textWidth)/2,
                (bounds.height() + textHeight)/2, paint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.bounds = bounds;

        float tmpTextSize = 48f;
        paint.setTextSize(tmpTextSize);

        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        float desiredTextSize = Math.min(
                tmpTextSize * (bounds.width() - 2 * textPadding) / textBounds.width(),
                tmpTextSize * (bounds.height() - 2 * textPadding) / textBounds.height());
        paint.setTextSize(desiredTextSize);

        paint.getTextBounds(text, 0, text.length(), textBounds);
        textWidth = paint.measureText(text);
        textHeight = textBounds.height();
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        backgroundPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
