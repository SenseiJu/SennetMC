package me.senseiju.sennetmc.events.event

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.settings.Setting
import me.senseiju.sennetmc.utils.ObjectSet
import org.bukkit.scheduler.BukkitRunnable

open class BaseEvent(private val plugin: SennetMC, private val eventsManager: EventsManager,
                     private val eventType: EventType) : BukkitRunnable() {
    var finished = false
        private set

    private val bossBar = EventBossBar(plugin, eventType)
    private val length =  eventsManager.eventsFile.config.getInt("$eventType.length", 120)
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

        eventsManager.currentEvent = null
        finished = true
        bossBar.unRegister()

        /* May remove

        val users = plugin.userManager.userMap
        plugin.server.onlinePlayers.forEach {
            val user = users[it.uniqueId] ?: return@forEach

            if (user.getSetting(Setting.TOGGLE_NOTIFY_EVENT_MESSAGES)) {
                it.sendConfigMessage("EVENTS-ENDED", false, ObjectSet("{eventName}", eventType.title))
            }
        }
         */

        finish()
    }

    open fun finish() {
    }
}