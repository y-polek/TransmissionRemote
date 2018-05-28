package net.yupol.transmissionremote.model

sealed class ListResource<T> {

    class Loading<T>: ListResource<T>()
    class Success<T>(val data: List<T>): ListResource<T>()
    class Failure<T>(val error: Throwable): ListResource<T>()
}

