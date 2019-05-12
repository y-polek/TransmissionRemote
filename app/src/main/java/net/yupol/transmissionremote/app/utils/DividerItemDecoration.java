package net.yupol.transmissionremote.app.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;

import net.yupol.transmissionremote.app.R;

import org.jetbrains.annotations.NotNull;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public DividerItemDecoration(Context context) {
        mDivider = ContextCompat.getDrawable(context, R.drawable.line_divider);
        int color = ColorUtils.resolveColor(context, android.R.attr.listDivider, android.R.color.holo_red_dark);
        mDivider.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onDraw(@NotNull Canvas c, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {

        int childCount = parent.getChildCount();
        for (int i=0; i<childCount-1; i++) {
            View child = parent.getChildAt(i);
            int left = parent.getPaddingLeft() + child.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight() - child.getPaddingRight();

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
