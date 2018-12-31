package net.yupol.transmissionremote.app

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onBackPressed() {
        val handled = handleBackPressByFragments()
        if (!handled) super.onBackPressed()
    }

    /**
     * @return `true` if back press handled by visible fragments
     */
    protected fun handleBackPressByFragments(): Boolean {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is OnBackPressedListener && fragment.isVisible) {
                val handled = (fragment as OnBackPressedListener).onBackPressed()
                if (handled) return true
            }
        }
        return false
    }
}
