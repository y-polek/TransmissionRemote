package net.yupol.transmissionremote.app.torrentdetails;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.TorrentDetailsPeersPageFragmentBinding;
import net.yupol.transmissionremote.app.model.json.Peer;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;
import net.yupol.transmissionremote.app.utils.DividerItemDecoration;
import net.yupol.transmissionremote.app.utils.IconUtils;

public class PeersPageFragment extends BasePageFragment {

    private PeersAdapter adapter = new PeersAdapter();
    private TorrentDetailsPeersPageFragmentBinding binding;
    private boolean viewCreated;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(
                inflater, R.layout.torrent_details_peers_page_fragment, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext()));
        binding.recyclerView.setItemAnimator(null);
        binding.recyclerView.setAdapter(adapter);

        if (getActivity() instanceof SwipeRefreshLayout.OnRefreshListener) {
            binding.swiperefresh.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) getActivity());
        }

        TorrentInfo torrentInfo = getTorrentInfo();
        if (torrentInfo != null) {
            Peer[] peers = torrentInfo.getPeers();
            adapter.setPeers(peers);
            binding.emptyText.setVisibility(peers.length > 0 ? View.GONE : View.VISIBLE);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.torrent_peers_menu, menu);
        IconUtils.setMenuIcon(getContext(), menu, R.id.action_sort_peers, CommunityMaterial.Icon.cmd_sort_variant);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_peers:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        super.setTorrentInfo(torrentInfo);
        if (viewCreated) {
            Peer[] peers = torrentInfo.getPeers();
            adapter.setPeers(peers);
            binding.emptyText.setVisibility(peers.length > 0 ? View.GONE : View.VISIBLE);
            binding.swiperefresh.setRefreshing(false);
        }
    }
}
