package net.yupol.transmissionremote.app.torrentlist;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.LayoutInflater;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.databinding.SetLocationDialogBinding;
import net.yupol.transmissionremote.app.utils.SimpleTextWatcher;
import net.yupol.transmissionremote.app.utils.TextUtils;
import net.yupol.transmissionremote.data.api.Transport;
import net.yupol.transmissionremote.data.api.model.FreeSpaceEntity;
import net.yupol.transmissionremote.data.api.rpc.RpcFailureException;
import net.yupol.transmissionremote.model.mapper.ServerMapper;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChooseLocationDialogFragment extends DialogFragment {

    public static final String ARG_INITIAL_LOCATION = "arg_initial_location";

    private OnLocationSelectedListener listener;
    private SetLocationDialogBinding binding;
    private Disposable runningFreeSpaceRequest;
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
            initialLocation = TransmissionRemote.getInstance().getDefaultDownloadDir();
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
        if (runningFreeSpaceRequest != null) runningFreeSpaceRequest.dispose();
    }

    private void updateFreeSpace(String path) {
        if (runningFreeSpaceRequest != null) runningFreeSpaceRequest.dispose();
        binding.setLoadingInProgress(true);

        new Transport(ServerMapper.toDomain(TransmissionRemote.getInstance().getActiveServer())).api().freeSpace(path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<FreeSpaceEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        runningFreeSpaceRequest = d;
                    }

                    @Override
                    public void onSuccess(FreeSpaceEntity freeSpace) {
                        runningFreeSpaceRequest = null;
                        binding.setLoadingInProgress(false);
                        binding.freeSpaceText.setText(getString(R.string.free_space,
                                TextUtils.displayableSize(freeSpace.getSize())));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof RpcFailureException) {
                            runningFreeSpaceRequest = null;
                            binding.setLoadingInProgress(false);
                            String failureMessage = e.getMessage();
                            binding.freeSpaceText.setText(failureMessage);
                        }
                    }
                });
    }

    public interface OnLocationSelectedListener {
        void onLocationSelected(String path, boolean moveData);
    }
}
