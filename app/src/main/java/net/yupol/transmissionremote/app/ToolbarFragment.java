package net.yupol.transmissionremote.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.TransmissionRemote.OnSpeedLimitChangedListener;
import net.yupol.transmissionremote.app.model.json.ServerSettings;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.SessionSetRequest;
import net.yupol.transmissionremote.app.utils.SizeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ToolbarFragment extends Fragment {

    private static final String TAG = ToolbarFragment.class.getSimpleName();

    private TransmissionRemote app;

    private boolean speedLimitEnabled;
    private ImageButton speedLimitButton;

    private TextView downloadRateText;
    private TextView uploadRateText;

    private OnSpeedLimitChangedListener speedLimitChangedListener = new OnSpeedLimitChangedListener() {
        @Override
        public void speedLimitEnabledChanged(boolean isEnabled) {
            speedLimitEnabled = isEnabled;
            updateSpeedLimitButton();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        app = (TransmissionRemote) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.toolbar_fragment, container, false);

        speedLimitButton = (ImageButton) view.findViewById(R.id.speed_limit_button);
        speedLimitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedLimitEnabled = !speedLimitEnabled;
                app.setSpeedLimitEnabled(speedLimitEnabled);
                updateSpeedLimitButton();
                updateServerPrefs();
            }
        });

        downloadRateText = (TextView) view.findViewById(R.id.download_speed_text);
        uploadRateText = (TextView) view.findViewById(R.id.upload_speed_text);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        speedLimitEnabled = app.isSpeedLimitEnabled();
        updateSpeedLimitButton();
        app.addOnSpeedLimitEnabledChangedListener(speedLimitChangedListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        app.removeOnSpeedLimitEnabledChangedListener(speedLimitChangedListener);
    }

    public void torrentsUpdated(List<Torrent> torrents) {
        int totalDownloadRate = 0;
        int totalUploadRate = 0;
        for (Torrent torrent : torrents) {
            totalDownloadRate += torrent.getDownloadRate();
            totalUploadRate += torrent.getUploadRate();
        }

        downloadRateText.setText(speedText(totalDownloadRate));
        uploadRateText.setText(speedText(totalUploadRate));
    }

    private String speedText(long bytes) {
        return Strings.padStart(SizeUtils.displayableSize(bytes), 5, ' ') + "/s";
    }

    private void updateSpeedLimitButton() {
        int res = speedLimitEnabled ? R.drawable.turtle_blue : R.drawable.turtle;
        speedLimitButton.setImageResource(res);
    }

    private void updateServerPrefs() {

        JSONObject sessionArgs = new JSONObject();
        try {
            sessionArgs.put(ServerSettings.ALT_SPEED_LIMIT_ENABLED, speedLimitEnabled);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create session arguments JSON object", e);
        }

        getTransportManager().doRequest(new SessionSetRequest(sessionArgs), new RequestListener<Void>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d(TAG, "Failed to update session settings");
            }

            @Override
            public void onRequestSuccess(Void aVoid) {
                Log.e(TAG, "Session settings updated successfully");
            }
        });
    }

    private TransportManager getTransportManager() {
        Activity activity = getActivity();
        if (activity == null) {
            throw new IllegalStateException("getTransportManager() should be called after onAttach()");
        }

        if (!(activity instanceof BaseSpiceActivity)) {
            throw new IllegalStateException("ToolbarFragment should be used with Activities extended from BaseSpiceActivity to be able to use transport system");
        }

        return ((BaseSpiceActivity) activity).getTransportManager();
    }
}
