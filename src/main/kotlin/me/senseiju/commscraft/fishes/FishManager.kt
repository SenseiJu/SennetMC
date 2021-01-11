package me.senseiju.commscraft.fishes

import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.fishes.listeners.PlayerFishListener
import java.util.*

class FishManager(private val plugin: CommsCraft) : BaseManager {

    init {
        registerEvents()
    }

    override fun registerCommands(cm: CommandManager) {}
    override fun registerEvents() {
        PlayerFishListener(plugin)
    }

    override fun reload() {
        FishType.dataFile.reload()
    }

    fun fetchPlayersFishCaught(uuid: UUID): EnumMap<FishType, FishCaughtData> {
        val set = plugin.database.query("SELECT * FROM `fish_caught` WHERE `uuid`=?;", uuid.toString())

        val fishCaught: EnumMap<FishType, FishCaughtData> = EnumMap(FishType::class.java)
        while (set.next()) {
            val fishType = FishType.valueOf(set.getString("fish_type"))
            fishCaught[fishType] = FishCaughtData(set.getInt("current"), set.getInt("total"))
        }

        return fishCaught
    }

    fun updatePlayersFishCaught(uuid: UUID, fishCaughtMap: EnumMap<FishType, FishCaughtData>) {
        for ((fishType, fishCaughtData) in fishCaughtMap) {
            val q = "INSERT INTO `fish_caught`(`uuid`, `fish_type`, `current`, `total`) VALUES(?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE `fish_type`=?, `current`=?, `total`=?;"
            plugin.database.updateQuery(q,
                uuid.toString(), fishType.toString(), 0, 0,
                fishType.toString(), fishCaughtData.current, fishCaughtData.total)
        }
    }
}