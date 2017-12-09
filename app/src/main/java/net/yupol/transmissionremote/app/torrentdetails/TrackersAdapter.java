package net.yupol.transmissionremote.app.torrentdetails;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.PeerItemLayoutBinding;
import net.yupol.transmissionremote.app.databinding.TrackerItemLayoutBinding;
import net.yupol.transmissionremote.app.model.json.Peer;
import net.yupol.transmissionremote.app.model.json.Tracker;
import net.yupol.transmissionremote.app.model.json.TrackerStats;
import net.yupol.transmissionremote.app.sorting.PeersSortedBy;
import net.yupol.transmissionremote.app.sorting.SortOrder;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.annotation.Nullable;

public class TrackersAdapter extends RecyclerView.Adapter<TrackersAdapter.ViewHolder> {

    private Comparator<TrackerStats> COMPARATOR = new Comparator<TrackerStats>() {
        @Override
        public int compare(TrackerStats t1, TrackerStats t2) {
            int x = t1.tier;
            int y = t2.tier;
            return (x < y) ? -1 : (x == y ? 0 : 1);
        }
    };

    private TrackerStats[] trackerStats = {};

    public TrackersAdapter() {
        setHasStableIds(true);
    }

    public void setTrackerStats(@NonNull TrackerStats[] trackerStats) {
        this.trackerStats = trackerStats;
        Arrays.sort(this.trackerStats, COMPARATOR);
        notifyDataSetChanged();
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TrackerItemLayoutBinding binding;

        public ViewHolder(TrackerItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // this listener is required for view selection background
                }
            });
        }
    }
}
