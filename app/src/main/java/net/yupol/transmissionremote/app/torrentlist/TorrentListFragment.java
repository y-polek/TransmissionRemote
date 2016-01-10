package net.yupol.transmissionremote.app.torrentlist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.TransmissionRemote.OnFilterSelectedListener;
import net.yupol.transmissionremote.app.TransmissionRemote.OnTorrentsUpdatedListener;
import net.yupol.transmissionremote.app.filtering.Filter;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.Request;
import net.yupol.transmissionremote.app.transport.request.StartTorrentRequest;
import net.yupol.transmissionremote.app.transport.request.StopTorrentRequest;
import net.yupol.transmissionremote.app.utils.SizeUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TorrentListFragment extends Fragment {

    private static final String TAG = TorrentListFragment.class.getSimpleName();

    private static final String MAX_STRING = "999.9 MB/s";

    private TransmissionRemote app;

    private Collection<Torrent> allTorrents = Collections.emptyList();

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
        public void filterSelected(Filter filter) {
            updateTorrentList();
        }
    };

    private OnTorrentSelectedListener torrentSelectedListener;
    private TorrentsAdapter adapter;
    private TextView emptyText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (TransmissionRemote) activity.getApplication();
        app.addOnFilterSetListener(filterListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.torrent_list_layout, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.torrent_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext()));
        adapter = new TorrentsAdapter(container.getContext());
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });
        recyclerView.setAdapter(adapter);

        emptyText = (TextView) view.findViewById(R.id.torrent_list_empty_text);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        app.addTorrentsUpdatedListener(torrentsListener);
        allTorrents = app.getTorrents();
        updateTorrentList();
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

    public void setOnTorrentSelectedListener(OnTorrentSelectedListener listener) {
        torrentSelectedListener = listener;
    }

    private void updateTorrentList() {
        Filter filter = app.getActiveFilter();
        List<Torrent> torrentsToShow = new ArrayList<>(FluentIterable.from(allTorrents).filter(filter).toList());
        if (comparator != null)
            Collections.sort(torrentsToShow, comparator);
        adapter.setTorrents(torrentsToShow);
        adapter.notifyDataSetChanged();

        setEmptyText(getResources().getString(filter.getEmptyMessageResId()));
        updateEmptyTextVisibility();
    }

    private void setEmptyText(String text) {
        emptyText.setText(text);
    }

    private void updateEmptyTextVisibility() {
        emptyText.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
    }

    public void setSort(Comparator<Torrent> comparator) {
        this.comparator = comparator;
        if (allTorrents != null && !allTorrents.isEmpty())
            updateTorrentList();
    }

    public interface OnTorrentSelectedListener {
        void onTorrentSelected(Torrent torrent);
    }

    private class TorrentsAdapter extends RecyclerView.Adapter<ViewHolder> {

        private Context context;
        private List<Torrent> torrents;

        public TorrentsAdapter(Context context) {
            this.context = context;
        }

        public void setTorrents(List<Torrent> torrents) {
            this.torrents = torrents;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.torrent_list_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder(itemView, ((BaseSpiceActivity) getActivity()).getTransportManager());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (torrentSelectedListener != null) {
                        torrentSelectedListener.onTorrentSelected(viewHolder.torrent);
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Torrent torrent = getItemAtPosition(position);
            holder.setTorrent(torrent);

            holder.nameText.setText(torrent.getName());

            String totalSize = SizeUtils.displayableSize(torrent.getTotalSize());
            String downloadedText;
            if (torrent.getPercentDone() == 1.0) {
                downloadedText = totalSize;
            } else {
                String downloadedSize = SizeUtils.displayableSize((long) (torrent.getPercentDone() * torrent.getTotalSize()));
                String percentDone = String.format("%.2f%%", 100 * torrent.getPercentDone());
                downloadedText = context.getString(R.string.downloaded_text, downloadedSize, totalSize, percentDone);
            }
            holder.downloadedTextView.setText(downloadedText);

            double uploadRatio = Math.max(torrent.getUploadRatio(), 0.0);
            String uploadedText = context.getString(R.string.uploaded_text,
                    SizeUtils.displayableSize(torrent.getUploadedSize()), uploadRatio);
            holder.uploadedTextView.setText(uploadedText);

            holder.progressBar.setProgress((int) (torrent.getPercentDone() * holder.progressBar.getMax()));
            boolean isPaused = torrent.getStatus() == Torrent.Status.STOPPED;
            int progressbarDrawable = isPaused ? R.drawable.torrent_progressbar_disabled : R.drawable.torrent_progressbar;
            holder.progressBar.setProgressDrawable(context.getResources().getDrawable(progressbarDrawable));

            holder.downloadRateText.setText(speedText(torrent.getDownloadRate()));
            holder.uploadRateText.setText(speedText(torrent.getUploadRate()));

            holder.pauseResumeBtn.setPaused(isPaused);

            Torrent.Error error = torrent.getError();
            if (error == Torrent.Error.NONE) {
                holder.errorMsgView.setVisibility(View.GONE);
            } else {
                String errorMsg = torrent.getErrorMessage();
                if (errorMsg != null && !errorMsg.trim().isEmpty()) {
                    holder.errorMsgView.setVisibility(View.VISIBLE);
                    holder.errorMsgView.setText(errorMsg);
                    int msgIconResId = error.isWarning() ? R.drawable.ic_action_warning : R.drawable.ic_action_error;
                    Drawable msgIcon = context.getResources().getDrawable(msgIconResId);
                    int size = context.getResources().getDimensionPixelSize(R.dimen.torrent_list_error_icon_size);
                    msgIcon.setBounds(0, 0, size, size);
                    holder.errorMsgView.setCompoundDrawables(msgIcon, null, null, null);
                } else {
                    holder.errorMsgView.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return torrents.size();
        }

        public Torrent getItemAtPosition(int position) {
            return torrents.get(position);
        }

        private String speedText(long bytes) {
            return Strings.padStart(SizeUtils.displayableSize(bytes), 5, ' ') + "/s";
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public Torrent torrent;

        public final TextView nameText;
        public final TextView downloadedTextView;
        public final TextView uploadedTextView;
        public final ProgressBar progressBar;
        public final TextView downloadRateText;
        public final TextView uploadRateText;
        public final PlayPauseButton pauseResumeBtn;
        public final TextView errorMsgView;

        public ViewHolder(View itemView, final TransportManager transportManager) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.name);
            downloadedTextView = (TextView) itemView.findViewById(R.id.downloaded_text);
            uploadedTextView = (TextView) itemView.findViewById(R.id.uploaded_text);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);

            downloadRateText = (TextView) itemView.findViewById(R.id.download_rate);
            uploadRateText = (TextView) itemView.findViewById(R.id.upload_rate);
            Rect bounds = new Rect();
            downloadRateText.getPaint().getTextBounds(MAX_STRING, 0, MAX_STRING.length(), bounds);
            int maxWidth = bounds.width();
            downloadRateText.setWidth(maxWidth);
            uploadRateText.setWidth(maxWidth);

            pauseResumeBtn = (PlayPauseButton) itemView.findViewById(R.id.pause_resume_button);
            pauseResumeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayPauseButton btn = (PlayPauseButton) v;
                    boolean wasPaused = btn.isPaused();
                    btn.toggle();

                    Request<Void> request = wasPaused
                            ? new StartTorrentRequest(Collections.singletonList(torrent))
                            : new StopTorrentRequest(Collections.singletonList(torrent));
                    transportManager.doRequest(request, null);
                }
            });

            errorMsgView = (TextView) itemView.findViewById(R.id.error_message);
        }

        public void setTorrent(Torrent torrent) {
            this.torrent = torrent;
        }
    }

    private static class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public DividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

            int childCount = parent.getChildCount();
            for (int i=0; i<childCount-1; i++) {
                View child = parent.getChildAt(i);
                int left = parent.getPaddingLeft() + child.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight() - child.getPaddingRight();

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
