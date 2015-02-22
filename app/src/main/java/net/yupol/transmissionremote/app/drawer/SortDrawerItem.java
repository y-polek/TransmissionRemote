package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.graphics.drawable.Drawable;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.sorting.SortOrder;

import java.util.Collections;
import java.util.Comparator;

public class SortDrawerItem extends DrawerItem {

    private Comparator<Torrent> baseComparator;
    private SortOrder sortOrder = SortOrder.UNSORTED;

    public SortDrawerItem(int textResId, Context c, Comparator<Torrent> comparator) {
        super(textResId, c);

        baseComparator = comparator;
    }

    public void setSorting(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SortOrder getSorting() {
        return sortOrder;
    }

    public Comparator<Torrent> getComparator() {
        if (sortOrder == SortOrder.UNSORTED) return null;
        return sortOrder == SortOrder.ASCENDING ? baseComparator : Collections.reverseOrder(baseComparator);
    }

    @Override
    public int getRightImage() {
        switch (sortOrder) {
            case ASCENDING: return R.drawable.arrow_up;
            case DESCENDING: return R.drawable.arrow_down;
            default: return 0;
        }
    }
}
