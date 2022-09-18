package net.yupol.transmissionremote.app.e2e.utils

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId

fun assertIdDisplayed(@IdRes id: Int) {
    onView(withId(id)).check(matches(isDisplayed()))
}

fun assertIdsDisplayed(@IdRes vararg ids: Int) = ids.forEach(::assertIdDisplayed)
