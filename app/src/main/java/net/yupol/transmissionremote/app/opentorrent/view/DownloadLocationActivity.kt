package net.yupol.transmissionremote.app.opentorrent.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import net.yupol.transmissionremote.app.BaseMvpActivity
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.opentorrent.presenter.DownloadLocationPresenter
import net.yupol.transmissionremote.app.opentorrent.view.DownloadLocationAdapter.Listener
import net.yupol.transmissionremote.app.utils.DividerItemDecoration

class DownloadLocationActivity:
        BaseMvpActivity<DownloadLocationView, DownloadLocationPresenter>(),
        DownloadLocationView
{

    @BindView(R.id.pinned_group) lateinit var pinnedGroup: Group
    @BindView(R.id.pinned_list) lateinit var pinnedList: RecyclerView

    private lateinit var pinnedAdapter: DownloadLocationAdapter
    private lateinit var othersAdapter: DownloadLocationAdapter

    override fun createPresenter(): DownloadLocationPresenter {
        return DownloadLocationPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.download_location_activity)
        ButterKnife.bind(this)

        pinnedAdapter = DownloadLocationAdapter(pinned = true, listener =  object : Listener {
            override fun onLocationSelected(location: String) = presenter.onLocationSelected(location)
            override fun onLocationDeselected(location: String) = presenter.onLocationDeselected()
            override fun onPinButtonClicked(location: String) = presenter.onLocationPinned(location)
        })

        othersAdapter = DownloadLocationAdapter(pinned = true, listener =  object : Listener {
            override fun onLocationSelected(location: String) = presenter.onLocationSelected(location)
            override fun onLocationDeselected(location: String) = presenter.onLocationDeselected()
            override fun onPinButtonClicked(location: String) = presenter.onLocationUnpinned(location)
        })

        pinnedList.layoutManager = LinearLayoutManager(this)
        pinnedList.addItemDecoration(DividerItemDecoration(this))
        pinnedList.adapter = pinnedAdapter
    }

    companion object {

        fun intent(context: Context): Intent {
            return Intent(context, DownloadLocationActivity::class.java)
        }
    }
}
