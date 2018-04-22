package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.yupol.transmissionremote.app.ProgressbarFragment;
import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.model.Dir;
import net.yupol.transmissionremote.model.json.TorrentInfo;

import java.util.ArrayList;
import java.util.Stack;

public class FilesPageFragment extends BasePageFragment implements DirectoryFragment.OnDirectorySelectedListener {

    private static final String TAG_PROGRESSBAR_FRAGMENT = "tag_progressbar_fragment";
    private static final String TAG_DIRECTORY_FRAGMENT = "tag_directory_fragment";
    private static final String KEY_PATH = "key_path";

    private boolean viewCreated;
    private Stack<Dir> path = new Stack<>();
    private BreadcrumbView breadcrumbView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.torrent_details_file_page_fragment, container, false);
        breadcrumbView = view.findViewById(R.id.breadcrumb_view);
        breadcrumbView.setOnNodeSelectedListener(new BreadcrumbView.OnNodeSelectedListener() {
            @Override
            public void onNodeSelected(int position) {
                if (position >= path.size() - 1) return;
                for (int i=path.size()-1; i>position; i--) {
                    path.remove(i);
                }
                breadcrumbView.setPath(path);
                showDirectory(path.peek(), AnimationDirection.LEFT_TO_RIGHT);
            }
        });

        if (savedInstanceState != null) {
            ArrayList<Dir> dirs = savedInstanceState.getParcelableArrayList(KEY_PATH);
            path.addAll(dirs);
            breadcrumbView.setPath(path);
        }

        if (getTorrentInfo() != null) {
            if (path.isEmpty()) {
                showRootDir();
            } else {
                showDirectory(path.peek(), null);
            }
        } else {
            showProgressbarFragment();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_PATH, new ArrayList<>(path));
    }


    @Override
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        boolean isUpdate = getTorrentInfo() != null;
        super.setTorrentInfo(torrentInfo);
        if (viewCreated) {
            if (!isUpdate) {
                showRootDir();
            } else {
                updateFileStats();
            }
        }
    }

    public void updateFileStats() {
        DirectoryFragment directoryFragment = (DirectoryFragment)
                getChildFragmentManager().findFragmentByTag(TAG_DIRECTORY_FRAGMENT);
        directoryFragment.setFiles(getTorrentInfo().getFiles());
        directoryFragment.setFileStats(getTorrentInfo().getFileStats());
    }

    @Override
    public void onDirectorySelected(Dir dir) {
        path.push(dir);
        breadcrumbView.setPath(path);
        showDirectory(dir, AnimationDirection.RIGHT_TO_LEFT);
    }

    @Override
    public boolean onBackPressed() {
        if (getUserVisibleHint() && path.size() > 1) {
            path.pop();
            breadcrumbView.setPath(path);
            showDirectory(path.peek(), AnimationDirection.LEFT_TO_RIGHT);
            return true;
        }
        return super.onBackPressed();
    }

    private void showRootDir() {
        Dir rootDir = Dir.createFileTree(getTorrentInfo().getFiles());
        showDirectory(rootDir, null);
        path.clear();
        path.push(rootDir);
        breadcrumbView.setPath(path);
    }

    private void showProgressbarFragment() {
        FragmentManager fm = getChildFragmentManager();
        ProgressbarFragment progressbarFragment = (ProgressbarFragment) fm.findFragmentByTag(TAG_PROGRESSBAR_FRAGMENT);
        if (progressbarFragment == null) {
            progressbarFragment = new ProgressbarFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content, progressbarFragment, TAG_PROGRESSBAR_FRAGMENT);
            ft.commitAllowingStateLoss();
        }
    }

    private void showDirectory(Dir dir, @Nullable AnimationDirection animation) {
        TorrentInfo torrentInfo = getTorrentInfo();
        DirectoryFragment fragment = DirectoryFragment.newInstance(
                getTorrent().getId(), dir, torrentInfo.getFiles(), torrentInfo.getFileStats());
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (animation != null) {
            ft.setCustomAnimations(animation.enter, animation.exit);
        }
        ft.replace(R.id.content, fragment, TAG_DIRECTORY_FRAGMENT);
        ft.commitAllowingStateLoss();
    }

    private enum AnimationDirection {
        RIGHT_TO_LEFT(R.anim.enter_from_right, R.anim.exit_to_left),
        LEFT_TO_RIGHT(R.anim.enter_from_left, R.anim.exit_to_right);

        @AnimRes public final int enter;
        @AnimRes public final int exit;

        AnimationDirection(@AnimRes int enter, @AnimRes int exit) {
            this.enter = enter;
            this.exit = exit;
        }
    }
}
