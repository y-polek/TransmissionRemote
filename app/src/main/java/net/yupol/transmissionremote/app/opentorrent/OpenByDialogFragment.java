package net.yupol.transmissionremote.app.opentorrent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import net.yupol.transmissionremote.app.R;

public class OpenByDialogFragment extends DialogFragment {

    private OnOpenTorrentSelectedListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.open_torrent)
               .setItems(R.array.open_torrent_by_entries, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       if (which == 0) { // by file
                           listener.onOpenTorrentByFile();
                       } else if (which == 1) { // by address
                           listener.onOpenTorrentByAddress();
                       }
                   }
               });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (OnOpenTorrentSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + OnOpenTorrentSelectedListener.class.getSimpleName());
        }
    }

    public interface OnOpenTorrentSelectedListener {
        void onOpenTorrentByFile();
        void onOpenTorrentByAddress();
    }
}
