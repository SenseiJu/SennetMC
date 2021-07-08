package me.senseiju.sennetmc.voting

import me.mattstudios.mf.base.CommandManager
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sentils.registerEvents

class VoteManager(plugin: SennetMC) : BaseManager() {

    init {
        registerCommands(plugin.commandManager)
        registerEvents(plugin)
    }

    override fun registerCommands(cm: CommandManager) {
        cm.register(VoteCommand())
    }

    override fun registerEvents(plugin: SennetMC) {
        plugin.registerEvents(VotifierListener(plugin))
    }
}