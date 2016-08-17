package net.yupol.transmissionremote.app.preferences;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.utils.IconUtils;

public class ServersFragment extends ListFragment {

    private TransmissionRemote app;

    private OnServerSelectedListener serverSelectedListener;

    public ServersFragment() {
        setListAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return app.getServers().size();
            }

            @Override
            public Server getItem(int position) {
                return app.getServers().get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = convertView;
                if (itemView == null) {
                    LayoutInflater li = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    itemView = li.inflate(R.layout.server_item_layout, parent, false);
                }

                Server server = getItem(position);

                TextView nameText = (TextView) itemView.findViewById(R.id.name);
                nameText.setText(server.getName());

                TextView addressText = (TextView) itemView.findViewById(R.id.address);
                addressText.setText((server.useHttps() ? "https://" : "http://") + server.getHost() + ":" + server.getPort());

                RadioButton radioButton = (RadioButton) itemView.findViewById(R.id.radio_button);
                radioButton.setChecked(server.equals(app.getActiveServer()));

                return itemView;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(getString(R.string.servers_empty_text));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (TransmissionRemote) activity.getApplication();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.servers_menu, menu);
        IconUtils.setMenuIcon(getActivity(), menu, R.id.action_add, GoogleMaterial.Icon.gmd_add);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        BaseAdapter adapter = (BaseAdapter) getListAdapter();
        Server server = (Server) adapter.getItem(position);
        if (!server.equals(app.getActiveServer())) {
            app.setActiveServer(server);
        }
        ((BaseAdapter) getListAdapter()).notifyDataSetChanged();

        if (serverSelectedListener != null) {
            serverSelectedListener.onServerSelected(server);
        }
    }

    public void setOnServerSelectedListener(OnServerSelectedListener listener) {
        serverSelectedListener = listener;
    }

    public interface OnServerSelectedListener {
        void onServerSelected(Server server);
    }
}
