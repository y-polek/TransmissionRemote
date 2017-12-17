package net.yupol.transmissionremote.app.torrentdetails;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.widget.Button;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.databinding.TrackerUrlDialogLayoutBinding;
import net.yupol.transmissionremote.app.model.json.TrackerStats;
import net.yupol.transmissionremote.app.utils.SimpleTextWatcher;

import org.apache.commons.lang3.StringUtils;

public class TrackerUrlDialog extends DialogFragment {

    private static final String KEY_TRACKER = "key_tracker";

    @Nullable private TrackerStats tracker;
    private OnTrackerUrlEnteredListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker = getArguments().getParcelable(KEY_TRACKER);

        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnTrackerUrlEnteredListener) {
            listener = (OnTrackerUrlEnteredListener) parentFragment;
        } else {
            Activity activity = getActivity();
            if (activity instanceof OnTrackerUrlEnteredListener) {
                listener = (OnTrackerUrlEnteredListener) activity;
            } else {
                throw new IllegalStateException("Parent fragment or activity must implement OnTrackerUrlEnteredListener");
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final TrackerUrlDialogLayoutBinding binding = DataBindingUtil.inflate(
                getActivity().getLayoutInflater(), R.layout.tracker_url_dialog_layout, null, false);

        final boolean edit = tracker != null;

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(edit ? R.string.trackers_edit_tracker_title : R.string.trackers_add_tracker_title)
                .setView(binding.getRoot())
                .setPositiveButton(edit ? R.string.trackers_done_button : R.string.trackers_add_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = formatUrl(binding.url.getText().toString());
                                listener.onTrackerUrlEntered(tracker, url);
                            }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                String url = formatUrl(binding.url.getText().toString());
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(isValidUrl(url));
            }
        });

        binding.url.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Button addButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (addButton != null) {
                    String url = formatUrl(s.toString());
                    addButton.setEnabled(isValidUrl(url));
                }
            }
        });

        if (edit) {
            binding.url.setText(StringUtils.isNotEmpty(tracker.host) ? tracker.host : tracker.announce);
        }

        return dialog;
    }

    public static TrackerUrlDialog newInstance(@Nullable TrackerStats trackerStats) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_TRACKER, trackerStats);
        TrackerUrlDialog fragment = new TrackerUrlDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private String formatUrl(@NonNull String url) {
        url = url.trim().toLowerCase();

        if (url.isEmpty()) return "";

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        } else {
            return "http://" + url;
        }
    }

    private boolean isValidUrl(String url) {
        return !url.isEmpty() && !StringUtils.containsWhitespace(url);
    }

    public interface OnTrackerUrlEnteredListener {

        void onTrackerUrlEntered(@Nullable TrackerStats tracker, String url);
    }
}
