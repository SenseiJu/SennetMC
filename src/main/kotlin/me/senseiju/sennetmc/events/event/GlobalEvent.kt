package me.senseiju.sennetmc.events.event

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sentils.runnables.CountdownRunnable
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable

abstract class GlobalEvent : CountdownRunnable() {
    abstract val plugin: SennetMC
    abstract val eventsManager: EventsManager
    abstract val eventType: EventType

    override var timeToComplete: Long = run { eventsManager.eventsFile.getLong("$eventType.length", 120) }

    private val bossBar = run { EventBossBar(plugin, eventType) }
    private val length = run { timeToComplete }
    private val listeners = arrayListOf<Listener>()

    fun registerEvents(vararg listeners: Listener) {
        plugin.registerEvents(*listeners)
        this.listeners.addAll(listeners.asList())
    }

    override fun onComplete() {
        eventsManager.currentEvent = null
        bossBar.unRegister()
        listeners.forEach {
            HandlerList.unregisterAll(it)
        }

        onEventFinished()
    }

    override fun executeEverySecond() {
        bossBar.bar.progress = timeToComplete.toDouble() / length.toDouble()
    }

    abstract fun onEventFinished()
}