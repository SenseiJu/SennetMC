package me.senseiju.commscraft.fishes

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.ParameterHandler
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.fishes.events.PlayerFishingListener
import java.util.*

class FishManager(private val plugin: CommsCraft) : BaseManager {

    init {
        registerEvents()
    }

    override fun registerCommands(cm: CommandManager) {}
    override fun registerEvents() {
        PlayerFishingListener(plugin)
    }

    override fun reload() {
        FishType.dataFile.reload()
    }

    fun fetchPlayersFishCaught(tableName: FishCaughtTableName, uuid: UUID) : EnumMap<FishType, Int> {
        val set = plugin.database.query("SELECT * FROM `{tableName}` WHERE `uuid`=?;"
                .replace("{tableName}", tableName.toString()), uuid.toString())

        val map: EnumMap<FishType, Int> = EnumMap(FishType::class.java)
        while (set.next()) {
            map[FishType.valueOf(set.getString("fish_type"))] = set.getInt("amount")
        }

        return map
    }

    fun updatePlayersFishCaught(tableName: FishCaughtTableName, uuid: UUID, fishCaughtMap: EnumMap<FishType, Int>) {
        for ((fishType, amount) in fishCaughtMap) {
            plugin.database.updateQuery("UPDATE `{tableName}` SET `amount`=? WHERE `uuid`=? AND `fish_type`=?"
                    .replace("{tableName}", tableName.toString()), amount, uuid.toString(), fishType.toString())
        }
    }

    suspend fun insertPlayersFishCaught(uuid: UUID) {
        for (tableName in FishCaughtTableName.values()) {
            for (fishType in FishType.values()) {
                plugin.database.asyncUpdateQuery("INSERT INTO `{tableName}`(`uuid`, `fish_type`, `amount`) VALUES(?,?,?);"
                        .replace("{tableName}", tableName.toString()), uuid.toString(), fishType.toString(), 0)
            }
        }
    }

    fun createEmptyFishCaughtMap() : EnumMap<FishType, Int> {
        val map: EnumMap<FishType, Int> = EnumMap(FishType::class.java)
        FishType.values().forEach { map[it] = 0 }
        return map
    }
}