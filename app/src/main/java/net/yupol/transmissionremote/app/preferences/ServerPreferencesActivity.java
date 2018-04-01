package net.yupol.transmissionremote.app.preferences;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import net.yupol.transmissionremote.app.ProgressbarFragment;
import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.torrentdetails.SaveChangesDialogFragment;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.request.SessionSetRequest;
import net.yupol.transmissionremote.model.Server;
import net.yupol.transmissionremote.model.json.ServerSettings;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import transport.RpcRequest;
import transport.Transport;

public class ServerPreferencesActivity extends BaseSpiceActivity implements SaveChangesDialogFragment.SaveDiscardListener {

    private static final String TAG = ServerPreferencesActivity.class.getSimpleName();
    private static final String TAG_SERVER_PREFERENCES_FRAGMENT = "tag_server_preferences_fragment";
    private static final String TAG_SAVE_CHANGES_DIALOG = "tag_save_changes_dialog";

    private static final String KEY_SAVE_CHANGES_REQUEST = "key_save_changes_request";

    private SessionSetRequest saveChangesRequest;
    private CompositeDisposable requests = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_preferences_activity);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }

        if (savedInstanceState == null) {
            showProgressbarFragment();

            final TransmissionRemote app = (TransmissionRemote) getApplication();
            Server activeServer = app.getActiveServer();
            new Transport(activeServer).getApi().serverSettings(RpcRequest.sessionGet())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<ServerSettings>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            requests.clear();
                        }

                        @Override
                        public void onSuccess(ServerSettings serverSettings) {
                            app.setSpeedLimitEnabled(serverSettings.isAltSpeedLimitEnabled());
                            showPreferencesFragment(serverSettings);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "Failed to obtain server settings", e);
                        }
                    });
        }
    }

    @Override
    protected void onStop() {
        requests.clear();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_SAVE_CHANGES_REQUEST, saveChangesRequest);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        saveChangesRequest = savedInstanceState.getParcelable(KEY_SAVE_CHANGES_REQUEST);
    }

    private void showProgressbarFragment() {
        FragmentManager fm = getSupportFragmentManager();

        ServerPreferencesFragment preferencesFragment = (ServerPreferencesFragment)
                fm.findFragmentById(R.id.server_preferences_fragment_container);

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        if (preferencesFragment != null) ft.remove(preferencesFragment);
        ft.add(R.id.progress_bar_fragment_container, new ProgressbarFragment());
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        ServerPreferencesFragment fragment = (ServerPreferencesFragment)
                getSupportFragmentManager().findFragmentByTag(TAG_SERVER_PREFERENCES_FRAGMENT);
        if (fragment == null) {
            super.onBackPressed();
            return;
        }

        SessionSetRequest.Builder requestBuilder = fragment.getPreferencesRequestBuilder();
        if (requestBuilder.isChanged()) {
            saveChangesRequest = requestBuilder.build();
            new SaveChangesDialogFragment().show(getFragmentManager(), TAG_SAVE_CHANGES_DIALOG);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onSavePressed() {
        getTransportManager().doRequest(saveChangesRequest, null);
        super.onBackPressed();
    }

    @Override
    public void onDiscardPressed() {
        super.onBackPressed();
    }

    private void showPreferencesFragment(ServerSettings settings) {
        FragmentManager fm = getSupportFragmentManager();

        ProgressbarFragment progressbarFragment = (ProgressbarFragment)
                fm.findFragmentById(R.id.progress_bar_fragment_container);

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (progressbarFragment != null) ft.remove(progressbarFragment);
        ServerPreferencesFragment fragment = new ServerPreferencesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ServerPreferencesFragment.KEY_SERVER_SETTINGS, settings);
        fragment.setArguments(args);
        ft.add(R.id.server_preferences_fragment_container, fragment, TAG_SERVER_PREFERENCES_FRAGMENT);
        ft.commit();
    }
}
