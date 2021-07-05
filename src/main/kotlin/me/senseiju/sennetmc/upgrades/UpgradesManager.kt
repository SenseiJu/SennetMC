package me.senseiju.sennetmc.upgrades

import me.mattstudios.mf.base.CommandManager
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.upgrades.commands.UpgradesCommand
import me.senseiju.sentils.storage.ConfigFile
import java.util.*

private const val SELECT_QUERY = "SELECT * FROM `upgrades` WHERE `uuid`=?;"

class UpgradesManager(private val plugin: SennetMC) : BaseManager() {

    val upgradesFile = ConfigFile(plugin, "upgrades.yml", true)

    init {
        registerCommands(plugin.commandManager)
    }

    override fun reload() {
        upgradesFile.reload()
    }

    override fun registerCommands(cm: CommandManager) {
        cm.register(UpgradesCommand())
    }

    suspend fun fetchUpgrades(uuid: UUID): EnumMap<Upgrade, Int> {
        val set = plugin.database.asyncQuery(SELECT_QUERY, uuid.toString())
        return if (set.next()) Upgrade.mapFromSet(set)
        else EnumMap<Upgrade, Int>(Upgrade::class.java)
    }

    fun updateUpgrades(uuid: UUID, upgradesMap: EnumMap<Upgrade, Int>) {
        val upgrades = Upgrade.values().map { upgradesMap.getOrDefault(it, 0) }.toTypedArray()

        plugin.database.updateQuery(Upgrade.buildUpdateQuery(), uuid.toString(), *upgrades, *upgrades)
    }
}