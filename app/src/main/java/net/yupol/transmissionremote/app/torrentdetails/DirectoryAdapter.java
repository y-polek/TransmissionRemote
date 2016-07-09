package net.yupol.transmissionremote.app.torrentdetails;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.FileItemBinding;
import net.yupol.transmissionremote.app.model.Dir;
import net.yupol.transmissionremote.app.model.json.File;

import java.util.List;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {

    private Dir dir = Dir.emptyDir();
    private OnItemSelectedListener listener;

    public DirectoryAdapter(OnItemSelectedListener listener) {
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
        switch (viewType) {
            case R.id.view_type_directory:
                Dir dir = getItem(position);
                holder.binding.setDir(dir);
                break;
            case R.id.view_type_file:
                File file = getItem(position);
                holder.binding.setFile(file);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dir.getDirs().size() + dir.getFiles().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position >= dir.getDirs().size() ? R.id.view_type_file : R.id.view_type_directory;
    }

    @SuppressWarnings("unchecked")
    private <T> T getItem(int position) {
        List<Dir> dirs = dir.getDirs();
        return (T) (position < dirs.size() ? dirs.get(position) : dir.getFiles().get(position - dirs.size()));
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
        }
    }

    public interface OnItemSelectedListener {
        void onDirectorySelected(int position);
        void onFileSelected(int position);
    }
}
