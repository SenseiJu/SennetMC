package me.senseiju.sennetmc.events

import me.mattstudios.mf.base.CommandManager
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.datastorage.DataFile
import me.senseiju.sennetmc.events.commands.EventsCommand

class EventsManager(private val plugin: SennetMC) : BaseManager {

    val eventsFile = DataFile(plugin, "events.yml", true)

    init {
        registerCommands(plugin.commandManager)
    }

    override fun registerCommands(cm: CommandManager) {
        cm.register(EventsCommand(plugin))
    }

    override fun registerEvents() {
        TODO("Not yet implemented")
    }

    override fun reload() {
        eventsFile.reload()
    }
}