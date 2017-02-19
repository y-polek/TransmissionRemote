package net.yupol.transmissionremote.app.opentorrent;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.databinding.DownloadLocationDialogBinding;
import net.yupol.transmissionremote.app.model.json.FreeSpace;
import net.yupol.transmissionremote.app.model.json.ServerSettings;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.FreeSpaceRequest;
import net.yupol.transmissionremote.app.transport.request.SessionGetRequest;
import net.yupol.transmissionremote.app.utils.SimpleTextWatcher;
import net.yupol.transmissionremote.app.utils.TextUtils;

import java.util.List;

public class DownloadLocationDialogFragment extends DialogFragment {

    private static final String TAG = DownloadLocationDialogFragment.class.getSimpleName();

    public static final String KEY_REQUEST_CODE = "key_request_code";
    public static final int REQUEST_CODE_BY_LOCAL_FILE = 0;
    public static final int REQUEST_CODE_BY_REMOTE_FILE = 1;
    public static final int REQUEST_CODE_BY_MAGNET = 2;
    public static final String KEY_FILE_BYTES = "key_file_bytes";
    public static final String KEY_FILE_URI = "key_file_uri";
    public static final String KEY_MAGNET_URI = "key_magnet_uri";

    private static final String KEY_LOADING_IN_PROGRESS = "key_loading_in_progress";

    private OnDownloadLocationSelectedListener listener;
    private FreeSpaceRequest currentRequest;
    private FreeSpace freeSpace;
    private DownloadLocationDialogBinding binding;
    private SessionGetRequest sessionGetRequest;
    private TransmissionRemote app;

    private ListPopupWindow downloadLocationsPopup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = TransmissionRemote.getApplication(getContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(),
                R.layout.download_location_dialog, null, false);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(binding.getRoot())
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        String defaultDownloadDir = TransmissionRemote.getInstance().getDefaultDownloadDir();
        boolean loadDefaultDownloadDir = defaultDownloadDir == null ||
                (savedInstanceState != null && savedInstanceState.getBoolean(KEY_LOADING_IN_PROGRESS, false));
        if (!loadDefaultDownloadDir) {
            binding.downloadLocationText.setText(defaultDownloadDir);
        } else {
            binding.setLoadingInProgress(true);
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    if (binding.getLoadingInProgress()) {
                        ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
            });
            sessionGetRequest = new SessionGetRequest();
            getTransportManager().doRequest(new SessionGetRequest(), new RequestListener<ServerSettings>() {
                @Override
                public void onRequestSuccess(ServerSettings serverSettings) {
                    binding.setLoadingInProgress(false);
                    binding.downloadLocationText.setText(Strings.nullToEmpty(serverSettings.getDownloadDir()));
                    AlertDialog dialog = (AlertDialog) getDialog();
                    if (dialog != null) dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }

                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    binding.setLoadingInProgress(false);
                    AlertDialog dialog = (AlertDialog) getDialog();
                    if (dialog != null) dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }
            });
        }

        binding.downloadLocationDropdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDownloadLocationSuggestionsShown()) {
                    showDownloadLocationSuggestions(app.getActiveServer());
                } else {
                    hideDownloadLocationSuggestions();
                }
            }
        });

        binding.downloadLocationText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!binding.getLoadingInProgress()) {
                    updateFreeSpaceInfo();
                }
            }
        });

        List<Server> servers = app.getServers();
        Server activeServer = app.getActiveServer();
        final ServerSpinnerAdapter serverAdapter = new ServerSpinnerAdapter(getContext(), servers);
        binding.serverSpinner.setAdapter(serverAdapter);
        binding.serverSpinner.setSelection(servers.indexOf(activeServer));
        binding.serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Server server = serverAdapter.getItem(position);
                if (server != null) {
                    listener.onServerSelected(server);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return dialog;
    }

    private void showDownloadLocationSuggestions(Server server) {
        if (downloadLocationsPopup != null && downloadLocationsPopup.isShowing()) return;
        downloadLocationsPopup = new ListPopupWindow(getContext());
        downloadLocationsPopup.setModal(false);

        final ArrayAdapter<String> downloadLocationAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, server.getSavedDownloadLocations());
        downloadLocationsPopup.setAdapter(downloadLocationAdapter);
        downloadLocationsPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String location = downloadLocationAdapter.getItem(position);
                binding.downloadLocationText.setText(location);
                downloadLocationsPopup.dismiss();
            }
        });

        downloadLocationsPopup.setAnchorView(binding.downloadLocationText);
        downloadLocationsPopup.show();
    }

    private boolean isDownloadLocationSuggestionsShown() {
        return downloadLocationsPopup != null && downloadLocationsPopup.isShowing();
    }

    private void hideDownloadLocationSuggestions() {
        if (isDownloadLocationSuggestionsShown()) {
            downloadLocationsPopup.dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_LOADING_IN_PROGRESS, binding.getLoadingInProgress());
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
                    if (freeSpace != null && freeSpace.getSizeInBytes() >= 0
                            || TransmissionRemote.getInstance().isFreeSpaceCheckDisabled()) {
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
                    String downloadDir = binding.downloadLocationText.getText().toString();
                    listener.onDownloadLocationSelected(
                            getArguments(),
                            downloadDir,
                            binding.startWhenAddedCheckbox.isChecked());

                    Server activeServer = app.getActiveServer();
                    activeServer.addSavedDownloadLocations(downloadDir);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!binding.getLoadingInProgress()) {
            updateFreeSpaceInfo();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (currentRequest != null) currentRequest.cancel();
        if (sessionGetRequest != null) sessionGetRequest.cancel();
        super.onDismiss(dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnDownloadLocationSelectedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement " + OnDownloadLocationSelectedListener.class.getSimpleName());
        }
    }

    private void updateFreeSpaceInfo() {
        if (currentRequest != null) currentRequest.cancel();
        if (getDialog() == null) return;
        freeSpace = null;

        binding.freeSpaceText.setVisibility(View.INVISIBLE);
        binding.freeSpaceProgressBar.setVisibility(View.VISIBLE);
        if (!TransmissionRemote.getInstance().isFreeSpaceCheckDisabled()) {
            ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        }

        final String path = binding.downloadLocationText.getText().toString();
        currentRequest = new FreeSpaceRequest(path);
        getTransportManager().doRequest(currentRequest,
            new RequestListener<FreeSpace>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Log.e(TAG, "Can't fetch free space for '" + path + "'. " + spiceException.getMessage());

                    AlertDialog dialog = (AlertDialog) getDialog();
                    if (dialog != null) {
                        binding.freeSpaceProgressBar.setVisibility(View.INVISIBLE);
                        binding.freeSpaceText.setVisibility(View.VISIBLE);
                        binding.freeSpaceText.setText(R.string.free_space_unknown);
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
                            binding.freeSpaceText.setText(getString(
                                    R.string.free_space, TextUtils.displayableSize(freeSpace.getSizeInBytes())));
                        } else {
                            String useDefaultText = getString(R.string.use_default_directory);
                            binding.freeSpaceText.setText(getString(R.string.no_directory) + ". " + useDefaultText);
                            clickify(binding.freeSpaceText, useDefaultText, new ClickSpan.OnClickListener() {
                                @Override
                                public void onClick() {
                                    TransmissionRemote app = (TransmissionRemote) getActivity().getApplicationContext();
                                    binding.downloadLocationText.setText(Strings.nullToEmpty(app.getDefaultDownloadDir()));
                                }
                            });
                        }
                        binding.freeSpaceProgressBar.setVisibility(View.INVISIBLE);
                        binding.freeSpaceText.setVisibility(View.VISIBLE);
                    }
                    currentRequest = null;
                }
            });
    }

    public interface OnDownloadLocationSelectedListener {
        void onDownloadLocationSelected(Bundle args, String downloadDir, boolean startWhenAdded);
        void onServerSelected(Server server);
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

    private TransportManager getTransportManager() {
        return ((BaseSpiceActivity) getActivity()).getTransportManager();
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
