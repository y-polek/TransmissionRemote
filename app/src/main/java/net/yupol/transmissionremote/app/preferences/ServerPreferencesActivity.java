package net.yupol.transmissionremote.app.preferences;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.model.json.ServerSettings;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.request.SessionGetRequest;

import static net.yupol.transmissionremote.app.preferences.ServerPreferences.ALT_SPEED_LIMIT_DOWN;
import static net.yupol.transmissionremote.app.preferences.ServerPreferences.ALT_SPEED_LIMIT_ENABLED;
import static net.yupol.transmissionremote.app.preferences.ServerPreferences.ALT_SPEED_LIMIT_UP;
import static net.yupol.transmissionremote.app.preferences.ServerPreferences.SPEED_LIMIT_DOWN;
import static net.yupol.transmissionremote.app.preferences.ServerPreferences.SPEED_LIMIT_DOWN_ENABLED;
import static net.yupol.transmissionremote.app.preferences.ServerPreferences.SPEED_LIMIT_UP;
import static net.yupol.transmissionremote.app.preferences.ServerPreferences.SPEED_LIMIT_UP_ENABLED;

public class ServerPreferencesActivity extends BaseSpiceActivity {

    private static final String TAG = ServerPreferencesActivity.class.getSimpleName();
    private static final String SERVER_PREFERENCES_FRAGMENT_TAG = "server_preferences_fragment_tag";

    public static final String EXTRA_SERVER_PREFERENCES = "extra_server_preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_preferences_activity);
        setTitle(R.string.server_preferences);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showProgressbarFragment();

        getTransportManager().doRequest(new SessionGetRequest(), new RequestListener<ServerSettings>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(TAG, "Failed to obtain server settings");
            }

            @Override
            public void onRequestSuccess(ServerSettings serverSettings) {
                ((TransmissionRemote) getApplication()).setSpeedLimitEnabled(serverSettings.isAltSpeedEnabled());
                showPreferencesFragment(toFragmentArguments(serverSettings));
            }
        });
    }

    @Override
    public void onBackPressed() {
        doFinish();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                doFinish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doFinish() {
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

    private Bundle toFragmentArguments(ServerSettings settings) {
        Bundle b = new Bundle();
        b.putInt(SPEED_LIMIT_DOWN, settings.getSpeedLimitDown());
        b.putBoolean(SPEED_LIMIT_DOWN_ENABLED, settings.isSpeedLimitDownEnabled());
        b.putInt(SPEED_LIMIT_UP, settings.getSpeedLimitUp());
        b.putBoolean(SPEED_LIMIT_UP_ENABLED, settings.isSpeedLimitUpEnabled());
        b.putInt(ALT_SPEED_LIMIT_DOWN, settings.getAltSpeedDown());
        b.putInt(ALT_SPEED_LIMIT_UP, settings.getAltSpeedUp());
        b.putBoolean(ALT_SPEED_LIMIT_ENABLED, settings.isAltSpeedEnabled());
        return b;
    }
}
