package net.yupol.transmissionremote.app.torrentdetails;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;

public class BandwidthLimitFragment extends Fragment implements View.OnFocusChangeListener {

    private static final String TAG = BandwidthLimitFragment.class.getSimpleName();

    private static final int LIMIT_MIN = 0;
    private static final int LIMIT_MAX = 99999;

    private EditText downLimitEdit;
    private TextView downLimitUnits;
    private CheckBox downLimitCheckbox;
    private EditText upLimitEdit;
    private TextView upLimitUnits;
    private CheckBox upLimitCheckbox;

    private int currentDownLimit;
    private int currentUpLimit;
    private boolean disableableLimits;

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);

        TypedArray a = activity.obtainStyledAttributes(attrs, R.styleable.BandwidthLimitFragment);
        disableableLimits = a.getBoolean(R.styleable.BandwidthLimitFragment_disableable_limits, true);
        a.recycle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bandwidth_limit_fragment, container, false);

        downLimitEdit = (EditText) view.findViewById(R.id.download_limit_edittext);
        downLimitEdit.setEnabled(!disableableLimits);
        InputFilter[] limitFilters = {new InputFilter.LengthFilter((int) (Math.log10(LIMIT_MAX) + 1))};
        downLimitEdit.setFilters(limitFilters);
        downLimitEdit.setOnFocusChangeListener(this);
        downLimitUnits = (TextView) view.findViewById(R.id.download_limit_units);
        downLimitUnits.setEnabled(!disableableLimits);
        downLimitCheckbox = (CheckBox) view.findViewById(R.id.download_limit_checkbox);
        downLimitCheckbox.setVisibility(disableableLimits ? View.VISIBLE : View.GONE);
        if (disableableLimits) {
            downLimitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    downLimitEdit.setEnabled(isChecked);
                    downLimitUnits.setEnabled(isChecked);
                }
            });
        }
        TextView downLimitText = (TextView) view.findViewById(R.id.download_limit_text);
        downLimitText.setVisibility(disableableLimits ? View.GONE : View.VISIBLE);

        upLimitEdit = (EditText) view.findViewById(R.id.upload_limit_edittext);
        upLimitEdit.setEnabled(!disableableLimits);
        upLimitEdit.setFilters(limitFilters);
        upLimitEdit.setOnFocusChangeListener(this);
        upLimitUnits = (TextView) view.findViewById(R.id.upload_limit_units);
        upLimitUnits.setEnabled(!disableableLimits);
        upLimitCheckbox = (CheckBox) view.findViewById(R.id.upload_limit_checkbox);
        upLimitCheckbox.setVisibility(disableableLimits ? View.VISIBLE : View.GONE);
        upLimitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                upLimitEdit.setEnabled(isChecked);
                upLimitUnits.setEnabled(isChecked);
            }
        });
        TextView upLimitText = (TextView) view.findViewById(R.id.upload_limit_text);
        upLimitText.setVisibility(disableableLimits ? View.GONE : View.VISIBLE);

        return view;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) return;

        readLimitValues(v);
    }

    private void readLimitValues(View v) {
        EditText editText = (EditText) v;
        String text = editText.getText().toString();
        try {
            int value = Integer.parseInt(text);

            if (value >= LIMIT_MIN && value <= LIMIT_MAX) {
                if (v == downLimitEdit) {
                    currentDownLimit = value;
                } else if (v == upLimitEdit) {
                    currentUpLimit = value;
                }
            }
        } catch (NumberFormatException e) {
            Log.d(TAG, "Failed to parse integer: '" + text + "'");
        }

        editText.setText(String.valueOf(v == downLimitEdit ? currentDownLimit : currentUpLimit));
    }

    public void setDownloadLimited(boolean isLimited) {
        downLimitCheckbox.setChecked(isLimited);
    }

    public boolean isDownloadLimited() {
        return downLimitCheckbox.isChecked();
    }

    /**
     * @param limit in KB/s
     */
    public void setDownloadLimit(long limit) {
        downLimitEdit.setText(String.valueOf(limit));
    }

    /**
     * @return download limit in KB/s
     */
    public long getDownloadLimit() {
        readLimitValues(downLimitEdit);
        return currentDownLimit;
    }

    public void setUploadLimited(boolean isLimited) {
        upLimitCheckbox.setChecked(isLimited);
    }

    public boolean isUploadLimited() {
        return upLimitCheckbox.isChecked();
    }

    /**
     * @param limit in KB/s
     */
    public void setUploadLimit(long limit) {
        upLimitEdit.setText(String.valueOf(limit));
    }

    /**
     * @return upload limit in KB/s
     */
    public long getUploadLimit() {
        readLimitValues(upLimitEdit);
        return currentUpLimit;
    }
}
