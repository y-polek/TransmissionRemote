package net.yupol.transmissionremote.app.opentorrent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.model.json.FreeSpace;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.request.FreeSpaceRequest;
import net.yupol.transmissionremote.app.utils.TextUtils;

public class DownloadLocationDialogFragment extends DialogFragment {

    private static final String TAG = DownloadLocationDialogFragment.class.getSimpleName();

    public static final String KEY_REQUEST_CODE = "key_request_code";
    public static final int REQUEST_CODE_BY_LOCAL_FILE = 0;
    public static final int REQUEST_CODE_BY_REMOTE_FILE = 1;
    public static final int REQUEST_CODE_BY_MAGNET = 2;
    public static final String KEY_FILE_BYTES = "key_file_bytes";
    public static final String KEY_FILE_URI = "key_file_uri";
    public static final String KEY_MAGNET_URI = "key_magnet_uri";

    private OnDownloadLocationSelectedListener listener;
    private EditText downloadLocationText;
    private TextView freeSpaceText;
    private ProgressBar freeSpaceProgressbar;
    private FreeSpaceRequest currentRequest;
    private FreeSpace freeSpace;
    private CheckBox startWhenAddedCheckbox;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.download_location_dialog, null);

        downloadLocationText = (EditText) view.findViewById(R.id.download_location);
        TransmissionRemote app = (TransmissionRemote) getActivity().getApplicationContext();
        downloadLocationText.setText(Strings.nullToEmpty(app.getDefaultDownloadDir()));

        freeSpaceText = (TextView) view.findViewById(R.id.free_space_text);
        freeSpaceProgressbar = (ProgressBar) view.findViewById(R.id.free_space_progress_bar);

        startWhenAddedCheckbox = (CheckBox) view.findViewById(R.id.start_when_added);

        builder.setTitle(R.string.download_to)
               .setView(view)
               .setPositiveButton(R.string.add, null)
               .setNegativeButton(android.R.string.cancel, null);

        downloadLocationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateFreeSpaceInfo();
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button addButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (freeSpace != null && freeSpace.getSizeInBytes() >= 0) {
                        notifyListener();
                        dialog.dismiss();
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setMessage(R.string.unknown_free_space_confirmation)
                                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface thisDialog, int which) {
                                        notifyListener();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();
                    }
                }

                private void notifyListener() {
                    listener.onDownloadLocationSelected(
                            getArguments(),
                            downloadLocationText.getText().toString(),
                            startWhenAddedCheckbox.isChecked());
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFreeSpaceInfo();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (currentRequest != null) currentRequest.cancel();
        super.onDismiss(dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnDownloadLocationSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + OnDownloadLocationSelectedListener.class.getSimpleName());
        }
    }

    private void updateFreeSpaceInfo() {
        if (currentRequest != null) currentRequest.cancel();
        if (getDialog() == null) return;
        freeSpace = null;

        freeSpaceText.setVisibility(View.INVISIBLE);
        freeSpaceProgressbar.setVisibility(View.VISIBLE);
        if (!TransmissionRemote.getInstance().isFreeSpaceCheckDisabled()) {
            ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        }

        final String path = downloadLocationText.getText().toString();
        currentRequest = new FreeSpaceRequest(path);
        ((BaseSpiceActivity) getActivity()).getTransportManager().doRequest(currentRequest,
            new RequestListener<FreeSpace>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Log.e(TAG, "Can't fetch free space for '" + path + "'. " + spiceException.getMessage());

                    AlertDialog dialog = (AlertDialog) getDialog();
                    if (dialog != null) {
                        freeSpaceProgressbar.setVisibility(View.INVISIBLE);
                        freeSpaceText.setVisibility(View.VISIBLE);
                        freeSpaceText.setText(R.string.free_space_unknown);
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    }
                    currentRequest = null;
                }

                @Override
                public void onRequestSuccess(FreeSpace freeSpace) {
                    DownloadLocationDialogFragment.this.freeSpace = freeSpace;
                    AlertDialog dialog = (AlertDialog) getDialog();
                    if (dialog != null) {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                        if (freeSpace.getSizeInBytes() >= 0) {
                            freeSpaceText.setText(getString(
                                    R.string.free_space, TextUtils.displayableSize(freeSpace.getSizeInBytes())));
                        } else {
                            String useDefaultText = getString(R.string.use_default_directory);
                            freeSpaceText.setText(getString(R.string.no_directory) + ". " + useDefaultText);
                            clickify(freeSpaceText, useDefaultText, new ClickSpan.OnClickListener() {
                                @Override
                                public void onClick() {
                                    TransmissionRemote app = (TransmissionRemote) getActivity().getApplicationContext();
                                    downloadLocationText.setText(Strings.nullToEmpty(app.getDefaultDownloadDir()));
                                }
                            });
                        }
                        freeSpaceProgressbar.setVisibility(View.INVISIBLE);
                        freeSpaceText.setVisibility(View.VISIBLE);
                    }
                    currentRequest = null;
                }
            });
    }

    public interface OnDownloadLocationSelectedListener {
        void onDownloadLocationSelected(Bundle args, String downloadDir, boolean startWhenAdded);
    }

    private static void clickify(TextView view, String clickableText,
                                ClickSpan.OnClickListener listener) {

        CharSequence text = view.getText();
        String string = text.toString();
        ClickableSpan span = new ClickSpan(listener);

        int start = string.indexOf(clickableText);
        int end = start + clickableText.length();
        if (start == -1) return;

        if (text instanceof Spannable) {
            ((Spannable)text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            SpannableString s = SpannableString.valueOf(text);
            s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(s);
        }

        MovementMethod m = view.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private static class ClickSpan extends ClickableSpan {

        private OnClickListener mListener;

        ClickSpan(OnClickListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View widget) {
            if (mListener != null) mListener.onClick();
        }

        interface OnClickListener {
            void onClick();
        }
    }
}
