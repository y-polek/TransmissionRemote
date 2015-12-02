package net.yupol.transmissionremote.app.preferences;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servers_activity_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            return;
        }

        FragmentManager fm = getFragmentManager();
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
                FragmentManager fm = getFragmentManager();
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
        });

        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Fragment detailsFragment = getFragmentManager().findFragmentByTag(TAG_SERVER_DETAILS);
        int menuResId = detailsFragment != null ? R.menu.server_details_menu : R.menu.servers_menu;
        getMenuInflater().inflate(menuResId, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getFragmentManager();
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
                            ServerDetailsFragment detailsFragment = (ServerDetailsFragment) getFragmentManager().findFragmentByTag(TAG_SERVER_DETAILS);
                            if (detailsFragment != null) {
                                Server server = detailsFragment.getServerArgument();
                                if (server != null) {
                                    ((TransmissionRemote) getApplication()).removeServer(server);
                                    getFragmentManager().popBackStack();
                                }
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .create().show();
                return true;
            case R.id.action_save:
                ServerDetailsFragment detailsFragment = (ServerDetailsFragment) getFragmentManager().findFragmentByTag(TAG_SERVER_DETAILS);
                if (detailsFragment != null) {
                    detailsFragment.saveServer();
                    ((TransmissionRemote) getApplication()).updateServer(detailsFragment.getServerArgument());
                    getFragmentManager().popBackStack();
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
            FragmentManager fm = getFragmentManager();
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
                TransmissionRemote app = (TransmissionRemote) getApplication();
                app.addServer(server);
                app.setActiveServer(server);
            }
        }
    }

    private void askForSave() {
        final ServerDetailsFragment detailsFragment = (ServerDetailsFragment) getFragmentManager().findFragmentByTag(TAG_SERVER_DETAILS);
        if (detailsFragment.hasChanges()) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.save_changes_question)
                    .setPositiveButton(R.string.save_changes_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            detailsFragment.saveServer();
                            FragmentManager fm = getFragmentManager();
                            fm.popBackStack();
                        }
                    }).setNegativeButton(R.string.save_changes_discard, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FragmentManager fm = getFragmentManager();
                    fm.popBackStack();
                }
            }).create().show();
        } else {
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
        }
    }
}
