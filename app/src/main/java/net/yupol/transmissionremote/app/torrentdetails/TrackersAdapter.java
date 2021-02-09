package net.yupol.transmissionremote.app.torrentdetails;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.TrackerItemLayoutBinding;
import net.yupol.transmissionremote.app.model.json.TrackerStats;
import net.yupol.transmissionremote.app.sorting.SortOrder;
import net.yupol.transmissionremote.app.sorting.TrackersSortedBy;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class TrackersAdapter extends RecyclerView.Adapter<TrackersAdapter.ViewHolder> {
    private Comparator<TrackerStats> comparator;
    private SortOrder order;

    private TrackerStats[] trackerStats = {};
    private TrackerActionListener listener;

    public TrackersAdapter(TrackerActionListener listener) {
        this.listener = listener;
        setHasStableIds(true);
    }

    public void setTrackerStats(@NonNull TrackerStats[] trackerStats) {
        this.trackerStats = trackerStats;
        sort();
        notifyDataSetChanged();
    }

    public void setSorting(TrackersSortedBy sorting, SortOrder order) {
        comparator = sorting.comparator;
        this.order = order;
        sort();
        notifyDataSetChanged();
    }

    private void sort() {
        if (ArrayUtils.isEmpty(trackerStats) || comparator == null) return;
        Arrays.sort(trackerStats, order == SortOrder.ASCENDING ? comparator : Collections.reverseOrder(comparator));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TrackerItemLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.tracker_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.setTrackerStats(trackerStats[position]);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return trackerStats.length;
    }

    @Override
    public long getItemId(int position) {
        return trackerStats[position].id;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

        private TrackerItemLayoutBinding binding;

        public ViewHolder(TrackerItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(v.getContext(), v);
                    menu.inflate(R.menu.tracker_menu);
                    menu.show();
                    menu.setOnMenuItemClickListener(ViewHolder.this);
                }
            });
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return false;

            switch (item.getItemId()) {
                case R.id.remove:
                    listener.onRemoveTrackerClicked(trackerStats[position]);
                    return true;
                case R.id.edit:
                    listener.onEditTrackerUrlClicked(trackerStats[position]);
                    return true;
                case R.id.copy:
                    listener.onCopyTrackerUrlClicked(trackerStats[position]);
                    return true;
            }
            return false;
        }
    }

    public interface TrackerActionListener {

        void onRemoveTrackerClicked(TrackerStats tracker);

        void onEditTrackerUrlClicked(TrackerStats tracker);

        void onCopyTrackerUrlClicked(TrackerStats tracker);
    }
}
