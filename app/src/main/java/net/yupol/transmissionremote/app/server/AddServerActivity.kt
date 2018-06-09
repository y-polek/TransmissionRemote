package net.yupol.transmissionremote.app.server

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.add_server_activity.*
import net.yupol.transmissionremote.app.R

class AddServerActivity : AppCompatActivity() {

    companion object {

        const val PARAM_CANCELABLE = "param_cancelable"
        const val EXTRA_SEVER = "extra_server"

        fun intent(context: Context): Intent {
            return Intent(context, AddServerActivity::class.java)
        }
    }

    private var serverDetailsFragment: ServerDetailsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_server_activity)
        ButterKnife.bind(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        val fm = supportFragmentManager
        serverDetailsFragment = fm.findFragmentById(R.id.addServerFragmentContainer) as ServerDetailsFragment?
        if (serverDetailsFragment == null) {
            val ft = fm.beginTransaction()
            serverDetailsFragment = ServerDetailsFragment()
            ft.add(R.id.addServerFragmentContainer, serverDetailsFragment)
            ft.commit()
        }

        val isCancelable = intent.getBooleanExtra(PARAM_CANCELABLE, true)
        cancelButton.visibility = if (isCancelable) View.VISIBLE else View.GONE
    }

    @OnClick(R.id.okButton)
    fun onOkButtonClicked() {
        val server = serverDetailsFragment!!.newServer
        if (server != null) {
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_SEVER, server))
            finish()
        }
    }

    @OnClick(R.id.cancelButton)
    fun onCancelButtonClicked() {
        cancel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                cancel()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cancel() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
