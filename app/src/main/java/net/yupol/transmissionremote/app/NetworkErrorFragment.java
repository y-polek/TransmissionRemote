package net.yupol.transmissionremote.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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

    public static final String KEY_MESSAGE = "key_message";

    private TextView messageView;

    private OnRefreshPressedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.network_error_fragment, container, false);

        messageView = (TextView) view.findViewById(R.id.error_message);
        messageView.setText(getArguments().getString(KEY_MESSAGE));

        int iconColor = ColorUtils.resolveColor(getContext(), android.R.attr.textColorSecondary, R.color.text_secondary);

        Button retryBtn = (Button) view.findViewById(R.id.retry_button);
        retryBtn.setCompoundDrawables(
                new IconicsDrawable(getContext()).icon(GoogleMaterial.Icon.gmd_refresh).color(iconColor).sizeRes(R.dimen.default_button_icon_size),
                null, null, null);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onRefreshPressed();
            }
        });

        Button serverSettingsBtn = (Button) view.findViewById(R.id.server_settings_button);
        serverSettingsBtn.setCompoundDrawables(
                new IconicsDrawable(getContext()).icon(GoogleMaterial.Icon.gmd_account_circle).color(iconColor).sizeRes(R.dimen.default_button_icon_size),
                null, null, null);
        serverSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ServersActivity.class);
                intent.putExtra(ServersActivity.KEY_SERVER_UUID, TransmissionRemote.getApplication(getContext()).getActiveServer().getId());
                startActivity(intent);
            }
        });

        Button networkSettingsBtn = (Button) view.findViewById(R.id.network_settings_button);
        networkSettingsBtn.setCompoundDrawables(
                new IconicsDrawable(getContext()).icon(GoogleMaterial.Icon.gmd_network_wifi).color(iconColor).sizeRes(R.dimen.default_button_icon_size),
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

    public interface OnRefreshPressedListener {
        void onRefreshPressed();
    }
}
