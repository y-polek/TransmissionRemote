package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.TransferPriority;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;

public class OptionsPageFragment extends BasePageFragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = OptionsPageFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.torrent_details_options_page_fragment, container, false);
        Spinner prioritySpinner = (Spinner) view.findViewById(R.id.priority_spinner);
        SpinnerAdapter priorityAdapter = new TransferPrioritySpinnerAdapter();
        prioritySpinner.setAdapter(priorityAdapter);

        TransferPriority priority = getTorrent().getTransferPriority();
        prioritySpinner.setSelection(priority.ordinal());

        prioritySpinner.setOnItemSelectedListener(this);


        return view;
    }

    @Override
    public int getPageTitleRes() {
        return R.string.options;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (getActivity() instanceof BaseSpiceActivity) {
            TransferPriority priority = (TransferPriority) parent.getItemAtPosition(position);
            TransportManager tm = ((BaseSpiceActivity) getActivity()).getTransportManager();
            tm.doRequest(TorrentSetRequest.builder(getTorrent().getId()).transferPriority(priority).build(), null);
        } else {
            throw new RuntimeException("OptionsPageFragment should be used with BaseSpiceActivity.");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
