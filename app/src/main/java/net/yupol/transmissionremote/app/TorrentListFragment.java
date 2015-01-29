package net.yupol.transmissionremote.app;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;

import net.yupol.transmissionremote.app.TransmissionRemote.OnFilterSelectedListener;
import net.yupol.transmissionremote.app.TransmissionRemote.OnTorrentsUpdatedListener;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.utils.SizeUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TorrentListFragment extends ListFragment {

    private static final String TAG = TorrentListFragment.class.getSimpleName();

    private static final String MAX_STRING = "999.9 MB/s";

    private TransmissionRemote app;

    private Collection<Torrent> allTorrents = Collections.emptyList();
    private List<Torrent> torrentsToShow = Collections.emptyList();

    private Comparator<Torrent> comparator;

    private OnTorrentsUpdatedListener torrentsListener = new OnTorrentsUpdatedListener() {
        @Override
        public void torrentsUpdated(Collection<Torrent> torrents) {
            allTorrents = torrents;
            TorrentListFragment.this.updateTorrentList();
        }
    };

    private OnFilterSelectedListener filterListener = new OnFilterSelectedListener() {
        @Override
        public void filterSelected(Predicate<Torrent> filter) {
            updateTorrentList();
        }
    };

    public TorrentListFragment() {
        setListAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return torrentsToShow.size();
            }

            @Override
            public Torrent getItem(int position) {
                return torrentsToShow.get(position);
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
                downloadRateText.setText(speedText(torrent.getDownloadRate()));

                TextView uploadRateText = (TextView) itemView.findViewById(R.id.upload_rate);
                uploadRateText.setText(speedText(torrent.getUploadRate()));

                Rect bounds = new Rect();
                downloadRateText.getPaint().getTextBounds(MAX_STRING, 0, MAX_STRING.length(), bounds);
                int maxWidth = bounds.width();
                downloadRateText.setWidth(maxWidth);
                uploadRateText.setWidth(maxWidth);

                return itemView;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (TransmissionRemote) activity.getApplication();
        app.addOnFilterSetListener(filterListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        app.addTorrentsUpdatedListener(torrentsListener);
    }

    @Override
    public void onStop() {
        app.removeTorrentsUpdatedListener(torrentsListener);
        super.onStop();
    }

    @Override
    public void onDetach() {
        app.removeOnFilterSelectedListener(filterListener);
        super.onDetach();
    }

    private void updateTorrentList() {
        torrentsToShow = new ArrayList<>(FluentIterable.from(allTorrents).filter(app.getFilter()).toList());
        new ArrayList<>();
        if (comparator != null)
            Collections.sort(torrentsToShow, comparator);
        ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public void setSort(Comparator<Torrent> comparator) {
        this.comparator = comparator;
        if (allTorrents != null && !allTorrents.isEmpty())
            updateTorrentList();
    }

    private String speedText(long bytes) {
        return Strings.padStart(SizeUtils.displayableSize(bytes), 5, ' ') + "/s";
    }
}
