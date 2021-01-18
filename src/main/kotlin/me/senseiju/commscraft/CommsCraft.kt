package me.senseiju.commscraft

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mfgui.gui.guis.BaseGui
import me.senseiju.commscraft.collectables.CollectablesManager
import me.senseiju.commscraft.commands.ReloadCommand
import me.senseiju.commscraft.crates.CratesManager
import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.datastorage.Database
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.fishes.FishManager
import me.senseiju.commscraft.npcs.NpcManager
import me.senseiju.commscraft.settings.SettingsManager
import me.senseiju.commscraft.speedboat.SpeedboatManager
import me.senseiju.commscraft.upgrades.UpgradesManager
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
    lateinit var speedboatManager: SpeedboatManager
    lateinit var cratesManager: CratesManager
    lateinit var upgradesManager: UpgradesManager
    lateinit var settingsManager: SettingsManager

    override fun onEnable() {
        setupCommands()

        collectablesManager = CollectablesManager(this)
        npcManager = NpcManager(this)
        fishManager = FishManager(this)
        userManager = UserManager(this)
        speedboatManager = SpeedboatManager(this)
        cratesManager = CratesManager(this)
        upgradesManager = UpgradesManager(this)
        settingsManager = SettingsManager(this)

        CommsCraftPlaceholderExpansion(this)
    }

    override fun onDisable() {
        for (player in server.onlinePlayers) {
            if (player.openInventory.topInventory.holder is BaseGui) {
                player.closeInventory()
            }
        }

        userManager.saveUsersTask.cancel()

        kickAllPlayers()
    }

    fun reload() {
        configFile.reload()
        messagesFile.reload()

        collectablesManager.reload()
        npcManager.reload()
        fishManager.reload()
        userManager.reload()
        cratesManager.reload()
    }

    private fun kickAllPlayers() {
        server.onlinePlayers.forEach {
            it.kickPlayer("&8&lCommsCraft &bis currently reloading...".color())
        }
    }

    private fun setupCommands() {
        commandManager = CommandManager(this)
        commandManager.messageHandler.register("cmd.no.permission") { it.sendConfigMessage("NO-PERMISSION") }
        commandManager.register(ReloadCommand(this))
    }
}
