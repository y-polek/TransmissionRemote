package net.yupol.transmissionremote.app.utils;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import net.yupol.transmissionremote.app.R;

public class IconUtils {

    public static void setMenuIcon(Context context, MenuItem item, IIcon icon, int color) {
        item.setIcon(new IconicsDrawable(context)
                .icon(icon)
                .color(color)
                .sizeRes(R.dimen.menu_item_icon_size)
        );
    }

    public static void setMenuIcon(Context context, Menu menu, int itemId, IIcon icon, int color) {
        MenuItem item = menu.findItem(itemId);
        setMenuIcon(context, item, icon, color);
    }

    public static void setMenuIcon(Context context, Menu menu, int itemId, IIcon icon) {
        int textColor = ColorUtils.resolveColor(context, android.R.attr.textColorPrimaryInverse, R.color.text_primary_inverse);
        setMenuIcon(context, menu, itemId, icon, textColor);
    }

    public static void setMenuIcon(Context context, MenuItem item, IIcon icon) {
        int textColor = ColorUtils.resolveColor(context, android.R.attr.textColorPrimaryInverse, R.color.text_primary_inverse);
        setMenuIcon(context, item, icon, textColor);
    }
}
