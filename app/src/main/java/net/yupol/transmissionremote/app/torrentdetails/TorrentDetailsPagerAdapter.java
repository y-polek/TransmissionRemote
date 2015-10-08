package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.yupol.transmissionremote.app.model.json.Torrent;

public class TorrentDetailsPagerAdapter extends FragmentPagerAdapter {

    private BasePageFragment[] fragments = {
            new FilesPageFragment(),
            new OptionsPageFragment(),
            new PeersPageFragment()
    };

    private Context context;

    public TorrentDetailsPagerAdapter(Context context, FragmentManager fragmentManager, Torrent torrent) {
        super(fragmentManager);
        this.context = context;
        for (BasePageFragment fragment : fragments) {
            fragment.setTorrent(torrent);
        }
    }

    @Override
    public BasePageFragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(getItem(position).getPageTitleRes());
    }
}
