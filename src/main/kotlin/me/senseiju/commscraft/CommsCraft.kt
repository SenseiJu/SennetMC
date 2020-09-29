package me.senseiju.commscraft

import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.datastorage.Database
import me.senseiju.commscraft.extensions.message
import me.senseiju.commscraft.utils.ObjectSet
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class CommsCraft : JavaPlugin() {
    val database = Database(this, "database.yml")

    val configFile = DataFile(this, "config.yml", true)
    private val messagesFile = DataFile(this, "messages.yml", true)

    override fun onEnable() {
    }

    override fun onDisable() {

    }

    fun sendMessage(player: Player, messageName: String, vararg replacements: ObjectSet = emptyArray()) {
        var message = messagesFile.config.getString(messageName, "Undefined message in 'messages.yml'")!!

        for (replacement in replacements) {
            message = message.replace(replacement.any1.toString(), replacement.any2.toString())
        }

        player.message(message)
    }
}
