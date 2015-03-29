package net.yupol.transmissionremote.app.server;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.api.client.repackaged.com.google.common.base.Strings;

import net.yupol.transmissionremote.app.R;

public class ServerDetailsFragment extends Fragment {

    public static final String ARGUMENT_SERVER = "argument_server";

    private static final String TAG = ServerDetailsFragment.class.getSimpleName();
    private static final int DEFAULT_PORT = 9091;

    private boolean isAuthEnabled = false;

    private EditText serverNameEdit;
    private EditText hostNameEdit;
    private EditText portNumberEdit;
    private CheckBox authCheckBox;
    private EditText userNameEdit;
    private EditText passwordEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.server_details_fragment, container, false);

        serverNameEdit = (EditText) view.findViewById(R.id.server_name_edit_text);
        hostNameEdit = (EditText) view.findViewById(R.id.host_edit_text);
        portNumberEdit = (EditText) view.findViewById(R.id.port_edit_text);
        authCheckBox = (CheckBox) view.findViewById(R.id.aunthentication_checkbox);
        userNameEdit = (EditText) view.findViewById(R.id.user_name_edit_text);
        passwordEdit = (EditText) view.findViewById(R.id.password_edit_text);

        portNumberEdit.setFilters(new InputFilter[]{PortNumberFilter.instance()});

        authCheckBox.setChecked(isAuthEnabled);
        updateAuth(isAuthEnabled);

        authCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAuthEnabled = authCheckBox.isChecked();
                updateAuth(isAuthEnabled);
            }
        });

        serverNameEdit.addTextChangedListener(new ClearErrorTextWatcher(serverNameEdit));
        hostNameEdit.addTextChangedListener(new ClearErrorTextWatcher(hostNameEdit));
        portNumberEdit.addTextChangedListener(new ClearErrorTextWatcher(portNumberEdit));

        updateUI(getServerArgument());

        return view;
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
        server.setName(getUiName());
        server.setHost(getUiHost());
        server.setPort(getUiPort());
        server.setAuthenticationEnabled(isAuthEnabled);
        server.setUserName(getUiUserName());
        server.setPassword(getUiPassword());
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
        return false;
    }

    private void updateUI(Server server) {
        if (server == null) {
            serverNameEdit.setText("");
            hostNameEdit.setText("");
            portNumberEdit.setText(String.valueOf(DEFAULT_PORT));
            updateAuth(false);
            userNameEdit.setText("");
            passwordEdit.setText("");
        } else {
            serverNameEdit.setText(server.getName());
            hostNameEdit.setText(server.getHost());
            portNumberEdit.setText(String.valueOf(server.getPort()));
            updateAuth(server.isAuthenticationEnabled());
            userNameEdit.setText(server.getUserName());
            passwordEdit.setText(server.getPassword());
        }
    }

    private String getUiName() {
        return serverNameEdit.getText().toString();
    }

    private String getUiHost() {
        return hostNameEdit.getText().toString();
    }

    private int getUiPort() {
        return Integer.parseInt(portNumberEdit.getText().toString());
    }

    private String getUiUserName() {
        return userNameEdit.getText().toString();
    }

    private String getUiPassword() {
        return passwordEdit.getText().toString();
    }

    private boolean checkValidity() {
        if (serverNameEdit.getText().length() == 0) {
            serverNameEdit.setError(getString(R.string.server_name_error_message));
            return false;
        }
        if (hostNameEdit.getText().length() == 0) {
            hostNameEdit.setError(getString(R.string.host_name_error_message));
            return false;
        }
        if (portNumberEdit.getText().length() == 0) {
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
