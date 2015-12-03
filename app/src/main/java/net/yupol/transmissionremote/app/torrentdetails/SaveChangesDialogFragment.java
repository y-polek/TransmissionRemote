package net.yupol.transmissionremote.app.torrentdetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import net.yupol.transmissionremote.app.R;

public class SaveChangesDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.save_changes_question);
        builder.setPositiveButton(R.string.save_changes_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity activity = getActivity();
                if (activity instanceof SaveDiscardListener) {
                    ((SaveDiscardListener) activity).onSavePressed();
                }
            }
        });
        builder.setNegativeButton(R.string.save_changes_discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity activity = getActivity();
                if (getActivity() instanceof SaveDiscardListener) {
                    ((SaveDiscardListener) activity).onDiscardPressed();
                }
            }
        });
        builder.setNeutralButton(android.R.string.cancel, null);
        return builder.create();
    }

    public interface SaveDiscardListener {
        void onSavePressed();
        void onDiscardPressed();
    }
}
