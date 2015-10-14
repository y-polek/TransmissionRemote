package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.TransferPriority;

public class TransferPrioritySpinnerAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return TransferPriority.values().length;
    }

    @Override
    public TransferPriority getItem(int position) {
        return TransferPriority.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TransferPriority priority = getItem(position);

        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setText(priority.getTextRes());
        Drawable img = parent.getResources().getDrawable(priority.getImageRes());
        if (img != null) {
            int side = (int) (13 * parent.getResources().getDisplayMetrics().scaledDensity); // 13sp
            img.setBounds(0, 0, side, side);
        }
        text.setCompoundDrawablePadding(parent.getResources()
                .getDimensionPixelSize(R.dimen.default_text_margin_small));
        text.setCompoundDrawables(img, null, null, null);

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setText(getItem(position).getTextRes());

        return view;
    }
}
