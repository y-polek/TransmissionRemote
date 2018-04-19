@file:JvmName(name = "Parcelables")

package net.yupol.transmissionremote.utils

import android.os.Parcelable

/**
 * Transforms array of [Parcelable]s to array of specified type.
 *
 * @return transformed array or **null** if target array is **null**
 */
inline fun <reified T> Array<Parcelable>?.toArrayOf() : Array<T>? {
    val size = this?.size ?: return null
    return Array(size) { i ->
        this[i] as T
    }
}

/**
 * Transforms array of [Parcelable]s to array of specified type.
 *
 * This function is for calling from Java code.
 * In Kotlin use [toArrayOf] extension function.
 */
fun <T> toArrayOfType(type: Class<T>, original: Array<Parcelable>?): Array<T>? {
    if (original == null) return null
    val newArray = java.lang.reflect.Array.newInstance(type, original.size) as Array<T>
    System.arraycopy(original, 0, newArray, 0, original.size)
    return newArray
}