package net.yupol.transmissionremote.app.utils;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class DialogUtils {

    public static void showDialogAllowingStateLoss(
            DialogFragment dialogFragment, FragmentManager fragmentManager, String tag) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(dialogFragment, tag);
        ft.commitAllowingStateLoss();
    }
}
