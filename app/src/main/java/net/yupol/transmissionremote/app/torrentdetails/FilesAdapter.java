package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.File;
import net.yupol.transmissionremote.app.model.json.FileStat;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;
import net.yupol.transmissionremote.app.utils.TextUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class FilesAdapter extends BaseAdapter {

    private TorrentInfo torrentInfo;
    private File[] sortedFiles;
    private CompoundButton.OnCheckedChangeListener listener;

    public FilesAdapter(@NonNull TorrentInfo torrentInfo, @NonNull CompoundButton.OnCheckedChangeListener listener) {
        this.torrentInfo = torrentInfo;
        this.listener = listener;

        File[] files = torrentInfo.getFiles();
        this.sortedFiles = Arrays.copyOf(files, files.length);
        Arrays.sort(this.sortedFiles, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
    }

    @Override
    public int getCount() {
        return sortedFiles.length;
    }

    @Override
    public File getItem(int position) {
        return sortedFiles[position];
    }

    /**
     * @param position in list view
     * @return file's index in original model
     */
    @Override
    public long getItemId(int position) {
        File[] files = torrentInfo.getFiles();
        for (int i = 0; i< files.length; i++) {
            if (files[i].equals(getItem(position))) {
                return i;
            }
        }
        throw new IllegalStateException("Can't find index of file with position '" + position + "'");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.file_list_item, parent, false);
            holder = new ViewHolder();
            holder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            holder.fileName = (TextView) view.findViewById(R.id.file_name);
            holder.dirName = (TextView) view.findViewById(R.id.dir_name);
            holder.starts = (TextView) view.findViewById(R.id.stats_text);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        String fileName;
        String dirName;
        File file = getItem(position);
        int lastDivider = file.getName().lastIndexOf('/');
        if (lastDivider >= 0) {
            fileName = lastDivider < (file.getName().length() - 1)
                    ? file.getName().substring(lastDivider + 1) : "";
            dirName = file.getName().substring(0, lastDivider + 1);
        } else {
            fileName = file.getName();
            dirName = "";
        }

        FileStat fileStat = getFileStat(position);
        holder.checkBox.setTag(fileStat);
        holder.checkBox.setTag(R.id.TAG_FILE_INDEX, (int) getItemId(position));
        boolean isCompleted = file.getBytesCompleted() >= file.getLength();
        if (isCompleted) {
            fileStat.setWanted(true);
        }
        holder.checkBox.setChecked(fileStat.isWanted() || isCompleted);
        holder.checkBox.setEnabled(!isCompleted);
        holder.checkBox.setOnCheckedChangeListener(listener);

        holder.fileName.setText(fileName);

        holder.dirName.setText(dirName);
        holder.dirName.setVisibility(dirName.isEmpty() ? View.GONE : View.VISIBLE);

        String stats = String.format(Locale.getDefault(), "%s of %s (%d%%)",
                TextUtils.displayableSize(file.getBytesCompleted()),
                TextUtils.displayableSize(file.getLength()),
                (int) (100 * file.getBytesCompleted()/(double) file.getLength()));
        holder.starts.setText(stats);

        return view;
    }

    private FileStat getFileStat(int position) {
        return torrentInfo.getFileStats()[(int) getItemId(position)];
    }

    private static class ViewHolder {
        CheckBox checkBox;
        TextView fileName;
        TextView dirName;
        TextView starts;
    }
}
