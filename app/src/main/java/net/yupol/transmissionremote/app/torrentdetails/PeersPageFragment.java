package net.yupol.transmissionremote.app.torrentdetails;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.TorrentDetailsPeersPageFragmentBinding;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;
import net.yupol.transmissionremote.app.utils.DividerItemDecoration;

public class PeersPageFragment extends BasePageFragment {

    private PeersAdapter adapter = new PeersAdapter();
    private TorrentDetailsPeersPageFragmentBinding binding;
    private boolean viewCreated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(
                inflater, R.layout.torrent_details_peers_page_fragment, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext()));
        binding.recyclerView.setAdapter(adapter);

        binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        TorrentInfo torrentInfo = getTorrentInfo();
        if (torrentInfo != null) {
            adapter.setPeers(torrentInfo.getPeers());
        } else {
            binding.swiperefresh.setRefreshing(true);
        }

        viewCreated = true;

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewCreated = false;
    }

    @Override
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        super.setTorrentInfo(torrentInfo);
        if (viewCreated) {
            adapter.setPeers(torrentInfo.getPeers());
            binding.swiperefresh.setRefreshing(false);
        }
    }
}
