package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sentils.runnables.PlayerCountdownRunnable
import me.senseiju.sentils.serializers.UUIDSerializer
import java.util.*

private val json = Json { ignoreUnknownKeys = true }

private val NPC_TYPE = NpcType.CHEF

@Serializable
class ChefSellRunnable(
    val initialSellPrice: Double,
    @Serializable(UUIDSerializer::class) override val uuid: UUID,
    override var timeToComplete: Long,
) : PlayerCountdownRunnable() {

    companion object {
        fun fromJson(s: String): ChefSellRunnable {
            return json.decodeFromString(s)
        }
    }

    override fun onComplete() {
        getPlayer()?.sendConfigMessage(
            "CHEF-FINISHED-RUNNING",
            false,
            PlaceholderSet("{chefName}", NPC_TYPE.npcName)
        )
    }

    fun toJson(): String {
        return Json.encodeToString(this)
    }
}