package net.yupol.transmissionremote.app.server;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import net.yupol.transmissionremote.app.OnBackPressedListener;
import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.utils.IconUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class ServerDetailsFragment extends Fragment implements OnBackPressedListener {

    public static final String ARGUMENT_SERVER = "argument_server";

    private static final String TAG = ServerDetailsFragment.class.getSimpleName();
    private static final int DEFAULT_PORT = 9091;
    private static final int DEFAULT_HTTPS_PORT = 443;
    private static final String KEY_IS_AUTH_ENABLED = "key_is_auth_enabled";
    private static final String CHARS_TO_STRIP_RPC = "/";
    private static final String PROTOCOL_HTTP = "http";
    private static final String PROTOCOL_HTTPS = "https";
    private static final String[] PROTOCOLS = { PROTOCOL_HTTP, PROTOCOL_HTTPS };

    private boolean isAuthEnabled = false;

    private EditText serverNameEdit;
    private Spinner protocolSpinner;
    private EditText hostNameEdit;
    private EditText portNumberEdit;
    private CheckBox selfSignedSslCheckbox;
    private CheckBox authCheckBox;
    private EditText userNameEdit;
    private EditText passwordEdit;
    private EditText rpcUrlEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(getServerArgument() != null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.server_details_fragment, container, false);

        serverNameEdit = view.findViewById(R.id.server_name_edit_text);
        protocolSpinner = view.findViewById(R.id.protocol_spinner);
        hostNameEdit = view.findViewById(R.id.host_edit_text);
        portNumberEdit = view.findViewById(R.id.port_edit_text);
        selfSignedSslCheckbox = view.findViewById(R.id.self_signed_ssl_checkbox);
        authCheckBox = view.findViewById(R.id.aunthentication_checkbox);
        userNameEdit = view.findViewById(R.id.user_name_edit_text);
        passwordEdit = view.findViewById(R.id.password_edit_text);
        rpcUrlEdit = view.findViewById(R.id.rpc_url_edit_text);
        Button defaultRpcUrlBtn = view.findViewById(R.id.default_rpc_url_button);
        defaultRpcUrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rpcUrlEdit.setText(Server.DEFAULT_RPC_URL);
            }
        });

        ArrayAdapter<String> protocolAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, PROTOCOLS);
        protocolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        protocolSpinner.setAdapter(protocolAdapter);

        portNumberEdit.setFilters(new InputFilter[]{PortNumberFilter.instance()});

        authCheckBox.setChecked(isAuthEnabled);
        updateAuth(isAuthEnabled);

        authCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAuth(authCheckBox.isChecked());
            }
        });

        final Server server = getServerArgument();
        rpcUrlEdit.setText(server != null ? server.getRpcUrl() : Server.DEFAULT_RPC_URL);

        serverNameEdit.addTextChangedListener(new ClearErrorTextWatcher(serverNameEdit));
        hostNameEdit.addTextChangedListener(new ClearErrorTextWatcher(hostNameEdit));
        portNumberEdit.addTextChangedListener(new ClearErrorTextWatcher(portNumberEdit));

        protocolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isHttps = PROTOCOL_HTTPS.equals(protocolSpinner.getSelectedItem());
                selfSignedSslCheckbox.setEnabled(isHttps);
                if (server == null) { // only if creating new server
                    int port = getUiPort();
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

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.server_details_menu, menu);
        IconUtils.setMenuIcon(getActivity(), menu, R.id.action_remove, GoogleMaterial.Icon.gmd_delete);
        IconUtils.setMenuIcon(getActivity(), menu, R.id.action_save, GoogleMaterial.Icon.gmd_save);
    }

    @Override
    public boolean onBackPressed() {
        if (hasChanges()) {
            askForSave();
            return true;
        }
        return false;
    }



    public Server getNewServer() {
        if (!checkValidity())
            return null;

        Server server;
        if (isAuthEnabled) {
            server = new Server(getUiName(), getUiHost(), getUiPort(), getUiUserName(), getUiPassword());
        } else {
            server = new Server(getUiName(), getUiHost(), getUiPort());
        }
        server.setRpcUrl(getUiRpcUrl());
        server.setUseHttps(getUiUseHttps());
        server.setTrustSelfSignedSslCert(getUiTrustSelfSignedCert());

        return server;
    }

    public Server getServerArgument() {
        Server server = null;
        if (getArguments() != null)
            server = getArguments().getParcelable(ARGUMENT_SERVER);
        return server;
    }

    public void saveServer() {
        Server server = getServerArgument();
        if (server == null) {
            throw new IllegalStateException("No server argument, can't save server");
        }

        if (!checkValidity()) return;

        server.setName(getUiName());
        server.setHost(getUiHost());
        server.setPort(getUiPort());
        server.setAuthenticationEnabled(isAuthEnabled);
        server.setUserName(getUiUserName());
        server.setPassword(getUiPassword());
        server.setRpcUrl(getUiRpcUrl());
        server.setUseHttps(getUiUseHttps());
        server.setTrustSelfSignedSslCert(getUiTrustSelfSignedCert());

        // clear redirect location after each sever settings change
        server.setRedirectLocation(null);
    }

    public boolean hasChanges() {
        Server server = getServerArgument();
        if (server == null) {
            return true;
        }
        if (!getUiName().equals(server.getName()))
            return true;
        if (!getUiHost().equals(server.getHost()))
            return true;
        if (getUiPort() != server.getPort())
            return true;
        if (isAuthEnabled != server.isAuthenticationEnabled())
            return true;
        if (!getUiUserName().equals(Strings.nullToEmpty(server.getUserName())))
            return true;
        if (!getUiPassword().equals(Strings.nullToEmpty(server.getPassword())))
            return true;
        if (!getUiRpcUrl().equals(server.getRpcUrl()))
            return true;
        if (getUiUseHttps() != server.useHttps())
            return true;
        return getUiTrustSelfSignedCert() != server.getTrustSelfSignedSslCert();
    }

    private void discardChanges() {
        Server server = getServerArgument();
        if (server == null) return;
        if (!getUiName().equals(server.getName())) {
            serverNameEdit.setText(server.getName());
        }
        if (!getUiHost().equals(server.getHost())) {
            hostNameEdit.setText(server.getHost());
        }
        if (getUiPort() != server.getPort()) {
            portNumberEdit.setText(String.valueOf(server.getPort()));
        }
        if (isAuthEnabled != server.isAuthenticationEnabled()) {
            isAuthEnabled = server.isAuthenticationEnabled();
            authCheckBox.setChecked(isAuthEnabled);
        }
        if (!getUiUserName().equals(Strings.nullToEmpty(server.getUserName()))) {
            userNameEdit.setText(Strings.nullToEmpty(server.getUserName()));
        }
        if (!getUiPassword().equals(Strings.nullToEmpty(server.getPassword()))) {
            passwordEdit.setText(Strings.nullToEmpty(server.getPassword()));
        }
        if (!getUiRpcUrl().equals(server.getRpcUrl())) {
            rpcUrlEdit.setText(server.getRpcUrl());
        }
        if (getUiUseHttps() != server.useHttps()) {
            protocolSpinner.setSelection(ArrayUtils.indexOf(PROTOCOLS, server.useHttps() ? PROTOCOL_HTTPS : PROTOCOL_HTTP));
        }
        if (getUiTrustSelfSignedCert() != server.getTrustSelfSignedSslCert()) {
            selfSignedSslCheckbox.setChecked(server.getTrustSelfSignedSslCert());
        }
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
            rpcUrlEdit.setText(Server.DEFAULT_RPC_URL);
        } else {
            serverNameEdit.setText(server.getName());
            protocolSpinner.setSelection(server.useHttps() ? 1 : 0);
            hostNameEdit.setText(server.getHost());
            portNumberEdit.setText(String.valueOf(server.getPort()));
            selfSignedSslCheckbox.setChecked(server.getTrustSelfSignedSslCert());
            updateAuth(server.isAuthenticationEnabled());
            userNameEdit.setText(server.getUserName());
            passwordEdit.setText(server.getPassword());
            rpcUrlEdit.setText(server.getRpcUrl());
        }
    }

    private void askForSave() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.save_changes_question)
                .setPositiveButton(R.string.save_changes_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveServer();
                        getActivity().onBackPressed();
                    }
                })
                .setNegativeButton(R.string.save_changes_discard, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        discardChanges();
                        getActivity().onBackPressed();
                    }
        }).create().show();
    }

    private String getUiName() {
        return serverNameEdit.getText().toString().trim();
    }

    private String getUiHost() {
        String host = hostNameEdit.getText().toString().trim();
        return host.replaceFirst("(?i)^http(s)?://", "");
    }

    private int getUiPort() {
        try {
            return Integer.parseInt(portNumberEdit.getText().toString());
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
            return false;
        }
        if (TextUtils.getTrimmedLength(hostNameEdit.getText()) == 0) {
            hostNameEdit.setError(getString(R.string.host_name_error_message));
            return false;
        }
        if (TextUtils.getTrimmedLength(portNumberEdit.getText()) == 0) {
            portNumberEdit.setError(getString(R.string.port_number_error_message));
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

        public ClearErrorTextWatcher(EditText editText) {
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
}
