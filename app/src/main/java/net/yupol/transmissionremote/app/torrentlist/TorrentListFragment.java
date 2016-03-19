package net.yupol.transmissionremote.app.torrentlist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.TransmissionRemote.OnFilterSelectedListener;
import net.yupol.transmissionremote.app.TransmissionRemote.OnSortingChangedListener;
import net.yupol.transmissionremote.app.TransmissionRemote.OnTorrentsUpdatedListener;
import net.yupol.transmissionremote.app.filtering.Filter;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.Torrents;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.Request;
import net.yupol.transmissionremote.app.transport.request.StartTorrentRequest;
import net.yupol.transmissionremote.app.transport.request.StopTorrentRequest;
import net.yupol.transmissionremote.app.transport.request.TorrentGetRequest;
import net.yupol.transmissionremote.app.utils.ColorUtils;
import net.yupol.transmissionremote.app.utils.SizeUtils;
import net.yupol.transmissionremote.app.utils.diff.Equals;
import net.yupol.transmissionremote.app.utils.diff.ListDiff;
import net.yupol.transmissionremote.app.utils.diff.Range;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TorrentListFragment extends Fragment {

    private static final String TAG = TorrentListFragment.class.getSimpleName();

    private static final String MAX_STRING = "999.9 MB/s";
    private static Equals<Torrent> DISPLAYED_FIELDS_EQUALS_IMPL = new DisplayedFieldsEquals();
    private static final long UPDATE_REQUEST_DELAY = 500;

    private TransmissionRemote app;
    private TransportManager transportManager;

    private Collection<Torrent> allTorrents = Collections.emptyList();
    private Set<Integer/*torrent ID*/> updateRequests = new HashSet<>();

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

    private OnSortingChangedListener sortingListener = new OnSortingChangedListener() {
        @Override
        public void onSortingChanged(Comparator<Torrent> comparator) {
            updateTorrentList();
        }
    };

    private OnTorrentSelectedListener torrentSelectedListener;
    private TorrentsAdapter adapter;
    private TextView emptyText;

    private ContextualActionBarListener cabListener;

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_torrent_list_menu, menu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().setStatusBarColor(ColorUtils.resolveColor(getContext(), R.attr.colorPrimaryDark, R.color.primary_dark));
            }

            if (cabListener != null) cabListener.onCABOpen();

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_remove_torrents:
                    mode.finish();
                    return true;
                case R.id.action_select_all:
                    if (adapter.getSelectedItemsCount() < adapter.getItemCount()) {
                        adapter.selectAll();
                    } else {
                        adapter.clearSelection();
                    }
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            actionMode = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
            }

            if (cabListener != null) cabListener.onCABClose();
        }
    };
    private ActionMode actionMode;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = getActivity();
        if (activity == null) throw new IllegalStateException("Fragment must be attached to activity");

        app = (TransmissionRemote) getActivity().getApplication();
        app.addOnFilterSetListener(filterListener);
        app.addOnSortingChangedListeners(sortingListener);

        if (!(activity instanceof BaseSpiceActivity))
            throw new IllegalStateException("Fragment must be used with BaseSpiceActivity");
        transportManager = ((BaseSpiceActivity) activity).getTransportManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.torrent_list_layout, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.torrent_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext()));
        recyclerView.setItemAnimator(null);
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
        if (actionMode != null) {
            actionMode.finish();
        }
        super.onStop();
    }

    @Override
    public void onDetach() {
        app.removeOnFilterSelectedListener(filterListener);
        app.removeOnSortingChangedListener(sortingListener);
        super.onDetach();
    }

    public void setOnTorrentSelectedListener(OnTorrentSelectedListener listener) {
        torrentSelectedListener = listener;
    }

    public void setContextualActionBarListener(ContextualActionBarListener listener) {
        cabListener = listener;
    }

    private void updateTorrentList() {
        Filter filter = app.getActiveFilter();
        List<Torrent> torrentsToShow = new ArrayList<>(FluentIterable.from(allTorrents).filter(filter).toList());

        Comparator<Torrent> comparator = app.getSortComparator();
        if (comparator != null)
            Collections.sort(torrentsToShow, comparator);

        ListDiff<Torrent> diff = new ListDiff<>(adapter.getTorrents(), torrentsToShow, DISPLAYED_FIELDS_EQUALS_IMPL);
        adapter.setTorrents(torrentsToShow);

        if (diff.containStructuralChanges()) {
            adapter.notifyDataSetChanged();
        } else {
            List<Range> changes = diff.getChangedItems();
            for (Range change : changes) {
                for (int position = change.start; position < change.start + change.count; position++) {
                    Torrent torrent = torrentsToShow.get(position);
                    if (!updateRequests.contains(torrent.getId())) {
                        adapter.notifyItemChanged(position);
                    }
                }
            }
        }

        if (actionMode != null) {
            adapter.updateSelection();
        }

        setEmptyText(getResources().getString(filter.getEmptyMessageResId()));
        updateEmptyTextVisibility();
    }

    private void setEmptyText(String text) {
        emptyText.setText(text);
    }

    private void updateEmptyTextVisibility() {
        emptyText.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
    }

    private class TorrentsAdapter extends RecyclerView.Adapter<ViewHolder> {

        private Context context;
        private List<Torrent> torrents = Collections.emptyList();
        private SparseBooleanArray selectedItemsIds = new SparseBooleanArray();

        public TorrentsAdapter(Context context) {
            this.context = context;
        }

        public void setTorrents(List<Torrent> torrents) {
            this.torrents = torrents;
        }

        public List<Torrent> getTorrents() {
            return torrents;
        }

        @Override
        public int getItemCount() {
            return torrents.size();
        }

        public Torrent getItemAtPosition(int position) {
            return torrents.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.torrent_list_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (actionMode == null) {
                        if (torrentSelectedListener != null) {
                            torrentSelectedListener.onTorrentSelected(viewHolder.torrent);
                        }
                    } else {
                        v.setSelected(true);
                        toggleSelection(viewHolder.getAdapterPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (actionMode != null) {
                        return false;
                    }

                    actionMode = getActivity().startActionMode(actionModeCallback);
                    toggleSelection(viewHolder.getAdapterPosition());
                    return true;
                }
            });

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Torrent torrent = getItemAtPosition(position);
            holder.setTorrent(torrent);
            holder.pauseResumeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayPauseButton btn = (PlayPauseButton) v;
                    boolean wasPaused = btn.isPaused();
                    btn.toggle();

                    Request<Void> request = wasPaused
                            ? new StartTorrentRequest(Collections.singletonList(torrent))
                            : new StopTorrentRequest(Collections.singletonList(torrent));
                    transportManager.doRequest(request, new RequestListener<Void>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            Log.e(TAG, "Failed to start/stop torrent", spiceException);
                            sendTorrentGetRequest(torrent);
                        }

                        @Override
                        public void onRequestSuccess(Void aVoid) {
                            sendTorrentGetRequest(torrent);
                        }
                    });
                }
            });

            holder.nameText.setText(torrent.getName());

            String totalSize = SizeUtils.displayableSize(torrent.getTotalSize());
            String downloadedText;
            if (torrent.getPercentDone() == 1.0) {
                downloadedText = totalSize;
            } else {
                String downloadedSize = SizeUtils.displayableSize((long) (torrent.getPercentDone() * torrent.getTotalSize()));
                String percentDone = String.format(Locale.getDefault(), "%.2f%%", 100 * torrent.getPercentDone());
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
                    if (msgIcon != null) msgIcon.setBounds(0, 0, size, size);
                    holder.errorMsgView.setCompoundDrawables(msgIcon, null, null, null);
                } else {
                    holder.errorMsgView.setVisibility(View.GONE);
                }
            }

            holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
        }

        public int getSelectedItemsCount() {
            return selectedItemsIds.size();
        }

        public int[] getSelectedItemsPositions() {
            int[] positions = new int[selectedItemsIds.size()];
            for (int i = 0; i< selectedItemsIds.size(); i++) {
                positions[i] = getPositionByTorrentId(selectedItemsIds.keyAt(i));
            }
            return positions;
        }

        public boolean isSelected(int position) {
            return selectedItemsIds.get(getItemAtPosition(position).getId(), false);
        }

        public void toggleSelection(int position) {
            int id = getItemAtPosition(position).getId();
            if (selectedItemsIds.get(id, false)) {
                selectedItemsIds.delete(id);
            } else {
                selectedItemsIds.put(id, true);
            }
            notifyItemChanged(position);
            updateCABTitle();
        }

        public void selectAll() {
            for (int i=0; i<getItemCount(); i++) {
                selectedItemsIds.put(getItemAtPosition(i).getId(), true);
                notifyItemChanged(i);
            }
            updateCABTitle();
        }

        public void clearSelection() {
            int[] positions = getSelectedItemsPositions();
            selectedItemsIds.clear();
            for (int position : positions) {
                notifyItemChanged(position);
            }
            updateCABTitle();
        }

        public void updateSelection() {
            int[] idsToRemove = new int[selectedItemsIds.size()];
            int removeCount = 0;

            for (int i=0; i<selectedItemsIds.size(); i++) {
                int id = selectedItemsIds.keyAt(i);
                if (getPositionByTorrentId(id) < 0) {
                    idsToRemove[removeCount++] = id;
                }
            }

            for (int i=0; i<removeCount; i++) {
                int id = idsToRemove[i];
                selectedItemsIds.delete(id);
            }

            updateCABTitle();
        }

        private void updateCABTitle() {
            actionMode.setTitle(adapter.getSelectedItemsCount() + " torrents");
            // TODO: implement
        }

        private void sendTorrentGetRequest(final Torrent torrent) {
            updateRequests.add(torrent.getId());
            transportManager.doRequest(new TorrentGetRequest(torrent.getId()), new RequestListener<Torrents>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    updateRequests.remove(torrent.getId());
                    Log.e(TAG, "Failed to update torrent", spiceException);
                }

                @Override
                public void onRequestSuccess(Torrents torrents) {
                    updateRequests.remove(torrent.getId());
                    if (torrents.size() != 1) {
                        Log.e(TAG, "Response must contain one torrent");
                        return;
                    }
                    updateTorrent(torrents.get(0));
                }
            }, UPDATE_REQUEST_DELAY);
        }

        private String speedText(long bytes) {
            return Strings.padStart(SizeUtils.displayableSize(bytes), 5, ' ') + "/s";
        }

        private void updateTorrent(Torrent torrent) {
            int position = getPositionByTorrentId(torrent.getId());
            if (position >= 0) {
                torrents.set(position, torrent);
                notifyItemChanged(position);
            }
        }

        private int getPositionByTorrentId(int torrentId) {
            for (int i=0; i<torrents.size(); i++) {
                if (torrents.get(i).getId() == torrentId)
                    return i;
            }
            return -1;
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
        public final View selectedOverlay;

        public ViewHolder(View itemView) {
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

            errorMsgView = (TextView) itemView.findViewById(R.id.error_message);

            selectedOverlay = itemView.findViewById(R.id.selected_overlay);
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

    private static class DisplayedFieldsEquals implements Equals<Torrent> {
        @Override
        public boolean equals(Torrent t1, Torrent t2) {
            if (t1 == null) return t2 == null;

            if (t1.getId() != t2.getId()) return false;
            if (t1.getTotalSize() != t2.getTotalSize()) return false;
            if (Double.compare(t1.getPercentDone(), t2.getPercentDone()) != 0) return false;
            if (t1.getStatus() != t2.getStatus()) return false;
            if (t1.getDownloadRate() != t2.getDownloadRate()) return false;
            if (t1.getUploadRate() != t2.getUploadRate()) return false;
            if (t1.getUploadedSize() != t2.getUploadedSize()) return false;
            if (Double.compare(t1.getUploadRatio(), t2.getUploadRatio()) != 0) return false;
            if (t1.getErrorId() != t2.getErrorId()) return false;
            String t1Name = t1.getName();
            String t2Name = t2.getName();
            return t1Name != null ? t1Name.equals(t2Name) : t2Name != null;
        }
    }

    public interface OnTorrentSelectedListener {
        void onTorrentSelected(Torrent torrent);
    }

    public interface ContextualActionBarListener {
        void onCABOpen();
        void onCABClose();
    }
}
