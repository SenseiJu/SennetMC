package me.senseiju.sennetmc.upgrades

import me.mattstudios.mf.base.CommandManager
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.datastorage.DataFile
import java.util.*

private const val SELECT_QUERY = "SELECT * FROM `upgrades` WHERE `uuid`=?;"

class UpgradesManager(private val plugin: SennetMC) : BaseManager {

    val upgradesFile = DataFile(plugin, "upgrades.yml", true)

    init {
        registerCommands(plugin.commandManager)
        registerEvents()
    }

    override fun registerCommands(cm: CommandManager) {
    }

    override fun registerEvents() {
    }

    override fun reload() {
        upgradesFile.reload()
    }

    suspend fun fetchUpgrades(uuid: UUID) : EnumMap<Upgrade, Int> {
        val set = plugin.database.asyncQuery(SELECT_QUERY, uuid.toString())
        return if (set.next()) Upgrade.mapFromSet(set)
        else EnumMap<Upgrade, Int>(Upgrade::class.java)
    }

    fun updateUpgrades(uuid: UUID, upgradesMap: EnumMap<Upgrade, Int>) {
        val upgrades = Upgrade.values().map { upgradesMap.getOrDefault(it, 0) }.toTypedArray()

        plugin.database.updateQuery(Upgrade.buildUpdateQuery(), uuid.toString(), *upgrades, *upgrades)
    }
}