package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.FileStat;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;

public class FilesPageFragment extends BasePageFragment {

    private static final String TAG = FilesPageFragment.class.getSimpleName();

    private ListView list;
    private ProgressBar progressBar;
    private TransportManager transportManager;

    private boolean viewCreated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.torrent_details_file_page_fragment, container, false);
        list = (ListView) view.findViewById(R.id.file_list);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        list.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        if (getTorrentInfo() != null) {
            showList();
        }

        viewCreated = true;

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewCreated = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof BaseSpiceActivity)) {
            Log.e(TAG, "Fragment should be used with BaseSpiceActivity");
            return;
        }
        transportManager = ((BaseSpiceActivity) getActivity()).getTransportManager();
    }

    @Override
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        super.setTorrentInfo(torrentInfo);
        if (viewCreated) {
            showList();
        }
    }

    private void showList() {
        list.setAdapter(new FilesAdapter(getTorrentInfo(), new FileSelectedListener(getTorrent(), transportManager)));
        list.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private static class FileSelectedListener implements CompoundButton.OnCheckedChangeListener {

        private Torrent torrent;
        private TransportManager transportManager;

        public FileSelectedListener(Torrent torrent, TransportManager transportManager) {
            this.torrent = torrent;
            this.transportManager = transportManager;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!(buttonView.getTag() instanceof FileStat)) return;

            FileStat fileStat = (FileStat) buttonView.getTag();
            if (fileStat.isWanted() != isChecked) {
                fileStat.setWanted(isChecked);

                int fileIndex = (int) buttonView.getTag(R.id.TAG_FILE_INDEX);
                TorrentSetRequest.Builder requestBuilder = TorrentSetRequest.builder(torrent.getId());
                if (fileStat.isWanted()) {
                    requestBuilder.filesWanted(fileIndex);
                } else {
                    requestBuilder.filesUnwanted(fileIndex);
                }
                transportManager.doRequest(requestBuilder.build(), null);
            }
        }
    }
}
