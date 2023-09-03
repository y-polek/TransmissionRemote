package net.yupol.transmissionremote.app.torrentdetails;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.Priority;

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
        Priority priority = requireNonNull(getItem(position));
        TextView textView = view.findViewById(R.id.text);
        textView.setText(priority.nameResId);
        textView.setCompoundDrawablesWithIntrinsicBounds(priority.iconRes, 0, 0, 0);
        return view;
    }
}
