package net.yupol.transmissionremote.model.mapper

import net.yupol.transmissionremote.data.api.model.TrackerEntity
import net.yupol.transmissionremote.data.api.model.TrackerStatsEntity
import net.yupol.transmissionremote.model.json.Tracker
import net.yupol.transmissionremote.model.json.TrackerStats

object TrackerMapper {

    @JvmStatic
    fun toViewModel(entity: TrackerEntity) = Tracker.fromEntity(entity)

    @JvmStatic
    fun Array<TrackerEntity>.toViewModel() = map(TrackerMapper::toViewModel).toTypedArray()

    @JvmStatic
    fun toViewModel(entity: TrackerStatsEntity) = TrackerStats.fromEntity(entity)

    @JvmStatic
    fun Array<TrackerStatsEntity>.toViewModel() = map(TrackerMapper::toViewModel).toTypedArray()
}
