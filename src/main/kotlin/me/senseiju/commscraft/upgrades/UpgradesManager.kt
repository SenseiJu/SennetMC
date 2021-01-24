package me.senseiju.commscraft.upgrades

import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.datastorage.DataFile
import java.util.*

private const val SELECT_QUERY = "SELECT * FROM `upgrades` WHERE `uuid`=?;"

class UpgradesManager(private val plugin: CommsCraft) : BaseManager {

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

        plugin.database.updateQuery(Upgrade.buildUpdateDatabaseQuery(), uuid.toString(), *upgrades, *upgrades)
    }
}