package net.yupol.transmissionremote.model

data class Parameter<out K, out V>(val key: K, val value: V)