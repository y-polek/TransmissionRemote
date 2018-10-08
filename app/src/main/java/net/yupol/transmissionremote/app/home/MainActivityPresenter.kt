package net.yupol.transmissionremote.app.home

import android.util.Log
import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import net.yupol.transmissionremote.domain.usecase.TorrentListInteractor

class MainActivityPresenter(private val interactor: TorrentListInteractor): MvpNullObjectBasePresenter<MainActivityView>() {

    companion object {
        const val TAG = "MainActivityPresenter"
    }

    private var torrentListDisposable: Disposable? = null

    override fun attachView(view: MainActivityView) {
        super.attachView(view)

        torrentListDisposable = interactor.loadTorrentList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    Log.d(TAG, "Torrents: $it")
                }
    }

    override fun detachView() {
        super.detachView()

        torrentListDisposable?.dispose()
    }
}
