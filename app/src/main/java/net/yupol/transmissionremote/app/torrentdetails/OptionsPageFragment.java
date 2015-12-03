package net.yupol.transmissionremote.app.torrentdetails;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.IdleLimitMode;
import net.yupol.transmissionremote.app.model.json.LimitMode;
import net.yupol.transmissionremote.app.model.json.RatioLimitMode;
import net.yupol.transmissionremote.app.model.json.ServerSettings;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.Torrents;
import net.yupol.transmissionremote.app.model.json.TransferPriority;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.SessionGetRequest;
import net.yupol.transmissionremote.app.transport.request.TorrentGetRequest;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;
import net.yupol.transmissionremote.app.utils.MinMaxTextWatcher;

public class OptionsPageFragment extends BasePageFragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = OptionsPageFragment.class.getSimpleName();

    private TransportManager transportManager;

    private Spinner prioritySpinner;
    private BandwidthLimitFragment bandwidthLimitFragment;
    private CheckBox stayWithGlobalCheckbox;
    private Spinner ratioLimitSpinner;
    private Spinner idleLimitSpinner;
    private EditText ratioLimitEdit;
    private TextView ratioLimitGlobalText;
    private EditText idleLimitEdit;
    private TextView idleLimitGlobalText;

    private ServerSettings serverSettings;

    private Menu menu;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseSpiceActivity) {
            transportManager = ((BaseSpiceActivity) activity).getTransportManager();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        transportManager.doRequest(new SessionGetRequest(), new RequestListener<ServerSettings>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(TAG, "Failed to retrieve server settings");
                // TODO: retry
            }

            @Override
            public void onRequestSuccess(ServerSettings settings) {
                serverSettings = settings;
                updateSeedingLimitsUi(false);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.torrent_details_options_page_fragment, container, false);

        prioritySpinner = (Spinner) view.findViewById(R.id.priority_spinner);
        prioritySpinner.setAdapter(new TransferPrioritySpinnerAdapter());

        stayWithGlobalCheckbox = (CheckBox) view.findViewById(R.id.stay_with_global_bandwidth_checkbox);

        bandwidthLimitFragment = (BandwidthLimitFragment)
                getChildFragmentManager().findFragmentById(R.id.torrent_bandwidth_limit_fragment);

        ratioLimitSpinner = (Spinner) view.findViewById(R.id.ratio_limit_mode_spinner);
        ratioLimitSpinner.setAdapter(new RatioLimitModeAdapter());
        ratioLimitEdit = (EditText) view.findViewById(R.id.ratio_limit_value);
        ratioLimitEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10)} );
        ratioLimitGlobalText = (TextView) view.findViewById(R.id.ratio_limit_global_value);

        idleLimitSpinner = (Spinner) view.findViewById(R.id.idle_limit_mode_spinner);
        idleLimitSpinner.setAdapter(new IdleLimitModeAdapter());
        idleLimitEdit = (EditText) view.findViewById(R.id.idle_limit_value);
        idleLimitEdit.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(5)} );
        idleLimitEdit.addTextChangedListener(new MinMaxTextWatcher(1, 0xFFFF));
        idleLimitGlobalText = (TextView) view.findViewById(R.id.idle_limit_global_value);

        updateUi(true);

        // Postpone listeners registration to avoid notifying listeners on layout.
        // http://stackoverflow.com/questions/2562248/how-to-keep-onitemselected-from-firing-off-on-a-newly-instantiated-spinner
        view.post(new Runnable() {
            @Override
            public void run() {
                ratioLimitSpinner.setOnItemSelectedListener(OptionsPageFragment.this);
                idleLimitSpinner.setOnItemSelectedListener(OptionsPageFragment.this);
            }
        });

        return view;
    }

    private void updateUi(boolean syncWithModel) {
        Torrent torrent = getTorrent();

        prioritySpinner.setSelection(torrent.getTransferPriority().ordinal());

        stayWithGlobalCheckbox.setChecked(torrent.isSessionLimitsHonored());

        bandwidthLimitFragment.setDownloadLimited(torrent.isDownloadLimited());
        bandwidthLimitFragment.setDownloadLimit(torrent.getDownloadLimit());
        bandwidthLimitFragment.setUploadLimited(torrent.isUploadLimited());
        bandwidthLimitFragment.setUploadLimit(torrent.getUploadLimit());

        ratioLimitSpinner.setSelection(torrent.getSeedRatioMode().ordinal());
        idleLimitSpinner.setSelection(torrent.getSeedIdleMode().ordinal());
        updateSeedingLimitsUi(syncWithModel);
    }

    private void updateSeedingLimitsUi(boolean syncWithModel) {
        Torrent torrent = getTorrent();

        switch (getRatioLimitMode()) {
            case STOP_AT_RATIO:
                if (syncWithModel || ratioLimitEdit.getText().length() == 0) {
                    ratioLimitEdit.setText(String.valueOf(torrent.getSeedRatioLimit()));
                }
                ratioLimitEdit.setVisibility(View.VISIBLE);
                ratioLimitEdit.setEnabled(true);
                ratioLimitGlobalText.setVisibility(View.INVISIBLE);
                break;
            case GLOBAL_SETTINGS:
                if (serverSettings != null) {
                    String text = serverSettings.isSeedRatioLimited()
                            ? String.valueOf(serverSettings.getSeedRatioLimit())
                            : getString(R.string.disabled);
                    ratioLimitGlobalText.setText(text);
                } else {
                    ratioLimitGlobalText.setText(R.string.three_dots);
                }
                ratioLimitEdit.setVisibility(View.INVISIBLE);
                ratioLimitGlobalText.setVisibility(View.VISIBLE);
                break;
            case UNLIMITED:
                if (syncWithModel || ratioLimitEdit.getText().length() == 0) {
                    ratioLimitEdit.setText(String.valueOf(torrent.getSeedRatioLimit()));
                }
                ratioLimitEdit.setVisibility(View.VISIBLE);
                ratioLimitEdit.setEnabled(false);
                ratioLimitGlobalText.setVisibility(View.INVISIBLE);
                break;
        }

        switch (getIdleLimitMode()) {
            case STOP_WHEN_INACTIVE:
                if (syncWithModel || idleLimitEdit.getText().length() == 0) {
                    idleLimitEdit.setText(String.valueOf(torrent.getSeedIdleLimit()));
                }
                idleLimitEdit.setVisibility(View.VISIBLE);
                idleLimitEdit.setEnabled(true);
                idleLimitGlobalText.setVisibility(View.INVISIBLE);
                break;
            case GLOBAL_SETTINGS:
                if (serverSettings != null) {
                    String text = serverSettings.isSeedIdleLimited()
                            ? String.valueOf(serverSettings.getSeedIdleLimit())
                            : getString(R.string.disabled);
                    idleLimitGlobalText.setText(text);
                }
                idleLimitEdit.setVisibility(View.INVISIBLE);
                idleLimitGlobalText.setVisibility(View.VISIBLE);
                break;
            case UNLIMITED:
                if (syncWithModel || idleLimitEdit.getText().length() == 0) {
                    idleLimitEdit.setText(String.valueOf(torrent.getSeedIdleLimit()));
                }
                idleLimitEdit.setVisibility(View.VISIBLE);
                idleLimitEdit.setEnabled(false);
                idleLimitGlobalText.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private RatioLimitMode getRatioLimitMode() {
        return (RatioLimitMode) ratioLimitSpinner.getSelectedItem();
    }

    private double getRatioLimit() {
        try {
            return Double.parseDouble(ratioLimitEdit.getText().toString());
        } catch (NumberFormatException e) {
            return getTorrent().getSeedRatioLimit();
        }
    }

    private IdleLimitMode getIdleLimitMode() {
        return (IdleLimitMode) idleLimitSpinner.getSelectedItem();
    }

    private int getIdleLimit() {
        try {
            return Integer.parseInt(idleLimitEdit.getText().toString());
        } catch (NumberFormatException e) {
            return getTorrent().getSeedIdleLimit();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.torrent_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                TorrentSetRequest.Builder requestBuilder = getSaveOptionsRequestBuilder();
                if (requestBuilder.isChanged()) {
                    sendUpdateOptionsRequest(requestBuilder.build());
                }
                return true;
        }

        return false;
    }

    @Override
    public int getPageTitleRes() {
        return R.string.options;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateSeedingLimitsUi(false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * @return optional which contain {@link TorrentSetRequest} or contain nothing if no options changed
     */
    public TorrentSetRequest.Builder getSaveOptionsRequestBuilder() {
        Torrent torrent = getTorrent();
        TorrentSetRequest.Builder requestBuilder = TorrentSetRequest.builder(torrent.getId());

        TransferPriority priority = (TransferPriority) prioritySpinner.getSelectedItem();
        if (priority != torrent.getTransferPriority()) {
            requestBuilder.transferPriority(priority);
        }

        boolean honorsSessionLimits = stayWithGlobalCheckbox.isChecked();
        if (honorsSessionLimits != torrent.isSessionLimitsHonored()) {
            requestBuilder.honorsSessionLimits(honorsSessionLimits);
        }

        boolean isDownloadLimited = bandwidthLimitFragment.isDownloadLimited();
        if (isDownloadLimited != torrent.isDownloadLimited()) {
            requestBuilder.downloadLimited(isDownloadLimited);
        }

        long downloadLimit = bandwidthLimitFragment.getDownloadLimit();
        if (downloadLimit != torrent.getDownloadLimit()) {
            requestBuilder.downloadLimit(downloadLimit);
        }

        boolean isUploadLimited = bandwidthLimitFragment.isUploadLimited();
        if (isUploadLimited != torrent.isUploadLimited()) {
            requestBuilder.uploadLimited(isUploadLimited);
        }

        long uploadLimit = bandwidthLimitFragment.getUploadLimit();
        if (uploadLimit != torrent.getUploadLimit()) {
            requestBuilder.uploadLimit(uploadLimit);
        }

        LimitMode ratioLimitMode = getRatioLimitMode();
        if (ratioLimitMode != torrent.getSeedRatioMode()) {
            requestBuilder.seedRatioMode(ratioLimitMode);
        }

        double ratioLimit = getRatioLimit();
        if (ratioLimit != torrent.getSeedRatioLimit()) {
            requestBuilder.seedRatioLimit(ratioLimit);
        }

        LimitMode idleLimitMode = getIdleLimitMode();
        if (idleLimitMode != torrent.getSeedIdleMode()) {
            requestBuilder.seedIdleMode(idleLimitMode);
        }

        int idleLimit = getIdleLimit();
        if (idleLimit != torrent.getSeedIdleLimit()) {
            requestBuilder.seedIdleLimit(idleLimit);
        }

        return requestBuilder;
    }

    private void sendUpdateOptionsRequest(TorrentSetRequest request) {
        if (transportManager == null)
            throw new RuntimeException("OptionsPageFragment should be used with BaseSpiceActivity.");

        saveStarted();

        transportManager.doRequest(request, new RequestListener<Void>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Toast.makeText(getActivity(), getString(R.string.options_update_failed), Toast.LENGTH_LONG).show();
                saveFinished();
            }

            @Override
            public void onRequestSuccess(Void aVoid) {
                sendTorrentUpdateRequest();
            }
        });
    }

    private void sendTorrentUpdateRequest() {
        transportManager.doRequest(new TorrentGetRequest(getTorrent().getId()), new RequestListener<Torrents>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Toast.makeText(getActivity(), getString(R.string.options_update_failed), Toast.LENGTH_LONG).show();
                saveFinished();
            }

            @Override
            public void onRequestSuccess(Torrents torrents) {
                if (torrents.size() == 1) {
                    setTorrent(torrents.get(0));
                    updateUi(true);
                    Toast.makeText(getActivity(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Torrents count does not match. One torrent expected, actual count: " + torrents.size());
                }

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
