package me.senseiju.sennetmc.utils.extensions

import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.applyPlaceholders
import org.bukkit.Server

fun Server.dispatchCommands(commands: List<String>, vararg replacements: PlaceholderSet = emptyArray()) {
    commands.forEach {
        this.dispatchCommand(this.consoleSender, applyPlaceholders(it, *replacements))
    }
}

fun Server.sendConfigMessage(messageName: String, vararg replacements: PlaceholderSet = emptyArray()) {
    this.sendConfigMessage(messageName, prefix = false, replacements = replacements)
}

fun Server.sendConfigMessage(
    messageName: String,
    prefix: Boolean = true,
    vararg replacements: PlaceholderSet = emptyArray()
) {
    this.onlinePlayers.forEach {
        it.sendConfigMessage(messageName, prefix = prefix, replacements = replacements)
    }
}