package net.yupol.transmissionremote.app.torrentlist;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.databinding.SetLocationDialogBinding;
import net.yupol.transmissionremote.app.model.json.FreeSpace;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.FreeSpaceRequest;
import net.yupol.transmissionremote.app.transport.request.ResponseFailureException;
import net.yupol.transmissionremote.app.utils.SimpleTextWatcher;
import net.yupol.transmissionremote.app.utils.TextUtils;

public class ChooseLocationDialogFragment extends DialogFragment {

    public static final String ARG_INITIAL_LOCATION = "arg_initial_location";

    private OnLocationSelectedListener listener;
    private SetLocationDialogBinding binding;
    private FreeSpaceRequest runningFreeSpaceRequest;
    private String initialLocation;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof OnLocationSelectedListener) {
            listener = (OnLocationSelectedListener) targetFragment;
        } else {
            Activity activity = getActivity();
            if (activity instanceof OnLocationSelectedListener) {
                listener = (OnLocationSelectedListener) activity;
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            initialLocation = args.getString(ARG_INITIAL_LOCATION);
        }
        if (initialLocation == null) {
            initialLocation = TransmissionRemote.getInstance().defaultDownloadDir;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.set_location_dialog, null, false);
        binding.locationEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateFreeSpace(s.toString());
            }
        });
        binding.locationEdit.setText(initialLocation);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.choose_location);
        builder.setView(binding.getRoot());
        builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onLocationSelected(binding.locationEdit.getText().toString(),
                                binding.moveDataCheckbox.isChecked());
                    }
                }
            });
        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (runningFreeSpaceRequest != null) runningFreeSpaceRequest.cancel();
    }

    private void updateFreeSpace(String path) {
        if (runningFreeSpaceRequest != null) runningFreeSpaceRequest.cancel();
        binding.setLoadingInProgress(true);
        runningFreeSpaceRequest = new FreeSpaceRequest(path);

        getTransportManager().doRequest(runningFreeSpaceRequest, new RequestListener<FreeSpace>() {
            @Override
            public void onRequestSuccess(FreeSpace freeSpace) {
                runningFreeSpaceRequest = null;
                binding.setLoadingInProgress(false);
                binding.freeSpaceText.setText(getString(R.string.free_space,
                        TextUtils.displayableSize(freeSpace.getSizeInBytes())));
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                if (spiceException.getCause() instanceof ResponseFailureException) {
                    runningFreeSpaceRequest = null;
                    binding.setLoadingInProgress(false);
                    String failureMessage = ((ResponseFailureException) spiceException.getCause()).getFailureMessage();
                    binding.freeSpaceText.setText(failureMessage);
                } else if (!(spiceException instanceof RequestCancelledException)) { // Retry if not canceled
                    getTransportManager().doRequest(runningFreeSpaceRequest, this);
                }
            }
        });
    }

    private TransportManager getTransportManager() {
        return ((BaseSpiceActivity) getActivity()).getTransportManager();
    }

    public interface OnLocationSelectedListener {
        void onLocationSelected(String path, boolean moveData);
    }
}
