package net.yupol.transmissionremote.app.torrentdetails;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.Torrents;
import net.yupol.transmissionremote.app.model.json.TransferPriority;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.TorrentGetRequest;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;

public class OptionsPageFragment extends BasePageFragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = OptionsPageFragment.class.getSimpleName();

    private TransportManager transportManager;

    private Spinner prioritySpinner;
    private BandwidthLimitFragment bandwidthLimitFragment;
    private CheckBox stayWithGlobalCheckbox;
    private Spinner ratioLimitSpinner;
    private Spinner idleLimitSpinner;

    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
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

        idleLimitSpinner = (Spinner) view.findViewById(R.id.idle_limit_mode_spinner);
        idleLimitSpinner.setAdapter(new IdleLimitModeAdapter());

        updateUi();

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

    private void updateUi() {
        Torrent torrent = getTorrent();

        prioritySpinner.setSelection(torrent.getTransferPriority().ordinal());

        stayWithGlobalCheckbox.setChecked(torrent.isSessionLimitsHonored());

        bandwidthLimitFragment.setDownloadLimited(torrent.isDownloadLimited());
        bandwidthLimitFragment.setDownloadLimit(torrent.getDownloadLimit());
        bandwidthLimitFragment.setUploadLimited(torrent.isUploadLimited());
        bandwidthLimitFragment.setUploadLimit(torrent.getUploadLimit());

        ratioLimitSpinner.setSelection(torrent.getSeedRatioMode().ordinal());
        idleLimitSpinner.setSelection(torrent.getSeedIdleMode().ordinal());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseSpiceActivity) {
            transportManager = ((BaseSpiceActivity) activity).getTransportManager();
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
                Optional<TorrentSetRequest> request = buildSaveOptionsRequest();
                if (request.isPresent()) {
                    sendUpdateOptionsRequest(request.get());
                }
                return true;
        }

        return false;
    }

    @Override
    public int getPageTitleRes() {
        return R.string.options;
    }

    /**
     * Listens for Transfer Priority spinner changes.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object selection = parent.getItemAtPosition(position);
        if (parent.getId() == R.id.ratio_limit_mode_spinner) {

        } else if (parent.getId() == R.id.idle_limit_mode_spinner) {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * @return optional which contain {@link TorrentSetRequest} or contain nothing if no options changed
     */
    public Optional<TorrentSetRequest> buildSaveOptionsRequest() {
        Torrent torrent = getTorrent();
        boolean optionsChanged = false;
        TorrentSetRequest.Builder requestBuilder = TorrentSetRequest.builder(torrent.getId());

        TransferPriority priority = (TransferPriority) prioritySpinner.getSelectedItem();
        if (priority != torrent.getTransferPriority()) {
            requestBuilder.transferPriority(priority);
            optionsChanged = true;
        }

        boolean honorsSessionLimits = stayWithGlobalCheckbox.isChecked();
        if (honorsSessionLimits != torrent.isSessionLimitsHonored()) {
            requestBuilder.honorsSessionLimits(honorsSessionLimits);
            optionsChanged = true;
        }

        boolean isDownloadLimited = bandwidthLimitFragment.isDownloadLimited();
        if (isDownloadLimited != torrent.isDownloadLimited()) {
            requestBuilder.downloadLimited(isDownloadLimited);
            optionsChanged = true;
        }

        long downloadLimit = bandwidthLimitFragment.getDownloadLimit();
        if (downloadLimit != torrent.getDownloadLimit()) {
            requestBuilder.downloadLimit(downloadLimit);
            optionsChanged = true;
        }

        boolean isUploadLimited = bandwidthLimitFragment.isUploadLimited();
        if (isUploadLimited != torrent.isUploadLimited()) {
            requestBuilder.uploadLimited(isUploadLimited);
            optionsChanged = true;
        }

        long uploadLimit = bandwidthLimitFragment.getUploadLimit();
        if (uploadLimit != torrent.getUploadLimit()) {
            requestBuilder.uploadLimit(uploadLimit);
            optionsChanged = true;
        }

        return optionsChanged ? Optional.of(requestBuilder.build()) : Optional.<TorrentSetRequest>absent();
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
                    updateUi();
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
