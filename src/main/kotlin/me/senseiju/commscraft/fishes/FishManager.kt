package me.senseiju.commscraft.fishes

import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.fishes.listeners.PlayerFishingListener
import java.util.*
import kotlin.collections.HashMap

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

    fun fetchPlayersFishCaught(uuid: UUID): EnumMap<FishType, HashMap<String, Int>> {
        val set = plugin.database.query("SELECT * FROM `fish_caught` WHERE `uuid`=?;", uuid.toString())

        val fishCaught: EnumMap<FishType, HashMap<String, Int>> = createEmptyFishCaughtMap()
        while (set.next()) {
            val fishType = FishType.valueOf(set.getString("fish_type"))
            val map = fishCaught[fishType]

            map?.set("current", map.getOrDefault("current", 0).plus(set.getInt("current")))
            map?.set("total", map.getOrDefault("total", 0).plus(set.getInt("total")))
        }

        return fishCaught
    }

    fun updatePlayersFishCaught(uuid: UUID, fishCaughtMap: EnumMap<FishType, HashMap<String, Int>>) {
        for ((fishType, map) in fishCaughtMap) {
            plugin.database.updateQuery("UPDATE `fish_caught` SET `current`=?, `total`=? WHERE `uuid`=? AND `fish_type`=?",
                    map.getOrDefault("current", 0),  map.getOrDefault("total", 0), uuid.toString(),
                    fishType.toString())
        }
    }

    suspend fun insertPlayersFishCaught(uuid: UUID) {
        for (fishType in FishType.values()) {
            plugin.database.asyncUpdateQuery("INSERT INTO `fish_caught`(`uuid`, `fish_type`, `current`, `total`) " +
                    "VALUES(?,?,?,?);", uuid.toString(), fishType.toString(), 0, 0)
        }
    }

    fun createEmptyFishCaughtMap(): EnumMap<FishType, HashMap<String, Int>> {
        val map: EnumMap<FishType, HashMap<String, Int>> = EnumMap(FishType::class.java)

        val hashMap: HashMap<String, Int> = HashMap()
        hashMap["current"] = 0
        hashMap["total"] = 0

        FishType.values().forEach {
            map[it] = HashMap(hashMap)
        }

        return map
    }
}