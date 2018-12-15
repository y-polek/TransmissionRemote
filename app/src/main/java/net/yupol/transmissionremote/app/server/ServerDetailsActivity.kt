package net.yupol.transmissionremote.app.server

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import net.yupol.transmissionremote.app.BaseActivity
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.domain.repository.ServerRepository
import javax.inject.Inject

class ServerDetailsActivity: BaseActivity() {

    companion object {
        private const val KEY_SERVER_NAME = "key_server_name"
        private const val FRAGMENT_TAG = "fragment_tag"

        @JvmStatic
        fun intent(context: Context, serverName: String): Intent {
            return Intent(context, ServerDetailsActivity::class.java)
                    .putExtra(KEY_SERVER_NAME, serverName)
        }
    }

    @Inject lateinit var repo: ServerRepository

    private lateinit var serverName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        TransmissionRemote.getInstance().appComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.server_details_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        serverName = intent.getStringExtra(KEY_SERVER_NAME) ?: throw IllegalArgumentException("Server name must be passed as an argument")
        if (savedInstanceState == null) {
            val fragment = ServerDetailsFragment.edit(serverName)
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment, FRAGMENT_TAG)
                    .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.server_details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveServer()
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
                finish()
                true
            }
            R.id.action_remove -> {
                promptRemove()
                true
            }
            else -> false
        }
    }

    override fun onBackPressed() {
        if (findFragment().hasChanges()) {
            promptSave()
        } else {
            super.onBackPressed()
        }
    }

    private fun findFragment(): ServerDetailsFragment {
        return supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as ServerDetailsFragment
    }

    private fun promptSave() {
        AlertDialog.Builder(this)
                .setMessage(R.string.save_changes_question)
                .setPositiveButton(R.string.save_changes_save) { _, _ ->
                    saveServer()
                    finish()
                }
                .setNegativeButton(R.string.save_changes_discard) { _, _ ->
                    finish()
                }
                .show()
    }

    private fun saveServer() {
        repo.updateServer(withName = serverName, server = findFragment().newServer)
    }

    private fun promptRemove() {
        AlertDialog.Builder(this)
                .setMessage(R.string.remove_server_confirmation)
                .setPositiveButton(R.string.remove) { _, _ ->
                    removeServer()
                    finish()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    private fun removeServer() {
        repo.removeServer(withName = serverName)
    }
}
