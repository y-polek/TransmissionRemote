package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;

public class BandwidthLimitFragment extends Fragment {

    private EditText downLimitEdit;
    private TextView downLimitUnits;
    private CheckBox downLimitCheckbox;
    private EditText upLimitEdit;
    private TextView upLimitUnits;
    private CheckBox upLimitCheckbox;

    private boolean enabled = true;

    private OnLimitChanged listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bandwidth_limit_fragment, container, false);

        downLimitEdit = (EditText) view.findViewById(R.id.download_limit_edittext);
        downLimitEdit.setEnabled(false);
        downLimitUnits = (TextView) view.findViewById(R.id.download_limit_units);
        downLimitUnits.setEnabled(false);
        downLimitCheckbox = (CheckBox) view.findViewById(R.id.download_limit_checkbox);
        downLimitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                downLimitEdit.setEnabled(isChecked && enabled);
                downLimitUnits.setEnabled(isChecked && enabled);

                if (listener != null) {
                    listener.onDownloadLimitedChanged(isChecked);
                }
            }
        });

        upLimitEdit = (EditText) view.findViewById(R.id.upload_limit_edittext);
        upLimitEdit.setEnabled(false);
        upLimitUnits = (TextView) view.findViewById(R.id.upload_limit_units);
        upLimitUnits.setEnabled(false);
        upLimitCheckbox = (CheckBox) view.findViewById(R.id.upload_limit_checkbox);
        upLimitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                upLimitEdit.setEnabled(isChecked && enabled);
                upLimitUnits.setEnabled(isChecked && enabled);

                if (listener != null) {
                    listener.onUploadLimitedChanged(isChecked);
                }
            }
        });

        return view;
    }

    /**
     * Sets enabled/disabled state for all controls in this fragment.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        downLimitCheckbox.setEnabled(enabled);
        downLimitEdit.setEnabled(enabled && downLimitCheckbox.isChecked());
        downLimitUnits.setEnabled(enabled && downLimitCheckbox.isChecked());
        upLimitCheckbox.setEnabled(enabled);
        upLimitEdit.setEnabled(enabled && upLimitCheckbox.isChecked());
        upLimitUnits.setEnabled(enabled && upLimitCheckbox.isChecked());
    }

    public void setDownloadLimited(boolean isLimited) {
        downLimitCheckbox.setChecked(isLimited);
    }

    /**
     * @param limit in KB/s
     */
    public void setDownloadLimit(long limit) {
        downLimitEdit.setText(String.valueOf(limit));
    }

    public void setUploadLimited(boolean isLimited) {
        upLimitCheckbox.setChecked(isLimited);
    }

    /**
     * @param limit in KB/s
     */
    public void setUploadLimit(long limit) {
        upLimitEdit.setText(String.valueOf(limit));
    }

    public void setOnLimitChangedListener(OnLimitChanged listener) {
        this.listener = listener;
    }

    public interface OnLimitChanged {
        void onDownloadLimitedChanged(boolean isLimited);
        void onUploadLimitedChanged(boolean isLimited);
    }
}
