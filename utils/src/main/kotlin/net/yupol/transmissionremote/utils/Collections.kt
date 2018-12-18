@file:JvmName(name = "Collections")

package net.yupol.transmissionremote.utils

fun <K, V> Map<K, V>.emptyToNull(): Map<K, V>? {
    return if (isNotEmpty()) this else null
}

fun Any.isEmptyArray(): Boolean {
    return when (this) {
        is Array<*> -> this.isEmpty()
        is ByteArray -> this.isEmpty()
        is ShortArray -> this.isEmpty()
        is IntArray -> this.isEmpty()
        is LongArray -> this.isEmpty()
        is FloatArray -> this.isEmpty()
        is DoubleArray -> this.isEmpty()
        is BooleanArray -> this.isEmpty()
        is CharArray -> this.isEmpty()
        else -> false
    }
}

fun Collection<Int>.toArray() = toIntArray()

fun <T> MutableCollection<T>.deleteIf(predicate: (T) -> Boolean) {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val element = iterator.next()
        if (predicate(element)) {
            iterator.remove()
        }
    }
}