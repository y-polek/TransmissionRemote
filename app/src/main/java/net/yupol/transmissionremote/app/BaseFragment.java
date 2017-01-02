package net.yupol.transmissionremote.app;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment implements OnBackPressedListener {

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
