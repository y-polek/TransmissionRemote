package net.yupol.transmissionremote.app.preferences;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
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
import net.yupol.transmissionremote.app.server.ServersRepository;
import net.yupol.transmissionremote.app.utils.IconUtils;
import net.yupol.transmissionremote.model.Server;

import java.util.Locale;

public class ServersFragment extends ListFragment {

    private TransmissionRemote app;
    private ServersRepository serversRepository;

    private OnServerSelectedListener serverSelectedListener;

    public ServersFragment() {
        setListAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return serversRepository.getServers().getValue().size();
            }

            @Override
            public Server getItem(int position) {
                return serversRepository.getServers().getValue().get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = convertView;
                if (itemView == null) {
                    LayoutInflater li = LayoutInflater.from(parent.getContext());
                    itemView = li.inflate(R.layout.server_item_layout, parent, false);
                }

                Server server = getItem(position);

                TextView nameText = itemView.findViewById(R.id.name);
                nameText.setText(server.getName());

                TextView addressText = itemView.findViewById(R.id.address);
                int port = server.getPort();
                String url = String.format(Locale.ROOT, "%s://%s%s",
                        server.useHttps() ? "https" : "http",
                        server.getHost(),
                        port >= 0 ? (":" + port) : "");
                addressText.setText(url);

                RadioButton radioButton = itemView.findViewById(R.id.radio_button);
                radioButton.setChecked(server.equals(serversRepository.getActiveServer().getValue()));

                return itemView;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        app = TransmissionRemote.getInstance();
        serversRepository = app.di.getApplicationComponent().serversRepository();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.servers_menu, menu);
        IconUtils.setMenuIcon(getActivity(), menu, R.id.action_add, GoogleMaterial.Icon.gmd_add);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        BaseAdapter adapter = (BaseAdapter) getListAdapter();
        Server server = (Server) adapter.getItem(position);
        if (!server.equals(serversRepository.getActiveServer().getValue())) {
            serversRepository.setActiveServer(server);
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
