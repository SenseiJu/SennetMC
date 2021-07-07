package me.senseiju.sennetmc.events.event.recycle

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.GlobalEvent
import java.util.*

class Recycle(
    plugin: SennetMC,
    eventsManager: EventsManager,
) : GlobalEvent(
    plugin,
    eventsManager,
    EventType.RECYCLE
) {
    val participants = HashSet<UUID>()

    init {
        registerEvents(RecycleListener(this))
    }

    override fun onEventFinished() {
        addCollectable(participants, "recycleevent")
    }
}