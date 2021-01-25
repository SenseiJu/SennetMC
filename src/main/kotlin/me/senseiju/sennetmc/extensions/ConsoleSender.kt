package me.senseiju.sennetmc.extensions

import me.senseiju.sennetmc.utils.ObjectSet
import me.senseiju.sennetmc.utils.applyPlaceholders
import org.bukkit.Server

fun Server.dispatchCommands(commands: List<String>, vararg replacements: ObjectSet = emptyArray()) {
    commands.forEach {
        this.dispatchCommand(this.consoleSender, applyPlaceholders(it, *replacements))
    }
}