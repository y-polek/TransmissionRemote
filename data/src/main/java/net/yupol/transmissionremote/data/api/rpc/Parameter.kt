package net.yupol.transmissionremote.data.api.rpc

data class Parameter<out K, out V>(val key: K, val value: V)