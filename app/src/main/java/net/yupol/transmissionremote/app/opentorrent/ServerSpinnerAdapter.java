package net.yupol.transmissionremote.app.opentorrent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.yupol.transmissionremote.app.server.Server;

import java.util.List;

public class ServerSpinnerAdapter extends ArrayAdapter<Server> {

    public ServerSpinnerAdapter(Context context, List<Server> servers) {
        super(context, android.R.layout.simple_spinner_item, android.R.id.text1, servers);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        setupView(view, position);
        return view;
    }

    private void setupView(View view, int position) {
        TextView textView;
        if (view instanceof TextView) {
            textView = (TextView) view;
        } else {
            textView = (TextView) view.findViewById(android.R.id.text1);
        }

        Server server = getItem(position);
        if (server != null && textView != null) {
            textView.setText(server.getName());
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        setupView(view, position);
        return view;
    }
}
