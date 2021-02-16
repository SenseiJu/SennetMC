package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.senseiju.sennetmc.utils.serializers.UUIDSerializer
import java.util.*

@Serializable
data class SerializableChefSellRunnable(@Serializable(UUIDSerializer::class) private val uuid: UUID,
                                        private val timeToComplete: Long,
                                        private val initialSellPrice: Double,
                                        private val finished: Boolean) {

    companion object {
        fun fromJson(s: String) : ChefSellRunnable {
            val data = Json.decodeFromString<SerializableChefSellRunnable>(s)

            return ChefSellRunnable(data.uuid,  data.initialSellPrice, data.timeToComplete, data.finished)
        }
    }
}
