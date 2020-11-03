package me.senseiju.commscraft.users

import me.senseiju.commscraft.fishes.FishType
import java.util.*
import kotlin.collections.HashMap

data class User(val uuid: UUID,
                val fishCaught: EnumMap<FishType, HashMap<String, Int>>,
                var fishCapacityUpgrades: Int = 0,
                var speedboatUpgrades: Int = 0) {

    fun addToCurrentFish(fishType: FishType, amount: Int) {
        if (!fishCaught.containsKey(fishType)) {
            val hashMap: HashMap<String, Int> = HashMap()
            hashMap["current"] = amount
            hashMap["total"] = amount

            fishCaught[fishType] = hashMap
            return
        }

        val map = fishCaught[fishType]
        map?.set("current", map.getOrDefault("current", 0).plus(amount))
        map?.set("total", map.getOrDefault("total", 0).plus(amount))
    }

    fun currentFishCaughtCapacity() : Int {
        var capacity = 0
        fishCaught.entries.forEach { capacity += (it.key.capacity() * it.value.getOrDefault("current", 0)) }
        return capacity
    }
}