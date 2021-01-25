package me.senseiju.sennetmc.events.event

import me.senseiju.sennetmc.SennetMC
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class EventBossBar(plugin: SennetMC, eventType: EventType) : Listener {
    val bar: BossBar = plugin.server.createBossBar(eventType.title, BarColor.WHITE, BarStyle.SOLID)

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)

        plugin.server.onlinePlayers.forEach {
            bar.addPlayer(it)
        }
    }

    @EventHandler
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        bar.addPlayer(e.player)
    }

    fun unRegister() {
        bar.removeAll()

        HandlerList.unregisterAll(this)
    }
}