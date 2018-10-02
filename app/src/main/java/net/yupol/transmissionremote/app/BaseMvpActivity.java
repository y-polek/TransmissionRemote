package net.yupol.transmissionremote.app;

import android.support.v4.app.Fragment;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

public abstract class BaseMvpActivity<VIEW extends MvpView, PRESENTER extends MvpPresenter<VIEW>> extends MvpActivity<VIEW, PRESENTER> {

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
