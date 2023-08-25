package net.yupol.transmissionremote.app.e2e.utils

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not

//region View is displayed
fun assertViewDisplayed(matcher: Matcher<View>) {
    onView(matcher).let {
        try {
            it.perform(scrollTo())
        } catch (e: PerformException) {
            it
        }
    }.check(matches(isDisplayed()))
}

fun assertViewWithIdDisplayed(@IdRes id: Int) = assertViewDisplayed(withId(id))

fun assertViewWithTextDisplayed(text: String) = assertViewDisplayed(withText(text))

fun assertViewOfTypeDisplayed(type: Class<out View>) = assertViewDisplayed(isAssignableFrom(type))
//endregion

//region View is hidden
fun assertViewHidden(matcher: Matcher<View>) {
    try {
        onView(matcher).check(matches(not(isDisplayed())))
    } catch (e: NoMatchingViewException) {
        onView(matcher).check(doesNotExist())
    }
}

fun assertViewWithIdHidden(@IdRes id: Int) = assertViewHidden(allOf(withId(id)))

fun assertViewOfTypeHidden(type: Class<out View>) = assertViewHidden(allOf(isAssignableFrom(type)))
//endregion

//region View exists
fun assertViewExists(matcher: Matcher<View>) {
    onView(matcher).check(matches(not(doesNotExist())))
}

fun assertViewWithIdExists(@IdRes id: Int) = assertViewExists(withId(id))
//endregion

//region RecyclerView
fun recyclerViewHasItemCount(matcher: Matcher<View>, expectedCount: Int) {
    onView(matcher).check(RecyclerViewItemCountAssertion(expectedCount))
}

fun recyclerViewWithIdHasItemCount(@IdRes id: Int, expectedCount: Int) {
    recyclerViewHasItemCount(withId(id), expectedCount)
}
//endregion

//region RecyclerView
fun assertViewWithIdHasText(@IdRes id: Int, text: String) {
    onView(withId(id)).check(matches(withText(text)))
}
//endregion
