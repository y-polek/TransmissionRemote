package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buildware.widget.indeterm.IndeterminateCheckBox;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.FileItemBinding;
import net.yupol.transmissionremote.app.model.Dir;
import net.yupol.transmissionremote.app.model.FileType;
import net.yupol.transmissionremote.app.model.json.File;
import net.yupol.transmissionremote.app.model.json.FileStat;

import java.util.List;

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
        switch (viewType) {
            case R.id.view_type_directory:
                Dir dir = getDir(position);
                holder.binding.setDir(dir);

                holder.binding.checkbox.setState(isDirectoryChecked(dir));
                holder.binding.checkbox.setEnabled(!isDirectoryCompleted(dir));

                icon = new IconicsDrawable(context, FontAwesome.Icon.faw_folder_o)
                        .color(ContextCompat.getColor(context, R.color.text_color_primary));
                break;
            case R.id.view_type_file:
                File file = getFile(position);
                holder.binding.setFile(file);

                holder.binding.checkbox.setChecked(isFileChecked(position));
                holder.binding.checkbox.setEnabled(!isFileCompleted(position));

                icon = new IconicsDrawable(context, FileType.iconFromName(file.getName()))
                        .color(ContextCompat.getColor(context, R.color.text_color_secondary));
                break;
        }
        holder.binding.icon.setImageDrawable(icon);
        holder.binding.executePendingBindings();
    }

    private boolean isFileChecked(int position) {
        return isFileChecked(getFile(position), getFileStat(position));
    }

    private boolean isFileChecked(File file, FileStat fileStat) {
        return fileStat.isWanted() || isFileCompleted(file);
    }

    private boolean isFileCompleted(int position) {
        return isFileCompleted(getFile(position));
    }

    private boolean isFileCompleted(File file) {
        return file.getBytesCompleted() >= file.getLength();
    }

    @Nullable
    private Boolean isDirectoryChecked(Dir dir) {
        boolean hasCheckedFiles = false;
        boolean hasUncheckedFiles = false;
        for (Integer fileIndex : dir.getFileIndices()) {
            boolean isFileChecked = isFileChecked(files[fileIndex], fileStats[fileIndex]);
            hasCheckedFiles |= isFileChecked;
            hasUncheckedFiles |= !isFileChecked;
            if (hasCheckedFiles && hasUncheckedFiles) return null;
        }
        return hasCheckedFiles;
    }

    private boolean isDirectoryCompleted(Dir dir) {
        for (Integer fileIndex : dir.getFileIndices()) {
            if (!isFileCompleted(files[fileIndex])) return false;
        }
        return true;
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
                        if (isChecked != null) listener.onDirectoryChecked(getAdapterPosition(), isChecked);
                    } else {
                        Integer fileIndex = getItem(getAdapterPosition());
                        assert isChecked != null;
                        listener.onFileChecked(fileIndex, isChecked);
                    }
                }
            });
        }
    }

    public interface OnItemSelectedListener {
        void onDirectorySelected(int position);
        void onDirectoryChecked(int position, boolean isChecked);
        void onFileChecked(int fileIndex, boolean isChecked);
    }
}
