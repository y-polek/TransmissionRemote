package net.yupol.transmissionremote.app.utils;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class DialogUtils {

    public static void showDialogAllowingStateLoss(
            DialogFragment dialogFragment, FragmentManager fragmentManager, String tag) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(dialogFragment, tag);
        ft.commitAllowingStateLoss();
    }
}
