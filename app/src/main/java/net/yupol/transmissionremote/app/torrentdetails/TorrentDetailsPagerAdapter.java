package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.model.json.Torrent;
import net.yupol.transmissionremote.model.json.TorrentInfo;

public class TorrentDetailsPagerAdapter extends FragmentStatePagerAdapter {

    private Class<?>[] fragmentsClasses = {
            TorrentInfoPageFragment.class,
            FilesPageFragment.class,
            TrackersPageFragment.class,
            PeersPageFragment.class,
            OptionsPageFragment.class,
    };

    private int[] pageTitles = {
            R.string.info,
            R.string.files,
            R.string.trackers,
            R.string.peers,
            R.string.options
    };

    private Context context;
    private Torrent torrent;
    private TorrentInfo torrentInfo;

    public TorrentDetailsPagerAdapter(Context context, FragmentManager fragmentManager, @NonNull Torrent torrent) {
        super(fragmentManager);
        this.context = context;
        this.torrent = torrent;
    }

    public void setTorrentInfo(TorrentInfo torrentInfo) {
        this.torrentInfo = torrentInfo;
    }

    @Override
    public BasePageFragment getItem(int position) {
        BasePageFragment fragment = (BasePageFragment) Fragment.instantiate(context, fragmentsClasses[position].getName());
        fragment.setTorrent(torrent);
        if (torrentInfo != null) fragment.setTorrentInfo(torrentInfo);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragmentsClasses.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(pageTitles[position]);
    }
}
