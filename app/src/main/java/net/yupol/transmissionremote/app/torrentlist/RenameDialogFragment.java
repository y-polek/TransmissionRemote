package net.yupol.transmissionremote.app.torrentlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.yupol.transmissionremote.app.R;

public class RenameDialogFragment extends DialogFragment {

    private static final String ARG_TORRENT_ID = "arg_torrent_id";
    private static final String ARG_PATH = "arg_path";
    private static final String ARG_CURRENT_NAME = "arg_name";
    private String oldName;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.rename_dialog, null);
        final EditText nameText = view.findViewById(R.id.text);

        builder.setView(view)
               .setTitle(R.string.rename_file)
               .setPositiveButton(R.string.rename, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       OnNameSelectedListener listener = getListener();
                       if (listener != null) {
                           int torrentId = getArguments().getInt(ARG_TORRENT_ID);
                           String path = getArguments().getString(ARG_PATH);
                           String newName = nameText.getText().toString().trim();
                           listener.onNameSelected(torrentId, path, newName);
                       }
                   }
               })
               .setNegativeButton(android.R.string.cancel, null);

        oldName = getArguments().getString(ARG_CURRENT_NAME, "");
        nameText.setText(oldName);
        nameText.setSelection(oldName.length());

        final AlertDialog dialog = builder.create();

        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Button openButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                String newName = s.toString().trim();
                updateRenameButton(openButton, newName);
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                String newName = nameText.getText().toString().trim();
                updateRenameButton(button, newName);
            }
        });

        return dialog;
    }

    private void updateRenameButton(Button button, String newName) {
        button.setEnabled(!newName.isEmpty() && !newName.equals(oldName));
    }

    private OnNameSelectedListener getListener() {
        Fragment targetFragment = getParentFragment();
        if (targetFragment instanceof OnNameSelectedListener) {
            return (OnNameSelectedListener) targetFragment;
        } else {
            Activity activity = getActivity();
            if (activity instanceof OnNameSelectedListener) {
                return (OnNameSelectedListener) activity;
            }
        }
        return null;
    }

    public static RenameDialogFragment newInstance(int torrentId, String path, String currentName) {
        RenameDialogFragment fragment = new RenameDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TORRENT_ID, torrentId);
        args.putString(ARG_PATH, path);
        args.putString(ARG_CURRENT_NAME, currentName);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnNameSelectedListener {
        void onNameSelected(int torrentId, String path, String name);
    }
}
