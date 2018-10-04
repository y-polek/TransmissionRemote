package net.yupol.transmissionremote.model.mapper

import net.yupol.transmissionremote.domain.model.Priority
import net.yupol.transmissionremote.model.Priority.*
import net.yupol.transmissionremote.model.json.TransferPriority

object PriorityMapper {

    @JvmStatic
    fun toDomain(priority: net.yupol.transmissionremote.model.Priority): Priority {
        return when (priority) {
            HIGH -> Priority.HIGH
            NORMAL -> Priority.NORMAL
            LOW -> Priority.LOW
        }
    }

    @JvmStatic
    fun toDomain(priority: TransferPriority): Priority {
        return when (priority) {
            TransferPriority.HIGH -> Priority.HIGH
            TransferPriority.NORMAL -> Priority.NORMAL
            TransferPriority.LOW -> Priority.LOW
        }
    }
}
