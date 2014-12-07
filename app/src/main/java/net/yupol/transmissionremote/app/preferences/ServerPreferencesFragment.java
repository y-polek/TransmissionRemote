package net.yupol.transmissionremote.app.preferences;

import android.app.Fragment;
import android.os.Bundle;
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

import static net.yupol.transmissionremote.app.preferences.ServerPreferences.*;

public class ServerPreferencesFragment extends Fragment {

    private static final String TAG = ServerPreferencesFragment.class.getSimpleName();

    private EditText downLimitEdit;
    private CheckBox downLimitCheckbox;
    private EditText upLimitEdit;
    private CheckBox upLimitCheckbox;
    private EditText altDownLimitEdit;
    private EditText altUpLimitEdit;
    private boolean isAltLimitEnabled;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.server_preferences_fragment, container, false);

        Bundle args = getArguments();

        if (args == null) return view;

        int globalLimitDown = args.getInt(SPEED_LIMIT_DOWN);
        boolean isGlobalLimitDownEnabled = args.getBoolean(SPEED_LIMIT_DOWN_ENABLED);
        int globalLimitUp = args.getInt(SPEED_LIMIT_UP);
        boolean isGlobalLimitUpEnabled = args.getBoolean(SPEED_LIMIT_UP_ENABLED);
        int altLimitDown = args.getInt(ALT_SPEED_LIMIT_DOWN);
        int altLimitUp = args.getInt(ALT_SPEED_LIMIT_UP);
        isAltLimitEnabled = args.getBoolean(ALT_SPEED_LIMIT_ENABLED);

        downLimitCheckbox = (CheckBox) view.findViewById(R.id.download_limit_checkbox);
        downLimitCheckbox.setChecked(isGlobalLimitDownEnabled);
        downLimitEdit = (EditText) view.findViewById(R.id.download_limit_edittext);
        downLimitEdit.setText(String.valueOf(globalLimitDown));
        downLimitEdit.setEnabled(isGlobalLimitDownEnabled);
        final TextView downLimitUnits = (TextView) view.findViewById(R.id.download_limit_units);
        downLimitUnits.setEnabled(isGlobalLimitDownEnabled);
        downLimitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                downLimitEdit.setEnabled(isChecked);
                downLimitUnits.setEnabled(isChecked);
            }
        });

        upLimitCheckbox = (CheckBox) view.findViewById(R.id.upload_limit_checkbox);
        upLimitCheckbox.setChecked(isGlobalLimitUpEnabled);
        upLimitEdit = (EditText) view.findViewById(R.id.upload_limit_edittext);
        upLimitEdit.setText(String.valueOf(globalLimitUp));
        upLimitEdit.setEnabled(isGlobalLimitUpEnabled);
        final TextView upLimitUnits = (TextView) view.findViewById(R.id.upload_limit_units);
        upLimitUnits.setEnabled(isGlobalLimitUpEnabled);
        upLimitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                upLimitEdit.setEnabled(isChecked);
                upLimitUnits.setEnabled(isChecked);
            }
        });

        altDownLimitEdit = (EditText) view.findViewById(R.id.turtle_download_limit_edittext);
        altDownLimitEdit.setText(String.valueOf(altLimitDown));
        altUpLimitEdit = (EditText) view.findViewById(R.id.turtle_upload_limit_edittext);
        altUpLimitEdit.setText(String.valueOf(altLimitUp));
        TextView altLimitHeader = (TextView) view.findViewById(R.id.turtle_limit_header_text);
        int turtleImage = isAltLimitEnabled ? R.drawable.turtle_blue : R.drawable.turtle;
        altLimitHeader.setCompoundDrawablesWithIntrinsicBounds(turtleImage, 0, 0, 0);

        return view;
    }

    public JSONObject getPreferences() {
        JSONObject obj = new JSONObject();

        try {
            obj.put(SPEED_LIMIT_DOWN, Integer.parseInt(downLimitEdit.getText().toString()));
            obj.put(SPEED_LIMIT_DOWN_ENABLED, downLimitCheckbox.isChecked());

            obj.put(SPEED_LIMIT_UP, Integer.parseInt(upLimitEdit.getText().toString()));
            obj.put(SPEED_LIMIT_UP_ENABLED, upLimitCheckbox.isChecked());

            obj.put(ALT_SPEED_LIMIT_DOWN, Integer.parseInt(altDownLimitEdit.getText().toString()));
            obj.put(ALT_SPEED_LIMIT_UP, Integer.parseInt(altUpLimitEdit.getText().toString()));
            obj.put(ALT_SPEED_LIMIT_ENABLED, isAltLimitEnabled);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON object with server parameters", e);
        }

        return obj;
    }
}
