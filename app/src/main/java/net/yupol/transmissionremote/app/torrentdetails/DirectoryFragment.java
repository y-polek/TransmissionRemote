package net.yupol.transmissionremote.app.torrentdetails;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.primitives.Ints;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.Dir;
import net.yupol.transmissionremote.app.model.Priority;
import net.yupol.transmissionremote.app.model.json.File;
import net.yupol.transmissionremote.app.model.json.FileStat;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;
import net.yupol.transmissionremote.app.utils.DividerItemDecoration;
import net.yupol.transmissionremote.app.utils.ParcelableUtils;

public class DirectoryFragment extends Fragment implements DirectoryAdapter.OnItemSelectedListener {

    private static final String TAG = DirectoryFragment.class.getSimpleName();
    private static final String ARG_TORRENT_ID = "arg_torrent_id";
    private static final String ARG_FILE_STATS = "arg_file_stats";

    private int torrentId;
    private Dir dir;
    private File[] files;
    private FileStat[] fileStats;

    private DirectoryAdapter adapter;
    private TransportManager transportManager;

    @SuppressWarnings("SuspiciousSystemArraycopy")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) throw new IllegalArgumentException("No args found");
        torrentId = args.getInt(ARG_TORRENT_ID, -1);

        if (!(getParentFragment() instanceof FilesPageFragment)) {
            throw new IllegalStateException("DirectoryFragment must be used only in FilesPageFragment. Actual parent: " + getParentFragment());
        }

        // Passing Dir, Files and FileStats through parent fragment instead of fragment arguments
        // because amount of data may be too large (> 1 MB) to pass it through Parcelable.
        FilesPageFragment parentFragment = (FilesPageFragment) getParentFragment();
        dir = parentFragment.getCurrentDir();
        if (dir == null) return;

        files = parentFragment.getTorrentInfo().getFiles();

        if (savedInstanceState == null) {
            fileStats = parentFragment.getTorrentInfo().getFileStats();
        } else {
            fileStats = ParcelableUtils.toArrayOfType(FileStat.class, savedInstanceState.getParcelableArray(ARG_FILE_STATS));
        }

        if (torrentId < 0 || dir == null || files == null || fileStats == null) {
            throw new IllegalArgumentException("Torrent ID, directory, files and file stats must be passed as arguments");
        }

        adapter = new DirectoryAdapter(getContext(), dir, files, fileStats, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.directory_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (!(activity instanceof BaseSpiceActivity)) {
            Log.e(TAG, "Fragment should be used with BaseSpiceActivity");
            return;
        }
        transportManager = ((BaseSpiceActivity) activity).getTransportManager();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(ARG_FILE_STATS, fileStats);
    }

    @Override
    public void onDirectorySelected(int position) {
        Dir selectedDir = dir.getDirs().get(position);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnDirectorySelectedListener) {
            ((OnDirectorySelectedListener) parentFragment).onDirectorySelected(selectedDir);
        }
    }

    @Override
    public void onDirectoryChecked(int position, boolean isChecked) {
        Dir checkedDir = dir.getDirs().get(position);
        int[] fileIndices = Ints.toArray(Dir.filesInDirRecursively(checkedDir));
        for (int fileIndex : fileIndices) {
            fileStats[fileIndex].setWanted(isChecked);
        }

        TorrentSetRequest.Builder requestBuilder = TorrentSetRequest.builder(torrentId);
        if (isChecked) {
            requestBuilder.filesWanted(fileIndices);
        } else {
            requestBuilder.filesUnwanted(fileIndices);
        }
        transportManager.doRequest(requestBuilder.build(), null);
    }

    @Override
    public void onFileChecked(int fileIndex, boolean isChecked) {
        FileStat fileStat = fileStats[fileIndex];
        fileStat.setWanted(isChecked);

        TorrentSetRequest.Builder requestBuilder = TorrentSetRequest.builder(torrentId);
        if (fileStat.isWanted()) {
            requestBuilder.filesWanted(fileIndex);
        } else {
            requestBuilder.filesUnwanted(fileIndex);
        }
        transportManager.doRequest(requestBuilder.build(), null);
    }

    @Override
    public void onDirectoryPriorityChanged(int position, Priority priority) {
        Dir changedDir = dir.getDirs().get(position);
        transportManager.doRequest(TorrentSetRequest.builder(torrentId)
                .filesWithPriority(priority, Dir.filesInDirRecursively(changedDir)).build(), null);
    }

    @Override
    public void onFilePriorityChanged(int fileIndex, Priority priority) {
        transportManager.doRequest(TorrentSetRequest.builder(torrentId)
                .filesWithPriority(priority, fileIndex).build(), new RequestListener<Void>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d(TAG, "failed to change priority");
            }

            @Override
            public void onRequestSuccess(Void aVoid) {
                Log.d(TAG, "success");
            }
        });
    }

    public void setFiles(File[] files) {
        this.files = files;
        adapter.setFiles(files);
    }

    public void setFileStats(FileStat[] fileStats) {
        this.fileStats = fileStats;
        adapter.setFileStats(fileStats);
    }

    /**
     * Dir, Files and FileStat argument will be queried from parent fragment
     */
    public static DirectoryFragment newInstance(int torrentId) {
        DirectoryFragment fragment = new DirectoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TORRENT_ID, torrentId);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnDirectorySelectedListener {
        void onDirectorySelected(Dir dir);
    }
}
