package net.yupol.transmissionremote.data.model

enum class ErrorType(val code: Int) {

    NONE(0),
    TRACKER_WARNING(1),
    TRACKER_ERROR(2),
    LOCAL_ERROR(3),
    UNKNOWN(-1);

    companion object {
        fun fromCode(code: Int?): ErrorType {
            return if (code == null) NONE
            else values().find { code == it.code } ?: UNKNOWN
        }
    }
}
