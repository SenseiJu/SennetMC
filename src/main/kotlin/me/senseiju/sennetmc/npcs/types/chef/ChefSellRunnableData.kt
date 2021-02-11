package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.senseiju.sennetmc.serializers.UUIDSerializer
import java.util.*

@Serializable
data class ChefSellRunnableData(@Serializable(UUIDSerializer::class) private val uuid: UUID,
                                private val timeToComplete: Long,
                                private val initialSellPrice: Double,
                                private val finished: Boolean) {

    companion object {
        fun fromJson(s: String) : ChefSellRunnable {
            val data = Json.decodeFromString<ChefSellRunnableData>(s)

            return ChefSellRunnable(data.uuid, data.timeToComplete, data.initialSellPrice, data.finished)
        }
    }
}
