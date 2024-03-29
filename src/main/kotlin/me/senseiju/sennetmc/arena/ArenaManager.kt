package me.senseiju.sennetmc.arena

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import me.mattstudios.mf.base.CommandManager
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.arena.commands.ArenaCommand
import me.senseiju.sennetmc.arena.commands.DuelCommand
import me.senseiju.sennetmc.arena.listeners.ArenaPlayerListener
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sentils.extensions.locationFromString
import me.senseiju.sentils.registerEvents
import org.bukkit.entity.Player
import java.util.*

class ArenaManager(private val plugin: SennetMC) : BaseManager() {

    val requests: BiMap<UUID, UUID> = HashBiMap.create()
    val matchQueue = LinkedList<ArenaMatch>()
    var currentMatch: ArenaMatch? = null

    private val config = plugin.configFile

    init {
        registerCommands(plugin.commandManager)
        registerEvents(plugin)
    }

    override fun registerCommands(cm: CommandManager) {
        cm.register(
            DuelCommand(plugin, this),
            ArenaCommand(plugin)
        )
    }

    override fun registerEvents(plugin: SennetMC) {
        plugin.registerEvents(ArenaPlayerListener(plugin, this))
    }

    override fun reload() {
    }

    fun isPlayerInQueue(player: Player): Boolean {
        return getPlayersMatchInQueue(player) != null
    }

    fun getPlayersMatchInQueue(player: Player): ArenaMatch? {
        matchQueue.forEach {
            if (it.involvesPlayer(player.uniqueId)) {
                return it
            }
        }

        return null
    }

    fun isPlayerInCurrentMatch(player: Player): Boolean = currentMatch?.involvesPlayer(player.uniqueId) ?: false

    fun createMatch(player1: ArenaPlayer, player2: ArenaPlayer) {
        val match = ArenaMatch(player1, player2)

        matchQueue.add(match)

        removeRequest(player1.player)

        startNextMatch()
    }

    fun cancelAllMatches() {
        matchQueue.forEach {
            it.refundWagers()
        }

        if (currentMatch != null) {
            currentMatch?.cancelMatch()
        }
    }

    private fun removeRequest(player: Player) {
        if (requests.containsKey(player.uniqueId)) {
            requests.remove(player.uniqueId)
        } else if (requests.inverse().containsKey(player.uniqueId)) {
            requests.inverse().remove(player.uniqueId)
        }
    }

    fun cancelRequest(player: Player) {
        if (requests.containsKey(player.uniqueId)) {
            player.sendConfigMessage("ARENA-CANCELLED-REQUEST")

            plugin.server.getPlayer(requests.remove(player.uniqueId) ?: return)
                ?.sendConfigMessage("ARENA-CANCELLED-REQUEST")
        } else if (requests.inverse().containsKey(player.uniqueId)) {
            player.sendConfigMessage("ARENA-CANCELLED-REQUEST")

            plugin.server.getPlayer(requests.inverse().remove(player.uniqueId) ?: return)
                ?.sendConfigMessage("ARENA-CANCELLED-REQUEST")
        }
    }

    fun startNextMatch() {
        if (currentMatch != null) {
            return
        }

        val location1 = locationFromString(config.getString("arena.location-1", "NO_LOCATION_SET"))
        val location2 = locationFromString(config.getString("arena.location-2", "NO_LOCATION_SET"))

        currentMatch = matchQueue.removeFirstOrNull()

        currentMatch?.start(plugin, location1, location2)
    }
}