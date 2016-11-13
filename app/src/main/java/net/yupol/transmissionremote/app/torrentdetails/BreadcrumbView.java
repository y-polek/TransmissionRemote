package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsButton;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import net.yupol.transmissionremote.app.R;

public class BreadcrumbView extends LinearLayout {

    private final LinearLayout pathLayout;
    @DrawableRes private int buttonBackground;
    private int textSize;
    private int arrowIconSize;
    @ColorInt private int primaryColor;
    @ColorInt private int secondaryColor;
    private int textPadding;

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
        homeButton.setText("{ion_ios_home_outline}");
        homeButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, homeButtonTextSize);
        homeButton.setTextColor(primaryColor);
        LayoutParams homeLayoutParams = new LayoutParams(homeButtonSize, homeButtonSize);
        homeLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        addView(homeButton, homeLayoutParams);

        HorizontalScrollView scrollView = new HorizontalScrollView(context);
        scrollView.setHorizontalScrollBarEnabled(false);
        LayoutParams scrollLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(scrollView, scrollLayoutParams);

        pathLayout = new LinearLayout(context);
        pathLayout.setOrientation(HORIZONTAL);
        ViewGroup.LayoutParams pathLayoutParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        scrollView.addView(pathLayout, pathLayoutParams);

        addNode("Hello");
        addNode("World");
        addNode("One");
        addNode("Two");
        addNode("Three");
        addNode("Four");
        addNode("Five");
    }

    private void addNode(final String text) {
        IconicsImageView dividerView = new IconicsImageView(getContext());
        dividerView.setIcon(Ionicons.Icon.ion_ios_arrow_right);
        dividerView.setColor(secondaryColor);
        LayoutParams dividerLayoutParams = new LayoutParams(arrowIconSize, arrowIconSize);
        dividerLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        pathLayout.addView(dividerView, dividerLayoutParams);

        TextView textView = new TextView(getContext());
        textView.setText(text);
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
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
