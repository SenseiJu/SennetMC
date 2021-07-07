package me.senseiju.sennetmc.events

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.components.ParameterResolver
import me.mattstudios.mf.base.components.TypeResult
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.commands.EventsCommand
import me.senseiju.sennetmc.events.event.EventType
import me.senseiju.sennetmc.events.event.GlobalEvent
import me.senseiju.sennetmc.events.event.fishrace.FishRace
import me.senseiju.sennetmc.events.event.fishycollab.FishyCollab
import me.senseiju.sennetmc.events.event.recycle.Recycle
import me.senseiju.sennetmc.events.event.shipwreck.Shipwreck
import me.senseiju.sennetmc.events.tasks.AutoEventScheduler
import me.senseiju.sennetmc.settings.Setting
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.applyPlaceholders
import me.senseiju.sennetmc.utils.extensions.message
import me.senseiju.sentils.storage.ConfigFile

class EventsManager(private val plugin: SennetMC) : BaseManager() {
    val eventsFile = ConfigFile(plugin, "events.yml", true)
    var currentEvent: GlobalEvent? = null

    private val users = plugin.userManager.userMap
    private val messages = plugin.messagesFile

    init {
        registerCommands(plugin.commandManager)

        AutoEventScheduler(plugin, this)
    }

    override fun registerCommands(cm: CommandManager) {
        cm.parameterHandler.register(EventType::class.java, ParameterResolver { argument ->
            try {
                return@ParameterResolver TypeResult(EventType.valueOf(argument.toString().uppercase()), argument)
            } catch (ex: IllegalArgumentException) {
                return@ParameterResolver TypeResult(argument)
            }
        })

        cm.register(EventsCommand(this))
    }

    override fun reload() {
        eventsFile.reload()
    }

    fun isEventActive(): Boolean = currentEvent != null

    fun startEvent(eventType: EventType) {
        currentEvent = when (eventType) {
            EventType.FISH_RACE -> FishRace(plugin, this)
            EventType.SHIPWRECK -> Shipwreck(plugin, this)
            EventType.FISHY_COLLAB -> FishyCollab(plugin, this)
            EventType.RECYCLE -> Recycle(plugin, this)
        }

        val message = applyPlaceholders(
            messages.getStringList("EVENTS-STARTED"),
            PlaceholderSet("{eventName}", eventType.title),
            PlaceholderSet("{eventHowToPlay}", eventsFile.getStringList("${eventType}.how-to-play"))
        )

        plugin.server.onlinePlayers.forEach {
            val user = users[it.uniqueId] ?: return@forEach

            if (user.getSetting(Setting.TOGGLE_NOTIFY_EVENT_MESSAGES)) {
                it.message(message)
            }
        }
    }
}