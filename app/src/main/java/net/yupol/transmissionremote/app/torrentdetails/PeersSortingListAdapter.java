package net.yupol.transmissionremote.app.torrentdetails;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.PeersSortListItemBinding;
import net.yupol.transmissionremote.app.sorting.PeersSortedBy;
import net.yupol.transmissionremote.app.sorting.SortOrder;


public class PeersSortingListAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return PeersSortedBy.values().length;
    }

    @Override
    public PeersSortedBy getItem(int position) {
        return PeersSortedBy.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            PeersSortListItemBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.peers_sort_list_item, parent, false);
            view = binding.getRoot();
            view.setTag(binding);
        }

        PeersSortListItemBinding binding = (PeersSortListItemBinding) view.getTag();
        binding.setSortedBy(getItem(position));
        binding.setSortOrder(SortOrder.ASCENDING);


        return view;
    }
}
