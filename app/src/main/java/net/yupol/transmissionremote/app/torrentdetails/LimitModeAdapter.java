package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.limitmode.LimitMode;

public abstract class LimitModeAdapter extends BaseAdapter {

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract LimitMode getItem(int position);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent, android.R.layout.simple_spinner_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item);
    }

    public View createView(int position, View convertView, ViewGroup parent, int viewResource) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(viewResource, parent, false);
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getTextRes());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                parent.getResources().getDimensionPixelSize(R.dimen.abc_text_size_body_1_material));

        return view;
    }
}
