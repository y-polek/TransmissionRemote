package net.yupol.transmissionremote.app.opentorrent.view

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import net.yupol.transmissionremote.app.BaseMvpActivity
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.opentorrent.presenter.OpenTorrentFilePresenter
import net.yupol.transmissionremote.app.torrentdetails.BreadcrumbView
import net.yupol.transmissionremote.app.utils.DividerItemDecoration
import net.yupol.transmissionremote.model.Dir
import java.io.File
import java.util.*
import javax.inject.Inject

class OpenTorrentFileActivity: BaseMvpActivity<OpenTorrentFileView, OpenTorrentFilePresenter>(),
        OpenTorrentFileView, FilesAdapter.Listener {

    @BindView(R.id.name_text) lateinit var nameText: TextView
    @BindView(R.id.size_text) lateinit var sizeText: TextView
    @BindView(R.id.breadcrumb_view) lateinit var breadcrumbView: BreadcrumbView
    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView
    @BindView(R.id.download_to_text) lateinit var downloadDirText: TextView
    @BindView(R.id.trash_torrent_file_checkbox) lateinit var trashTorrentFileCheckbox: CheckBox
    @BindView(R.id.start_when_added_checkbox) lateinit var startWhenAddedCheckbox: CheckBox

    @Inject lateinit var presenterFactory: OpenTorrentFilePresenter.Factory

    override fun createPresenter(): OpenTorrentFilePresenter {
        val torrentFilePath = intent?.getStringExtra(KEY_TORRENT_FILE_PATH)
                ?: throw IllegalArgumentException("Torrent file must be passed as an argument")
        return presenterFactory.create(torrentFilePath)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TransmissionRemote.getInstance().appComponent().serverManager().serverComponent?.inject(this)
        super.onCreate(savedInstanceState)
        if (!resources.getBoolean(R.bool.is_tablet)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setContentView(R.layout.open_torrent_file_activity)
        ButterKnife.bind(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.open_torrent)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this))

        breadcrumbView.setOnNodeSelectedListener { position ->
            presenter.onBreadcrumbClicked(position)
        }

        presenter.viewCreated()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun showNameText(text: String) {
        nameText.text = text
    }

    override fun showSizeText(text: String) {
        sizeText.text = text
    }

    override fun showDir(dir: Dir) {
        recyclerView.adapter = FilesAdapter(presenter.torrentFile, dir, this)
    }

    override fun showBreadcrumbs(path: Stack<Dir>) {
        breadcrumbView.setPath(path)
    }

    override fun getDownloadDirectory(): String {
        return downloadDirText.text.trim().toString()
    }

    override fun isTrashTorrentFileChecked() = trashTorrentFileCheckbox.isChecked

    override fun isStartWhenAddedChecked() = startWhenAddedCheckbox.isChecked

    override fun updateFileList() {
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onDirectorySelected(dir: Dir) {
        presenter.onDirectorySelected(dir)
    }

    override fun onFileSelectionChanged() {
        presenter.onFileSelectionChanged()
    }

    @OnClick(R.id.select_all_button)
    fun onSelectAllFilesClicked() {
        presenter.onSelectAllFilesClicked()
    }

    @OnClick(R.id.select_none_button)
    fun onSelectNoneFilesClicked() {
        presenter.onSelectNoneFilesClicked()
    }

    @OnClick(R.id.add_button)
    fun onAddButtonClicked() {
        presenter.onAddButtonClicked()
    }

    companion object {

        private const val KEY_TORRENT_FILE_PATH = "key_torrent_file_path"

        fun intent(context: Context, file: File): Intent {
            return Intent(context, OpenTorrentFileActivity::class.java).apply {
                putExtra(KEY_TORRENT_FILE_PATH, file.absolutePath)
            }
        }
    }
}
