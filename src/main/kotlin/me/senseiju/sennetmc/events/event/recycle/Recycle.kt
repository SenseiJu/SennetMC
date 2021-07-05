package me.senseiju.sennetmc.events.event.recycle

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.GlobalEvent

class Recycle(
    override val plugin: SennetMC,
    override val eventsManager: EventsManager
) : GlobalEvent() {
    override val eventType = EventType.RECYCLE

    init {
        registerEvents(RecycleListener())
    }

    override fun onEventFinished() {

    }
}