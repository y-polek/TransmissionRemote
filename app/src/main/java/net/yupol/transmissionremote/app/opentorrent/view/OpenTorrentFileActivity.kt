package net.yupol.transmissionremote.app.opentorrent.view

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.*
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import net.yupol.transmissionremote.app.BaseMvpActivity
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.opentorrent.presenter.OpenTorrentFilePresenter
import net.yupol.transmissionremote.app.torrentdetails.BreadcrumbView
import net.yupol.transmissionremote.app.utils.DividerItemDecoration
import net.yupol.transmissionremote.model.Dir
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OpenTorrentFileActivity: BaseMvpActivity<OpenTorrentFileView, OpenTorrentFilePresenter>(),
        OpenTorrentFileView, FilesAdapter.Listener {

    @BindView(R.id.name_text) lateinit var nameText: TextView
    @BindView(R.id.size_text) lateinit var sizeText: TextView
    @BindView(R.id.breadcrumb_view) lateinit var breadcrumbView: BreadcrumbView
    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView
    @BindView(R.id.download_to_text) lateinit var downloadDirText: TextView
    @BindView(R.id.free_space_text) lateinit var freeSpaceText: TextView
    @BindView(R.id.free_space_progress_bar) lateinit var freeSpaceProgressbar: ProgressBar
    @BindView(R.id.trash_torrent_file_checkbox) lateinit var trashTorrentFileCheckbox: CheckBox
    @BindView(R.id.start_when_added_checkbox) lateinit var startWhenAddedCheckbox: CheckBox

    @BindColor(R.color.text_color_secondary) @JvmField var secondaryTextColor: Int = 0
    @BindColor(R.color.highlight_color) @JvmField var accentColor: Int = 0

    @Inject lateinit var presenterFactory: OpenTorrentFilePresenter.Factory

    private var downloadDirTextSubscription: Disposable? = null

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

    override fun onResume() {
        super.onResume()

        downloadDirTextSubscription = downloadDirText.textChanges()
                .debounce(500, TimeUnit.MILLISECONDS)
                .map { it.trim().toString() }
                .distinct()
                .observeOn(mainThread())
                .subscribe { text ->
                    presenter.onDownloadLocationTextChanged(text)
                }
    }

    override fun onPause() {
        downloadDirTextSubscription?.dispose()
        super.onPause()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // region OpenTorrentFileView implementation

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

    override fun updateFileList() {
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun setDownloadDirectory(text: String) {
        downloadDirText.text = text
    }

    override fun setTrashTorrentFile(trash: Boolean) {
        trashTorrentFileCheckbox.isChecked = trash
    }

    override fun setStartTorrentWhenAdded(start: Boolean) {
        startWhenAddedCheckbox.isChecked = start
    }

    override fun showFreeSpaceLoading() {
        freeSpaceProgressbar.visibility = VISIBLE
    }

    override fun hideFreeSpaceLoading() {
        freeSpaceProgressbar.visibility = GONE
    }

    override fun showFreeSpaceText(text: String, highlight: Boolean) {
        freeSpaceText.text = text
        freeSpaceText.setTextColor(if (highlight) accentColor else secondaryTextColor)
    }

    override fun showDownloadLocationHistory() {
        startActivity(DownloadLocationActivity.intent(this))
    }

    // endregion

    // region Event Listeners

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

    @OnClick(R.id.download_location_history_button)
    fun onDownloadLocationHistoryButtonClicked() {
        presenter.onDownloadLocationHistoryButtonClicked()
    }

    @OnClick(R.id.add_button)
    fun onAddButtonClicked() {
        presenter.onAddButtonClicked()
    }

    @OnCheckedChanged(R.id.trash_torrent_file_checkbox)
    fun onTrashTorrentFileChanged(checked: Boolean) {
        presenter.onTrashTorrentFileChanged(checked)
    }

    @OnCheckedChanged(R.id.start_when_added_checkbox)
    fun onStartTorrentWhenAddedChanged(checked: Boolean) {
        presenter.onStartTorrentWhenAddedChanged(checked)
    }

    // endregion

    companion object {

        private const val KEY_TORRENT_FILE_PATH = "key_torrent_file_path"

        fun intent(context: Context, file: File): Intent {
            return Intent(context, OpenTorrentFileActivity::class.java).apply {
                putExtra(KEY_TORRENT_FILE_PATH, file.absolutePath)
            }
        }
    }
}
