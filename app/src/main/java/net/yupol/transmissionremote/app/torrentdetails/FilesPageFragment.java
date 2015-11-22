package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.File;
import net.yupol.transmissionremote.app.model.json.FileStat;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;
import net.yupol.transmissionremote.app.utils.SizeUtils;

import java.util.Arrays;
import java.util.Comparator;

public class FilesPageFragment extends BasePageFragment {

    private static final String TAG = FilesPageFragment.class.getSimpleName();

    private FileSelectedListener selectionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.torrent_details_file_page_fragment, container, false);
        ListView list = (ListView) view.findViewById(R.id.file_list);
        if (getActivity() instanceof BaseSpiceActivity) {
            selectionListener = new FileSelectedListener(getTorrent(),
                    ((BaseSpiceActivity) getActivity()).getTransportManager());
        } else {
            Log.w(TAG, "FilesPageFragment should be used with BaseSpiceActivity. " +
                    "Otherwise fragment will not support changing file selection.");
        }

        final File[] files = getTorrent().getFiles();
        final File[] sortedFiles = Arrays.copyOf(files, files.length);
        Arrays.sort(sortedFiles, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });

        list.setAdapter(new BaseAdapter() {
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
                for (int i=0; i<files.length; i++) {
                    File file = files[i];
                    if (file.equals(getItem(position))) {
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
                holder.checkBox.setEnabled(!isCompleted && selectionListener != null);
                holder.checkBox.setOnCheckedChangeListener(selectionListener);

                holder.fileName.setText(fileName);

                holder.dirName.setText(dirName);
                holder.dirName.setVisibility(dirName.isEmpty() ? View.GONE : View.VISIBLE);

                String stats = String.format("%s of %s (%d%%)",
                        SizeUtils.displayableSize(file.getBytesCompleted()),
                        SizeUtils.displayableSize(file.getLength()),
                        (int) (100 * file.getBytesCompleted()/(double) file.getLength()));
                holder.starts.setText(stats);

                return view;
            }

            private FileStat getFileStat(int position) {
                return getTorrent().getFileStats()[(int) getItemId(position)];
            }
        });
        return view;
    }

    @Override
    public int getPageTitleRes() {
        return R.string.files;
    }

    private static class ViewHolder {
        CheckBox checkBox;
        TextView fileName;
        TextView dirName;
        TextView starts;
    }

    private static class FileSelectedListener implements CompoundButton.OnCheckedChangeListener {

        private Torrent torrent;
        private TransportManager transportManager;

        public FileSelectedListener(Torrent torrent, TransportManager transportManager) {
            this.torrent = torrent;
            this.transportManager = transportManager;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!(buttonView.getTag() instanceof FileStat)) return;

            FileStat fileStat = (FileStat) buttonView.getTag();
            if (fileStat.isWanted() != isChecked) {
                fileStat.setWanted(isChecked);

                int fileIndex = (int) buttonView.getTag(R.id.TAG_FILE_INDEX);
                TorrentSetRequest.Builder requestBuilder = TorrentSetRequest.builder(torrent.getId());
                if (fileStat.isWanted()) {
                    requestBuilder.filesWanted(fileIndex);
                } else {
                    requestBuilder.filesUnwanted(fileIndex);
                }
                transportManager.doRequest(requestBuilder.build(), null);
            }
        }
    }
}
