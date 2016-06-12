package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.yupol.transmissionremote.app.R;

public class TorrentInfoPageFragment extends BasePageFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.torrent_details_info_page_fragment, container, false);


        return view;
    }
}
