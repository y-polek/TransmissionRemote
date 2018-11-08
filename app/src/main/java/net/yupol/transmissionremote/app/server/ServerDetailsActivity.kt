package net.yupol.transmissionremote.app.server

import android.content.Context
import android.content.Intent
import android.os.Bundle
import net.yupol.transmissionremote.app.BaseActivity
import net.yupol.transmissionremote.app.R

class ServerDetailsActivity: BaseActivity(), ServerDetailsFragment.OnServerActionListener {
    companion object {
        private const val KEY_SERVER_NAME = "key_server_name"
        private const val FRAGMENT_TAG = "fragment_tag"

        fun intent(context: Context, serverName: String): Intent {
            return Intent(context, ServerDetailsActivity::class.java)
                    .putExtra(KEY_SERVER_NAME, serverName)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.server_details_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            val fragment = ServerDetailsFragment.edit(intent.getStringExtra(KEY_SERVER_NAME))
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment, FRAGMENT_TAG)
                    .commit()
        }
    }

    override fun onSaveServerRequested() {
        TODO("not implemented")
    }

}
