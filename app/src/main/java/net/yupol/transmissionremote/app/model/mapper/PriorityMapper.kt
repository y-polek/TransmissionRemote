package net.yupol.transmissionremote.app.model.mapper

import net.yupol.transmissionremote.app.model.PriorityViewModel
import net.yupol.transmissionremote.app.model.PriorityViewModel.*
import net.yupol.transmissionremote.domain.model.Priority

object PriorityMapper {

    fun toViewModel(priority: Priority): PriorityViewModel {
        return when (priority) {
            Priority.LOW -> LOW
            Priority.NORMAL -> NORMAL
            Priority.HIGH -> HIGH
        }
    }

    fun toDomain(priority: PriorityViewModel): Priority {
        return when (priority) {
            LOW -> Priority.LOW
            NORMAL -> Priority.NORMAL
            HIGH -> Priority.HIGH
        }
    }
}
