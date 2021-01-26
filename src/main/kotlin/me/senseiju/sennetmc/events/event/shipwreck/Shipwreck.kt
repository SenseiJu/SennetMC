package me.senseiju.sennetmc.events.event.shipwreck

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.BaseEvent
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.shipwreck.listeners.PlayerCaughtFishListener
import java.util.*
import kotlin.collections.HashSet

private val eventType = EventType.SHIPWRECK

class Shipwreck(private val plugin: SennetMC, eventsManager: EventsManager) : BaseEvent(plugin, eventsManager, eventType) {
    val participants = HashSet<UUID>()

    init {
        PlayerCaughtFishListener(plugin, this)

        runTaskTimer(plugin, 20L, 20L)
    }

    override fun finish() {
        participants.forEach {
            plugin.collectablesManager.addCollectable(it, "shipwreckevent")
        }
    }
}