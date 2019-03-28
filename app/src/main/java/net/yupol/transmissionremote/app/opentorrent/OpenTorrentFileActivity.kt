package net.yupol.transmissionremote.app.opentorrent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.turn.ttorrent.common.Torrent
import net.yupol.transmissionremote.app.BaseActivity
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.torrentdetails.BreadcrumbView
import net.yupol.transmissionremote.app.utils.DividerItemDecoration
import net.yupol.transmissionremote.app.utils.TextUtils
import net.yupol.transmissionremote.app.utils.fileStats
import net.yupol.transmissionremote.app.utils.files
import net.yupol.transmissionremote.model.Dir
import java.io.File
import java.util.*

class OpenTorrentFileActivity: BaseActivity(), FilesAdapter.Listener {

    @BindView(R.id.name_text) lateinit var nameText: TextView
    @BindView(R.id.size_text) lateinit var sizeText: TextView
    @BindView(R.id.breadcrumb_view) lateinit var breadcrumbView: BreadcrumbView
    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView
    @BindView(R.id.trash_torrent_file_checkbox) lateinit var trashTorrentFileCheckbox: CheckBox
    @BindView(R.id.start_when_added_checkbox) lateinit var startWhenAddedCheckbox: CheckBox

    private val torrentFile: Torrent by lazy {
        val path = intent?.getStringExtra(KEY_TORRENT_FILE_PATH)
                ?: throw IllegalArgumentException("Torrent file must be passed as an argument")
        return@lazy Torrent(File(path).readBytes(), false)
    }
    private val files by lazy { torrentFile.files() }
    private val fileStats by lazy { torrentFile.fileStats() }
    private val rootDir by lazy { Dir.createFileTree(files) }

    private lateinit var currentDir: Dir
    private val path: Stack<Dir> = Stack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.open_torrent_file_activity)
        ButterKnife.bind(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.open_torrent)

        nameText.text = torrentFile.name
        sizeText.text = TextUtils.displayableSize(torrentFile.size)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this))

        breadcrumbView.setOnNodeSelectedListener { position ->
            if (position >= path.size - 1) return@setOnNodeSelectedListener
            for (i in path.size - 1 downTo position + 1) {
                path.removeAt(i)
            }
            breadcrumbView.setPath(path)
            currentDir = path.peek()
            recyclerView.adapter = FilesAdapter(torrentFile, currentDir, this@OpenTorrentFileActivity)
        }

        currentDir = rootDir
        path.push(rootDir)
        breadcrumbView.setPath(path)
        recyclerView.adapter = FilesAdapter(torrentFile, rootDir, this)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDirectorySelected(dir: Dir) {
        currentDir = dir
        path.push(dir)
        breadcrumbView.setPath(path)
        recyclerView.adapter = FilesAdapter(torrentFile, dir, this)
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
