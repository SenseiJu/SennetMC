package me.senseiju.commscraft.users

import me.senseiju.commscraft.fishes.FishType
import java.util.*

data class User(val uuid: UUID,
                var maxFishCapacity: Int,
                val currentFishCaught: EnumMap<FishType, Int>,
                var totalFishCaught: EnumMap<FishType, Int>) {

    fun addToCurrentFish(fishType: FishType, amount: Int) {
        if (!currentFishCaught.containsKey(fishType)) currentFishCaught[fishType] = amount
        else currentFishCaught[fishType] = currentFishCaught[fishType]?.plus(amount)

        if (!totalFishCaught.containsKey(fishType)) totalFishCaught[fishType] = amount
        else totalFishCaught[fishType] = totalFishCaught[fishType]?.plus(amount)
    }


    fun currentFishCaughtCapacity() : Int {
        var capacity = 0
        currentFishCaught.entries.forEach { capacity += (it.key.capacity() * it.value) }
        return capacity
    }
}