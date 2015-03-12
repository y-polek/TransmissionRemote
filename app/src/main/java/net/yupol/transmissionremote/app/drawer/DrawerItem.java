package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;

public class DrawerItem {

    private Context context;
    private String text;
    private boolean isActive;

    public DrawerItem(String text, Context context) {
        this.text = text;
        this.context = context;
    }

    public DrawerItem(int textResId, Context context) {
        this(context.getString(textResId), context);
    }

    public String getText() {
        return text;
    }

    public int getLeftImage() {
        return 0;
    }

    public int getRightImage() {
        return 0;
    }

    public String getRightText() {
        return null;
    }

    public void itemSelected() {}

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public View getView(ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(getLayoutId(), parent, false);

        TextView itemTextView = (TextView) itemView.findViewById(R.id.drawer_list_item_text);
        ImageView leftImageView = (ImageView) itemView.findViewById(R.id.item_image_left);
        ImageView rightImageView = (ImageView) itemView.findViewById(R.id.item_image_right);
        TextView rightTextView = (TextView) itemView.findViewById(R.id.item_text_right);

        itemTextView.setText(getText());
        if (isActive) {
            itemTextView.setTypeface(null, Typeface.BOLD);
            itemTextView.setTextColor(context.getResources().getColor(R.color.drawer_list_active_item_text_color));
            rightTextView.setTypeface(null, Typeface.BOLD);
            rightTextView.setTextColor(context.getResources().getColor(R.color.drawer_list_active_item_text_color));
        }

        int leftImage = getLeftImage();
        int rightImage = getRightImage();
        leftImageView.setImageResource(leftImage);
        rightImageView.setImageResource(rightImage);
        leftImageView.setVisibility(leftImage != 0 ? View.VISIBLE : View.GONE);
        rightImageView.setVisibility(rightImage != 0 ? View.VISIBLE : View.GONE);

        String rightText = getRightText();
        if (rightText != null) rightTextView.setText(rightText);
        rightTextView.setVisibility(rightText != null ? View.VISIBLE : View.GONE);

        return itemView;
    }

    protected final Context getContext() {
        return context;
    }

    protected int getLayoutId() {
        return R.layout.drawer_list_item;
    }
}
