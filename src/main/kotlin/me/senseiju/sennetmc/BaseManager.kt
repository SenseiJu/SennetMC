package me.senseiju.sennetmc

import me.mattstudios.mf.base.CommandManager

abstract class BaseManager {

    open fun registerCommands(cm: CommandManager) {}

    open fun registerEvents(plugin: SennetMC) {}

    open fun reload() {}
}