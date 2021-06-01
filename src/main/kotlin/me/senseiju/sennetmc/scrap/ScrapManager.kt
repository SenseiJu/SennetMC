package me.senseiju.sennetmc.scrap

import me.mattstudios.mf.base.CommandManager
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.scrap.commands.ScrapCommand
import me.senseiju.sennetmc.scrap.listeners.CombineScrapListener

class ScrapManager(plugin: SennetMC) : BaseManager() {

    init {
        registerCommands(plugin.commandManager)
        registerEvents(plugin)
    }

    override fun registerCommands(cm: CommandManager) {
        cm.register(ScrapCommand())
    }

    override fun registerEvents(plugin: SennetMC) {
        plugin.registerEvents(
            CombineScrapListener()
        )
    }
}