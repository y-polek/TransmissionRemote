package net.yupol.transmissionremote.app.server;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.domain.model.Server;
import net.yupol.transmissionremote.domain.repository.ServerRepository;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;

public class ServerDetailsFragment extends Fragment {

    private static final String KEY_SERVER_NAME = "key_server_name";

    private static final int DEFAULT_PORT = 9091;
    private static final int DEFAULT_HTTPS_PORT = 443;
    private static final String DEFAULT_RPC_URL = "transmission/rpc";
    private static final String KEY_IS_AUTH_ENABLED = "key_is_auth_enabled";
    private static final String CHARS_TO_STRIP_RPC = "/";
    private static final String PROTOCOL_HTTP = "http";
    private static final String PROTOCOL_HTTPS = "https";
    private static final String[] PROTOCOLS = { PROTOCOL_HTTP, PROTOCOL_HTTPS };

    private boolean isAuthEnabled = false;

    @BindView(R.id.server_name_edit_text) EditText serverNameEdit;
    @BindView(R.id.protocol_spinner) Spinner protocolSpinner;
    @BindView(R.id.host_edit_text) EditText hostNameEdit;
    @BindView(R.id.port_edit_text) EditText portNumberEdit;
    @BindView(R.id.self_signed_ssl_checkbox) CheckBox selfSignedSslCheckbox;
    @BindView(R.id.aunthentication_checkbox) CheckBox authCheckBox;
    @BindView(R.id.user_name_edit_text) EditText userNameEdit;
    @BindView(R.id.password_edit_text) EditText passwordEdit;
    @BindView(R.id.rpc_url_edit_text) EditText rpcUrlEdit;

    @Inject ServerRepository repo;

    @Nullable private Server server;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        TransmissionRemote.getInstance().appComponent().inject(this);
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString(KEY_SERVER_NAME);
            if (name != null) {
                server = repo.servers()
                        .flatMap(Observable::fromIterable)
                        .filter(server -> name.equals(server.name))
                        .blockingFirst();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.server_details_fragment, container, false);
        ButterKnife.bind(this, view);

        ArrayAdapter<String> protocolAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, PROTOCOLS);
        protocolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        protocolSpinner.setAdapter(protocolAdapter);

        portNumberEdit.setFilters(new InputFilter[]{PortNumberFilter.instance()});

        authCheckBox.setChecked(isAuthEnabled);
        updateAuth(isAuthEnabled);

        authCheckBox.setOnClickListener(view1 -> updateAuth(authCheckBox.isChecked()));

        rpcUrlEdit.setText(server != null ? server.rpcPath : DEFAULT_RPC_URL);

        serverNameEdit.addTextChangedListener(new ClearErrorTextWatcher(serverNameEdit));
        hostNameEdit.addTextChangedListener(new ClearErrorTextWatcher(hostNameEdit));
        portNumberEdit.addTextChangedListener(new ClearErrorTextWatcher(portNumberEdit));

        protocolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isHttps = PROTOCOL_HTTPS.equals(protocolSpinner.getSelectedItem());
                selfSignedSslCheckbox.setEnabled(isHttps);
                if (server == null) { // only if creating new server
                    Integer uiPort = getUiPort();
                    int port = uiPort != null ? uiPort : 0;
                    if (isHttps) {
                        if (port == DEFAULT_PORT)
                            portNumberEdit.setText(String.valueOf(DEFAULT_HTTPS_PORT));
                    } else {
                        if (port == DEFAULT_HTTPS_PORT)
                            portNumberEdit.setText(String.valueOf(DEFAULT_PORT));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        updateUI(server);
        if (server != null) {
            checkValidity();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_AUTH_ENABLED, isAuthEnabled);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_IS_AUTH_ENABLED)) {
            isAuthEnabled = savedInstanceState.getBoolean(KEY_IS_AUTH_ENABLED);
            updateAuth(isAuthEnabled);
        }
    }

    @OnClick(R.id.default_rpc_url_button)
    void onDefaultRpcUrlClicked() {
        rpcUrlEdit.setText(DEFAULT_RPC_URL);
    }

    public Server getNewServer() {
        if (!checkValidity())
            return null;

        String name = getUiName();
        String host = getUiHost();
        Integer port = getUiPort();
        boolean https = getUiUseHttps();
        boolean trustSelfSignedCert = getUiTrustSelfSignedCert();
        String rpcPath = getUiRpcUrl();
        String login = isAuthEnabled ? getUiUserName() : null;
        String password = isAuthEnabled ? getUiPassword() : null;

        return new Server(name, host, port, https, login, password, rpcPath, trustSelfSignedCert);
    }

    public boolean hasChanges() {
        if (server == null) {
            return true;
        }
        if (!getUiName().equals(server.name))
            return true;
        if (!getUiHost().equals(server.host))
            return true;

        int uiPort = getUiPort() != null ? getUiPort() : 0;
        int port = server.port != null ? server.port : 0;
        if (uiPort != port)
            return true;
        if (isAuthEnabled != server.authEnabled())
            return true;
        if (!getUiUserName().equals(Strings.nullToEmpty(server.login)))
            return true;
        if (!getUiPassword().equals(Strings.nullToEmpty(server.password)))
            return true;
        if (!getUiRpcUrl().equals(server.rpcPath))
            return true;
        if (getUiUseHttps() != server.https)
            return true;
        return getUiTrustSelfSignedCert() != server.trustSelfSignedSslCert;
    }

    private void updateUI(Server server) {
        if (server == null) {
            serverNameEdit.setText("");
            protocolSpinner.setSelection(0);
            hostNameEdit.setText("");
            portNumberEdit.setText(String.valueOf(DEFAULT_PORT));
            selfSignedSslCheckbox.setChecked(false);
            userNameEdit.setText("");
            passwordEdit.setText("");
            rpcUrlEdit.setText(DEFAULT_RPC_URL);
        } else {
            serverNameEdit.setText(server.name);
            protocolSpinner.setSelection(server.https ? 1 : 0);
            hostNameEdit.setText(server.host);
            portNumberEdit.setText(server.port != null ? String.valueOf(server.port) : "");
            selfSignedSslCheckbox.setChecked(server.trustSelfSignedSslCert);
            updateAuth(server.authEnabled());
            userNameEdit.setText(server.login != null ? server.login : "");
            passwordEdit.setText(server.password != null ? server.password : "");
            rpcUrlEdit.setText(server.rpcPath);
        }
    }

    private String getUiName() {
        return serverNameEdit.getText().toString().trim();
    }

    private String getUiHost() {
        String host = hostNameEdit.getText().toString().trim();
        return host.replaceFirst("(?i)^http(s)?://", "");
    }

    private boolean isHostValid() {
        String host = getUiHost();
        return InternetDomainName.isValid(host) || InetAddresses.isInetAddress(host);
    }

    @Nullable
    private Integer getUiPort() {
        String portStr = portNumberEdit.getText().toString().trim();
        if (portStr.isEmpty()) return null;

        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            return getUiUseHttps() ? DEFAULT_HTTPS_PORT : DEFAULT_PORT;
        }
    }

    private String getUiUserName() {
        return userNameEdit.getText().toString().trim();
    }

    private String getUiPassword() {
        return passwordEdit.getText().toString().trim();
    }

    private String getUiRpcUrl() {
        return StringUtils.stripEnd(StringUtils.stripStart(rpcUrlEdit.getText().toString(), CHARS_TO_STRIP_RPC), CHARS_TO_STRIP_RPC);
    }

    private boolean getUiUseHttps() {
        return PROTOCOL_HTTPS.equals(protocolSpinner.getSelectedItem());
    }

    private boolean getUiTrustSelfSignedCert() {
        return selfSignedSslCheckbox.isChecked();
    }

    private boolean checkValidity() {
        if (TextUtils.getTrimmedLength(serverNameEdit.getText()) == 0) {
            serverNameEdit.setError(getString(R.string.server_name_error_message));
            serverNameEdit.requestFocus();
            return false;
        }
        if (TextUtils.getTrimmedLength(hostNameEdit.getText()) == 0) {
            hostNameEdit.setError(getString(R.string.host_name_empty_message));
            hostNameEdit.requestFocus();
            return false;
        } else if (!isHostValid()) {
            hostNameEdit.setError(getString(R.string.host_name_invalid_message));
            hostNameEdit.requestFocus();
            return false;
        }

        return true;
    }

    private void updateAuth(boolean isEnabled) {
        isAuthEnabled = isEnabled;
        authCheckBox.setChecked(isEnabled);
        userNameEdit.setEnabled(isEnabled);
        passwordEdit.setEnabled(isEnabled);
    }

    private static class ClearErrorTextWatcher implements TextWatcher {

        private EditText editText;

        ClearErrorTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable editable) {
            editText.setError(null);
        }
    }

    public static ServerDetailsFragment edit(String serverName) {
        ServerDetailsFragment fragment = new ServerDetailsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SERVER_NAME, serverName);
        fragment.setArguments(args);
        return fragment;
    }

    public static ServerDetailsFragment create() {
        return new ServerDetailsFragment();
    }
}
