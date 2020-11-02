package me.senseiju.commscraft

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.ParameterHandler
import org.bukkit.plugin.PluginManager

interface BaseManager {

    fun registerCommands(cm: CommandManager)

    fun registerEvents()

    fun reload()
}