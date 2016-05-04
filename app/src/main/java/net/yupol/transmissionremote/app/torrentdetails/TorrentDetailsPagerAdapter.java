package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;

public class TorrentDetailsPagerAdapter extends FragmentPagerAdapter {

    private Class<?>[] fragmentsClasses = {
            FilesPageFragment.class,
            OptionsPageFragment.class,
    };

    private SparseArray<BasePageFragment> fragments = new SparseArray<>();

    private Context context;
    private Torrent torrent;

    public TorrentDetailsPagerAdapter(Context context, FragmentManager fragmentManager, Torrent torrent) {
        super(fragmentManager);
        this.context = context;
        this.torrent = torrent;
    }

    public void setTorrentInfo(TorrentInfo torrentInfo) {
        for (int i=0; i<fragments.size(); i++) {
            fragments.valueAt(i).setTorrentInfo(torrentInfo);
        }
    }

    @Override
    public BasePageFragment getItem(int position) {
        BasePageFragment fragment = (BasePageFragment) Fragment.instantiate(context, fragmentsClasses[position].getName());
        fragment.setTorrent(torrent);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragmentsClasses.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(getItem(position).getPageTitleRes());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BasePageFragment fragment = (BasePageFragment) super.instantiateItem(container, position);
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public BasePageFragment getFragment(Class<? extends BasePageFragment> c) {
        for (int i=0; i<fragments.size(); i++) {
            BasePageFragment f = fragments.valueAt(i);
            if (c.isInstance(f)) {
                return f;
            }
        }
        return null;
    }
}
