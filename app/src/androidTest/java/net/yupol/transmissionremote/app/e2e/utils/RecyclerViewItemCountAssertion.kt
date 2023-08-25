package net.yupol.transmissionremote.app.e2e.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import com.google.common.truth.Truth.assertThat

class RecyclerViewItemCountAssertion(private val expectedCount: Int) : ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        noViewFoundException?.let { throw it }

        val recyclerView = view as RecyclerView
        assertThat(recyclerView.adapter?.itemCount).isEqualTo(expectedCount)
    }
}
