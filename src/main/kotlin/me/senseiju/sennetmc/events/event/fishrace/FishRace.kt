package me.senseiju.sennetmc.events.event.fishrace

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.GlobalEvent
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.dispatchCommands
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import java.util.*

class FishRace(
    override val plugin: SennetMC,
    override val eventsManager: EventsManager,
) : GlobalEvent() {
    override val eventType = EventType.FISH_RACE

    val playersFishCaught = HashMap<UUID, Int>()

    private val eventsFile = eventsManager.eventsFile

    init {
        registerEvents(FishRaceListener(this))
    }

    override fun onEventFinished() {
        if (playersFishCaught.isEmpty()) {
            return
        }

        val sortedPlayersFishCaught = playersFishCaught.toList().sortedByDescending { (_, value) -> value }.toMap()

        val commands = eventsFile.getStringList("$eventType.winner-commands")
        val numberOfWinners = eventsFile.getInt("$eventType.number-of-winners", 5)

        var currentValue = sortedPlayersFishCaught.values.first()
        var playersRewarded = 0

        sortedPlayersFishCaught.forEach { (uuid, value) ->
            val player = plugin.server.getPlayer(uuid) ?: return@forEach
            if (!player.isOnline) {
                return@forEach
            }

            if (playersRewarded >= numberOfWinners && currentValue != value) {
                player.sendConfigMessage("EVENTS-LOSER", false, PlaceholderSet("{eventName}", eventType.title))
                return@forEach
            }

            plugin.server.dispatchCommands(commands, PlaceholderSet("{player}", player.name))

            player.sendConfigMessage("EVENTS-WINNER", false, PlaceholderSet("{eventName}", eventType.title))

            currentValue = value
            playersRewarded++
        }
    }
}