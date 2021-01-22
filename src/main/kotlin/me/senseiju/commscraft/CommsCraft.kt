package me.senseiju.commscraft

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mfgui.gui.guis.BaseGui
import me.senseiju.commscraft.collectables.CollectablesManager
import me.senseiju.commscraft.models.commands.HatCommand
import me.senseiju.commscraft.models.commands.ModelCommand
import me.senseiju.commscraft.commands.ReloadCommand
import me.senseiju.commscraft.crates.CratesManager
import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.datastorage.Database
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.fishes.FishManager
import me.senseiju.commscraft.models.ModelType
import me.senseiju.commscraft.models.ModelsManager
import me.senseiju.commscraft.models.removeModelArmorStandPassenger
import me.senseiju.commscraft.npcs.NpcManager
import me.senseiju.commscraft.settings.Setting
import me.senseiju.commscraft.settings.SettingsManager
import me.senseiju.commscraft.speedboat.SpeedboatManager
import me.senseiju.commscraft.upgrades.Upgrade
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
    lateinit var modelsManager: ModelsManager

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
        modelsManager = ModelsManager(this)

        CommsCraftPlaceholderExpansion(this)
    }

    override fun onDisable() {
        server.onlinePlayers.forEach {
            if (it.openInventory.topInventory.holder is BaseGui) {
                it.closeInventory()
            }

            removeModelArmorStandPassenger(it, ModelType.BACKPACK)

            it.kickPlayer("&8&lCommsCraft &bis currently reloading...".color())
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
        cratesManager.reload()
        settingsManager.reload()
        modelsManager.reload()
    }

    private fun setupCommands() {
        commandManager = CommandManager(this)
        commandManager.messageHandler.register("cmd.no.permission") { it.sendConfigMessage("NO-PERMISSION") }
        commandManager.register(ReloadCommand(this))
    }
}
