package net.yupol.transmissionremote.app.preferences;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Lifecycle;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.server.Server;

import java.util.Locale;

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
                radioButton.setChecked(server.equals(app.getActiveServer()));

                return itemView;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(getString(R.string.servers_empty_text));

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.servers_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // Handle menu item selection here if needed
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        app = (TransmissionRemote) requireActivity().getApplication();
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
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
