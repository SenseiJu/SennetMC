package me.senseiju.sennetmc.events.event.shipwreck

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.GlobalEvent
import java.util.*

class Shipwreck(
    override val plugin: SennetMC,
    override val eventsManager: EventsManager,
) : GlobalEvent() {
    override val eventType = EventType.SHIPWRECK

    val participants = HashSet<UUID>()

    init {
        registerEvents(ShipwreckListener(plugin, this))
    }

    override fun onEventFinished() {
        participants.forEach {
            plugin.collectablesManager.addCollectable(it, "shipwreckevent")
        }
    }

}