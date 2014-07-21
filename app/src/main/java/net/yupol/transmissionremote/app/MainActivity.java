package net.yupol.transmissionremote.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;

import net.yupol.transmissionremote.app.drawer.Drawer;
import net.yupol.transmissionremote.app.drawer.DrawerGroupItem;
import net.yupol.transmissionremote.app.drawer.DrawerItem;
import net.yupol.transmissionremote.app.drawer.NewServerDrawerItem;
import net.yupol.transmissionremote.app.drawer.ServerDrawerItem;
import net.yupol.transmissionremote.app.server.AddServerActivity;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.Torrent;
import net.yupol.transmissionremote.app.transport.TransportThread;
import net.yupol.transmissionremote.app.transport.request.CheckPortRequest;
import net.yupol.transmissionremote.app.transport.request.Request;
import net.yupol.transmissionremote.app.transport.request.UpdateTorrentsRequest;
import net.yupol.transmissionremote.app.transport.response.CheckPortResponse;
import net.yupol.transmissionremote.app.transport.response.Response;

import java.util.List;

public class MainActivity extends Activity implements Drawer.OnItemSelectedListener,
            TorrentUpdater.TorrentUpdateListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static int REQUEST_CODE_SERVER_PARAMS = 1;

    private TransmissionRemote application;
    private TransportThread transportThread;
    private TorrentUpdater torrentUpdater;

    private Drawer drawer;
    private ActionBarDrawerToggle drawerToggle;
    private TorrentListFragment torrentListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        application = TransmissionRemote.getApplication(this);

        ListView drawerList = (ListView) findViewById(R.id.drawer_list);

        drawer = new Drawer(drawerList);
        for (Server server : application.getServers())
            drawer.addServer(server);
        drawer.setOnItemSelectedListener(this);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                    R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (getActionBar() != null) getActionBar().setTitle(getTitle());
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (getActionBar() != null) getActionBar().setTitle(getTitle());
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        FragmentManager fm = getFragmentManager();

        torrentListFragment = (TorrentListFragment) fm.findFragmentById(R.id.torrent_list_container);
        if (torrentListFragment == null) {
            torrentListFragment = new TorrentListFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.torrent_list_container, torrentListFragment);
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Server> servers = application.getServers();
        if (servers.isEmpty()) {
            Intent intent = new Intent(this, AddServerActivity.class);
            intent.putExtra(AddServerActivity.PARAM_CANCELABLE, false);
            startActivityForResult(intent, REQUEST_CODE_SERVER_PARAMS);
        } else {
            Server server = application.getActiveServer();
            torrentUpdater = new TorrentUpdater(server, this);
            startTransportThread(server);

            Log.d(TAG, "Check port message sent");
            Message msg = transportThread.getHandler().obtainMessage(TransportThread.REQUEST);
            msg.obj = new CheckPortRequest();
            transportThread.getHandler().sendMessage(msg);
        }
    }

    @Override
    protected void onPause() {
        torrentUpdater.stop();
        stopTransportThread();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SERVER_PARAMS) {
            if (resultCode == RESULT_OK) {
                Server server = data.getParcelableExtra(AddServerActivity.EXTRA_SEVER);
                addNewServer(server);
            }
        }
    }

    @Override
    public void onDrawerItemSelected(DrawerGroupItem group, DrawerItem item) {
        Log.d(TAG, "item '" + item.getText() + "' in group '" + group.getText() + "' selected");

        if (group.getId() == Drawer.Groups.SERVERS.id()) {
            if (item instanceof NewServerDrawerItem) {
                startActivityForResult(new Intent(this, AddServerActivity.class), REQUEST_CODE_SERVER_PARAMS);
            } else if (item instanceof ServerDrawerItem) {
                Server server = ((ServerDrawerItem) item).getServer();
                if (server != application.getActiveServer()) {
                    application.setActiveServer(server);
                    startTransportThread(server);
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTorrentUpdate(List<Torrent> torrents) {
        if (torrentListFragment != null) {
            torrentListFragment.torrentsUpdated(torrents);
        }

        String text = Joiner.on("\n").join(FluentIterable.from(torrents).transform(new Function<Torrent, String>() {
            @Override
            public String apply(Torrent torrent) {
                String percents = String.format("%.2f", torrent.getPercentDone() * 100);
                return torrent.getStatus() + " " + percents + "% " + torrent.getName();
            }
        }));

        Log.d(TAG, "Torrents:\n" + text);
    }

    private void addNewServer(Server server) {
        application.addServer(server);
        drawer.addServer(server);

        if (application.getServers().size() == 1) {
            application.setActiveServer(server);
            torrentUpdater = new TorrentUpdater(server, this);
            // TODO: drawer.setActiveServer(server);
            startTransportThread(server);
        }
    }

    private void startTransportThread(Server server) {
        stopTransportThread();
        Handler responseHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case TransportThread.RESPONSE:
                        handleResponse(msg);
                        break;
                    default:
                        Log.e(TAG, "Unknown message: " + msg);
                }
            }
        };
        transportThread = new TransportThread(server, responseHandler);
        transportThread.start();
    }

    private void stopTransportThread() {
        if (transportThread != null) {
            transportThread.quit();
            transportThread = null;
        }
    }

    private void sendRequest(Request request) {
        Message msg = transportThread.getHandler().obtainMessage(TransportThread.REQUEST);
        msg.obj = request;
        transportThread.getHandler().sendMessage(msg);
    }

    private void handleResponse(Message msg) {
        if (!(msg.obj instanceof Response))
            throw new IllegalArgumentException("Response message must contain Response object in its 'obj' field");

        Response response = (Response) msg.obj;

        Log.d(TAG, "Response received: " + response.getBody());

        if (response instanceof CheckPortResponse) {
            boolean isOpen = ((CheckPortResponse) response).isOpen();
            if (isOpen) {
                sendRequest(new UpdateTorrentsRequest());
                torrentUpdater.start();
            } else {
                Toast.makeText(this, "Port " + application.getActiveServer().getPort() +
                        " is closed. Check Transmission settings.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Port " + application.getActiveServer().getPort() + " is closed");
            }
        }




    }
}
