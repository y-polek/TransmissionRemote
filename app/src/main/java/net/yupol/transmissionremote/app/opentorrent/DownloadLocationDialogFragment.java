package net.yupol.transmissionremote.app.opentorrent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.model.json.FreeSpace;
import net.yupol.transmissionremote.app.model.json.ServerSettings;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.request.FreeSpaceRequest;
import net.yupol.transmissionremote.app.transport.request.SessionGetRequest;
import net.yupol.transmissionremote.app.utils.TextUtils;

import java.util.List;

public class DownloadLocationDialogFragment extends android.support.v4.app.DialogFragment {

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
    private View downloadDirLayout;
    private ProgressBar downloadDirProgressbar;
    private SessionGetRequest currentSessionGetRequest;
    private FreeSpaceRequest currentFreeSpaceRequest;
    private TransmissionRemote app;
    private String defaultDownloadDir;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.download_location_dialog, null);

        app = TransmissionRemote.getApplication(getContext());
        Spinner serversSpinner = (Spinner) view.findViewById(R.id.servers_spinner);
        final ServersSpinnerAdapter serversAdapter = new ServersSpinnerAdapter(app.getServers());
        serversSpinner.setAdapter(serversAdapter);
        serversSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switchServer(serversAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        downloadLocationText = (EditText) view.findViewById(R.id.download_location);

        if (savedInstanceState != null && savedInstanceState.containsKey("down_location")) {
            downloadLocationText.setText(savedInstanceState.getString("down_location"));
        } else {
            serversSpinner.setSelection(serversAdapter.getItemPosition(app.getActiveServer()));
        }

        freeSpaceText = (TextView) view.findViewById(R.id.free_space_text);
        freeSpaceProgressbar = (ProgressBar) view.findViewById(R.id.free_space_progress_bar);

        final CheckBox startWhenAddedCheckbox = (CheckBox) view.findViewById(R.id.start_when_added);

        builder.setView(view)
               .setTitle(R.string.open_torrent_title)
               .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String downloadDir = downloadLocationText.getText().toString();
                       app.getActiveServer().setDownloadDir(downloadDir);
                       listener.onDownloadLocationSelected(getArguments(), downloadDir, startWhenAddedCheckbox.isChecked());
                   }
               })
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

        downloadDirLayout = view.findViewById(R.id.download_dir_content);
        downloadDirProgressbar = (ProgressBar) view.findViewById(R.id.download_dir_progress_bar);

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (currentFreeSpaceRequest != null) currentFreeSpaceRequest.cancel();
        if (currentSessionGetRequest != null) currentSessionGetRequest.cancel();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("down_location", downloadLocationText.getText().toString());
    }

    private void switchServer(final Server server) {
        if (currentSessionGetRequest != null) currentSessionGetRequest.cancel();

        app.setActiveServer(server);

        ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

        downloadDirLayout.setVisibility(View.INVISIBLE);
        downloadDirProgressbar.setVisibility(View.VISIBLE);

        currentSessionGetRequest = new SessionGetRequest();
        ((BaseSpiceActivity) getActivity()).getTransportManager().doRequest(currentSessionGetRequest, new RequestListener<ServerSettings>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                currentSessionGetRequest = null;
                // try again if request was not canceled and dialog was not closed
                if (!(spiceException instanceof RequestCancelledException) && getDialog() != null) {
                    switchServer(server);
                }
            }

            @Override
            public void onRequestSuccess(ServerSettings serverSettings) {
                if (getDialog() == null) return;

                defaultDownloadDir = Strings.nullToEmpty(serverSettings.getDownloadDir());


                String downloadDir = Optional.fromNullable(server.getDownloadDir()).or(defaultDownloadDir);
                if (!downloadDir.equals(downloadLocationText.getText().toString())) {
                    downloadLocationText.setText(downloadDir);
                }

                downloadDirProgressbar.setVisibility(View.INVISIBLE);
                downloadDirLayout.setVisibility(View.VISIBLE);

                currentSessionGetRequest = null;
            }
        });
    }

    private void updateFreeSpaceInfo() {
        if (getDialog() == null) return;

        if (currentFreeSpaceRequest != null) currentFreeSpaceRequest.cancel();
        freeSpaceText.setVisibility(View.INVISIBLE);
        freeSpaceProgressbar.setVisibility(View.VISIBLE);

        ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

        final String path = downloadLocationText.getText().toString();
        currentFreeSpaceRequest = new FreeSpaceRequest(path);
        ((BaseSpiceActivity) getActivity()).getTransportManager().doRequest(currentFreeSpaceRequest,
            new RequestListener<FreeSpace>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Log.e(TAG, "Can't fetch free space for '" + path + "'. " + spiceException.getMessage());

                    AlertDialog dialog = (AlertDialog) getDialog();
                    if (dialog != null) {
                        freeSpaceProgressbar.setVisibility(View.INVISIBLE);
                        freeSpaceText.setVisibility(View.VISIBLE);
                        freeSpaceText.setText(R.string.free_space_unknown);
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }
                    currentFreeSpaceRequest = null;
                }

                @Override
                public void onRequestSuccess(FreeSpace freeSpace) {
                    AlertDialog dialog = (AlertDialog) getDialog();
                    if (dialog != null) {
                        if (freeSpace.getSizeInBytes() >= 0) {
                            freeSpaceText.setText(getString(
                                    R.string.free_space, TextUtils.displayableSize(freeSpace.getSizeInBytes())));
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                        } else {
                            freeSpaceText.setText(getString(R.string.no_directory));
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                        }
                        freeSpaceProgressbar.setVisibility(View.INVISIBLE);
                        freeSpaceText.setVisibility(View.VISIBLE);
                    }
                    currentFreeSpaceRequest = null;
                }
            });
    }

    public interface OnDownloadLocationSelectedListener {
        void onDownloadLocationSelected(Bundle args, String downloadDir, boolean startWhenAdded);
    }

    private static class ServersSpinnerAdapter extends BaseAdapter {

        private final List<Server> servers;

        public ServersSpinnerAdapter(List<Server> servers) {
            this.servers = servers;
        }

        @Override
        public int getCount() {
            return servers.size();
        }

        @Override
        public Server getItem(int position) {
            return servers.get(position);
        }

        public int getItemPosition(Server server) {
            return servers.indexOf(server);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent, android.R.layout.simple_spinner_item);
        }

        public View getView(int position, View convertView, ViewGroup parent, int layoutRes) {
            View view = convertView;
            if (view == null) {
                LayoutInflater li = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(layoutRes, parent, false);
            }

            TextView text = (TextView) view.findViewById(android.R.id.text1);
            text.setText(getItem(position).getName());

            return view;
        }
    }
}
