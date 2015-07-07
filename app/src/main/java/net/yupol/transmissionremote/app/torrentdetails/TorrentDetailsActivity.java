package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;

public class TorrentDetailsActivity extends BaseSpiceActivity {

    public static final String EXTRA_NAME_TORRENT = "extra_key_torrent";

    private Torrent torrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.torrent_details_layout);

        torrent = getIntent().getParcelableExtra(EXTRA_NAME_TORRENT);

        setupActionBar();
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
}
