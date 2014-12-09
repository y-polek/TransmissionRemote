package net.yupol.transmissionremote.app.preferences;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.TransportThread;
import net.yupol.transmissionremote.app.transport.request.SessionGetRequest;
import net.yupol.transmissionremote.app.transport.response.SessionGetResponse;

import org.apache.http.HttpStatus;

import static net.yupol.transmissionremote.app.preferences.ServerPreferences.*;

public class ServerPreferencesActivity extends Activity {

    private static final String TAG = ServerPreferencesActivity.class.getSimpleName();
    private static final String SERVER_PREFERENCES_FRAGMENT_TAG = "server_preferences_fragment_tag";

    public static final String EXTRA_SERVER_PREFERENCES = "extra_server_preferences";

    private TransportThread transportThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_preferences_activity);
        setTitle(R.string.server_preferences);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showProgressbarFragment();

        TransmissionRemote app = (TransmissionRemote) getApplication();
        Server server = app.getActiveServer();

        transportThread = new TransportThread(server, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof SessionGetResponse) {
                    SessionGetResponse response = (SessionGetResponse) msg.obj;
                    if (response.getStatusCode() == HttpStatus.SC_OK) {
                        ((TransmissionRemote) getApplication()).setSpeedLimitEnabled(response.isAltSpeedEnabled());
                        showPreferencesFragment(toFragmentArguments(response));
                    } else {
                        // TODO: show error message
                    }
                }
            }
        });
        transportThread.start();

        Message msg = transportThread.getHandler().obtainMessage(TransportThread.REQUEST);
        msg.obj = new SessionGetRequest();
        transportThread.getHandler().sendMessage(msg);
    }

    @Override
    public void onBackPressed() {
        ServerPreferencesFragment fragment = (ServerPreferencesFragment)
                getFragmentManager().findFragmentByTag(SERVER_PREFERENCES_FRAGMENT_TAG);
        if (fragment != null) {
            Intent result = new Intent();
            result.putExtra(EXTRA_SERVER_PREFERENCES, fragment.getPreferences().toString());
            setResult(RESULT_OK, result);
        } else {
            setResult(RESULT_CANCELED);
        }

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        transportThread.quit();
    }

    private void showProgressbarFragment() {
        FragmentManager fm = getFragmentManager();

        ServerPreferencesFragment preferencesFragment = (ServerPreferencesFragment)
                fm.findFragmentById(R.id.server_preferences_fragment_container);

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        if (preferencesFragment != null) ft.remove(preferencesFragment);
        ft.add(R.id.progress_bar_fragment_container, new ProgressbarFragment());
        ft.commit();
    }

    private void showPreferencesFragment(Bundle arguments) {
        FragmentManager fm = getFragmentManager();

        ProgressbarFragment progressbarFragment = (ProgressbarFragment)
                fm.findFragmentById(R.id.progress_bar_fragment_container);

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (progressbarFragment != null) ft.remove(progressbarFragment);
        ServerPreferencesFragment fragment = new ServerPreferencesFragment();
        fragment.setArguments(arguments);
        ft.add(R.id.server_preferences_fragment_container, fragment, SERVER_PREFERENCES_FRAGMENT_TAG);
        ft.commit();
    }

    private Bundle toFragmentArguments(SessionGetResponse resp) {
        Bundle b = new Bundle();
        b.putInt(SPEED_LIMIT_DOWN, resp.getSpeedLimitDown());
        b.putBoolean(SPEED_LIMIT_DOWN_ENABLED, resp.isSpeedLimitDownEnabled());
        b.putInt(SPEED_LIMIT_UP, resp.getSpeedLimitUp());
        b.putBoolean(SPEED_LIMIT_UP_ENABLED, resp.isSpeedLimitUpEnabled());
        b.putInt(ALT_SPEED_LIMIT_DOWN, resp.getAltSpeedDown());
        b.putInt(ALT_SPEED_LIMIT_UP, resp.getAltSpeedUp());
        b.putBoolean(ALT_SPEED_LIMIT_ENABLED, resp.isAltSpeedEnabled());
        return b;
    }
}
