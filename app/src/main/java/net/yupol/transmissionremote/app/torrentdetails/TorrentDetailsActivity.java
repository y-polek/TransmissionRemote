package net.yupol.transmissionremote.app.torrentdetails;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;

public class TorrentDetailsActivity extends BaseSpiceActivity {

    public static final String EXTRA_NAME_TORRENT = "extra_key_torrent";

    private static String TAG_TORRENT_DETAILS = "tag_torrent_details";

    private Torrent torrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.torrent_details_layout);

        torrent = getIntent().getParcelableExtra(EXTRA_NAME_TORRENT);

        setupActionBar();

        FragmentManager fm = getFragmentManager();
        TorrentDetailsFragment detailsFragment = (TorrentDetailsFragment) fm.findFragmentByTag(TAG_TORRENT_DETAILS);
        if (detailsFragment == null) {
            detailsFragment = new TorrentDetailsFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, detailsFragment, TAG_TORRENT_DETAILS);
            ft.commit();
        }
        detailsFragment.setTorrent(torrent);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ActionBarTitleAppearance);
        toolbar.setSubtitleTextAppearance(this, R.style.ActionBarSubTitleAppearance);

        toolbar.setTitle(R.string.torrent_details);
        toolbar.setSubtitle(torrent.getName());

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {

        TorrentDetailsFragment torrentDetailsFragment =
                (TorrentDetailsFragment)getFragmentManager().findFragmentByTag(TAG_TORRENT_DETAILS);
        final TorrentSetRequest.Builder requestBuilder = torrentDetailsFragment.getSetOptionsRequestBuilder();

        if (requestBuilder == null || !requestBuilder.isChanged()) {
            super.onBackPressed();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.save_changes_question);
        builder.setPositiveButton(R.string.save_changes_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTransportManager().doRequest(requestBuilder.build(), null);
                TorrentDetailsActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.save_changes_discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TorrentDetailsActivity.super.onBackPressed();
            }
        });
        builder.setNeutralButton(android.R.string.cancel, null);
        builder.create().show();
    }
}
