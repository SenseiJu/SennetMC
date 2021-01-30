package me.senseiju.sennetmc.events.event.fishrace

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.BaseEvent
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.fishrace.listeners.PlayerCaughtFishListener
import me.senseiju.sennetmc.extensions.dispatchCommands
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.utils.ObjectSet
import java.util.*
import kotlin.collections.HashMap

private val eventType = EventType.FISH_RACE

class FishRace(private val plugin: SennetMC, eventsManager: EventsManager) : BaseEvent(plugin, eventsManager, eventType){
    val playersFishCaught = HashMap<UUID, Int>()

    private val eventsFile = eventsManager.eventsFile

    init {
        PlayerCaughtFishListener(plugin, this)

        runTaskTimer(plugin, 20L, 20L)
    }

    override fun finish() {
        if (playersFishCaught.isEmpty()) {
            return
        }

        val sortedPlayersFishCaught = playersFishCaught.toList().sortedByDescending { (_, value) -> value }.toMap()

        val commands = eventsFile.config.getStringList("$eventType.winner-commands")
        val numberOfWinners = eventsFile.config.getInt("$eventType.number-of-winners", 5)

        var currentValue = sortedPlayersFishCaught.values.first()
        var playersRewarded = 0

        sortedPlayersFishCaught.forEach { (uuid, value) ->
            val player = plugin.server.getPlayer(uuid) ?: return@forEach
            if (!player.isOnline) {
                return@forEach
            }

            if (playersRewarded >= numberOfWinners && currentValue != value) {
                player.sendConfigMessage("EVENTS-LOSER", false, ObjectSet("{eventName}", eventType.title))
                return@forEach
            }

            plugin.server.dispatchCommands(commands, ObjectSet("{player}", player.name))

            player.sendConfigMessage("EVENTS-WINNER", false, ObjectSet("{eventName}", eventType.title))

            currentValue = value
            playersRewarded++
        }
    }
}