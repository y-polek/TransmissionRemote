package net.yupol.transmissionremote.app.e2e.utils

import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.withId

fun clickId(@IdRes id: Int) {
    onView(withId(id)).perform(click())
}

fun clickItemAtPosition(@IdRes id: Int, position: Int) {
    onView(withId(id)).perform(actionOnItemAtPosition<ViewHolder>(position, click()))
}

fun inputText(@IdRes id: Int, text: String) {
    onView(withId(id)).perform(
        replaceText(text),
        closeSoftKeyboard()
    )
}
