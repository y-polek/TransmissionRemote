package net.yupol.transmissionremote.app.torrentdetails;

import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.view.LayoutInflaterCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mikepenz.iconics.context.IconicsLayoutInflater2;

import net.yupol.transmissionremote.app.BaseActivity;
import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.databinding.TorrentDetailsLayoutBinding;
import net.yupol.transmissionremote.app.preferences.Preferences;
import net.yupol.transmissionremote.app.torrentlist.ChooseLocationDialogFragment;
import net.yupol.transmissionremote.app.torrentlist.RemoveTorrentsDialogFragment;
import net.yupol.transmissionremote.app.torrentlist.RenameDialogFragment;
import net.yupol.transmissionremote.data.api.Transport;
import net.yupol.transmissionremote.data.api.model.TorrentEntity;
import net.yupol.transmissionremote.data.api.model.TorrentInfoEntity;
import net.yupol.transmissionremote.data.api.rpc.RpcArgs;
import net.yupol.transmissionremote.model.json.Torrent;
import net.yupol.transmissionremote.model.json.TorrentInfo;
import net.yupol.transmissionremote.model.mapper.ServerMapper;
import net.yupol.transmissionremote.model.mapper.TorrentMapper;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TorrentDetailsActivity extends BaseActivity implements
        RemoveTorrentsDialogFragment.OnRemoveTorrentSelectionListener, ChooseLocationDialogFragment.OnLocationSelectedListener,
        TorrentInfoUpdater.OnTorrentInfoUpdatedListener, SwipeRefreshLayout.OnRefreshListener, RenameDialogFragment.OnNameSelectedListener {

    private static final String TAG = TorrentDetailsActivity.class.getSimpleName();

    public static final String EXTRA_TORRENT = "extra_key_torrent";

    private static final String TAG_CHOOSE_LOCATION_DIALOG = "tag_choose_location_dialog";

    private static final String KEY_TORRENT_INFO = "key_torrent_info";
    private static final String KEY_LAST_PAGE_POSITION = "key_last_position";

    private static final String RENAME_TORRENT_FRAGMENT_TAG = "rename_torrent_fragment_tag";

    private Torrent torrent;
    private TorrentInfo torrentInfo;
    private List<OnDataAvailableListener<TorrentInfo>> torrentInfoListeners = new LinkedList<>();
    private TorrentDetailsPagerAdapter pagerAdapter;
    private MenuItem setLocationMenuItem;
    private TorrentInfoUpdater torrentInfoUpdater;
    private TorrentDetailsLayoutBinding binding;
    private SharedPreferences sharedPreferences;
    private Transport transport;
    private CompositeDisposable requests = new CompositeDisposable();

    @Inject Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        TransmissionRemote.getInstance().appComponent().inject(this);
        binding = DataBindingUtil.setContentView(this, R.layout.torrent_details_layout);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TorrentDetailsActivity.this);
        transport = new Transport(ServerMapper.toDomain(TransmissionRemote.getInstance().getActiveServer()));

        if (savedInstanceState != null) {
            torrentInfo = savedInstanceState.getParcelable(KEY_TORRENT_INFO);
        }

        torrent = getIntent().getParcelableExtra(EXTRA_TORRENT);
        setupPager();

        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                sharedPreferences.edit().putInt(KEY_LAST_PAGE_POSITION, position).apply();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        if (setLocationMenuItem != null) {
            setLocationMenuItem.setEnabled(torrentInfo != null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        torrentInfoUpdater.start(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        torrentInfoUpdater.stop();
    }

    @Override
    protected void onStop() {
        requests.clear();
        super.onStop();
    }

    @Override
    public void onTorrentInfoUpdated(TorrentInfo torrentInfo) {
        // TorrentInfo may be empty if torrent is removed after request was sent.
        // Show content only if TorrentInfo contain files data.
        if (torrentInfo.getFiles() != null) {
            TorrentDetailsActivity.this.torrentInfo = torrentInfo;
            pagerAdapter.setTorrentInfo(torrentInfo);
            notifyTorrentInfoListeners();
            if (setLocationMenuItem != null) {
                setLocationMenuItem.setEnabled(true);
            }
        } else {
            Log.e(TAG, "Empty TorrentInfo");
            showErrorAndExit();
        }
    }

    private void showErrorAndExit() {
        Toast.makeText(TorrentDetailsActivity.this, R.string.error_retrieve_torrent_details, Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_TORRENT_INFO, torrentInfo);
        super.onSaveInstanceState(outState);
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

    private void setupPager() {
        pagerAdapter = new TorrentDetailsPagerAdapter(this, getSupportFragmentManager(), torrent);

        boolean restartUpdater = false;
        if (torrentInfoUpdater != null) {
            torrentInfoUpdater.stop();
            restartUpdater = true;
        }
        torrentInfoUpdater = new TorrentInfoUpdater(transport, torrent.getId(),
                1000 * preferences.getUpdateInterval());
        if (restartUpdater) torrentInfoUpdater.start(this);

        if (torrentInfo != null) pagerAdapter.setTorrentInfo(torrentInfo);

        setupActionBar();

        binding.pager.setAdapter(pagerAdapter);

        int lastPagePosition = sharedPreferences.getInt(KEY_LAST_PAGE_POSITION, 0);
        binding.pager.setCurrentItem(lastPagePosition < pagerAdapter.getCount() ? lastPagePosition : 0);
    }

    private void setupActionBar() {
        binding.toolbar.setSubtitle(torrent.getName());
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        boolean handled = handleBackPressByFragments();
        if (handled) return;
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.torrent_details_actions_menu, menu);
        setLocationMenuItem = menu.findItem(R.id.action_set_location);
        setLocationMenuItem.setEnabled(torrentInfo != null);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_remove_torrents:
                RemoveTorrentsDialogFragment.newInstance(torrent.getId())
                        .show(getSupportFragmentManager(), RemoveTorrentsDialogFragment.TAG_REMOVE_TORRENTS_DIALOG);
                return true;
            case R.id.action_pause:
                transport.api().stopTorrents(torrent.getId())
                        .subscribeOn(Schedulers.io())
                        .onErrorComplete()
                        .subscribe();
                return true;
            case R.id.action_start:
                transport.api().startTorrents(torrent.getId())
                        .subscribeOn(Schedulers.io())
                        .onErrorComplete()
                        .subscribe();
                return true;
            case R.id.action_start_now:
                transport.api().startTorrentsNoQueue(torrent.getId())
                        .subscribeOn(Schedulers.io())
                        .onErrorComplete()
                        .subscribe();
                return true;
            case R.id.action_rename:
                RenameDialogFragment dialogFragment = RenameDialogFragment.newInstance(torrent.getId(), torrent.getName(), torrent.getName());
                dialogFragment.show(getSupportFragmentManager(), RENAME_TORRENT_FRAGMENT_TAG);
                return true;
            case R.id.action_set_location:
                ChooseLocationDialogFragment dialog = new ChooseLocationDialogFragment();
                Bundle args = new Bundle();
                args.putString(ChooseLocationDialogFragment.ARG_INITIAL_LOCATION, torrentInfo.getDownloadDir());
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), TAG_CHOOSE_LOCATION_DIALOG);
                return true;
            case R.id.action_verify:
                transport.api().verifyTorrents(torrent.getId())
                        .subscribeOn(Schedulers.io())
                        .onErrorComplete()
                        .subscribe();
                return true;
            case R.id.action_reannounce:
                transport.api().reannounceTorrents(torrent.getId())
                        .subscribeOn(Schedulers.io())
                        .onErrorComplete()
                        .subscribe();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRemoveTorrentsSelected(int[] torrentsToRemove, boolean removeData) {
        Completable removeTorrent;
        if (removeData) {
            removeTorrent = transport.api().removeTorrentsAndDeleteData(torrentsToRemove);
        } else {
            removeTorrent = transport.api().removeTorrents(torrentsToRemove);
        }
        removeTorrent
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        requests.add(d);
                    }

                    @Override
                    public void onComplete() {
                        TorrentDetailsActivity.super.onBackPressed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(TorrentDetailsActivity.this, R.string.remove_failed, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to remove torrent", e);
                    }
                });
    }

    @Override
    public void onLocationSelected(String path, boolean moveData) {
        transport.api().setTorrentLocation(RpcArgs.setLocation(path, moveData, torrent.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        requests.add(d);
                    }

                    @Override
                    public void onComplete() {
                        torrentInfoUpdater.updateNow(TorrentDetailsActivity.this);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Failed to set location", e);
                    }
                });
    }

    @Override
    public void onNameSelected(final int torrentId, String path, String name) {
        transport.api().renameTorrent(RpcArgs.renameTorrent(torrentId, path, name))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        requests.add(d);
                    }

                    @Override
                    public void onComplete() {
                        updateTorrentAndTorrentInfo(torrentId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Failed to rename torrent", e);
                    }
                });
    }

    @Override
    public void onRefresh() {
        torrentInfoUpdater.updateNow(this);
    }

    private void updateTorrentAndTorrentInfo(int torrentId) {
        transport.api().torrentList(torrentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<TorrentEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        requests.add(d);
                    }

                    @Override
                    public void onSuccess(List<TorrentEntity> torrents) {
                        if (torrents.size() != 1) {
                            Log.e(TAG, "Wrong number of torrents");
                            return;
                        }

                        updateTorrentInfo(TorrentMapper.INSTANCE.toViewModel(torrents.get(0)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Failed to reload torrent", e);
                    }
                });
    }

    private void updateTorrentInfo(final Torrent torrent) {
        transport.api().torrentInfo(torrent.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<TorrentInfoEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        requests.add(d);
                    }

                    @Override
                    public void onSuccess(TorrentInfoEntity torrentInfo) {
                        onTorrentInfoUpdated(TorrentMapper.toViewModel(torrentInfo));
                        TorrentDetailsActivity.this.torrent = torrent;
                        getIntent().putExtra(EXTRA_TORRENT, torrent);
                        setupPager();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Failed to reload torrent", e);
                    }
                });
    }
}
