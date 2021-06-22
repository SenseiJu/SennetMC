package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.PlayerCountdownBukkitRunnable
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sennetmc.utils.serializers.UUIDSerializer
import org.bukkit.Bukkit
import java.util.*

private val NPC_TYPE = NpcType.CHEF

@Serializable
class ChefSellRunnable(
    val initialSellPrice: Double,
    @Serializable(UUIDSerializer::class) override val uuid: UUID,
    override var timeToComplete: Long,
    override var finished: Boolean = false
) : PlayerCountdownBukkitRunnable() {

    companion object {
        fun fromJson(s: String): ChefSellRunnable {
            return Json.decodeFromString(s)
        }
    }

    override fun onComplete() {
        player?.sendConfigMessage(
            "CHEF-FINISHED-RUNNING",
            false,
            PlaceholderSet("{chefName}", NPC_TYPE.npcName)
        )
    }

    fun toJson(): String {
        return Json.encodeToString(this)
    }
}