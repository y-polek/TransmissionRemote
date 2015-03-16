package net.yupol.transmissionremote.app.opentorrent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

import net.yupol.transmissionremote.app.R;

import javax.annotation.Nonnull;

public class OpenByDialogFragment extends DialogFragment {

    private OnSelectionListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.open_torrent)
               .setItems(R.array.open_torrent_by_entries, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       if (listener == null) {
                           throw new IllegalStateException("There is no selection listener");
                       }
                       if (which == 0) { // by file
                           listener.byFile();
                       } else if (which == 1) { // by address
                           listener.byAddress();
                       }
                   }
               });
        return builder.create();
    }

    public void show(FragmentManager fm, String tag, @Nonnull OnSelectionListener listener) {
        this.listener = listener;
        show(fm, tag);
    }

    public static interface OnSelectionListener {
        void byFile();
        void byAddress();
    }
}
