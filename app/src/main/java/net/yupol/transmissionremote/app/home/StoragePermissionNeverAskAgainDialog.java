package net.yupol.transmissionremote.app.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import net.yupol.transmissionremote.app.R;

public class StoragePermissionNeverAskAgainDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setMessage(R.string.storage_permission_never_ask_again_message)
                .setPositiveButton(R.string.app_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
