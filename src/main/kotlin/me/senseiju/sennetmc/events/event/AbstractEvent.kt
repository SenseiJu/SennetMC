package me.senseiju.sennetmc.events.event

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import org.bukkit.scheduler.BukkitRunnable

abstract class AbstractEvent(plugin: SennetMC,
                             private val eventsManager: EventsManager,
                             eventType: EventType) : BukkitRunnable() {

    var finished = false
        private set

    private val bossBar = EventBossBar(plugin, eventType)
    private val length =  eventsManager.eventsFile.config.getInt("$eventType.length", 120)
    private var timeRemaining = length

    override fun run() {
        if (timeRemaining <= 0) {
            cancel()

            eventsManager.currentEvent = null
            finished = true

            bossBar.unRegister()

            finish()

            return
        }

        timeRemaining--

        bossBar.bar.progress = timeRemaining.toDouble() / length.toDouble()
    }

    abstract fun finish()
}