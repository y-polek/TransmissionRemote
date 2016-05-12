package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.request.TorrentInfoGetRequest;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;

public class TorrentDetailsActivity extends BaseSpiceActivity implements SaveChangesDialogFragment.SaveDiscardListener {

    private static final String TAG = TorrentDetailsActivity.class.getSimpleName();

    public static final String EXTRA_NAME_TORRENT = "extra_key_torrent";

    private static final String TAG_SAVE_CHANGES_DIALOG = "tag_save_changes_dialog";

    private static final String KEY_OPTIONS_CHANGE_REQUEST = "key_options_request";
    private static final String KEY_HAS_TORRENT_INFO = "key_has_torrent_info";

    private Torrent torrent;
    private TorrentSetRequest saveChangesRequest;
    private TorrentDetailsPagerAdapter pagerAdapter;
    private boolean hasTorrentInfo = false;
    private TorrentInfoGetRequest lastTorrentInfoRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.torrent_details_layout);

        hasTorrentInfo = savedInstanceState != null && savedInstanceState.getBoolean(KEY_HAS_TORRENT_INFO, false);

        torrent = getIntent().getParcelableExtra(EXTRA_NAME_TORRENT);

        setupActionBar();

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new TorrentDetailsPagerAdapter(this, getSupportFragmentManager(), torrent);
        assert pager != null;
        pager.setAdapter(pagerAdapter);

        if (!hasTorrentInfo) {
            lastTorrentInfoRequest = new TorrentInfoGetRequest(torrent.getId());
            getTransportManager().doRequest(lastTorrentInfoRequest, new RequestListener<TorrentInfo>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    if (spiceException instanceof RequestCancelledException) return;
                    Log.e(TAG, "Failed to get torrent info", spiceException);
                    Toast.makeText(TorrentDetailsActivity.this, R.string.error_retrieve_torrent_details, Toast.LENGTH_LONG).show();
                    onBackPressed();
                }

                @Override
                public void onRequestSuccess(TorrentInfo torrentInfo) {
                    pagerAdapter.setTorrentInfo(torrentInfo);
                    hasTorrentInfo = true;
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_OPTIONS_CHANGE_REQUEST, saveChangesRequest);
        outState.putBoolean(KEY_HAS_TORRENT_INFO, hasTorrentInfo);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        saveChangesRequest = savedInstanceState.getParcelable(KEY_OPTIONS_CHANGE_REQUEST);
        super.onRestoreInstanceState(savedInstanceState);
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

        if (!hasTorrentInfo) {
            if (lastTorrentInfoRequest != null) lastTorrentInfoRequest.cancel();
            super.onBackPressed();
            return;
        }

        OptionsPageFragment optionsPage = (OptionsPageFragment) pagerAdapter.getFragment(OptionsPageFragment.class);
        TorrentSetRequest.Builder saveChangesRequestBuilder = optionsPage.getSaveOptionsRequestBuilder();

        if (saveChangesRequestBuilder == null || !saveChangesRequestBuilder.isChanged()) {
            super.onBackPressed();
            return;
        }

        saveChangesRequest = saveChangesRequestBuilder.build();
        new SaveChangesDialogFragment().show(getFragmentManager(), TAG_SAVE_CHANGES_DIALOG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSavePressed() {
        getTransportManager().doRequest(saveChangesRequest, null);
        super.onBackPressed();
    }

    @Override
    public void onDiscardPressed() {
        super.onBackPressed();
    }
}
