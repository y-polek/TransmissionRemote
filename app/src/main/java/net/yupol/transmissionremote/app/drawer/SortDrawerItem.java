package net.yupol.transmissionremote.app.drawer;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import net.yupol.transmissionremote.app.sorting.SortOrder;
import net.yupol.transmissionremote.app.sorting.SortedBy;

public class SortDrawerItem extends PrimaryDrawerItem {

    private SortedBy sortedBy;
    private SortOrder sortOrder = null;

    public SortDrawerItem(SortedBy sortedBy) {
        this.sortedBy = sortedBy;
    }

    @Override
    public SortDrawerItem withName(@StringRes int nameRes) {
        super.withName(nameRes);
        return this;
    }

    public SortedBy getSortedBy() {
        return sortedBy;
    }

    public void setSortOrder(@Nullable SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        withBadge(sortOrder != null ? sortOrder.getSymbol() : "");
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }
}
