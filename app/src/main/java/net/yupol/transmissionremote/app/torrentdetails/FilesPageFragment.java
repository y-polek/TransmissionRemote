package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.os.Bundle;
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
import net.yupol.transmissionremote.app.utils.SizeUtils;

public class FilesPageFragment extends BasePageFragment {

    private static final String TAG = FilesPageFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.torrent_details_file_page_fragment, container, false);
        ListView list = (ListView) view.findViewById(R.id.file_list);
        list.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return getTorrent().getFiles().length;
            }

            @Override
            public File getItem(int position) {
                return getTorrent().getFiles()[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
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
                holder.checkBox.setChecked(fileStat.isWanted());
                holder.checkBox.setOnCheckedChangeListener(FileSelectedListener.INSTANCE);

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
                return getTorrent().getFileStats()[position];
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

        private static final FileSelectedListener INSTANCE = new FileSelectedListener();

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!(buttonView.getTag() instanceof FileStat)) return;

            FileStat fileStat = (FileStat) buttonView.getTag();
            fileStat.setWanted(isChecked);
        }
    }
}
