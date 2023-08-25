package net.yupol.transmissionremote.app;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment implements OnBackPressedListener {

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
