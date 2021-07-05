package me.senseiju.sennetmc.events.event.fishycollab

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.GlobalEvent
import java.util.*

class FishyCollab(
    override val plugin: SennetMC,
    override val eventsManager: EventsManager
) : GlobalEvent() {
    override val eventType = EventType.FISHY_COLLAB

    val participants = hashSetOf<UUID>()

    var fishCaughtTarget = plugin.server.onlinePlayers.size * 10

    override fun onEventFinished() {

    }
}