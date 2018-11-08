package net.yupol.transmissionremote.domain.model

data class ProtectedProperty<T>(@JvmField val value: T) {
    override fun toString() = "████████"
}
