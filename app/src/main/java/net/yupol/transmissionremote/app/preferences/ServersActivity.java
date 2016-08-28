package net.yupol.transmissionremote.app.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.server.AddServerActivity;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.server.ServerDetailsFragment;

public class ServersActivity extends AppCompatActivity {

    private static final String TAG = ServersActivity.class.getSimpleName();

    private static final String TAG_SERVERS = "tag_servers";
    private static final String TAG_SERVER_DETAILS = "tag_server_details";

    private static final int REQUEST_CODE_NEW_SERVER = 1;

    public static final String KEY_SERVER_UUID = "key_server_uuid";

    private TransmissionRemote app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servers_activity_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }

        if (savedInstanceState != null) {
            return;
        }

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        ServersFragment serversFragment = (ServersFragment) fm.findFragmentByTag(TAG_SERVERS);
        if (serversFragment == null) {
            serversFragment = new ServersFragment();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, serversFragment, TAG_SERVERS);
        ft.commit();
        invalidateOptionsMenu();

        serversFragment.setOnServerSelectedListener(new ServersFragment.OnServerSelectedListener() {
            @Override
            public void onServerSelected(Server server) {
                showServerDetails(server);
            }
        });

        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                invalidateOptionsMenu();
            }
        });

        app = TransmissionRemote.getApplication(this);

        if (getIntent().hasExtra(KEY_SERVER_UUID)) {
            String id = getIntent().getStringExtra(KEY_SERVER_UUID);
            showServerDetails(app.getServerById(id));
        }
    }

    private void showServerDetails(Server server) {
        FragmentManager fm = getSupportFragmentManager();
        ServerDetailsFragment serverDetailsFragment = (ServerDetailsFragment) fm.findFragmentByTag(TAG_SERVER_DETAILS);
        if (serverDetailsFragment == null) {
            serverDetailsFragment = new ServerDetailsFragment();
        }

        Bundle arguments = new Bundle();
        arguments.putParcelable(ServerDetailsFragment.ARGUMENT_SERVER, server);
        serverDetailsFragment.setArguments(arguments);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, serverDetailsFragment, TAG_SERVER_DETAILS);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    askForSave();
                } else {
                    finish();
                }
                return true;
            case R.id.action_add:
                startActivityForResult(new Intent(this, AddServerActivity.class), REQUEST_CODE_NEW_SERVER);
                return true;
            case R.id.action_remove:
                new AlertDialog.Builder(this)
                    .setMessage(R.string.remove_server_confirmation)
                    .setPositiveButton(R.string.remove_server_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ServerDetailsFragment detailsFragment = (ServerDetailsFragment) getSupportFragmentManager().findFragmentByTag(TAG_SERVER_DETAILS);
                            if (detailsFragment != null) {
                                Server server = detailsFragment.getServerArgument();
                                if (server != null) {
                                    app.removeServer(server);
                                    getSupportFragmentManager().popBackStack();
                                }
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .create().show();
                return true;
            case R.id.action_save:
                ServerDetailsFragment detailsFragment = (ServerDetailsFragment) getSupportFragmentManager().findFragmentByTag(TAG_SERVER_DETAILS);
                if (detailsFragment != null) {
                    detailsFragment.saveServer();
                    app.updateServer(detailsFragment.getServerArgument());
                    getSupportFragmentManager().popBackStack();
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "ServerDetailsFragment is not active while save server action performed");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                askForSave();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NEW_SERVER) {
            if (resultCode == RESULT_OK) {
                Server server = data.getParcelableExtra(AddServerActivity.EXTRA_SEVER);
                app.addServer(server);
                app.setActiveServer(server);
            }
        }
    }

    private void askForSave() {
        final ServerDetailsFragment detailsFragment = (ServerDetailsFragment) getSupportFragmentManager().findFragmentByTag(TAG_SERVER_DETAILS);
        if (detailsFragment.hasChanges()) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.save_changes_question)
                    .setPositiveButton(R.string.save_changes_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            detailsFragment.saveServer();
                            FragmentManager fm = getSupportFragmentManager();
                            fm.popBackStack();
                        }
                    }).setNegativeButton(R.string.save_changes_discard, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getSupportFragmentManager().popBackStack();
                }
            }).create().show();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
