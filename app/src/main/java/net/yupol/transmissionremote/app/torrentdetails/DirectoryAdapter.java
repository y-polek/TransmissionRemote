package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.buildware.widget.indeterm.IndeterminateCheckBox;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.FileItemBinding;
import net.yupol.transmissionremote.app.model.Dir;
import net.yupol.transmissionremote.app.model.FileType;
import net.yupol.transmissionremote.app.model.Priority;
import net.yupol.transmissionremote.model.json.File;
import net.yupol.transmissionremote.model.json.FileStat;
import net.yupol.transmissionremote.app.utils.MetricsUtils;
import net.yupol.transmissionremote.app.utils.TextUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {

    private Context context;
    private File[] files;
    private FileStat[] fileStats;
    private Dir currentDir;
    private OnItemSelectedListener listener;

    public DirectoryAdapter(Context context, Dir dir, File[] files, FileStat[] fileStats, OnItemSelectedListener listener) {
        this.context = context;
        currentDir = dir;
        this.files = files;
        this.fileStats = fileStats;
        this.listener = listener;
        setHasStableIds(true);
    }

    public void setFiles(File[] files) {
        this.files = files;
        notifyItemChanged(0, getItemCount());
    }

    public void setFileStats(FileStat[] fileStats) {
        this.fileStats = fileStats;
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FileItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.file_item, parent, false);
        return new ViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        Drawable icon = null;
        long bytesCompleted = 0;
        long filesLength = 0;
        switch (viewType) {
            case R.id.view_type_directory:
                Dir dir = getDir(position);
                holder.binding.setDir(dir);

                boolean isDirectoryCompleted = isDirectoryCompleted(dir);
                holder.binding.checkbox.setState(isDirectoryChecked(dir));
                holder.binding.checkbox.setEnabled(!isDirectoryCompleted);

                Set<Integer> priorities = dirPriorities(dir);
                holder.binding.priorityButton.setText(formatDirPriorities(priorities));
                holder.binding.priorityButton.setEnabled(!isDirectoryCompleted);

                bytesCompleted = calculateBytesCompletedInDir(dir);
                filesLength = calculateFilesLengthInDir(dir);

                icon = new IconicsDrawable(context, FontAwesome.Icon.faw_folder_o)
                        .color(ContextCompat.getColor(context, R.color.text_color_primary));
                break;
            case R.id.view_type_file:
                File file = getFile(position);
                FileStat fileStat = getFileStat(position);
                holder.binding.setFile(file);

                boolean isFileCompleted = isFileCompleted(position);
                holder.binding.checkbox.setChecked(isFileChecked(position));
                holder.binding.checkbox.setEnabled(!isFileCompleted);

                Priority priority = filePriority(position);
                holder.binding.priorityButton.setText(priority.icon.getFormattedName());
                holder.binding.priorityButton.setEnabled(!isFileCompleted);

                bytesCompleted = fileStat.getBytesCompleted();
                filesLength = file.getLength();

                icon = new IconicsDrawable(context, FileType.iconFromName(file.getName()))
                        .color(ContextCompat.getColor(context, R.color.text_color_secondary));
                break;
        }

        String stats = String.format(Locale.getDefault(), "%s of %s (%d%%)",
                TextUtils.displayableSize(bytesCompleted),
                TextUtils.displayableSize(filesLength),
                (int) (100 * bytesCompleted/(double) filesLength));
        holder.binding.statsText.setText(stats);

        holder.binding.icon.setImageDrawable(icon);
        holder.binding.executePendingBindings();
    }

    private boolean isFileChecked(int position) {
        return isFileChecked(getFile(position), getFileStat(position));
    }

    private boolean isFileChecked(File file, FileStat fileStat) {
        return fileStat.isWanted() || isFileCompleted(file, fileStat);
    }

    private boolean isFileCompleted(int position) {
        return isFileCompleted(getFile(position), getFileStat(position));
    }

    private boolean isFileCompleted(File file, FileStat fileStat) {
        return fileStat.getBytesCompleted() >= file.getLength();
    }

    @Nullable
    private Boolean isDirectoryChecked(Dir dir) {
        boolean hasCheckedFiles = false;
        boolean hasUncheckedFiles = false;

        for (Dir subDir : dir.getDirs()) {
            Boolean isSubDirChecked = isDirectoryChecked(subDir);
            if (isSubDirChecked == null) return null;
            hasCheckedFiles |= isSubDirChecked;
            hasUncheckedFiles |= !isSubDirChecked;
        }

        for (Integer fileIndex : dir.getFileIndices()) {
            boolean isFileChecked = isFileChecked(files[fileIndex], fileStats[fileIndex]);
            hasCheckedFiles |= isFileChecked;
            hasUncheckedFiles |= !isFileChecked;
            if (hasCheckedFiles && hasUncheckedFiles) return null;
        }

        return hasCheckedFiles;
    }

    private boolean isDirectoryCompleted(Dir dir) {
        for (Dir subDir : dir.getDirs()) {
            if (!isDirectoryCompleted(subDir)) return false;
        }
        for (Integer fileIndex : dir.getFileIndices()) {
            if (!isFileCompleted(files[fileIndex], fileStats[fileIndex])) return false;
        }
        return true;
    }

    private long calculateBytesCompletedInDir(Dir dir) {
        long bytesCompleted = 0;

        for (Dir subDir : dir.getDirs()) {
            bytesCompleted += calculateBytesCompletedInDir(subDir);
        }

        for (Integer fileIndex : dir.getFileIndices()) {
            bytesCompleted += fileStats[fileIndex].getBytesCompleted();
        }

        return bytesCompleted;
    }

    private long calculateFilesLengthInDir(Dir dir) {
        long length = 0;

        for (Dir subDir : dir.getDirs()) {
            length += calculateFilesLengthInDir(subDir);
        }

        for (Integer fileIndex : dir.getFileIndices()) {
            length += files[fileIndex].getLength();
        }

        return length;
    }

    private Priority filePriority(int position) {
        return Priority.fromValue(getFileStat(position).getPriority(), Priority.NORMAL);
    }

    private Set<Integer> dirPriorities(Dir dir) {
        Set<Integer> priorities = new HashSet<>();

        for (Dir subDir : dir.getDirs()) {
            priorities.addAll(dirPriorities(subDir));
        }

        for (Integer fileIndex : dir.getFileIndices()) {
            if (!isFileCompleted(files[fileIndex], fileStats[fileIndex])) {
                priorities.add(fileStats[fileIndex].getPriority());
            }
        }

        return priorities;
    }

    private String formatDirPriorities(Set<Integer> prioritiesSet) {
        Integer[] priorities = prioritiesSet.toArray(new Integer[prioritiesSet.size()]);
        Arrays.sort(priorities);
        StringBuilder b = new StringBuilder();
        for (Integer priority : priorities) {
            b.append(Priority.fromValue(priority, Priority.NORMAL).icon.getFormattedName());
        }
        return b.toString();
    }

    @Override
    public int getItemCount() {
        return currentDir.getDirs().size() + currentDir.getFileIndices().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position >= currentDir.getDirs().size() ? R.id.view_type_file : R.id.view_type_directory;
    }

    @SuppressWarnings("unchecked")
    private <T> T getItem(int position) {
        List<Dir> dirs = currentDir.getDirs();
        if (position < dirs.size()) {
            return (T) dirs.get(position);
        } else {
            Integer fileIndex = currentDir.getFileIndices().get(position - dirs.size());
            return (T) fileIndex;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Dir getDir(int position) {
        return getItem(position);
    }

    private File getFile(int position) {
        Integer fileIndex = getItem(position);
        return files[fileIndex];
    }

    private FileStat getFileStat(int position) {
        Integer fileIndex = getItem(position);
        return fileStats[fileIndex];
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private FileItemBinding binding;

        public ViewHolder(final FileItemBinding binding, final OnItemSelectedListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            if (listener == null) return;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItemViewType() == R.id.view_type_directory) {
                        listener.onDirectorySelected(getAdapterPosition());
                    } else {
                        if (binding.checkbox.isEnabled()) {
                            binding.checkbox.setChecked(!binding.checkbox.isChecked());
                        }
                    }
                }
            });
            binding.checkbox.setOnStateChangedListener(new IndeterminateCheckBox.OnStateChangedListener() {
                @Override
                public void onStateChanged(IndeterminateCheckBox buttonView, @Nullable Boolean isChecked) {
                    if (getItemViewType() == R.id.view_type_directory) {
                        boolean changed = isChecked != isDirectoryChecked(getDir(getAdapterPosition()));
                        if (isChecked != null && changed) listener.onDirectoryChecked(getAdapterPosition(), isChecked);
                    } else {
                        Integer fileIndex = getItem(getAdapterPosition());
                        assert isChecked != null;
                        boolean changed = isChecked != isFileChecked(getAdapterPosition());
                        if (changed) {
                            listener.onFileChecked(fileIndex, isChecked);
                        }
                    }
                }
            });
            binding.priorityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ListPopupWindow popup = new ListPopupWindow(context);
                    popup.setModal(true);
                    PriorityListAdapter adapter = new PriorityListAdapter(context);
                    popup.setAdapter(adapter);
                    popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int priorityPosition, long id) {
                            Priority priority = (Priority) parent.getItemAtPosition(priorityPosition);

                            int position = getAdapterPosition();
                            if (getItemViewType() == R.id.view_type_directory) {
                                listener.onDirectoryPriorityChanged(position, priority);
                                setDirPriority(getDir(position), priority);
                            } else {
                                Integer fileIndex = getItem(position);
                                listener.onFilePriorityChanged(fileIndex, priority);
                                setFilePriority(fileIndex, priority);
                            }
                            notifyDataSetChanged();

                            popup.dismiss();
                        }
                    });
                    popup.setAnchorView(view);
                    int contentWidth = MetricsUtils.measurePopupSize(context, adapter).width;
                    popup.setContentWidth(contentWidth);
                    popup.setHorizontalOffset(
                            view.getWidth() - contentWidth - context.getResources().getDimensionPixelOffset(R.dimen.priority_popup_offset));
                    popup.show();
                }
            });
        }
    }

    private void setFilePriority(Integer fileIndex, Priority priority) {
        fileStats[fileIndex].setPriority(priority.value);
    }

    private void setDirPriority(Dir dir, Priority priority) {
        for (Dir subDir : dir.getDirs()) {
            setDirPriority(subDir, priority);
        }
        for (Integer fileIndex : dir.getFileIndices()) {
            if (!isFileCompleted(files[fileIndex], fileStats[fileIndex])) {
                setFilePriority(fileIndex, priority);
            }
        }
    }

    public interface OnItemSelectedListener {
        void onDirectorySelected(int position);
        void onDirectoryChecked(int position, boolean isChecked);
        void onFileChecked(int fileIndex, boolean isChecked);
        void onDirectoryPriorityChanged(int position, Priority priority);
        void onFilePriorityChanged(int fileIndex, Priority priority);
    }
}
