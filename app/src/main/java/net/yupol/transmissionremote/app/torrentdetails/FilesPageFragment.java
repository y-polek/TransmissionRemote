package net.yupol.transmissionremote.app.torrentdetails;

import static com.google.common.base.Strings.nullToEmpty;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.yupol.transmissionremote.app.ProgressbarFragment;
import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.model.Dir;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;

import java.util.Stack;

public class FilesPageFragment extends BasePageFragment implements DirectoryFragment.OnDirectorySelectedListener {

    private static final String TAG = FilesPageFragment.class.getSimpleName();
    private static final String TAG_PROGRESSBAR_FRAGMENT = "tag_progressbar_fragment";
    private static final String TAG_DIRECTORY_FRAGMENT = "tag_directory_fragment";
    private static final String KEY_PATH = "key_path";

    private boolean viewCreated;
    private final Stack<Dir> path = new Stack<>();
    private BreadcrumbView breadcrumbView;
    @Nullable private String[] savedPath;
    private Dir currentDir;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
            savedPath = savedInstanceState.getStringArray(KEY_PATH);
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
    public void onResume() {
        super.onResume();
        TransmissionRemote.getInstance().getAnalytics().logScreenView(
                "Files page",
                FilesPageFragment.class
        );
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
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String[] pathNames = path.stream().map(Dir::getName).toArray(String[]::new);
        outState.putStringArray(KEY_PATH, pathNames);
    }


    @Override
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        boolean isUpdate = getTorrentInfo() != null;
        super.setTorrentInfo(torrentInfo);
        if (viewCreated) {
            if (!isUpdate) {
                if (isNotEmpty(savedPath)) {
                    restoreSavedPath();
                } else {
                    showRootDir();
                }
            } else {
                updateFileStats();
            }
        }
    }

    public Dir getCurrentDir() {
        return currentDir;
    }

    private void restoreSavedPath() {
        path.clear();

        if (isNotEmpty(savedPath)) {

            final Dir root = Dir.createFileTree(getTorrentInfo().getFiles());

            Dir dir = null;
            for (String dirName : savedPath) {
                Dir subDir;
                if (dir == null) {
                    subDir = root;
                } else {
                    subDir = dir.findDir(nullToEmpty(dirName));
                }
                if (subDir != null) {
                    path.push(subDir);
                    dir = subDir;
                } else {
                    path.clear();
                    break;
                }
            }

            breadcrumbView.setPath(path);

            if (path.isEmpty()) {
                showRootDir();
            } else {
                showDirectory(path.peek(), null);
            }
        }

        savedPath = null;
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
        currentDir = dir;
        DirectoryFragment fragment = DirectoryFragment.newInstance(getTorrent().getId());
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
