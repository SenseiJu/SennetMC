package me.senseiju.sennetmc.npcs.types.scrapper

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.sennetmc.equipment.Equipment
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sentils.runnables.PlayerCountdownRunnable
import me.senseiju.sentils.serializers.UUIDSerializer
import java.util.*

@Serializable
class CraftableEquipment(
    private val equipment: Equipment,
    @Serializable(UUIDSerializer::class) override val uuid: UUID,
    override var timeToComplete: Long,
) : PlayerCountdownRunnable() {

    companion object {
        fun fromJson(s: String): CraftableEquipment {
            return Json.decodeFromString(s)
        }
    }

    override fun onComplete() {
        getPlayer()?.sendConfigMessage(
            "SCRAPPER-FINISHED-CRAFTING",
            false,
            PlaceholderSet("npcName", NpcType.SCRAPPER.npcName),
            PlaceholderSet("equipment", equipment.eqName)
        )
    }

    fun toJson(): String {
        return Json.encodeToString(this)
    }
}