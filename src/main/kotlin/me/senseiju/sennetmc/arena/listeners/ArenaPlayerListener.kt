package me.senseiju.sennetmc.arena.listeners

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.arena.ArenaManager
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class ArenaPlayerListener(private val plugin: SennetMC, private val arenaManager: ArenaManager) : Listener {

    @EventHandler
    private fun onPlayerDeath(e: PlayerDeathEvent) {
        val currentMatch = arenaManager.currentMatch ?: return

        if (currentMatch.involvesPlayer(e.entity.uniqueId)) {
            e.isCancelled = true

            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                currentMatch.handleWinner(currentMatch.getOpposingPlayer(e.entity))
            }, 1L)

            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                arenaManager.currentMatch = null
                arenaManager.startNextMatch()
            }, 40L)
        }
    }

    @EventHandler
    private fun onPlayerQuit(e: PlayerQuitEvent) {
        val queueMatch = arenaManager.getPlayersMatchInQueue(e.player)
        if (queueMatch != null) {
            queueMatch.refundWagers()
            queueMatch.getOpposingPlayer(e.player).sendConfigMessage("ARENA-QUEUE-CANCELLED")

            arenaManager.matchQueue.remove(queueMatch)

            return
        }

        val currentMatch = arenaManager.currentMatch
        if (currentMatch?.involvesPlayer(e.player.uniqueId) == true) {
            currentMatch.handleWinner(currentMatch.getOpposingPlayer(e.player))

            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                arenaManager.currentMatch = null
                arenaManager.startNextMatch()
            }, 40L)
        }
    }

    @EventHandler
    private fun onPlayerMove(e: PlayerMoveEvent) {
        val currentMatch = arenaManager.currentMatch ?: return

        if (!currentMatch.pvpEnabled && currentMatch.involvesPlayer(e.player.uniqueId)) {
            e.isCancelled = true
        }
    }
}