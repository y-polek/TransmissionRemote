package net.yupol.transmissionremote.app.preferences;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import static net.yupol.transmissionremote.app.model.json.ServerSettings.ALT_SPEED_LIMIT_DOWN;
import static net.yupol.transmissionremote.app.model.json.ServerSettings.ALT_SPEED_LIMIT_ENABLED;
import static net.yupol.transmissionremote.app.model.json.ServerSettings.ALT_SPEED_LIMIT_UP;
import static net.yupol.transmissionremote.app.model.json.ServerSettings.SPEED_LIMIT_DOWN;
import static net.yupol.transmissionremote.app.model.json.ServerSettings.SPEED_LIMIT_DOWN_ENABLED;
import static net.yupol.transmissionremote.app.model.json.ServerSettings.SPEED_LIMIT_UP;
import static net.yupol.transmissionremote.app.model.json.ServerSettings.SPEED_LIMIT_UP_ENABLED;

public class ServerPreferencesFragment extends Fragment {

    private static final String TAG = ServerPreferencesFragment.class.getSimpleName();

    private Bundle serverPreferences;
    private Set<String> changedPreferences;

    private EditText downLimitEdit;
    private CheckBox downLimitCheckbox;
    private TextView downLimitUnits;

    private EditText upLimitEdit;
    private CheckBox upLimitCheckbox;
    private TextView upLimitUnits;

    private EditText altDownLimitEdit;
    private EditText altUpLimitEdit;
    private TextView altLimitHeader;
    private boolean isAltLimitEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changedPreferences = new HashSet<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.server_preferences_fragment, container, false);

        downLimitEdit = (EditText) view.findViewById(R.id.download_limit_edittext);
        downLimitEdit.addTextChangedListener(new LimitChangeWatcher(SPEED_LIMIT_DOWN, downLimitEdit));
        downLimitUnits = (TextView) view.findViewById(R.id.download_limit_units);
        downLimitCheckbox = (CheckBox) view.findViewById(R.id.download_limit_checkbox);
        downLimitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                downLimitEdit.setEnabled(isChecked);
                downLimitUnits.setEnabled(isChecked);

                if (isChecked != serverPreferences.getBoolean(SPEED_LIMIT_DOWN_ENABLED, false)) {
                    changedPreferences.add(SPEED_LIMIT_DOWN_ENABLED);
                } else {
                    changedPreferences.remove(SPEED_LIMIT_DOWN_ENABLED);
                }
            }
        });

        upLimitEdit = (EditText) view.findViewById(R.id.upload_limit_edittext);
        upLimitEdit.addTextChangedListener(new LimitChangeWatcher(SPEED_LIMIT_UP, upLimitEdit));
        upLimitUnits = (TextView) view.findViewById(R.id.upload_limit_units);
        upLimitCheckbox = (CheckBox) view.findViewById(R.id.upload_limit_checkbox);
        upLimitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                upLimitEdit.setEnabled(isChecked);
                upLimitUnits.setEnabled(isChecked);

                if (isChecked != serverPreferences.getBoolean(SPEED_LIMIT_UP_ENABLED, false)) {
                    changedPreferences.add(SPEED_LIMIT_UP_ENABLED);
                } else {
                    changedPreferences.remove(SPEED_LIMIT_UP_ENABLED);
                }
            }
        });

        altDownLimitEdit = (EditText) view.findViewById(R.id.turtle_download_limit_edittext);
        altDownLimitEdit.addTextChangedListener(new LimitChangeWatcher(ALT_SPEED_LIMIT_DOWN, altDownLimitEdit));
        altUpLimitEdit = (EditText) view.findViewById(R.id.turtle_upload_limit_edittext);
        altUpLimitEdit.addTextChangedListener(new LimitChangeWatcher(ALT_SPEED_LIMIT_UP, altUpLimitEdit));
        altLimitHeader = (TextView) view.findViewById(R.id.turtle_limit_header_text);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        serverPreferences = getArguments();
        updateUi();
    }

    public JSONObject getChangedPreferences() {
        JSONObject obj = new JSONObject();

        try {
            if (changedPreferences.contains(SPEED_LIMIT_DOWN))
                obj.put(SPEED_LIMIT_DOWN, Integer.parseInt(downLimitEdit.getText().toString()));
            if (changedPreferences.contains(SPEED_LIMIT_DOWN_ENABLED))
                obj.put(SPEED_LIMIT_DOWN_ENABLED, downLimitCheckbox.isChecked());

            if (changedPreferences.contains(SPEED_LIMIT_UP))
                obj.put(SPEED_LIMIT_UP, Integer.parseInt(upLimitEdit.getText().toString()));
            if (changedPreferences.contains(SPEED_LIMIT_UP_ENABLED))
                obj.put(SPEED_LIMIT_UP_ENABLED, upLimitCheckbox.isChecked());

            if (changedPreferences.contains(ALT_SPEED_LIMIT_DOWN))
                obj.put(ALT_SPEED_LIMIT_DOWN, Integer.parseInt(altDownLimitEdit.getText().toString()));
            if (changedPreferences.contains(ALT_SPEED_LIMIT_UP))
                obj.put(ALT_SPEED_LIMIT_UP, Integer.parseInt(altUpLimitEdit.getText().toString()));
            if (changedPreferences.contains(ALT_SPEED_LIMIT_ENABLED))
                obj.put(ALT_SPEED_LIMIT_ENABLED, isAltLimitEnabled);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON object with server parameters", e);
        }

        return obj;
    }

    private void updateUi() {

        if (serverPreferences == null) {
            throw new IllegalStateException("No server preferences set." +
                    " Ensure that setArguments(Bundle) called with bundle containing server preferences.");
        }

        View view = getView();
        if (view == null) {
            Log.e(TAG, "trying to update fragment before onCreateView()");
            return;
        }

        int globalLimitDown = serverPreferences.getInt(SPEED_LIMIT_DOWN);
        boolean isGlobalLimitDownEnabled = serverPreferences.getBoolean(SPEED_LIMIT_DOWN_ENABLED);
        int globalLimitUp = serverPreferences.getInt(SPEED_LIMIT_UP);
        boolean isGlobalLimitUpEnabled = serverPreferences.getBoolean(SPEED_LIMIT_UP_ENABLED);
        int altLimitDown = serverPreferences.getInt(ALT_SPEED_LIMIT_DOWN);
        int altLimitUp = serverPreferences.getInt(ALT_SPEED_LIMIT_UP);
        isAltLimitEnabled = serverPreferences.getBoolean(ALT_SPEED_LIMIT_ENABLED);

        if (!changedPreferences.contains(SPEED_LIMIT_DOWN_ENABLED)) {
            downLimitCheckbox.setChecked(isGlobalLimitDownEnabled);
            downLimitEdit.setEnabled(isGlobalLimitDownEnabled);
            downLimitUnits.setEnabled(isGlobalLimitDownEnabled);

        }
        if (!changedPreferences.contains(SPEED_LIMIT_DOWN)) {
            downLimitEdit.setText(String.valueOf(globalLimitDown));
        }

        if (!changedPreferences.contains(SPEED_LIMIT_UP_ENABLED)) {
            upLimitCheckbox.setChecked(isGlobalLimitUpEnabled);
            upLimitEdit.setEnabled(isGlobalLimitUpEnabled);
            upLimitUnits.setEnabled(isGlobalLimitUpEnabled);
        }
        if (!changedPreferences.contains(SPEED_LIMIT_UP)) {
            upLimitEdit.setText(String.valueOf(globalLimitUp));
        }

        if (!changedPreferences.contains(ALT_SPEED_LIMIT_ENABLED)) {
            int turtleImage = isAltLimitEnabled ? R.drawable.turtle_blue : R.drawable.turtle;
            altLimitHeader.setCompoundDrawablesWithIntrinsicBounds(turtleImage, 0, 0, 0);
        }
        if (!changedPreferences.contains(ALT_SPEED_LIMIT_DOWN)) {
            altDownLimitEdit.setText(String.valueOf(altLimitDown));
        }
        if (!changedPreferences.contains(ALT_SPEED_LIMIT_UP)) {
            altUpLimitEdit.setText(String.valueOf(altLimitUp));
        }
    }

    private class LimitChangeWatcher implements TextWatcher {

        private String prefKey;
        private EditText editText;

        public LimitChangeWatcher(String prefKey, EditText editText) {
            this.prefKey = prefKey;
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            int limit = Integer.parseInt(editText.getText().toString());

            if (limit != serverPreferences.getInt(prefKey, -1)) {
                changedPreferences.add(prefKey);
            } else {
                changedPreferences.remove(prefKey);
            }
        }
    }
}
