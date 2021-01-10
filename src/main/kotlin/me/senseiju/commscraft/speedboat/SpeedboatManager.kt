package me.senseiju.commscraft.speedboat

import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.speedboat.listeners.SpeedboatListener
import java.util.*
import kotlin.collections.HashMap

class SpeedboatManager(private val plugin: CommsCraft) : BaseManager {

    var playerSpeedboatToggle = HashMap<UUID, Boolean>()

    init {
        registerEvents()
    }

    override fun registerCommands(cm: CommandManager) {
        TODO("Not yet implemented")
    }

    override fun registerEvents() {
        SpeedboatListener(plugin, this)
    }

    override fun reload() {
        TODO("Not yet implemented")
    }

}