package me.senseiju.sennetmc.events.event

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import org.bukkit.scheduler.BukkitRunnable

open class BaseEvent(plugin: SennetMC, eventsManager: EventsManager, eventType: EventType) : BukkitRunnable() {
    var finished = false
        private set

    private val eventsFile = eventsManager.eventsFile
    private val bossBar = EventBossBar(plugin, eventType)
    private val length = eventsFile.config.getInt("$eventType.length", 120)
    private var timeRemaining = length

    override fun run() {
        if (timeRemaining <= 0) {
            cancel()
            return
        }

        timeRemaining--

        bossBar.bar.progress = timeRemaining.toDouble() / length.toDouble()
    }

    override fun cancel() {
        super.cancel()

        finished = true

        bossBar.unRegister()

        finish()
    }

    open fun finish() {

    }
}