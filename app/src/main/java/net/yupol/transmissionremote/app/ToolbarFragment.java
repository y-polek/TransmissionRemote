package net.yupol.transmissionremote.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.base.Strings;

import net.yupol.transmissionremote.app.preferences.ServerPreferences;
import net.yupol.transmissionremote.app.transport.Torrent;
import net.yupol.transmissionremote.app.transport.TransportThread;
import net.yupol.transmissionremote.app.transport.request.Request;
import net.yupol.transmissionremote.app.transport.request.SessionGetRequest;
import net.yupol.transmissionremote.app.transport.request.SessionSetRequest;
import net.yupol.transmissionremote.app.transport.response.Response;
import net.yupol.transmissionremote.app.transport.response.SessionGetResponse;
import net.yupol.transmissionremote.app.utils.SizeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ToolbarFragment extends Fragment {

    private static final String TAG = ToolbarFragment.class.getSimpleName();

    private TransmissionRemote app;

    private boolean speedLimitEnabled;
    private ImageButton speedLimitButton;

    private TransportThread transportThread;
    private TextView downloadRateText;
    private TextView uploadRateText;

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
    public void onDestroyView() {
        super.onDestroyView();

        if (transportThread != null)
            transportThread.quit();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        app = (TransmissionRemote) getActivity().getApplication();

        sendRequest(new SessionGetRequest());
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
            sessionArgs.put(ServerPreferences.ALT_SPEED_LIMIT_ENABLED, speedLimitEnabled);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create session arguments JSON object", e);
        }

        sendRequest(new SessionSetRequest(sessionArgs));
    }

    private TransportThread getTransportThread() {
        if (transportThread == null) {
            TransmissionRemote app = (TransmissionRemote) getActivity().getApplication();
            transportThread = new TransportThread(app.getActiveServer(), new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.obj instanceof Response) {
                        handleResponse((Response) msg.obj);
                    }
                }
            });
            transportThread.start();
        }
        return transportThread;
    }

    private void sendRequest(Request request) {
        TransportThread transportThread = getTransportThread();
        Message msg = transportThread.getHandler().obtainMessage(TransportThread.REQUEST);
        msg.obj = request;
        transportThread.getHandler().sendMessage(msg);
    }

    private void handleResponse(Response response) {
        if (response instanceof SessionGetResponse) {
            SessionGetResponse sessionGetResponse = (SessionGetResponse) response;
            speedLimitEnabled = sessionGetResponse.isAltSpeedEnabled();
            app.setSpeedLimitEnabled(speedLimitEnabled);
            updateSpeedLimitButton();
        }
    }
}
