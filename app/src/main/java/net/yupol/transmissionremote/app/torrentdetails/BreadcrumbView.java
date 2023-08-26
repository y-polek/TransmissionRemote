package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsButton;
import com.mikepenz.iconics.view.IconicsImageView;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.Dir;

import java.util.Stack;

public class BreadcrumbView extends LinearLayout {

    private final LinearLayout pathLayout;
    @DrawableRes private final int buttonBackground;
    private final int textSize;
    private final int arrowIconSize;
    @ColorInt private final int primaryColor;
    @ColorInt private final int secondaryColor;
    private final int textPadding;

    private final Stack<Dir> path = new Stack<>();
    private OnNodeSelectedListener listener;
    private final HorizontalScrollView scrollView;

    public BreadcrumbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);

        TypedValue buttonBackgroundValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, buttonBackgroundValue, true);
        buttonBackground = buttonBackgroundValue.resourceId;

        textSize = getResources().getDimensionPixelSize(R.dimen.tr_abc_text_size_body_1_material);
        int homeButtonSize = getResources().getDimensionPixelSize(R.dimen.home_button_size);
        int homeButtonTextSize = getResources().getDimensionPixelSize(R.dimen.home_button_text_size);
        arrowIconSize = getResources().getDimensionPixelSize(R.dimen.arrow_icon_size);
        primaryColor = ContextCompat.getColor(context, R.color.text_color_primary);
        secondaryColor = ContextCompat.getColor(context, R.color.text_color_secondary);
        textPadding = getResources().getDimensionPixelSize(R.dimen.text_padding_horizontal);

        IconicsButton homeButton = new IconicsButton(context);
        homeButton.setBackgroundResource(buttonBackground);
        homeButton.setPadding(0, 0, 0, 0);
        homeButton.setText("{cmd_home_outline}");
        homeButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, homeButtonTextSize);
        homeButton.setTextColor(primaryColor);
        LayoutParams homeLayoutParams = new LayoutParams(homeButtonSize, homeButtonSize);
        homeLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        addView(homeButton, homeLayoutParams);
        homeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onNodeSelected(0);
            }
        });

        scrollView = new HorizontalScrollView(context);
        scrollView.setHorizontalScrollBarEnabled(false);
        LayoutParams scrollLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(scrollView, scrollLayoutParams);

        pathLayout = new LinearLayout(context);
        pathLayout.setOrientation(HORIZONTAL);
        ViewGroup.LayoutParams pathLayoutParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        scrollView.addView(pathLayout, pathLayoutParams);
    }

    public void setPath(Stack<Dir> path) {
        this.path.clear();
        this.path.addAll(path);

        pathLayout.removeAllViews();
        addSeparator();
        for (int i=1; i<path.size(); i++) { // skip root dir
            if (i != 1) addSeparator();
            addNode(i);
        }
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(FOCUS_RIGHT);
            }
        });
    }

    public void setOnNodeSelectedListener(OnNodeSelectedListener listener) {
        this.listener = listener;
    }

    private void addNode(final int position) {
        Dir dir = path.get(position);

        TextView textView = new TextView(getContext());
        textView.setText(dir.getName());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setBackgroundResource(buttonBackground);
        textView.setTextColor(primaryColor);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setPadding(textPadding, 0, textPadding, 0);
        textView.setGravity(Gravity.CENTER);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        pathLayout.addView(textView, lp);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onNodeSelected(position);
            }
        });
    }

    private void addSeparator() {
        IconicsImageView dividerView = new IconicsImageView(getContext());
        dividerView.setIcon(new IconicsDrawable(getContext(), CommunityMaterial.Icon.cmd_chevron_right));
        dividerView.setColorFilter(secondaryColor);
        LayoutParams dividerLayoutParams = new LayoutParams(arrowIconSize, arrowIconSize);
        dividerLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        pathLayout.addView(dividerView, dividerLayoutParams);
    }

    public interface OnNodeSelectedListener {
        void onNodeSelected(int position);
    }
}
