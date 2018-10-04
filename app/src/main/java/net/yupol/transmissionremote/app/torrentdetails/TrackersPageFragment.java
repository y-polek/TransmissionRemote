package net.yupol.transmissionremote.app.torrentdetails;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.databinding.TorrentDetailsTrackersPageFragmentBinding;
import net.yupol.transmissionremote.model.mapper.ServerMapper;
import net.yupol.transmissionremote.app.utils.DividerItemDecoration;
import net.yupol.transmissionremote.app.utils.IconUtils;
import net.yupol.transmissionremote.model.json.TorrentInfo;
import net.yupol.transmissionremote.model.json.TrackerStats;
import net.yupol.transmissionremote.data.api.Transport;
import net.yupol.transmissionremote.data.api.rpc.RpcArgs;

import org.apache.commons.lang3.StringUtils;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TrackersPageFragment extends BasePageFragment implements TrackersAdapter.TrackerActionListener,
        TrackerUrlDialog.OnTrackerUrlEnteredListener {

    private static final String TAG_EDIT_URL_DIALOG = "tag_edit_url_dialog";

    private TrackersAdapter adapter = new TrackersAdapter(this);
    private TorrentDetailsTrackersPageFragmentBinding binding;
    private boolean viewCreated;
    private ClipboardManager clipboardManager;
    private Transport transport;
    private CompositeDisposable requests = new CompositeDisposable();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        transport = new Transport(ServerMapper.toDomain(TransmissionRemote.getInstance().getActiveServer()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(
                inflater, R.layout.torrent_details_trackers_page_fragment, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext()));
        binding.recyclerView.setItemAnimator(null);
        binding.recyclerView.setAdapter(adapter);

        if (getActivity() instanceof SwipeRefreshLayout.OnRefreshListener) {
            binding.swipeRefresh.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) getActivity());
        }

        TorrentInfo torrentInfo = getTorrentInfo();
        if (torrentInfo != null) {
            TrackerStats[] trackerStats = torrentInfo.getTrackerStats();
            adapter.setTrackerStats(trackerStats);
            binding.emptyText.setVisibility(trackerStats.length > 0 ? View.GONE : View.VISIBLE);
        } else {
            binding.swipeRefresh.setRefreshing(true);
        }

        viewCreated = true;

        return binding.getRoot();
    }

    @Override
    public void onStop() {
        requests.clear();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewCreated = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.torrent_trackers_menu, menu);
        IconUtils.setMenuIcon(getContext(), menu, R.id.action_add, CommunityMaterial.Icon2.cmd_plus);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                showEditTrackerUrlDialog(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        super.setTorrentInfo(torrentInfo);
        if (viewCreated) {
            TrackerStats[] trackerStats = torrentInfo.getTrackerStats();
            adapter.setTrackerStats(trackerStats);
            binding.emptyText.setVisibility(trackerStats.length > 0 ? View.GONE : View.VISIBLE);
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onRemoveTrackerClicked(final TrackerStats tracker) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.trackers_remove_confirmation_title)
                .setMessage(R.string.trackers_remove_confirmation_message)
                .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeTracker(tracker.id);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onEditTrackerUrlClicked(TrackerStats tracker) {
        showEditTrackerUrlDialog(tracker);
    }

    @Override
    public void onCopyTrackerUrlClicked(TrackerStats tracker) {
        String url = StringUtils.isNotEmpty(tracker.host) ? tracker.host : tracker.announce;
        ClipData clip = ClipData.newPlainText(url, url);
        clipboardManager().setPrimaryClip(clip);
        Toast.makeText(getContext(), getString(R.string.copied), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTrackerUrlEntered(@Nullable TrackerStats tracker, String url) {
        if (tracker == null) {
            addTracker(url);
        } else {
            editTracker(tracker, url);
        }
    }

    private void removeTracker(int trackerId) {
        binding.swipeRefresh.setRefreshing(true);

        transport.api().removeTracker(RpcArgs.removeTracker(getTorrent().getId(), trackerId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        requests.add(d);
                    }

                    @Override
                    public void onComplete() {
                        binding.swipeRefresh.setRefreshing(false);
                        refresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.swipeRefresh.setRefreshing(false);
                        refresh();
                    }
                });
    }

    private void addTracker(String url) {
        binding.swipeRefresh.setRefreshing(true);
        transport.api().addTracker(RpcArgs.addTracker(getTorrent().getId(), url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        requests.add(d);
                    }

                    @Override
                    public void onComplete() {
                        binding.swipeRefresh.setRefreshing(false);
                        refresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.swipeRefresh.setRefreshing(false);
                        refresh();
                    }
                });
    }

    private void editTracker(@NonNull TrackerStats tracker, String url) {
        binding.swipeRefresh.setRefreshing(true);

        transport.api().editTracker(RpcArgs.editTracker(getTorrent().getId(), tracker.id, url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        requests.add(d);
                    }

                    @Override
                    public void onComplete() {
                        binding.swipeRefresh.setRefreshing(false);
                        refresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.swipeRefresh.setRefreshing(false);
                        refresh();
                    }
                });
    }

    private void showEditTrackerUrlDialog(@Nullable TrackerStats tracker) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment prev = requireFragmentManager().findFragmentByTag(TAG_EDIT_URL_DIALOG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        TrackerUrlDialog dialog = TrackerUrlDialog.newInstance(tracker);
        dialog.show(ft, TAG_EDIT_URL_DIALOG);
    }

    private ClipboardManager clipboardManager() {
        if (clipboardManager == null) {
            clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        }
        return clipboardManager;
    }

    private void refresh() {
        ((SwipeRefreshLayout.OnRefreshListener) requireActivity()).onRefresh();
    }
}
