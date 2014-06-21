package net.yupol.transmissionremote.app.server;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import net.yupol.transmissionremote.app.R;

public class AddServerFragment extends Fragment {

    private static final String TAG = AddServerFragment.class.getSimpleName();

    // Authentication disabled by default
    private boolean isAuthEnabled = false;

    private EditText serverNameEdit;
    private EditText hostNameEdit;
    private EditText portNumberEdit;
    private CheckBox authCheckBox;
    private EditText usernameEdit;
    private EditText passwordEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_server_fragment, container, false);

        serverNameEdit = (EditText) view.findViewById(R.id.server_name_edit_text);
        hostNameEdit = (EditText) view.findViewById(R.id.host_edit_text);
        portNumberEdit = (EditText) view.findViewById(R.id.port_edit_text);
        authCheckBox = (CheckBox) view.findViewById(R.id.aunthentication_checkbox);
        usernameEdit = (EditText) view.findViewById(R.id.user_name_edit_text);
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

        return view;
    }

    public Server getServer() {
        if (!checkValidity())
            return null;

        String serverName = serverNameEdit.getText().toString();
        String hostName = hostNameEdit.getText().toString();
        String portNumberText = portNumberEdit.getText().toString();
        int portNumber = 1;
        try {
            portNumber = Integer.parseInt(portNumberText);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error while parsing port number string: " + portNumberText, e);
        }
        String userName = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        Server server;
        if (userName.isEmpty()) {
            server = new Server(serverName, hostName, portNumber);
        } else {
            server = new Server(serverName, hostName, portNumber, userName, password);
        }

        return server;
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
        usernameEdit.setEnabled(isEnabled);
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
