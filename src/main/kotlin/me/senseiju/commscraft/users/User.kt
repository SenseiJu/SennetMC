package me.senseiju.commscraft.users

import me.senseiju.commscraft.fishes.FishCaughtData
import me.senseiju.commscraft.fishes.FishType
import java.util.*
import kotlin.collections.ArrayList

data class User(val uuid: UUID,
                val collectables: ArrayList<String> = ArrayList(),
                val fishCaught: EnumMap<FishType, FishCaughtData> = EnumMap(FishType::class.java),
                var fishCapacityUpgrades: Int = 0,
                var speedboatUpgrades: Int = 0) {

    fun addToCurrentFish(fishType: FishType, amount: Int) {
        if (!fishCaught.containsKey(fishType)) {
            fishCaught[fishType] = FishCaughtData(amount, amount)
            return
        }

        fishCaught[fishType]?.plus(amount)
    }

    fun currentFishCaughtCapacity() : Int {
        var capacity = 0
        fishCaught.entries.forEach { capacity += (it.key.capacity() * it.value.current) }
        return capacity
    }
}