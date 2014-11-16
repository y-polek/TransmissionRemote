package net.yupol.transmissionremote.app;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.yupol.transmissionremote.app.transport.Torrent;

import org.apache.commons.io.FileUtils;

import java.util.Collections;
import java.util.List;

public class TorrentListFragment extends ListFragment {

    private List<Torrent> torrents = Collections.emptyList();

    public TorrentListFragment() {
        setListAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return torrents.size();
            }

            @Override
            public Torrent getItem(int position) {
                return torrents.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView;

                if (convertView == null) {
                    LayoutInflater li = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    itemView = li.inflate(R.layout.torrent_list_item, parent, false);
                } else {
                    itemView = convertView;
                }

                int bgColorId = position%2 == 0 ? R.color.torrent_list_odd_item_background
                        : R.color.torrent_list_even_item_background;
                itemView.setBackgroundColor(getResources().getColor(bgColorId));

                Torrent torrent = getItem(position);

                TextView nameText = (TextView) itemView.findViewById(R.id.name);
                nameText.setText(torrent.getName());

                TextView donePercentageText = (TextView) itemView.findViewById(R.id.done_percentage);
                donePercentageText.setText(String.format("%.2f%%", 100 * torrent.getPercentDone()));

                ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
                progressBar.setProgress((int) (torrent.getPercentDone() * progressBar.getMax()));

                TextView downloadRateText = (TextView) itemView.findViewById(R.id.download_rate);
                downloadRateText.setText(FileUtils.byteCountToDisplaySize(torrent.getDownloadRate()) + "/s");
                TextView uploadRateText = (TextView) itemView.findViewById(R.id.upload_rate);
                uploadRateText.setText(FileUtils.byteCountToDisplaySize(torrent.getUploadRate()) + "/s");

                return itemView;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void torrentsUpdated(List<Torrent> torrents) {
        this.torrents = torrents;
        ((BaseAdapter) getListAdapter()).notifyDataSetInvalidated();
    }
}
