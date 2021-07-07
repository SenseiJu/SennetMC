package me.senseiju.sennetmc.events.event.shipwreck

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.GlobalEvent
import java.util.*

class Shipwreck(
    plugin: SennetMC,
    eventsManager: EventsManager,
) : GlobalEvent(
    plugin,
    eventsManager,
    EventType.SHIPWRECK
) {
    val participants = HashSet<UUID>()

    init {
        registerEvents(ShipwreckListener(plugin, this))
    }

    override fun onEventFinished() {
        addCollectable(participants, "shipwreckevent")
    }
}