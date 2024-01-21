package net.yupol.transmissionremote.app.navigation

sealed class NavigationEvent  {
    class NavigateUp : NavigationEvent()
    class NavigateToRoute(val route: String) : NavigationEvent()

    private var hasBeenHandled = false

    fun handleIfNotHandled(handle: (NavigationEvent) -> Unit) {
        if (!hasBeenHandled) {
            hasBeenHandled = true
            handle(this)
        }
    }
}
