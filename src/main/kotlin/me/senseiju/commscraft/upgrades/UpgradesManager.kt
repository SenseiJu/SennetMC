package me.senseiju.commscraft.upgrades

import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import java.util.*

private val UPDATE_QUERY = "INSERT INTO `upgrades`(`uuid`, " +
        "`${Upgrade.FISH_CAPACITY.databaseField}`, `${Upgrade.SPEEDBOAT_SPEED.databaseField}`, " +
        "`${Upgrade.TREASURE_FINDER.databaseField}`, `${Upgrade.DISCOVERY.databaseField}`, " +
        "`${Upgrade.CRATE_MASTER.databaseField}`, `${Upgrade.NEGOTIATE.databaseField}`, " +
        "`${Upgrade.PLAYER_SPEED.databaseField}`) VALUES(?,?,?,?,?,?,?,?)" +
        "ON DUPLICATE KEY UPDATE " +
        "`${Upgrade.FISH_CAPACITY.databaseField}`=?, `${Upgrade.SPEEDBOAT_SPEED.databaseField}`=?, " +
        "`${Upgrade.TREASURE_FINDER.databaseField}`=?, `${Upgrade.DISCOVERY.databaseField}`=?, " +
        "`${Upgrade.CRATE_MASTER.databaseField}`=?, `${Upgrade.NEGOTIATE.databaseField}`=?, " +
        "`${Upgrade.PLAYER_SPEED.databaseField}`=?;"

private const val SELECT_QUERY = "SELECT * FROM `upgrades` WHERE `uuid`=?;"

class UpgradesManager(private val plugin: CommsCraft) : BaseManager {

    init {
        registerCommands(plugin.commandManager)
        registerEvents()
    }

    override fun registerCommands(cm: CommandManager) {
    }

    override fun registerEvents() {
    }

    override fun reload() {
    }

    suspend fun fetchUpgrades(uuid: UUID) : EnumMap<Upgrade, Int> {
        val set = plugin.database.asyncQuery(SELECT_QUERY, uuid.toString())
        return if (set.next()) Upgrade.mapFromSet(set)
        else EnumMap<Upgrade, Int>(Upgrade::class.java)
    }

    fun updateUpgrades(uuid: UUID, upgradesMap: EnumMap<Upgrade, Int>) {
        val upgrades = Upgrade.values().map { upgradesMap.getOrDefault(it, 0) }.toTypedArray()

        plugin.database.updateQuery(UPDATE_QUERY, uuid.toString(), *upgrades, *upgrades)
    }
}