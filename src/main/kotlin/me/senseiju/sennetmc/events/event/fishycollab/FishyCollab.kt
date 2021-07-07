package me.senseiju.sennetmc.events.event.fishycollab

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.GlobalEvent
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sentils.functions.getOnlinePlayer
import java.util.*

class FishyCollab(
    plugin: SennetMC,
    eventsManager: EventsManager,
) : GlobalEvent(
    plugin,
    eventsManager,
    EventType.FISHY_COLLAB
) {
    val participants = hashMapOf<UUID, Int>()

    private val commands: List<String>
    private val minimumLimit: Int
    private val fishCaughtTarget: Int

    init {
        registerEvents(FishyListener(this))

        val config = eventsManager.eventsFile
        commands = config.getStringList("winner-commands")
        minimumLimit = config.getInt("minimum-for-reward", 7)
        fishCaughtTarget = plugin.server.onlinePlayers.size * config.getInt("fish-caught-per-player", 10)

    }

    override fun onEventFinished() {
        if (participants.map { it.value }.sum() < fishCaughtTarget) {
            participants.keys.forEach { uuid ->
                sendLoserMessage(getOnlinePlayer(uuid) ?: return@forEach)
            }
            return
        }

        participants.forEach { (uuid, fishCaught) ->
            val player = getOnlinePlayer(uuid) ?: return@forEach

            if (fishCaught >= minimumLimit) {
                addCollectable(uuid, "fishycollabevent")
                sendWinnerMessage(player)
            } else {
                sendLoserMessage(player)
            }
        }
    }
}