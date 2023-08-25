package net.yupol.transmissionremote.app;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        boolean handled = handleBackPressByFragments();
        if (!handled) super.onBackPressed();
    }

    /**
     * @return {@code true} if back press handled by visible fragments
     */
    protected boolean handleBackPressByFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof OnBackPressedListener && fragment.isVisible()) {
                    boolean handled = ((OnBackPressedListener) fragment).onBackPressed();
                    if (handled) return true;
                }
            }
        }
        return false;
    }
}
