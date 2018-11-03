package net.yupol.transmissionremote.domain.model

data class ProtectedProperty<T>(val value: T) {
    override fun toString() = "████████"
}
