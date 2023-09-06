package net.yupol.transmissionremote.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof OnBackPressedListener && fragment.isVisible()) {
                boolean handled = ((OnBackPressedListener) fragment).onBackPressed();
                if (handled) return true;
            }
        }
        return false;
    }
}
