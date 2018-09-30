package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.model.json.TransferPriority;
import net.yupol.transmissionremote.app.utils.ColorUtils;

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

        IIcon icon = null;
        switch (priority) {
            case HIGH:
                icon = FontAwesome.Icon.faw_angle_up;
                break;
            case NORMAL:
                icon = CommunityMaterial.Icon2.cmd_minus;
                break;
            case LOW:
                icon = FontAwesome.Icon.faw_angle_down;
                break;
        }
        Drawable img = new IconicsDrawable(parent.getContext())
                .icon(icon)
                .color(ColorUtils.resolveColor(parent.getContext(), android.R.attr.textColorPrimary, R.color.text_primary))
                .sizeRes(R.dimen.transfer_priority_icon_size);
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
