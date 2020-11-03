package me.senseiju.commscraft

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mfgui.gui.guis.BaseGui
import me.senseiju.commscraft.speedboat.events.SpeedboatListener
import me.senseiju.commscraft.collectables.CollectablesManager
import me.senseiju.commscraft.commands.ReloadCommand
import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.datastorage.Database
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.fishes.FishManager
import me.senseiju.commscraft.npcs.NpcManager
import me.senseiju.commscraft.users.UserManager
import org.bukkit.plugin.java.JavaPlugin

class CommsCraft : JavaPlugin() {
    val database = Database(this, "database.yml")

    val configFile = DataFile(this, "config.yml", true)
    val messagesFile = DataFile(this, "messages.yml", true)

    lateinit var commandManager: CommandManager
    lateinit var collectablesManager: CollectablesManager
    lateinit var npcManager: NpcManager
    lateinit var userManager: UserManager
    lateinit var fishManager: FishManager

    override fun onEnable() {
        commandManager = CommandManager(this)
        commandManager.messageHandler.register("cmd.no.permission") { it.sendConfigMessage("NO-PERMISSION") }
        commandManager.register(ReloadCommand(this))

        collectablesManager = CollectablesManager(this)
        npcManager = NpcManager(this)
        fishManager = FishManager(this)
        userManager = UserManager(this)

        userManager.fetchUsers()

        SpeedboatListener(this)
    }

    override fun onDisable() {
        for (player in server.onlinePlayers) {
            if (player.openInventory.topInventory.holder is BaseGui) {
                player.closeInventory()
            }
        }

        userManager.saveUsersTask.cancel()
    }

    fun reload() {
        configFile.reload()
        messagesFile.reload()

        collectablesManager.reload()
        npcManager.reload()
        fishManager.reload()
        userManager.reload()
    }
}
