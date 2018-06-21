package net.yupol.transmissionremote.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import net.yupol.transmissionremote.app.preferences.ServersActivity;
import net.yupol.transmissionremote.app.utils.ColorUtils;

public class NetworkErrorFragment extends Fragment {

    private static final String KEY_SERVER_ID = "key_server_id";
    private static final String KEY_MESSAGE = "key_message";

    private String serverId;
    private String message;

    private TextView messageView;

    private OnRefreshPressedListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            serverId = args.getString(KEY_SERVER_ID);
            message = args.getString(KEY_MESSAGE);
        }
        if (serverId == null || message == null) {
            throw new IllegalArgumentException("Server ID nad message must be passed as an argument");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.network_error_fragment, container, false);

        messageView = view.findViewById(R.id.error_message);
        messageView.setText(message);

        int iconColor = ColorUtils.resolveColor(requireContext(), R.attr.colorAccent, R.color.accent);

        Button retryBtn = view.findViewById(R.id.retry_button);
        retryBtn.setCompoundDrawables(
                new IconicsDrawable(requireContext()).icon(GoogleMaterial.Icon.gmd_refresh).color(iconColor).sizeRes(R.dimen.default_button_icon_size),
                null, null, null);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onRefreshPressed();
            }
        });

        Button serverSettingsBtn = view.findViewById(R.id.server_settings_button);
        serverSettingsBtn.setCompoundDrawables(
                new IconicsDrawable(requireContext()).icon(GoogleMaterial.Icon.gmd_account_circle).color(iconColor).sizeRes(R.dimen.default_button_icon_size),
                null, null, null);
        serverSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ServersActivity.class);
                intent.putExtra(ServersActivity.KEY_SERVER_UUID, serverId);
                startActivity(intent);
            }
        });

        Button networkSettingsBtn = view.findViewById(R.id.network_settings_button);
        networkSettingsBtn.setCompoundDrawables(
                new IconicsDrawable(requireContext()).icon(GoogleMaterial.Icon.gmd_network_wifi).color(iconColor).sizeRes(R.dimen.default_button_icon_size),
                null, null, null);
        networkSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        FragmentActivity activity = getActivity();
        if (activity instanceof OnRefreshPressedListener) {
            listener = (OnRefreshPressedListener) activity;
        }
    }

    public void setErrorMessage(String message) {
        messageView.setText(message);
    }

    public static NetworkErrorFragment newInstance(String serverId, String message) {
        NetworkErrorFragment fragment = new NetworkErrorFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SERVER_ID, serverId);
        args.putString(KEY_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnRefreshPressedListener {
        void onRefreshPressed();
    }
}
