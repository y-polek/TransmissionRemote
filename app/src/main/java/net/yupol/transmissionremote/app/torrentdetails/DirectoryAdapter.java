package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;

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
    private Dir dir = Dir.emptyDir();
    private OnItemSelectedListener listener;
    private int lastPosition = -1;

    public DirectoryAdapter(Context context, File[] files, FileStat[] fileStats, OnItemSelectedListener listener) {
        this.context = context;
        this.files = files;
        this.fileStats = fileStats;
        this.listener = listener;
    }

    public void setDirectory(@NonNull Dir dir) {
        this.dir = dir;
        notifyDataSetChanged();
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
                icon = new IconicsDrawable(context, FontAwesome.Icon.faw_folder_o)
                        .color(ContextCompat.getColor(context, R.color.text_color_primary));
                break;
            case R.id.view_type_file:
                File file = getFile(position);
                holder.binding.setFile(file);
                holder.binding.setFileStat(getFileStat(position));
                icon = new IconicsDrawable(context, FileType.iconFromName(file.getName()))
                        .color(ContextCompat.getColor(context, R.color.text_color_secondary));
                break;
        }
        holder.binding.setFileStats(fileStats);
        holder.binding.executePendingBindings();
        holder.binding.icon.setImageDrawable(icon);
        setAnimation(holder.binding.getRoot(), position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.enter_from_right);
        viewToAnimate.startAnimation(animation);
        lastPosition = position;
    }

    @Override
    public int getItemCount() {
        return dir.getDirs().size() + dir.getFileIndices().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position >= dir.getDirs().size() ? R.id.view_type_file : R.id.view_type_directory;
    }

    @SuppressWarnings("unchecked")
    private <T> T getItem(int position) {
        List<Dir> dirs = dir.getDirs();
        if (position < dirs.size()) {
            return (T) dirs.get(position);
        } else {
            Integer fileIndex = dir.getFileIndices().get(position - dirs.size());
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

        public ViewHolder(FileItemBinding binding, final OnItemSelectedListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            if (listener == null) return;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItemViewType() == R.id.view_type_directory) {
                        listener.onDirectorySelected(getAdapterPosition());
                    } else {
                        listener.onFileSelected(getAdapterPosition() - dir.getDirs().size());
                    }

                }
            });
            CheckBox checkBox = (CheckBox) binding.getRoot().findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (getItemViewType() == R.id.view_type_directory) {
                        listener.onDirectoryChecked(getAdapterPosition(), isChecked);
                    } else {
                        Integer fileIndex = getItem(getAdapterPosition());
                        listener.onFileChecked(fileIndex, isChecked);
                    }
                }
            });
        }
    }

    public interface OnItemSelectedListener {
        void onDirectorySelected(int position);
        void onFileSelected(int position);
        void onDirectoryChecked(int position, boolean isChecked);
        void onFileChecked(int fileIndex, boolean isChecked);
    }
}
