package me.senseiju.sennetmc.events.commands

import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_EVENTS_START
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.extensions.sendConfigMessage
import org.bukkit.command.CommandSender

@Command("Events")
class EventsCommand(private val eventsManager: EventsManager) : CommandBase() {

    @SubCommand("start")
    @Permission(PERMISSION_EVENTS_START)
    fun onStartSubCommand(sender: CommandSender, @Completion("#enum") eventType: EventType?) {
        if (eventType == null) {
            sender.sendConfigMessage("EVENTS-INVALID-EVENT-TYPE")
            return
        }

        if (eventsManager.isEventActive()) {
            sender.sendConfigMessage("EVENTS-CURRENTLY-ACTIVE")
            return
        }

        eventsManager.startEvent(eventType)
    }
}