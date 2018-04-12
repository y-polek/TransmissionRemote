package net.yupol.transmissionremote.app.torrentdetails;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.TorrentDetailsPeersPageFragmentBinding;
import net.yupol.transmissionremote.model.json.Peer;
import net.yupol.transmissionremote.model.json.TorrentInfo;
import net.yupol.transmissionremote.app.sorting.PeersSortedBy;
import net.yupol.transmissionremote.app.sorting.SortOrder;
import net.yupol.transmissionremote.app.utils.DividerItemDecoration;
import net.yupol.transmissionremote.app.utils.IconUtils;
import net.yupol.transmissionremote.app.utils.MetricsUtils;
import net.yupol.transmissionremote.app.utils.Size;

public class PeersPageFragment extends BasePageFragment {

    private static final String KEY_SORT_BY = "key_sort_by";
    private static final String KEY_SORT_ORDER = "key_sort_order";

    private PeersAdapter adapter = new PeersAdapter();
    private TorrentDetailsPeersPageFragmentBinding binding;
    private boolean viewCreated;
    private SharedPreferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
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
        adapter.setSorting(currentSorting(), currentSortOrder());

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
                showSortingList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortingList() {
        ListPopupWindow popup = new ListPopupWindow(getContext());
        popup.setModal(true);

        final PeersSortingListAdapter sortingAdapter = new PeersSortingListAdapter();
        final PeersSortedBy sortedBy = currentSorting();
        SortOrder sortOrder = currentSortOrder();
        sortingAdapter.setCurrentSorting(sortedBy, sortOrder);
        popup.setAdapter(sortingAdapter);
        popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PeersSortedBy sorting = sortingAdapter.getItem(position);
                SortOrder order = SortOrder.ASCENDING;
                if (sorting == currentSorting()) {
                    order = currentSortOrder().reversed();
                }
                setCurrentSorting(sorting, order);
                sortingAdapter.setCurrentSorting(sorting, order);
                adapter.setSorting(sorting, order);
            }
        });

        popup.setAnchorView(binding.getRoot());
        Size popupSize = MetricsUtils.measurePopupSize(getContext(), sortingAdapter);
        popup.setContentWidth(popupSize.width);
        popup.setHeight(popupSize.height);
        popup.setHorizontalOffset(binding.getRoot().getWidth() - popupSize.width
                - getResources().getDimensionPixelOffset(R.dimen.sort_popup_offset_horizontal));
        popup.setVerticalOffset(-getResources().getDimensionPixelOffset(R.dimen.sort_popup_offset_vertical));
        popup.show();
    }

    private PeersSortedBy currentSorting() {
        try {
            return PeersSortedBy.valueOf(preferences.getString(KEY_SORT_BY, PeersSortedBy.ADDRESS.toString()));
        } catch (IllegalArgumentException e) {
            // Unknown sorting, return default
            return PeersSortedBy.ADDRESS;
        }
    }

    private void setCurrentSorting(PeersSortedBy sorting, SortOrder sortOrder) {
        preferences.edit()
                .putString(KEY_SORT_BY, sorting.toString())
                .putString(KEY_SORT_ORDER, sortOrder.toString())
                .apply();
    }

    private SortOrder currentSortOrder() {
        try {
            return SortOrder.valueOf(preferences.getString(KEY_SORT_ORDER, SortOrder.ASCENDING.toString()));
        } catch (IllegalArgumentException e) {
            // Unknown order, return default
            return SortOrder.ASCENDING;
        }
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
