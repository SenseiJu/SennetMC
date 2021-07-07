package me.senseiju.sennetmc.events.event

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sentils.functions.getOnlinePlayer
import me.senseiju.sentils.runnables.CountdownRunnable
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import java.util.*

abstract class GlobalEvent(
    val plugin: SennetMC,
    val eventsManager: EventsManager,
    val eventType: EventType
) : CountdownRunnable() {
    override var timeToComplete = eventsManager.eventsFile.getLong("$eventType.length", 120)

    private val bossBar = EventBossBar(plugin, eventType)
    private val length = eventsManager.eventsFile.getLong("$eventType.length", 120)
    private val listeners = arrayListOf<Listener>()

    init {
        start(plugin)
    }

    fun registerEvents(vararg listeners: Listener) {
        plugin.registerEvents(*listeners)
        this.listeners.addAll(listeners.asList())
    }

    fun addCollectable(uuids: Set<UUID>, collectable: String) {
        uuids.forEach { uuid ->
            getOnlinePlayer(uuid)?.let { plugin.collectablesManager.addCollectable(uuid, collectable) }
        }
    }

    fun addCollectable(uuid: UUID, collectable: String) {
        getOnlinePlayer(uuid)?.let { plugin.collectablesManager.addCollectable(uuid, collectable) }
    }

    fun sendWinnerMessage(player: Player) {
        player.sendConfigMessage(
            "EVENTS-WINNER",
            false,
            PlaceholderSet("{eventName}", eventType.title)
        )
    }

    fun sendLoserMessage(player: Player) {
        player.sendConfigMessage(
            "EVENTS-LOSER",
            false,
            PlaceholderSet("{eventName}", eventType.title)
        )
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