package net.yupol.transmissionremote.app.home

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import net.yupol.transmissionremote.app.torrentlist.TorrentsRepository
import javax.inject.Inject

class HomeViewModel(private val repository: TorrentsRepository): ViewModel() {

    class Factory @Inject constructor(private val repository: TorrentsRepository)
        : ViewModelProvider.Factory
    {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }

    }
}