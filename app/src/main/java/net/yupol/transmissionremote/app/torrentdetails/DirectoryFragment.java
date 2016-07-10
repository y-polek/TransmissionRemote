package net.yupol.transmissionremote.app.torrentdetails;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.primitives.Ints;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.Dir;
import net.yupol.transmissionremote.app.model.json.FileStat;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;
import net.yupol.transmissionremote.app.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Stack;

public class DirectoryFragment extends BasePageFragment implements DirectoryAdapter.OnItemSelectedListener {

    private static final String TAG = DirectoryFragment.class.getSimpleName();
    private static final String KEY_PATH = "key_path";

    private DirectoryAdapter adapter;
    private boolean viewCreated;
    private Stack<Dir> path = new Stack<>();
    private RecyclerView recyclerView;

    private TransportManager transportManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.directory_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setAdapter(adapter);

        path.clear();
        TorrentInfo torrentInfo = getTorrentInfo();
        if (savedInstanceState != null) {
            ArrayList<Dir> dirs = savedInstanceState.getParcelableArrayList(KEY_PATH);
            path.addAll(dirs);
        } else if (torrentInfo != null) {
            path.push(Dir.createFileTree(torrentInfo.getFiles()));
        }

        if (torrentInfo != null) {
            setupAdapter();
            showTopDir();
        }

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
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        super.setTorrentInfo(torrentInfo);
        if (viewCreated) {
            setupAdapter();
            path.push(Dir.createFileTree(torrentInfo.getFiles()));
            showTopDir();
        }
    }

    private void setupAdapter() {
        TorrentInfo torrentInfo = getTorrentInfo();
        adapter = new DirectoryAdapter(torrentInfo.getFiles(), torrentInfo.getFileStats(), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewCreated = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewCreated = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_PATH, new ArrayList<>(path));
    }

    @Override
    public void onDirectorySelected(int position) {
        Dir currentDir = path.peek();
        Dir newDir = currentDir.getDirs().get(position);
        path.push(newDir);
        showTopDir();
    }

    @Override
    public void onFileSelected(int position) {

    }

    @Override
    public void onDirectoryChecked(int position, boolean isChecked) {
        Dir dir = path.peek().getDirs().get(position);
        int[] fileIndices = Ints.toArray(Dir.filesInDirRecursively(dir));
        FileStat[] fileStats = getTorrentInfo().getFileStats();
        for (int fileIndex : fileIndices) {
            fileStats[fileIndex].setWanted(isChecked);
        }

        TorrentSetRequest.Builder requestBuilder = TorrentSetRequest.builder(getTorrent().getId());
        if (isChecked) {
            requestBuilder.filesWanted(fileIndices);
        } else {
            requestBuilder.filesUnwanted(fileIndices);
        }
        transportManager.doRequest(requestBuilder.build(), null);
    }

    @Override
    public void onFileChecked(int fileIndex, boolean isChecked) {
        FileStat fileStat = getTorrentInfo().getFileStats()[fileIndex];
        fileStat.setWanted(isChecked);

        TorrentSetRequest.Builder requestBuilder = TorrentSetRequest.builder(getTorrent().getId());
        if (fileStat.isWanted()) {
            requestBuilder.filesWanted(fileIndex);
        } else {
            requestBuilder.filesUnwanted(fileIndex);
        }
        transportManager.doRequest(requestBuilder.build(), null);
    }

    @Override
    public boolean onBackPressed() {
        if (path.size() <= 1) return false;
        showPrevDir();
        return true;
    }

    private void showTopDir() {
        adapter.setDirectory(path.peek());
    }

    private void showPrevDir() {
        path.pop();
        adapter.setDirectory(path.peek());
    }
}
