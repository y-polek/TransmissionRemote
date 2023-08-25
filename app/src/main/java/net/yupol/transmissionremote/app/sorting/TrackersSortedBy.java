package net.yupol.transmissionremote.app.sorting;

import android.support.annotation.StringRes;

import com.google.common.base.Strings;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.TrackerStats;

import java.util.Comparator;

public enum TrackersSortedBy {

    TIERS(R.string.trackers_sort_by_tiers, new Comparator<TrackerStats>() {
        @Override
        public int compare(TrackerStats p1, TrackerStats p2) {
            int x = p1.tier;
            int y = p2.tier;
            return (x < y) ? -1 : (x == y ? 0 : 1);
        }
    }),

    SEEDERS(R.string.trackers_sort_by_seeders, new Comparator<TrackerStats>() {
        @Override
        public int compare(TrackerStats p1, TrackerStats p2) {
            return Integer.compare(p1.seederCount, p2.seederCount);
        }
    }),

    LEECHERS(R.string.trackers_sort_by_leechers, new Comparator<TrackerStats>() {
        @Override
        public int compare(TrackerStats p1, TrackerStats p2) {
            return Integer.compare(p1.leecherCount, p2.leecherCount);
        }
    }),

    DOWNLOADED(R.string.trackers_sort_by_downloaded, new Comparator<TrackerStats>() {
        @Override
        public int compare(TrackerStats p1, TrackerStats p2) {
            return Integer.compare(p1.downloadCount, p2.downloadCount);
        }
    });

    @StringRes
    public final int nameResId;
    public final Comparator<TrackerStats> comparator;

    TrackersSortedBy(@StringRes int nameResId, Comparator<TrackerStats> comparator) {
        this.nameResId = nameResId;
        this.comparator = comparator;
    }
}
