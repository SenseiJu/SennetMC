package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.senseiju.sennetmc.serializers.UUIDSerializer
import java.util.*

@Serializable
data class ChefSellRunnableData(@Serializable(UUIDSerializer::class) private val uuid: UUID,
                                private val timeRemaining: Int,
                                private val initialSellPrice: Double) {

    companion object {
        fun fromJson(s: String) : ChefSellRunnable {
            val data = Json.decodeFromString<ChefSellRunnableData>(s)

            return ChefSellRunnable(data.uuid, data.timeRemaining, data.initialSellPrice)
        }
    }
}
