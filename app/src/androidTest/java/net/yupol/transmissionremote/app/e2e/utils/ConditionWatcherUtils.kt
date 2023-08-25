package net.yupol.transmissionremote.app.e2e.utils

fun waitForCondition(description: String, condition: () -> Unit) {
    ConditionWatcher().waitForCondition(
        description,
        object : ConditionWatcher.Condition() {
            override fun checkCondition() = condition()
        }
    )
}
