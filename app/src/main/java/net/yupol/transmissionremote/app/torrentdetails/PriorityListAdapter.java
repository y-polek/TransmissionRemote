package net.yupol.transmissionremote.app.torrentdetails;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.Priority;
import net.yupol.transmissionremote.app.utils.ColorUtils;

public class PriorityListAdapter extends ArrayAdapter<Priority> {

    public PriorityListAdapter(Context context) {
        super(context, 0, Priority.values());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.priority_list_item_layout, parent, false);
        }
        Priority priority = getItem(position);
        assert priority != null;
        IconicsImageView imageView = view.findViewById(R.id.icon);
        imageView.setColorFilter(ColorUtils.resolveColor(getContext(), android.R.attr.textColorPrimary, R.color.text_color_primary));
        imageView.setIcon(new IconicsDrawable(view.getContext(), priority.icon));
        TextView textView = view.findViewById(R.id.text);
        textView.setText(priority.nameResId);
        return view;
    }
}
