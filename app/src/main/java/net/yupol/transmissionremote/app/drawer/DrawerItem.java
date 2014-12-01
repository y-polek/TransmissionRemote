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

    public Drawable getImage() {
        return null;
    }

    public View getView(ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(getLayoutId(), parent, false);

        TextView itemTextView = (TextView) itemView.findViewById(R.id.drawer_list_item_text);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_image);

        itemTextView.setText(getText());

        Drawable image = getImage();
        if (image != null)
            imageView.setImageDrawable(image);
        imageView.setVisibility(image != null ? View.VISIBLE : View.GONE);

        return itemView;
    }

    protected int getLayoutId() {
        return R.layout.drawer_list_item;
    }
}
