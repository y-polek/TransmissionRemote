package net.yupol.transmissionremote.app.torrentdetails;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.PeerItemLayoutBinding;
import net.yupol.transmissionremote.model.json.Peer;
import net.yupol.transmissionremote.app.sorting.PeersSortedBy;
import net.yupol.transmissionremote.app.sorting.SortOrder;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class PeersAdapter extends RecyclerView.Adapter<PeersAdapter.ViewHolder> {

    private Peer[] peers = {};
    private Comparator<Peer> comparator;
    private SortOrder order;

    public PeersAdapter() {
        setHasStableIds(true);
    }

    public void setPeers(@NonNull Peer[] peers) {
        this.peers = peers;
        sort();
        notifyDataSetChanged();
    }

    public void setSorting(PeersSortedBy sorting, SortOrder order) {
        comparator = sorting.comparator;
        this.order = order;
        sort();
        notifyDataSetChanged();
    }

    private void sort() {
        if (ArrayUtils.isEmpty(peers) || comparator == null) return;
        Arrays.sort(peers, order == SortOrder.ASCENDING ? comparator : Collections.reverseOrder(comparator));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PeerItemLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.peer_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.setPeer(peers[position]);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return peers.length;
    }

    @Override
    public long getItemId(int position) {
        return peers[position].address.hashCode();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private PeerItemLayoutBinding binding;

        public ViewHolder(PeerItemLayoutBinding binding) {
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
