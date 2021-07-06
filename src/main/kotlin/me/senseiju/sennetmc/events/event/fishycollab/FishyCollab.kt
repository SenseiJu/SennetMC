package me.senseiju.sennetmc.events.event.fishycollab

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.GlobalEvent
import me.senseiju.sentils.functions.getOnlinePlayer
import java.util.*

class FishyCollab(
    override val plugin: SennetMC,
    override val eventsManager: EventsManager
) : GlobalEvent() {
    override val eventType = EventType.FISHY_COLLAB

    val participants = hashMapOf<UUID, Int>()

    private val minimumLimit = eventsManager.eventsFile.getInt("minimum-for-reward", 7)
    private var fishCaughtTarget = plugin.server.onlinePlayers.size * eventsManager.eventsFile.getInt("fish-caught-per-player", 10)

    init {
        registerEvents(FishyListener(this))
    }

    override fun onEventFinished() {
        if (participants.map { it.value }.sum() < fishCaughtTarget) {
            // TODO send message "not enough fish caught"
            return
        }

        participants.forEach { (uuid, fishCaught) ->
            plugin.collectablesManager.addCollectable(uuid, "shipwreckevent")

            val player = getOnlinePlayer(uuid) ?: return@forEach

            if (fishCaught >= minimumLimit) {
                // TODO reward
            } else {
                // TODO did not qualify message
            }
        }
    }
}