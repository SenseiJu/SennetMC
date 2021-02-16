package me.senseiju.sennetmc.events

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.components.ParameterResolver
import me.mattstudios.mf.base.components.TypeResult
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.datastorage.DataFile
import me.senseiju.sennetmc.events.commands.EventsCommand
import me.senseiju.sennetmc.events.event.AbstractEvent
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.fishrace.FishRace
import me.senseiju.sennetmc.events.event.shipwreck.Shipwreck
import me.senseiju.sennetmc.events.tasks.AutoEventScheduler
import me.senseiju.sennetmc.utils.extensions.message
import me.senseiju.sennetmc.settings.Setting
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.applyPlaceholders

class EventsManager(private val plugin: SennetMC) : BaseManager {
    val eventsFile = DataFile(plugin, "events.yml", true)
    var currentEvent: AbstractEvent? = null

    private val users = plugin.userManager.userMap
    private val messagesFile = plugin.messagesFile

    init {
        registerCommands(plugin.commandManager)

        AutoEventScheduler(plugin, this)
    }

    override fun registerCommands(cm: CommandManager) {
        cm.parameterHandler.register(EventType::class.java, ParameterResolver { argument ->
            try {
                return@ParameterResolver TypeResult(EventType.valueOf(argument.toString().toUpperCase()), argument)
            } catch (ex: IllegalArgumentException) {
                return@ParameterResolver TypeResult(argument)
            }
        })

        cm.register(EventsCommand(this))
    }

    override fun registerEvents() {
    }

    override fun reload() {
        eventsFile.reload()
    }

    fun isEventActive() : Boolean = currentEvent != null

    fun startEvent(eventType: EventType) {
        currentEvent = when (eventType) {
            EventType.FISH_RACE -> FishRace(plugin, this)
            EventType.SHIPWRECK -> Shipwreck(plugin, this)
        }

        val message = applyPlaceholders(messagesFile.config.getStringList("EVENTS-STARTED"),
                PlaceholderSet("{eventName}", eventType.title),
                PlaceholderSet("{eventHowToPlay}", eventsFile.config.getStringList("${eventType}.how-to-play")))

        plugin.server.onlinePlayers.forEach {
            val user = users[it.uniqueId] ?: return@forEach

            if (user.getSetting(Setting.TOGGLE_NOTIFY_EVENT_MESSAGES)) {
                it.message(message)
            }
        }
    }
}