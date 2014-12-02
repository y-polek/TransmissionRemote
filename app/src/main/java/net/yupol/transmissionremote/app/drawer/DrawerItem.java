package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;

public class DrawerItem {

    private String text;

    public DrawerItem(String text) {
        this.text = text;
    }

    public DrawerItem(int textResId, Context context) {
        this(context.getString(textResId));
    }

    public String getText() {
        return text;
    }

    public Drawable getLeftImage() {
        return null;
    }

    public Drawable getRightImage() {
        return null;
    }

    public View getView(ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(getLayoutId(), parent, false);

        TextView itemTextView = (TextView) itemView.findViewById(R.id.drawer_list_item_text);
        ImageView leftImageView = (ImageView) itemView.findViewById(R.id.item_image_left);
        ImageView rightImageView = (ImageView) itemView.findViewById(R.id.item_image_right);

        itemTextView.setText(getText());

        Drawable leftImage = getLeftImage();
        Drawable rightImage = getRightImage();
        if (leftImage != null)
            leftImageView.setImageDrawable(leftImage);
        if (rightImage != null)
            rightImageView.setImageDrawable(rightImage);
        leftImageView.setVisibility(leftImage != null ? View.VISIBLE : View.GONE);
        rightImageView.setVisibility(rightImage != null ? View.VISIBLE : View.GONE);

        return itemView;
    }

    protected int getLayoutId() {
        return R.layout.drawer_list_item;
    }
}
