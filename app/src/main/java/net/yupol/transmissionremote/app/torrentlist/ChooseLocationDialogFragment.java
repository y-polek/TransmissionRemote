package net.yupol.transmissionremote.app.torrentlist;


import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.LayoutInflater;

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

    private OnLocationSelectedListener listener;
    private SetLocationDialogBinding binding;
    private FreeSpaceRequest runningFreeSpaceRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (OnLocationSelectedListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Target fragment must implement "
                    + OnLocationSelectedListener.class.getName());
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
        binding.locationEdit.setText(TransmissionRemote.getInstance().getDefaultDownloadDir());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.choose_location);
        builder.setView(binding.getRoot());
        builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onLocationSelected(binding.locationEdit.getText().toString(),
                            binding.moveDataCheckbox.isChecked());
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
                } else { // Retry
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
