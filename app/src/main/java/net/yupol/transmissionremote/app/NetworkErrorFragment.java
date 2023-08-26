package net.yupol.transmissionremote.app;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.mikepenz.iconics.IconicsColor;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.IconicsSize;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;

import net.yupol.transmissionremote.app.preferences.ServersActivity;
import net.yupol.transmissionremote.app.utils.ColorUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class NetworkErrorFragment extends Fragment {

    private static final String KEY_MESSAGE = "key_message";
    private static final String KEY_DETAILED_MESSAGE = "key_detailed_message";

    private TextView messageView;
    private TextView detailedMessageView;
    private String detailedMessage;

    private OnRefreshPressedListener listener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.network_error_fragment, container, false);

        Bundle args = getArguments();
        if (args == null || args.getString(KEY_MESSAGE) == null) {
            throw new IllegalArgumentException("Message argument must be provided");
        }

        messageView = view.findViewById(R.id.error_message);
        detailedMessageView = view.findViewById(R.id.detailed_error_text);
        detailedMessageView.setMovementMethod(LinkMovementMethod.getInstance());
        detailedMessageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyDetailedErrorMessageToClipboard();
                return true;
            }
        });

        String message = Optional.ofNullable(args.getString(KEY_MESSAGE)).orElse("");
        String detailedMessage = args.getString(KEY_DETAILED_MESSAGE);
        setErrorMessage(message, detailedMessage);

        final IconicsColor iconColor = IconicsColor.colorInt(
                ColorUtils.resolveColor(requireContext(), R.attr.colorAccent, R.color.accent)
        );
        final IconicsSize iconSize = IconicsSize.res(R.dimen.default_button_icon_size);

        Button retryBtn = view.findViewById(R.id.retry_button);
        retryBtn.setCompoundDrawables(
                new IconicsDrawable(requireContext()).icon(GoogleMaterial.Icon.gmd_refresh).color(iconColor).size(iconSize),
                null, null, null);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onRefreshPressed();
            }
        });

        Button serverSettingsBtn = view.findViewById(R.id.server_settings_button);
        serverSettingsBtn.setCompoundDrawables(
                new IconicsDrawable(requireContext()).icon(GoogleMaterial.Icon.gmd_account_circle).color(iconColor).size(iconSize),
                null, null, null);
        serverSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ServersActivity.class);
                intent.putExtra(ServersActivity.KEY_SERVER_UUID, TransmissionRemote.getApplication(requireContext()).getActiveServer().getId());
                startActivity(intent);
            }
        });

        Button networkSettingsBtn = view.findViewById(R.id.network_settings_button);
        networkSettingsBtn.setCompoundDrawables(
                new IconicsDrawable(requireContext()).icon(GoogleMaterial.Icon.gmd_network_wifi).color(iconColor).size(iconSize),
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        FragmentActivity activity = getActivity();
        if (activity instanceof OnRefreshPressedListener) {
            listener = (OnRefreshPressedListener) activity;
        }
    }

    public void setErrorMessage(@NonNull String message, @Nullable String detailedMessage) {
        messageView.setText(message);

        if (StringUtils.equals(this.detailedMessage, detailedMessage)) return;

        this.detailedMessage = detailedMessage;
        detailedMessageView.setVisibility(detailedMessage != null ? VISIBLE : GONE);
        if (detailedMessage != null) {
            detailedMessageView.setText(Html.fromHtml(detailedMessage, Html.FROM_HTML_MODE_COMPACT));
        }
    }

    private void copyDetailedErrorMessageToClipboard() {
        if (detailedMessage == null || detailedMessage.isEmpty()) return;

        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Error message", detailedMessage);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), R.string.copied, Toast.LENGTH_SHORT).show();
        }
    }

    public static NetworkErrorFragment newInstance(@NonNull String message, @Nullable String detailedMessage) {
        Bundle args = new Bundle();
        args.putString(KEY_MESSAGE, message);
        args.putString(KEY_DETAILED_MESSAGE, detailedMessage);
        NetworkErrorFragment fragment = new NetworkErrorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnRefreshPressedListener {
        void onRefreshPressed();
    }
}
