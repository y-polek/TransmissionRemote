package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;

public class TorrentDetailsPagerAdapter extends FragmentStatePagerAdapter {

    private Class<?>[] fragmentsClasses = {
            TorrentInfoPageFragment.class,
            FilesPageFragment.class,
            FilesPageFragment2.class,
            OptionsPageFragment.class,
    };

    private int[] pageTitles = {
            R.string.info,
            R.string.files,
            R.string.torrent_details,
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

    public BasePageFragment findFragment(FragmentManager fragmentManager, int position) {
        Class fragmentClass = fragmentsClasses[position];
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragmentClass.isInstance(fragment)) {
                return (BasePageFragment) fragment;
            }
        }
        return null;
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
