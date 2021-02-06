package me.senseiju.sennetmc.extensions

import me.senseiju.sennetmc.utils.ObjectSet
import me.senseiju.sennetmc.utils.applyPlaceholders
import org.bukkit.Server

fun Server.dispatchCommands(commands: List<String>, vararg replacements: ObjectSet = emptyArray()) {
    commands.forEach {
        this.dispatchCommand(this.consoleSender, applyPlaceholders(it, *replacements))
    }
}

fun Server.sendConfigMessage(messageName: String, vararg replacements: ObjectSet = emptyArray()) {
    this.sendConfigMessage(messageName, prefix = false, replacements = replacements)
}

fun Server.sendConfigMessage(messageName: String, prefix: Boolean = true, vararg replacements: ObjectSet = emptyArray()) {
    this.onlinePlayers.forEach {
        it.sendConfigMessage(messageName, prefix = prefix, replacements = replacements)
    }
}