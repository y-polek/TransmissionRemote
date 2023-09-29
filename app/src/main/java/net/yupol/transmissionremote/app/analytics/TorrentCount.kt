package net.yupol.transmissionremote.app.analytics

enum class TorrentCount(
    val value: String,
    private val range: IntRange
) {
    C_0("0", 0.. 0),
    C_1("1", 1..1),
    C_2_5("2-5", 2..5),
    C_6_10("6-10", 6..10),
    C_11_20("11-20", 11..20),
    C_21_30("21-30", 21..30),
    C_31_40("31-40", 31..40),
    C_41_50("41-50", 41..50),
    C_51_100("51-100", 51..100),
    C_101_200("101-200", 101..200),
    C_201_500("201-500", 201..500),
    C_501_1000("501-1000", 501..1000),
    C_1001_2000("1001-2000", 1001..2000),
    C_2001_INF("2001+", 2001..Int.MAX_VALUE),
    UNKNOWN("Unknown", Int.MIN_VALUE..-1);

    companion object {
        fun fromCount(count: Int): TorrentCount {
            return values().find { it.range.contains(count) } ?: UNKNOWN
        }
    }
}
