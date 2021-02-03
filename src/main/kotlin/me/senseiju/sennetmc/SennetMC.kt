package me.senseiju.sennetmc

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mfgui.gui.guis.BaseGui
import me.senseiju.sennetmc.collectables.CollectablesManager
import me.senseiju.sennetmc.commands.DailyCommand
import me.senseiju.sennetmc.commands.ReloadCommand
import me.senseiju.sennetmc.commands.ResourcePackCommand
import me.senseiju.sennetmc.crates.CratesManager
import me.senseiju.sennetmc.datastorage.DataFile
import me.senseiju.sennetmc.datastorage.Database
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.extensions.color
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.fishes.FishManager
import me.senseiju.sennetmc.models.ModelsManager
import me.senseiju.sennetmc.models.listeners.playerPassengers
import me.senseiju.sennetmc.npcs.NpcManager
import me.senseiju.sennetmc.settings.SettingsManager
import me.senseiju.sennetmc.speedboat.SpeedboatManager
import me.senseiju.sennetmc.upgrades.UpgradesManager
import me.senseiju.sennetmc.users.UserManager
import org.bukkit.plugin.java.JavaPlugin

class SennetMC : JavaPlugin() {
    val database = Database(this, "database.yml")

    val configFile = DataFile(this, "config.yml", true)
    val messagesFile = DataFile(this, "messages.yml", true)
    val warpsFile = DataFile(this, "warps.yml", true)

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
    lateinit var eventsManager: EventsManager

    override fun onEnable() {
        commandManager = CommandManager(this)
        commandManager.messageHandler.register("cmd.no.permission") { it.sendConfigMessage("NO-PERMISSION") }

        userManager = UserManager(this)
        modelsManager = ModelsManager(this)
        upgradesManager = UpgradesManager(this)
        collectablesManager = CollectablesManager(this)
        npcManager = NpcManager(this)
        fishManager = FishManager(this)
        speedboatManager = SpeedboatManager(this)
        cratesManager = CratesManager(this)
        settingsManager = SettingsManager(this)
        eventsManager = EventsManager(this)

        SennetMCPlaceholderExpansion(this)

        setupCommands()
    }

    override fun onDisable() {
        server.onlinePlayers.forEach {
            if (it.openInventory.topInventory.holder is BaseGui) {
                it.closeInventory()
            }

            playerPassengers.forEach { (_, stand) -> stand.remove() }

            it.kickPlayer(messagesFile.config.getString("RELOADING", "#914ef5&lSennetMC &bis currently reloading...")?.color())
        }

        userManager.saveUsersTask.cancel()
    }

    fun reload() {
        configFile.reload()
        messagesFile.reload()
        warpsFile.reload()

        upgradesManager.reload()
        collectablesManager.reload()
        npcManager.reload()
        fishManager.reload()
        userManager.reload()
        cratesManager.reload()
        settingsManager.reload()
        modelsManager.reload()
        eventsManager.reload()
    }

    private fun setupCommands() {
        commandManager.register(ReloadCommand(this))
        commandManager.register(ResourcePackCommand())
        commandManager.register(DailyCommand(this))
        //commandManager.register(BuyCommand())
    }
}
