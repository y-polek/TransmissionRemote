package net.yupol.transmissionremote.app.torrentdetails;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.TorrentDetailsInfoPageFragmentBinding;
import net.yupol.transmissionremote.model.json.Torrent;
import net.yupol.transmissionremote.model.json.TorrentInfo;

public class TorrentInfoPageFragment extends BasePageFragment {

    private TorrentDetailsInfoPageFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.torrent_details_info_page_fragment, container, false);
        binding.setTorrent(getTorrent());
        binding.setTorrentInfo(getTorrentInfo());

        return binding.getRoot();
    }

    @Override
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        super.setTorrentInfo(torrentInfo);
        if (binding != null) {
            binding.setTorrentInfo(torrentInfo);
        }
    }

    @Override
    public void setTorrent(Torrent torrent) {
        super.setTorrent(torrent);
        if (binding != null) {
            binding.setTorrent(torrent);
        }
    }
}
