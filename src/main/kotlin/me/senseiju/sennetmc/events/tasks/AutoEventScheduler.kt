package me.senseiju.sennetmc.events.tasks

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.EventType
import org.bukkit.scheduler.BukkitRunnable

class AutoEventScheduler(plugin: SennetMC, private val eventsManager: EventsManager) : BukkitRunnable() {
    private val eventsFile = eventsManager.eventsFile

    init {
        val periodInSeconds = eventsFile.getInt("event-period", 1800)

        runTaskTimer(plugin, (periodInSeconds * 20).toLong(), (periodInSeconds * 20).toLong())
    }

    override fun run() {
        if (eventsManager.isEventActive()) {
            return
        }

        eventsManager.startEvent(EventType.selectRandom())
    }
}