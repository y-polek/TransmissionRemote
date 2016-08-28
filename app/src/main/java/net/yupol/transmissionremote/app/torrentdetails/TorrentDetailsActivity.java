package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;
import net.yupol.transmissionremote.app.torrentlist.RemoveTorrentsDialogFragment;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.request.ReannounceTorrentRequest;
import net.yupol.transmissionremote.app.transport.request.StartTorrentRequest;
import net.yupol.transmissionremote.app.transport.request.StopTorrentRequest;
import net.yupol.transmissionremote.app.transport.request.TorrentInfoGetRequest;
import net.yupol.transmissionremote.app.transport.request.TorrentRemoveRequest;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;
import net.yupol.transmissionremote.app.transport.request.VerifyTorrentRequest;

import java.util.LinkedList;
import java.util.List;

public class TorrentDetailsActivity extends BaseSpiceActivity implements SaveChangesDialogFragment.SaveDiscardListener,
        RemoveTorrentsDialogFragment.OnRemoveTorrentSelectionListener {

    private static final String TAG = TorrentDetailsActivity.class.getSimpleName();

    public static final String EXTRA_NAME_TORRENT = "extra_key_torrent";

    private static final String TAG_SAVE_CHANGES_DIALOG = "tag_save_changes_dialog";

    private static final String KEY_OPTIONS_CHANGE_REQUEST = "key_options_request";
    private static final String KEY_TORRENT_INFO = "key_torrent_info";

    private Torrent torrent;
    private TorrentInfo torrentInfo;
    private SparseArray<TorrentSetRequest> saveChangesRequests = new SparseArray<>();
    private TorrentInfoGetRequest lastTorrentInfoRequest;
    private List<OnDataAvailableListener<TorrentInfo>> torrentInfoListeners = new LinkedList<>();
    private List<OnActivityExitingListener<TorrentSetRequest.Builder>> activityExitingListeners = new LinkedList<>();
    private TorrentDetailsPagerAdapter pagerAdapter;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.torrent_details_layout);

        torrent = getIntent().getParcelableExtra(EXTRA_NAME_TORRENT);
        pagerAdapter = new TorrentDetailsPagerAdapter(this, getSupportFragmentManager(), torrent);

        if (savedInstanceState != null) {
            torrentInfo = savedInstanceState.getParcelable(KEY_TORRENT_INFO);
        }
        if (torrentInfo == null) {
            lastTorrentInfoRequest = new TorrentInfoGetRequest(torrent.getId());
            getTransportManager().doRequest(lastTorrentInfoRequest, new RequestListener<TorrentInfo>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    if (spiceException instanceof RequestCancelledException) return;
                    Log.e(TAG, "Failed to get torrent info", spiceException);
                    showErrorAndExit();
                }

                @Override
                public void onRequestSuccess(TorrentInfo torrentInfo) {
                    // TorrentInfo may be empty if torrent is removed after request was sent.
                    // Show content only if TorrentInfo contain files data.
                    if (torrentInfo.getFiles() != null) {
                        TorrentDetailsActivity.this.torrentInfo = torrentInfo;
                        pagerAdapter.setTorrentInfo(torrentInfo);
                        notifyTorrentInfoListeners();
                    } else {
                        Log.e(TAG, "Empty TorrentInfo");
                        showErrorAndExit();
                    }
                }

                private void showErrorAndExit() {
                    Toast.makeText(TorrentDetailsActivity.this, R.string.error_retrieve_torrent_details, Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            });
        } else {
            pagerAdapter.setTorrentInfo(torrentInfo);
        }

        setupActionBar();

        pager = (ViewPager) findViewById(R.id.pager);
        assert pager != null;
        pager.setAdapter(pagerAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSparseParcelableArray(KEY_OPTIONS_CHANGE_REQUEST, saveChangesRequests);
        outState.putParcelable(KEY_TORRENT_INFO, torrentInfo);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        saveChangesRequests = savedInstanceState.getSparseParcelableArray(KEY_OPTIONS_CHANGE_REQUEST);
        super.onRestoreInstanceState(savedInstanceState);
    }

    public TorrentInfo getTorrentInfo() {
        return torrentInfo;
    }

    public void addTorrentInfoListener(OnDataAvailableListener<TorrentInfo> listener) {
        if (!torrentInfoListeners.contains(listener)) {
            torrentInfoListeners.add(listener);
        }
    }

    public void removeTorrentInfoListener(OnDataAvailableListener<TorrentInfo> listener) {
        torrentInfoListeners.remove(listener);
    }

    private void notifyTorrentInfoListeners() {
        for (OnDataAvailableListener<TorrentInfo> listener : torrentInfoListeners) {
            listener.onDataAvailable(torrentInfo);
        }
    }

    public void addOnActivityExitingListener(OnActivityExitingListener<TorrentSetRequest.Builder> listener) {
        if (!activityExitingListeners.contains(listener)) {
            activityExitingListeners.add(listener);
        }
    }

    public void removeOnActivityExitingListener(OnActivityExitingListener<TorrentSetRequest.Builder> listener) {
        activityExitingListeners.remove(listener);
    }

    public void addSaveChangesRequest(TorrentSetRequest.Builder requestBuilder) {
        saveChangesRequests.put(requestBuilder.getTorrentId(), requestBuilder.build());
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_toolbar);
        assert toolbar != null;
        toolbar.setSubtitle(torrent.getName());
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {

        BasePageFragment currentFragment = pagerAdapter.findFragment(getSupportFragmentManager(), pager.getCurrentItem());
        boolean handled = currentFragment.onBackPressed();
        if (handled) return;

        if (torrentInfo == null) {
            if (lastTorrentInfoRequest != null) lastTorrentInfoRequest.cancel();
            super.onBackPressed();
            return;
        }

        for (OnActivityExitingListener<TorrentSetRequest.Builder> listener : activityExitingListeners) {
            TorrentSetRequest.Builder saveChangesRequestBuilder = listener.onActivityExiting();
            if (saveChangesRequestBuilder != null && saveChangesRequestBuilder.isChanged()) {
                addSaveChangesRequest(saveChangesRequestBuilder);
            }
        }

        if (saveChangesRequests.size() > 0) {
            new SaveChangesDialogFragment().show(getFragmentManager(), TAG_SAVE_CHANGES_DIALOG);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.torrent_details_actions_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_remove_torrents:
                RemoveTorrentsDialogFragment.newInstance(torrent.getId())
                        .show(getSupportFragmentManager(), RemoveTorrentsDialogFragment.TAG_REMOVE_TORRENTS_DIALOG);
                return true;
            case R.id.action_pause:
                getTransportManager().doRequest(new StopTorrentRequest(torrent.getId()), null);
                return true;
            case R.id.action_start:
                getTransportManager().doRequest(new StartTorrentRequest(torrent.getId()), null);
                return true;
            case R.id.action_start_now:
                getTransportManager().doRequest(new StartTorrentRequest(new int[] { torrent.getId() }, true), null);
                return true;
            case R.id.action_verify:
                getTransportManager().doRequest(new VerifyTorrentRequest(torrent.getId()), null);
                return true;
            case R.id.action_reannounce:
                getTransportManager().doRequest(new ReannounceTorrentRequest(torrent.getId()), null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSavePressed() {
        for (int i=0; i<saveChangesRequests.size(); i++) {
            TorrentSetRequest request = saveChangesRequests.valueAt(i);
            getTransportManager().doRequest(request, null);
        }
        super.onBackPressed();
    }

    @Override
    public void onDiscardPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRemoveTorrentsSelected(int[] torrentsToRemove, boolean removeData) {
        getTransportManager().doRequest(new TorrentRemoveRequest(torrentsToRemove, removeData), new RequestListener<Void>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Toast.makeText(TorrentDetailsActivity.this, R.string.remove_failed, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to remove torrent", spiceException);
            }

            @Override
            public void onRequestSuccess(Void aVoid) {
                TorrentDetailsActivity.super.onBackPressed();
            }
        });
    }
}
