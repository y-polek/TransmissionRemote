package net.yupol.transmissionremote.app.torrentdetails;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.TransferPriority;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.Request;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;

public class OptionsPageFragment extends BasePageFragment implements AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener, BandwidthLimitFragment.OnLimitChanged {

    private static final String TAG = OptionsPageFragment.class.getSimpleName();

    private BandwidthLimitFragment bandwidthLimitFragment;
    private TransportManager transportManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.torrent_details_options_page_fragment, container, false);

        Torrent torrent = getTorrent();

        Spinner prioritySpinner = (Spinner) view.findViewById(R.id.priority_spinner);
        prioritySpinner.setAdapter(new TransferPrioritySpinnerAdapter());
        prioritySpinner.setSelection(torrent.getTransferPriority().ordinal());
        prioritySpinner.setOnItemSelectedListener(this);

        CheckBox stayWithGlobalCheckbox = (CheckBox) view.findViewById(R.id.stay_with_global_bandwidth_checkbox);
        stayWithGlobalCheckbox.setChecked(torrent.isSessionLimitsHonored());
        stayWithGlobalCheckbox.setOnCheckedChangeListener(this);

        bandwidthLimitFragment = (BandwidthLimitFragment)
                getChildFragmentManager().findFragmentById(R.id.torrent_bandwidth_limit_fragment);
        bandwidthLimitFragment.setEnabled(!stayWithGlobalCheckbox.isChecked());
        bandwidthLimitFragment.setDownloadLimited(torrent.isDownloadLimited());
        bandwidthLimitFragment.setDownloadLimit(torrent.getDownloadLimit());
        bandwidthLimitFragment.setUploadLimited(torrent.isUploadLimited());
        bandwidthLimitFragment.setUploadLimit(torrent.getUploadLimit());
        bandwidthLimitFragment.setOnLimitChangedListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseSpiceActivity) {
            transportManager = ((BaseSpiceActivity) activity).getTransportManager();
        }
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
        TransferPriority priority = (TransferPriority) parent.getItemAtPosition(position);
        sendRequest(TorrentSetRequest.builder(getTorrent().getId()).transferPriority(priority).build());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * Listens for "Stay with the global bandwidth limits" checkbox changes.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        bandwidthLimitFragment.setEnabled(!isChecked);
        sendRequest(TorrentSetRequest.builder(getTorrent().getId()).honorsSessionLimits(isChecked).build());
    }

    @Override
    public void onDownloadLimitedChanged(boolean isLimited) {
        sendRequest(TorrentSetRequest.builder(getTorrent().getId()).downloadLimited(isLimited).build());
    }

    @Override
    public void onUploadLimitedChanged(boolean isLimited) {
        sendRequest(TorrentSetRequest.builder(getTorrent().getId()).uploadLimited(isLimited).build());
    }

    private void sendRequest(Request<?> request) {
        if (transportManager == null)
            throw new RuntimeException("OptionsPageFragment should be used with BaseSpiceActivity.");

        transportManager.doRequest(request, null);
    }
}
