package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

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

        TextView text = view.findViewById(android.R.id.text1);

        text.setText(getTextRes(priority));
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                parent.getResources().getDimensionPixelSize(R.dimen.tr_abc_text_size_subhead_material));
        text.setCompoundDrawablePadding(parent.getResources()
                .getDimensionPixelSize(R.dimen.default_text_margin_small));

        @DrawableRes final int iconRes;
        switch (priority) {
            case HIGH:
                iconRes = R.drawable.ic_priority_high;
                break;
            case NORMAL:
                iconRes = R.drawable.ic_priority_normal;
                break;
            case LOW:
                iconRes = R.drawable.ic_priority_low;
                break;
            default:
                iconRes = 0;
        }
        text.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        TextView text = view.findViewById(android.R.id.text1);
        text.setText(getTextRes(getItem(position)));
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                parent.getResources().getDimensionPixelSize(R.dimen.tr_abc_text_size_subhead_material));

        return view;
    }

    public int getTextRes(TransferPriority priority) {
        switch (priority) {
            case HIGH:
                return R.string.priority_high;
            case NORMAL:
                return R.string.priority_normal;
            case LOW:
                return R.string.priority_low;
        }
        return 0;
    }
}
