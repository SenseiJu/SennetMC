package me.senseiju.sennetmc.speedboat

import me.mattstudios.mf.base.CommandManager
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.speedboat.commands.BoatCommand
import me.senseiju.sennetmc.speedboat.commands.ToggleSpeedboatCommand
import me.senseiju.sennetmc.speedboat.listeners.SpeedboatListener
import me.senseiju.sentils.registerEvents
import java.util.*

class SpeedboatManager(plugin: SennetMC) : BaseManager() {

    val playerSpeedboatToggle = HashMap<UUID, Boolean>()

    init {
        registerCommands(plugin.commandManager)
        registerEvents(plugin)
    }

    override fun registerCommands(cm: CommandManager) {
        cm.register(
            ToggleSpeedboatCommand(this),
            BoatCommand()
        )
    }

    override fun registerEvents(plugin: SennetMC) {
        plugin.registerEvents(SpeedboatListener(plugin, this))
    }
}