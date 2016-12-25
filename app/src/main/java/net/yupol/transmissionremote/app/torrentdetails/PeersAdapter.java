package net.yupol.transmissionremote.app.torrentdetails;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.PeerItemLayoutBinding;
import net.yupol.transmissionremote.app.model.json.Peer;

public class PeersAdapter extends RecyclerView.Adapter<PeersAdapter.ViewHolder> {

    private Peer[] peers = {};

    public void setPeers(@NonNull Peer[] peers) {
        this.peers = peers;
        notifyDataSetChanged();
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
    }

    @Override
    public int getItemCount() {
        return peers.length;
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
