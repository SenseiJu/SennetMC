package me.senseiju.sennetmc.fishes

import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.fishes.listeners.PlayerCaughtFishListener
import me.senseiju.sennetmc.fishes.listeners.PlayerFishListener
import me.senseiju.sentils.registerEvents
import java.util.*

class FishManager(private val plugin: SennetMC) : BaseManager() {

    init {
        registerEvents(plugin)
    }

    override fun registerEvents(plugin: SennetMC) {
        plugin.registerEvents(
            PlayerFishListener(plugin),
            PlayerCaughtFishListener(plugin)
        )
    }

    override fun reload() {
        FishType.dataFile.reload()
    }

    suspend fun fetchFishCaught(uuid: UUID): EnumMap<FishType, FishCaughtData> {
        val set = plugin.database.asyncQuery("SELECT * FROM `fish_caught` WHERE `uuid`=?;", uuid.toString())

        val fishCaught: EnumMap<FishType, FishCaughtData> = EnumMap(FishType::class.java)
        while (set.next()) {
            val fishType = FishType.valueOf(set.getString("fish_type"))
            fishCaught[fishType] = FishCaughtData(set.getInt("current"), set.getInt("total"))
        }

        return fishCaught
    }

    fun updateFishCaught(uuid: UUID, fishCaughtMap: EnumMap<FishType, FishCaughtData>) {
        for ((fishType, fishCaughtData) in fishCaughtMap) {
            val q = "INSERT INTO `fish_caught`(`uuid`, `fish_type`, `current`, `total`) VALUES(?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE `fish_type`=?, `current`=?, `total`=?;"
            plugin.database.updateQuery(
                q,
                uuid.toString(), fishType.toString(), 0, 0,
                fishType.toString(), fishCaughtData.current, fishCaughtData.total
            )
        }
    }
}