package net.yupol.transmissionremote.app.torrentdetails;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
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
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.TorrentDetailsOptionsPageFragmentBinding;
import net.yupol.transmissionremote.app.model.json.ServerSettings;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;
import net.yupol.transmissionremote.app.model.json.TransferPriority;
import net.yupol.transmissionremote.app.model.limitmode.IdleLimitMode;
import net.yupol.transmissionremote.app.model.limitmode.LimitMode;
import net.yupol.transmissionremote.app.model.limitmode.RatioLimitMode;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.SessionGetRequest;
import net.yupol.transmissionremote.app.transport.request.TorrentInfoGetRequest;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;
import net.yupol.transmissionremote.app.utils.IconUtils;
import net.yupol.transmissionremote.app.utils.MinMaxTextWatcher;

public class OptionsPageFragment extends BasePageFragment implements AdapterView.OnItemSelectedListener,
        OnActivityExitingListener<TorrentSetRequest.Builder> {

    private static final String TAG = OptionsPageFragment.class.getSimpleName();

    private TransportManager transportManager;

    private ServerSettings serverSettings;

    private BandwidthLimitFragment bandwidthLimitFragment;
    private MenuItem saveMenuItem;

    private boolean viewCreated;
    private SessionGetRequest sessionGetRequest;
    private TorrentDetailsOptionsPageFragmentBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (activity instanceof BaseSpiceActivity) {
            transportManager = ((BaseSpiceActivity) activity).getTransportManager();
        }
        if (activity instanceof TorrentDetailsActivity) {
            ((TorrentDetailsActivity) activity).addOnActivityExitingListener(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (sessionGetRequest != null) {
            sessionGetRequest.cancel();
        }

        if (getActivity() instanceof TorrentDetailsActivity) {
            TorrentDetailsActivity activity = (TorrentDetailsActivity) getActivity();
            activity.removeOnActivityExitingListener(this);
            if (getTorrentInfo() != null) {
                TorrentSetRequest.Builder requestBuilder = getSaveOptionsRequestBuilder();
                if (requestBuilder.isChanged()) activity.addSaveChangesRequest(requestBuilder);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        sessionGetRequest = new SessionGetRequest();
        transportManager.doRequest(sessionGetRequest, new RequestListener<ServerSettings>() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.torrent_details_options_page_fragment, container, false);
        bandwidthLimitFragment = (BandwidthLimitFragment)
                getChildFragmentManager().findFragmentById(R.id.bandwidth_limit_fragment);

        binding.prioritySpinner.setAdapter(new TransferPrioritySpinnerAdapter());

        binding.ratioLimitSpinner.setAdapter(new RatioLimitModeAdapter());
        binding.ratioLimitEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10)} );

        binding.idleLimitSpinner.setAdapter(new IdleLimitModeAdapter());
        binding.idleLimitEdit.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(5)} );
        binding.idleLimitEdit.addTextChangedListener(new MinMaxTextWatcher(1, 0xFFFF));

        if (getTorrentInfo() == null) {
            binding.contentView.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            showContent();
            updateUi(true);
        }

        // Postpone listeners registration to avoid notifying listeners on layout.
        // http://stackoverflow.com/questions/2562248/how-to-keep-onitemselected-from-firing-off-on-a-newly-instantiated-spinner
        binding.getRoot().post(new Runnable() {
            @Override
            public void run() {
                binding.ratioLimitSpinner.setOnItemSelectedListener(OptionsPageFragment.this);
                binding.idleLimitSpinner.setOnItemSelectedListener(OptionsPageFragment.this);
            }
        });

        viewCreated = true;

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewCreated = false;
    }

    @Override
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        boolean isUpdate = getTorrentInfo() != null;
        super.setTorrentInfo(torrentInfo);
        if (viewCreated) {
            showContent();
            if (!isUpdate) {
                updateUi(true);
            } else {
                // TODO: implement UI updates
            }
        }
        if (saveMenuItem != null) {
            saveMenuItem.setEnabled(true);
        }
    }

    @Override
    public TorrentSetRequest.Builder onActivityExiting() {
        return getSaveOptionsRequestBuilder();
    }

    private void showContent() {
        binding.contentView.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void updateUi(boolean syncWithModel) {
        TorrentInfo torrentInfo = getTorrentInfo();

        binding.prioritySpinner.setSelection(torrentInfo.getTransferPriority().ordinal());

        binding.stayWithGlobalBandwidthCheckbox.setChecked(torrentInfo.isSessionLimitsHonored());

        bandwidthLimitFragment.setDownloadLimited(torrentInfo.isDownloadLimited());
        bandwidthLimitFragment.setDownloadLimit(torrentInfo.getDownloadLimit());
        bandwidthLimitFragment.setUploadLimited(torrentInfo.isUploadLimited());
        bandwidthLimitFragment.setUploadLimit(torrentInfo.getUploadLimit());

        binding.ratioLimitSpinner.setSelection(torrentInfo.getSeedRatioMode().ordinal());
        binding.idleLimitSpinner.setSelection(torrentInfo.getSeedIdleMode().ordinal());
        updateSeedingLimitsUi(syncWithModel);
    }

    private void updateSeedingLimitsUi(boolean syncWithModel) {
        TorrentInfo torrentInfo = getTorrentInfo();

        switch (getRatioLimitMode()) {
            case STOP_AT_RATIO:
                if (syncWithModel || binding.ratioLimitEdit.getText().length() == 0) {
                    binding.ratioLimitEdit.setText(String.valueOf(torrentInfo.getSeedRatioLimit()));
                }
                binding.ratioLimitEdit.setVisibility(View.VISIBLE);
                binding.ratioLimitEdit.setEnabled(true);
                binding.ratioLimitGlobalText.setVisibility(View.INVISIBLE);
                break;
            case GLOBAL_SETTINGS:
                if (serverSettings != null) {
                    String text = serverSettings.isSeedRatioLimited()
                            ? String.valueOf(serverSettings.getSeedRatioLimit())
                            : getString(R.string.disabled);
                    binding.ratioLimitGlobalText.setText(text);
                } else {
                    binding.ratioLimitGlobalText.setText(R.string.three_dots);
                }
                binding.ratioLimitEdit.setVisibility(View.INVISIBLE);
                binding.ratioLimitGlobalText.setVisibility(View.VISIBLE);
                break;
            case UNLIMITED:
                if (syncWithModel || binding.ratioLimitEdit.getText().length() == 0) {
                    binding.ratioLimitEdit.setText(String.valueOf(torrentInfo.getSeedRatioLimit()));
                }
                binding.ratioLimitEdit.setVisibility(View.VISIBLE);
                binding.ratioLimitEdit.setEnabled(false);
                binding.ratioLimitGlobalText.setVisibility(View.INVISIBLE);
                break;
        }

        switch (getIdleLimitMode()) {
            case STOP_WHEN_INACTIVE:
                if (syncWithModel || binding.idleLimitEdit.getText().length() == 0) {
                    binding.idleLimitEdit.setText(String.valueOf(torrentInfo.getSeedIdleLimit()));
                }
                binding.idleLimitEdit.setVisibility(View.VISIBLE);
                binding.idleLimitEdit.setEnabled(true);
                binding.idleLimitGlobalText.setVisibility(View.INVISIBLE);
                break;
            case GLOBAL_SETTINGS:
                if (serverSettings != null) {
                    String text = serverSettings.isSeedIdleLimited()
                            ? String.valueOf(serverSettings.getSeedIdleLimit())
                            : getString(R.string.disabled);
                    binding.idleLimitGlobalText.setText(text);
                }
                binding.idleLimitEdit.setVisibility(View.INVISIBLE);
                binding.idleLimitGlobalText.setVisibility(View.VISIBLE);
                break;
            case UNLIMITED:
                if (syncWithModel || binding.idleLimitEdit.getText().length() == 0) {
                    binding.idleLimitEdit.setText(String.valueOf(torrentInfo.getSeedIdleLimit()));
                }
                binding.idleLimitEdit.setVisibility(View.VISIBLE);
                binding.idleLimitEdit.setEnabled(false);
                binding.idleLimitGlobalText.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private RatioLimitMode getRatioLimitMode() {
        return (RatioLimitMode) binding.ratioLimitSpinner.getSelectedItem();
    }

    private double getRatioLimit() {
        try {
            return Double.parseDouble(binding.ratioLimitEdit.getText().toString());
        } catch (NumberFormatException e) {
            return getTorrentInfo().getSeedRatioLimit();
        }
    }

    private IdleLimitMode getIdleLimitMode() {
        return (IdleLimitMode) binding.idleLimitSpinner.getSelectedItem();
    }

    private int getIdleLimit() {
        try {
            return Integer.parseInt(binding.idleLimitEdit.getText().toString());
        } catch (NumberFormatException e) {
            return getTorrentInfo().getSeedIdleLimit();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.torrent_options_menu, menu);
        saveMenuItem = menu.findItem(R.id.action_save);
        saveMenuItem.setEnabled(getTorrentInfo() != null);
        IconUtils.setMenuIcon(getContext(), saveMenuItem, GoogleMaterial.Icon.gmd_save);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateSeedingLimitsUi(false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * @return optional which contain {@link TorrentSetRequest} or contain nothing if no options changed
     */
    public TorrentSetRequest.Builder getSaveOptionsRequestBuilder() {
        TorrentInfo torrentInfo = getTorrentInfo();
        TorrentSetRequest.Builder saveChangesRequestBuilder = TorrentSetRequest.builder(torrentInfo.getId());

        TransferPriority priority = (TransferPriority) binding.prioritySpinner.getSelectedItem();
        if (priority != torrentInfo.getTransferPriority()) {
            saveChangesRequestBuilder.transferPriority(priority);
        }

        boolean honorsSessionLimits = binding.stayWithGlobalBandwidthCheckbox.isChecked();
        if (honorsSessionLimits != torrentInfo.isSessionLimitsHonored()) {
            saveChangesRequestBuilder.honorsSessionLimits(honorsSessionLimits);
        }

        boolean isDownloadLimited = bandwidthLimitFragment.isDownloadLimited();
        if (isDownloadLimited != torrentInfo.isDownloadLimited()) {
            saveChangesRequestBuilder.downloadLimited(isDownloadLimited);
        }

        long downloadLimit = bandwidthLimitFragment.getDownloadLimit();
        if (downloadLimit != torrentInfo.getDownloadLimit()) {
            saveChangesRequestBuilder.downloadLimit(downloadLimit);
        }

        boolean isUploadLimited = bandwidthLimitFragment.isUploadLimited();
        if (isUploadLimited != torrentInfo.isUploadLimited()) {
            saveChangesRequestBuilder.uploadLimited(isUploadLimited);
        }

        long uploadLimit = bandwidthLimitFragment.getUploadLimit();
        if (uploadLimit != torrentInfo.getUploadLimit()) {
            saveChangesRequestBuilder.uploadLimit(uploadLimit);
        }

        LimitMode ratioLimitMode = getRatioLimitMode();
        if (ratioLimitMode != torrentInfo.getSeedRatioMode()) {
            saveChangesRequestBuilder.seedRatioMode(ratioLimitMode);
        }

        double ratioLimit = getRatioLimit();
        if (ratioLimit != torrentInfo.getSeedRatioLimit()) {
            saveChangesRequestBuilder.seedRatioLimit(ratioLimit);
        }

        LimitMode idleLimitMode = getIdleLimitMode();
        if (idleLimitMode != torrentInfo.getSeedIdleMode()) {
            saveChangesRequestBuilder.seedIdleMode(idleLimitMode);
        }

        int idleLimit = getIdleLimit();
        if (idleLimit != torrentInfo.getSeedIdleLimit()) {
            saveChangesRequestBuilder.seedIdleLimit(idleLimit);
        }

        return saveChangesRequestBuilder;
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
        transportManager.doRequest(new TorrentInfoGetRequest(getTorrent().getId()), new RequestListener<TorrentInfo>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Toast.makeText(getActivity(), getString(R.string.options_update_failed), Toast.LENGTH_LONG).show();
                saveFinished();
            }

            @Override
            public void onRequestSuccess(TorrentInfo torrentInfo) {
                setTorrentInfo(torrentInfo);
                Toast.makeText(getActivity(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveStarted() {
        saveMenuItem.setEnabled(false);
    }

    private void saveFinished() {
        saveMenuItem.setEnabled(true);
    }
}
