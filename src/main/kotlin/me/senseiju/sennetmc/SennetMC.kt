package me.senseiju.sennetmc

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mfgui.gui.guis.BaseGui
import me.senseiju.sennetmc.arena.ArenaManager
import me.senseiju.sennetmc.collectables.CollectablesManager
import me.senseiju.sennetmc.commands.*
import me.senseiju.sennetmc.crates.CratesManager
import me.senseiju.sennetmc.equipment.EquipmentManager
import me.senseiju.sennetmc.events.EventsManager
import me.senseiju.sennetmc.fishes.FishManager
import me.senseiju.sennetmc.npcs.NpcManager
import me.senseiju.sennetmc.scrap.ScrapManager
import me.senseiju.sennetmc.settings.Setting
import me.senseiju.sennetmc.settings.SettingsManager
import me.senseiju.sennetmc.speedboat.SpeedboatManager
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.upgrades.UpgradesManager
import me.senseiju.sennetmc.users.UserManager
import me.senseiju.sennetmc.users.UserTable
import me.senseiju.sennetmc.utils.datastorage.DataFile
import me.senseiju.sennetmc.utils.datastorage.Database
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sentils.extensions.primitives.color
import net.kyori.adventure.text.Component
import net.milkbowl.vault.economy.Economy
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class SennetMC : JavaPlugin() {
    val database = Database(this, "database.yml")

    val configFile = DataFile(this, "config.yml", true)
    val messagesFile = DataFile(this, "messages.yml", true)
    val warpsFile = DataFile(this, "warps.yml", true)

    lateinit var commandManager: CommandManager
    lateinit var collectablesManager: CollectablesManager
    lateinit var userManager: UserManager
    lateinit var fishManager: FishManager
    lateinit var speedboatManager: SpeedboatManager
    lateinit var cratesManager: CratesManager
    lateinit var upgradesManager: UpgradesManager
    lateinit var settingsManager: SettingsManager
    lateinit var arenaManager: ArenaManager
    private lateinit var npcManager: NpcManager
    private lateinit var eventsManager: EventsManager
    private lateinit var scrapManager: ScrapManager
    lateinit var equipmentManager: EquipmentManager

    override fun onEnable() {
        commandManager = CommandManager(this)
        commandManager.messageHandler.register("cmd.no.permission") { it.sendConfigMessage("NO-PERMISSION") }

        userManager = UserManager(this)
        upgradesManager = UpgradesManager(this)
        collectablesManager = CollectablesManager(this)
        npcManager = NpcManager(this)
        fishManager = FishManager(this)
        speedboatManager = SpeedboatManager(this)
        cratesManager = CratesManager(this)
        settingsManager = SettingsManager(this)
        eventsManager = EventsManager(this)
        arenaManager = ArenaManager(this)
        scrapManager = ScrapManager(this)
        equipmentManager = EquipmentManager(this)

        if (!validEconomy()) {
            server.pluginManager.disablePlugin(this)
            return
        }

        SennetMCPlaceholderExpansion(this)

        setupCommands()

        createTables()
    }

    override fun onDisable() {
        arenaManager.cancelAllMatches()

        npcManager.save()

        server.onlinePlayers.forEach {
            if (it.openInventory.topInventory.holder is BaseGui) {
                it.closeInventory()
            }

            it.kick(
                Component.text(
                    messagesFile.config.getString("RELOADING", "&#914ef5&lSennetMC &bis currently reloading...")!!.color()
                )
            )
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
        eventsManager.reload()
        arenaManager.reload()
    }

    fun registerEvents(vararg listeners: Listener) {
        listeners.forEach { this.server.pluginManager.registerEvents(it, this) }
    }

    private fun validEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }

        server.servicesManager.getRegistration(Economy::class.java) ?: return false

        return true
    }

    private fun setupCommands() {
        commandManager.register(ReloadCommand(this))
        commandManager.register(ResourcePackCommand())
        commandManager.register(DailyCommand(this))
        commandManager.register(BuycraftCommand())
        commandManager.register(DiscordCommand())
    }

    private fun createTables() {
        database.updateQuery(UserTable.buildCreateTableQuery())

        database.updateQuery(
            "CREATE TABLE IF NOT EXISTS `models`(`uuid` CHAR(36) NOT NULL, `model_type` CHAR(255) NOT NULL, " +
                    "`model_data` INT NOT NULL, UNIQUE KEY `key_uuid_model`(`uuid`, `model_type`, `model_data`));"
        )

        database.updateQuery(
            "CREATE TABLE IF NOT EXISTS `active_models`(`uuid` CHAR(36) NOT NULL, `model_type` CHAR(255) NOT NULL, " +
                    "`model_data` INT NOT NULL, UNIQUE KEY `key_uuid_model_type`(`uuid`, `model_type`));"
        )

        database.updateQuery(Setting.buildCreateTableQuery())

        database.updateQuery(Upgrade.buildCreateTableQuery())

        database.updateQuery(
            "CREATE TABLE IF NOT EXISTS `fish_caught`(`uuid` CHAR(36) NOT NULL, `fish_type` CHAR(255) NOT NULL, " +
                    "`current` INT, `total` INT, UNIQUE KEY `key_uuid_fish_type`(`uuid`, `fish_type`));"
        )

        database.updateQuery(
            "CREATE TABLE IF NOT EXISTS `collectables`(`uuid` CHAR(36) NOT NULL, `collectable_id` CHAR(255) NOT NULL, " +
                    "UNIQUE KEY `key_uuid_collectable_id`(`uuid`, `collectable_id`));"
        )
    }
}
