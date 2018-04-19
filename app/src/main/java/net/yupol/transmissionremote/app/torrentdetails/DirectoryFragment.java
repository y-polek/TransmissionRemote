package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.primitives.Ints;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.model.Dir;
import net.yupol.transmissionremote.app.utils.DividerItemDecoration;
import net.yupol.transmissionremote.model.Priority;
import net.yupol.transmissionremote.model.json.File;
import net.yupol.transmissionremote.model.json.FileStat;
import net.yupol.transmissionremote.transport.Transport;
import net.yupol.transmissionremote.transport.rpc.RpcArgs;
import net.yupol.transmissionremote.utils.Parcelables;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static net.yupol.transmissionremote.transport.rpc.TorrentParameters.filesUnwanted;
import static net.yupol.transmissionremote.transport.rpc.TorrentParameters.filesWanted;
import static net.yupol.transmissionremote.transport.rpc.TorrentParameters.filesWithPriority;
import static net.yupol.transmissionremote.utils.Collections.toArray;

public class DirectoryFragment extends Fragment implements DirectoryAdapter.OnItemSelectedListener {

    private static final String ARG_TORRENT_ID = "arg_torrent_id";
    private static final String ARG_DIRECTORY = "arg_directory";
    private static final String ARG_FILES = "arg_files";
    private static final String ARG_FILE_STATS = "arg_file_stats";

    private int torrentId;
    private Dir dir;
    private File[] files;
    private FileStat[] fileStats;

    private DirectoryAdapter adapter;
    private Transport transport;

    @SuppressWarnings("SuspiciousSystemArraycopy")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        torrentId = args.getInt(ARG_TORRENT_ID, -1);
        dir = args.getParcelable(ARG_DIRECTORY);
        files = Parcelables.toArrayOfType(File.class, args.getParcelableArray(ARG_FILES));

        transport = new Transport(TransmissionRemote.getInstance().getActiveServer());

        if (savedInstanceState == null) {
            fileStats = Parcelables.toArrayOfType(FileStat.class, args.getParcelableArray(ARG_FILE_STATS));
        } else {
            fileStats = Parcelables.toArrayOfType(FileStat.class, savedInstanceState.getParcelableArray(ARG_FILE_STATS));
        }

        if (torrentId < 0 || dir == null || files == null || fileStats == null) {
            throw new IllegalArgumentException("Torrent ID, directory, files and file stats must be passed as arguments");
        }

        adapter = new DirectoryAdapter(getContext(), dir, files, fileStats, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
    public void onSaveInstanceState(Bundle outState) {
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

        Map<String, Object> parameters = RpcArgs.parameters(
                torrentId,
                isChecked ? filesWanted(fileIndices) : filesUnwanted(fileIndices)
        );
        transport.api().setTorrentSettings(parameters)
                .subscribeOn(Schedulers.io())
                .onErrorComplete()
                .subscribe();
    }

    @Override
    public void onFileChecked(int fileIndex, boolean isChecked) {
        FileStat fileStat = fileStats[fileIndex];
        fileStat.setWanted(isChecked);

        Map<String, Object> parameters = RpcArgs.parameters(
                torrentId,
                fileStat.isWanted() ? filesWanted(fileIndex) : filesUnwanted(fileIndex));
        transport.api().setTorrentSettings(parameters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorComplete()
                .subscribe();
    }

    @Override
    public void onDirectoryPriorityChanged(int position, Priority priority) {
        Dir changedDir = dir.getDirs().get(position);
        int[] fileIndices = toArray(Dir.filesInDirRecursively(changedDir));

        Map<String, Object> parameters = RpcArgs.parameters(torrentId, filesWithPriority(priority, fileIndices));
        transport.api().setTorrentSettings(parameters)
                .subscribeOn(Schedulers.io())
                .onErrorComplete()
                .subscribe();
    }

    @Override
    public void onFilePriorityChanged(int fileIndex, Priority priority) {
        Map<String, Object> parameters = RpcArgs.parameters(torrentId, filesWithPriority(priority, fileIndex));
        transport.api().setTorrentSettings(parameters)
                .subscribeOn(Schedulers.io())
                .onErrorComplete()
                .subscribe();
    }

    public void setFiles(File[] files) {
        this.files = files;
        adapter.setFiles(files);
    }

    public void setFileStats(FileStat[] fileStats) {
        this.fileStats = fileStats;
        adapter.setFileStats(fileStats);
    }

    public static DirectoryFragment newInstance(int torrentId, Dir dir, File[] files, FileStat[] fileStats) {
        DirectoryFragment fragment = new DirectoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TORRENT_ID, torrentId);
        args.putParcelable(ARG_DIRECTORY, dir);
        args.putParcelableArray(ARG_FILES, files);
        args.putParcelableArray(ARG_FILE_STATS, fileStats);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnDirectorySelectedListener {
        void onDirectorySelected(Dir dir);
    }
}
