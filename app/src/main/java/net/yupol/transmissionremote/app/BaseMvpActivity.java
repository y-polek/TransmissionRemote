package net.yupol.transmissionremote.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.delegate.ActivityMvpDelegate;
import com.hannesdorfmann.mosby3.mvp.delegate.ActivityMvpDelegateImpl;
import com.hannesdorfmann.mosby3.mvp.delegate.MvpDelegateCallback;

import net.yupol.transmissionremote.app.mvp.MvpViewCallback;

import java.util.List;

public abstract class BaseMvpActivity<VIEW extends MvpView, PRESENTER extends MvpPresenter<VIEW>>
        extends AppCompatActivity
        implements MvpView, MvpDelegateCallback<VIEW, PRESENTER>
{
    protected ActivityMvpDelegate<VIEW, PRESENTER> mvpDelegate;
    protected PRESENTER presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpDelegate().onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getMvpDelegate().onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getMvpDelegate().onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getMvpDelegate().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMvpDelegate().onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMvpDelegate().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getMvpDelegate().onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getMvpDelegate().onRestart();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        getMvpDelegate().onContentChanged();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getMvpDelegate().onPostCreate(savedInstanceState);
    }

    /**
     * Instantiate a presenter instance
     *
     * @return The {@link MvpPresenter} for this view
     */
    @NonNull
    public abstract PRESENTER createPresenter();

    @NonNull protected ActivityMvpDelegate<VIEW, PRESENTER> getMvpDelegate() {
        if (mvpDelegate == null) {
            mvpDelegate = new ActivityMvpDelegateImpl<VIEW, PRESENTER>(this, this, true) {
                @Override
                public void onStart() {
                    super.onStart();
                    if (presenter instanceof MvpViewCallback) {
                        ((MvpViewCallback) presenter).viewStarted();
                    }
                }

                @Override
                public void onStop() {
                    super.onStop();
                    if (presenter instanceof MvpViewCallback) {
                        ((MvpViewCallback) presenter).viewStopped();
                    }
                }
            };
        }

        return mvpDelegate;
    }

    @NonNull @Override public PRESENTER getPresenter() {
        return presenter;
    }

    @Override public void setPresenter(@NonNull PRESENTER presenter) {
        this.presenter = presenter;
    }

    @NonNull @Override public VIEW getMvpView() {
        return (VIEW) this;
    }

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
