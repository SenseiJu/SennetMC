package me.senseiju.sennetmc.speedboat

import me.mattstudios.mf.base.CommandManager
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.speedboat.commands.ToggleSpeedboatCommand
import me.senseiju.sennetmc.speedboat.listeners.SpeedboatListener
import java.util.*
import kotlin.collections.HashMap

class SpeedboatManager(private val plugin: SennetMC) : BaseManager {

    val playerSpeedboatToggle = HashMap<UUID, Boolean>()

    init {
        registerEvents()
        registerCommands(plugin.commandManager)
    }

    override fun registerCommands(cm: CommandManager) {
        cm.register(ToggleSpeedboatCommand(this))
    }

    override fun registerEvents() {
        SpeedboatListener(plugin, this)
    }

    override fun reload() {
        TODO("Not yet implemented")
    }

}