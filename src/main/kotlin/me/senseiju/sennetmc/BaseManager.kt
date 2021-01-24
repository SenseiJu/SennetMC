package me.senseiju.sennetmc

import me.mattstudios.mf.base.CommandManager

interface BaseManager {

    fun registerCommands(cm: CommandManager)

    fun registerEvents()

    fun reload()
}