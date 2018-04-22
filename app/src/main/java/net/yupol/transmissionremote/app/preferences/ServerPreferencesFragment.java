package net.yupol.transmissionremote.app.preferences;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.torrentdetails.BandwidthLimitFragment;
import net.yupol.transmissionremote.app.utils.IconUtils;
import net.yupol.transmissionremote.model.Parameter;
import net.yupol.transmissionremote.model.json.ServerSettings;
import net.yupol.transmissionremote.transport.Transport;
import net.yupol.transmissionremote.transport.rpc.RpcArgs;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static net.yupol.transmissionremote.transport.rpc.SessionParameters.altSpeedLimitDown;
import static net.yupol.transmissionremote.transport.rpc.SessionParameters.altSpeedLimitUp;
import static net.yupol.transmissionremote.transport.rpc.SessionParameters.speedLimitDown;
import static net.yupol.transmissionremote.transport.rpc.SessionParameters.speedLimitDownEnabled;
import static net.yupol.transmissionremote.transport.rpc.SessionParameters.speedLimitUp;
import static net.yupol.transmissionremote.transport.rpc.SessionParameters.speedLimitUpEnabled;

public class ServerPreferencesFragment extends Fragment {

    private static final String TAG = ServerPreferencesFragment.class.getSimpleName();

    public static final String KEY_SERVER_SETTINGS = "extra_server_preferences";

    private ServerSettings serverSettings;

    private BandwidthLimitFragment globalBandwidthLimitFragment;
    private BandwidthLimitFragment altBandwidthLimitFragment;
    private TextView altLimitHeader;
    private Menu menu;
    private CompositeDisposable requests = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.server_preferences_fragment, container, false);

        globalBandwidthLimitFragment = (BandwidthLimitFragment)
                getChildFragmentManager().findFragmentById(R.id.global_bandwidth_limit_fragment);
        altBandwidthLimitFragment = (BandwidthLimitFragment)
                getChildFragmentManager().findFragmentById(R.id.alt_bandwidth_limit_fragment);
        altLimitHeader = view.findViewById(R.id.turtle_limit_header_text);

        return view;
    }

    @Override
    public void onStop() {
        requests.clear();
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            serverSettings = args.getParcelable(KEY_SERVER_SETTINGS);
        }
        if (savedInstanceState != null) {
            serverSettings = savedInstanceState.getParcelable(KEY_SERVER_SETTINGS);
        }
        updateUi();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_SERVER_SETTINGS, serverSettings);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.server_preferences_menu, menu);
        IconUtils.setMenuIcon(getContext(), menu, R.id.action_save, GoogleMaterial.Icon.gmd_save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                List<Parameter<String, ?>> parameters = getSessionParameters();
                if (!parameters.isEmpty()) {
                    sendUpdateOptionsRequest(parameters);
                }
                return true;
        }
        return false;
    }

    public List<Parameter<String, ?>> getSessionParameters() {

        List<Parameter<String, ?>> params = new LinkedList<>();

        boolean downloadLimited = globalBandwidthLimitFragment.isDownloadLimited();
        if (serverSettings.isSpeedLimitDownEnabled() != downloadLimited) {
            params.add(speedLimitDownEnabled(downloadLimited));
        }

        long downloadLimit = globalBandwidthLimitFragment.getDownloadLimit();
        if (serverSettings.getSpeedLimitDown() != downloadLimit) {
            params.add(speedLimitDown(downloadLimit));
        }

        boolean uploadLimited = globalBandwidthLimitFragment.isUploadLimited();
        if (serverSettings.isSpeedLimitUpEnabled() != uploadLimited) {
            params.add(speedLimitUpEnabled(uploadLimited));
        }

        long uploadLimit = globalBandwidthLimitFragment.getUploadLimit();
        if (serverSettings.getSpeedLimitUp() != uploadLimit) {
            params.add(speedLimitUp(uploadLimit));
        }

        long altDownloadLimit = altBandwidthLimitFragment.getDownloadLimit();
        if (serverSettings.getAltSpeedLimitDown() != altDownloadLimit) {
            params.add(altSpeedLimitDown(altDownloadLimit));
        }

        long altUploadLimit = altBandwidthLimitFragment.getUploadLimit();
        if (serverSettings.getAltSpeedLimitUp() != altUploadLimit) {
            params.add(altSpeedLimitUp(altUploadLimit));
        }

        return params;
    }

    private void updateUi() {

        if (serverSettings == null) {
            throw new IllegalStateException("No server preferences set." +
                    " Ensure that setArguments(Bundle) called with bundle containing server preferences.");
        }

        View view = getView();
        if (view == null) {
            Log.e(TAG, "trying to update fragment before onCreateView()");
            return;
        }

        int globalLimitDown = serverSettings.getSpeedLimitDown();
        boolean isGlobalLimitDownEnabled = serverSettings.isSpeedLimitDownEnabled();
        int globalLimitUp = serverSettings.getSpeedLimitUp();
        boolean isGlobalLimitUpEnabled = serverSettings.isSpeedLimitUpEnabled();
        int altLimitDown = serverSettings.getAltSpeedLimitDown();
        int altLimitUp = serverSettings.getAltSpeedLimitUp();
        boolean isAltLimitEnabled = serverSettings.isAltSpeedLimitEnabled();

        globalBandwidthLimitFragment.setDownloadLimited(isGlobalLimitDownEnabled);
        globalBandwidthLimitFragment.setDownloadLimit(globalLimitDown);
        globalBandwidthLimitFragment.setUploadLimited(isGlobalLimitUpEnabled);
        globalBandwidthLimitFragment.setUploadLimit(globalLimitUp);

        altBandwidthLimitFragment.setDownloadLimit(altLimitDown);
        altBandwidthLimitFragment.setUploadLimit(altLimitUp);

        int turtleImage = isAltLimitEnabled ? R.drawable.ic_turtle_active : R.drawable.ic_turtle_black;
        altLimitHeader.setCompoundDrawablesWithIntrinsicBounds(turtleImage, 0, 0, 0);
    }

    private void sendUpdateOptionsRequest(List<Parameter<String, ?>> parameters) {
        saveStarted();

        new Transport(TransmissionRemote.getInstance().getActiveServer()).api().setServerSettings(RpcArgs.parameters(parameters))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        requests.add(d);
                    }

                    @Override
                    public void onComplete() {
                        sendPreferencesUpdateRequest();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), getString(R.string.preferences_update_failed), Toast.LENGTH_LONG).show();
                        saveFinished();
                    }
                });
    }

    private void sendPreferencesUpdateRequest() {
        new Transport(TransmissionRemote.getInstance().getActiveServer()).api().serverSettings(ImmutableMap.<String, Object>of())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ServerSettings>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        requests.add(d);
                    }

                    @Override
                    public void onSuccess(ServerSettings settings) {
                        serverSettings = settings;
                        updateUi();
                        Toast.makeText(getActivity(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
                        saveFinished();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), getString(R.string.preferences_update_failed), Toast.LENGTH_LONG).show();
                        saveFinished();
                    }
                });
    }

    private void saveStarted() {
        menu.findItem(R.id.action_save).setEnabled(false);
    }

    private void saveFinished() {
        menu.findItem(R.id.action_save).setEnabled(true);
    }
}
