package me.senseiju.sennetmc.events.event.fishrace

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.GlobalEvent
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.dispatchCommands
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sentils.functions.getOnlinePlayer
import java.util.*

class FishRace(
    plugin: SennetMC,
    eventsManager: EventsManager,
) : GlobalEvent(
    plugin,
    eventsManager,
    EventType.FISH_RACE
) {
    val participants = hashMapOf<UUID, Int>()

    private val commands: List<String>
    private val winnerPerPlayerCountOf: Int

    init {
        registerEvents(FishRaceListener(this))

        val config = eventsManager.eventsFile

        commands = config.getStringList("$eventType.winner-commands")
        winnerPerPlayerCountOf = config.getInt("winner-per-player-count-of", 4)
    }

    override fun onEventFinished() {
        if (participants.isEmpty()) {
            return
        }

        val sortedPlayersFishCaught = participants.toList().sortedByDescending { (_, value) -> value }.toMap()

        val numberOfWinners = (plugin.server.onlinePlayers.size / winnerPerPlayerCountOf) + 1

        var currentValue = sortedPlayersFishCaught.values.first()
        var playersRewarded = 0

        sortedPlayersFishCaught.forEach { (uuid, value) ->
            val player = getOnlinePlayer(uuid) ?: return@forEach

            if (playersRewarded >= numberOfWinners && currentValue != value) {
                sendLoserMessage(player)
                return@forEach
            }
            addCollectable(uuid, "fishraceevent")
            sendWinnerMessage(player)

            plugin.server.dispatchCommands(commands, PlaceholderSet("{player}", player.name))

            currentValue = value
            playersRewarded++
        }
    }
}