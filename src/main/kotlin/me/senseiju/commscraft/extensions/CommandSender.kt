package me.senseiju.commscraft.extensions

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.utils.ObjectSet
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

private val config = JavaPlugin.getPlugin(CommsCraft::class.java).messagesFile.config

fun CommandSender.message(s: String) {
    this.sendMessage(s.color())
}

fun CommandSender.message(list: List<String>) {
    list.forEach{ this.message(it) }
}

fun CommandSender.sendConfigMessage(messageName: String, vararg replacements: ObjectSet = emptyArray()) {
    this.sendConfigMessage(messageName, prefix = true, isStringList = false, replacements = replacements)
}

fun CommandSender.sendConfigMessage(messageName: String, prefix: Boolean = true, vararg replacements: ObjectSet = emptyArray()) {
    this.sendConfigMessage(messageName, prefix = prefix, isStringList = false, replacements = replacements)
}

fun CommandSender.sendConfigMessage(messageName: String, prefix: Boolean = true, isStringList: Boolean = false, vararg replacements: ObjectSet = emptyArray()) {
    if (config.isString(messageName)) {
        var message = config.getString(messageName, "Undefined message for '$messageName'")!!

        for (replacement in replacements) {
            message = message.replace(replacement.any1.toString(), replacement.any2.toString())
        }

        if (prefix) {
            message = "${config.getString("PREFIX", "&8&lCommsCraft >")} $message"
        }

        this.message(message)
    } else {
        val messages = config.getStringList(messageName)
        val messagesToSend = ArrayList<String>()

        for (message in messages) {
            var line = message
            for (replacement in replacements) {
                line = line.replace(replacement.any1.toString(), replacement.any2.toString())
            }

            if (prefix) {
                line = "${config.getString("PREFIX", "&8&lCommsCraft >")} $line"
            }
            messagesToSend.add(line)
        }

        this.message(messagesToSend)
    }
}