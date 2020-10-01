package me.senseiju.commscraft

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mfgui.gui.guis.BaseGui
import me.senseiju.commscraft.collectables.CollectablesManager
import me.senseiju.commscraft.collectables.commands.CollectablesCommand
import me.senseiju.commscraft.collectables.commands.CollectablesRemoveCommand
import me.senseiju.commscraft.collectables.commands.CollectablesSetCommand
import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.datastorage.Database
import me.senseiju.commscraft.extensions.message
import me.senseiju.commscraft.utils.ObjectSet
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class CommsCraft : JavaPlugin() {
    val database = Database(this, "database.yml")

    val configFile = DataFile(this, "config.yml", true)
    val collectablesFile = DataFile(this, "collectables.yml", true)
    private val messagesFile = DataFile(this, "messages.yml", true)

    private lateinit var commandManager: CommandManager
    lateinit var collectablesManager: CollectablesManager

    override fun onEnable() {
        collectablesManager = CollectablesManager(this)

        registerCommands()
    }

    override fun onDisable() {
        for (player in server.onlinePlayers) {
            if (player.openInventory.topInventory.holder is BaseGui) {
                player.closeInventory()
            }
        }
    }

    private fun registerCommands() {
        commandManager = CommandManager(this);

        commandManager.register(CollectablesCommand(this))
        commandManager.register(CollectablesSetCommand(this))
        commandManager.register(CollectablesRemoveCommand(this))
    }

    fun sendMessage(player: Player, messageName: String, prefix: Boolean = true, vararg replacements: ObjectSet = emptyArray()) {
        var message = messagesFile.config.getString(messageName, "Undefined message for '$messageName'")!!

        for (replacement in replacements) {
            message = message.replace(replacement.any1.toString(), replacement.any2.toString())
        }

        if (prefix) {
            message = "${messagesFile.config.getString("PREFIX", "&8&lCommsCraft >")} $message"
        }

        player.message(message)
    }
}
