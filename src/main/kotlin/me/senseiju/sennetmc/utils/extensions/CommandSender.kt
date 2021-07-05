package me.senseiju.sennetmc.utils.extensions

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sentils.extensions.primitives.color
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

private val messages = JavaPlugin.getPlugin(SennetMC::class.java).messagesFile

fun CommandSender.message(s: String) {
    this.sendMessage(s.color())
}

fun CommandSender.message(list: List<String>) {
    list.forEach { this.message(it) }
}

fun CommandSender.sendConfigMessage(messageName: String, vararg replacements: PlaceholderSet = emptyArray()) {
    this.sendConfigMessage(messageName, prefix = true, replacements = replacements)
}

fun CommandSender.sendConfigMessage(
    messageName: String,
    prefix: Boolean = true,
    vararg replacements: PlaceholderSet = emptyArray()
) {
    if (messages.isString(messageName)) {
        var message = messages.getString(messageName, "Undefined message for '$messageName'")

        for (replacement in replacements) {
            message = message.replace(replacement.placeholder, replacement.value.toString())
        }

        if (prefix) {
            message = "${messages.getString("PREFIX", "&#914ef5&lSennetMC »")} $message"
        }

        this.message(message)
    } else {
        val msgs = messages.getStringList(messageName)
        val messagesToSend = ArrayList<String>()

        for (message in msgs) {
            var line = message
            for (replacement in replacements) {
                line = line.replace(replacement.placeholder, replacement.value.toString())
            }

            if (prefix) {
                line = "${messages.getString("PREFIX", "&#914ef5&lSennetMC »")} $line"
            }
            messagesToSend.add(line)
        }

        this.message(messagesToSend)
    }
}