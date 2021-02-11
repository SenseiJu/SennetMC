package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class ChefSellRunnable(val uuid: UUID, timeToComplete: Long, val initialSellPrice: Double,
                       finished: Boolean = false) : BukkitRunnable() {

    var timeToComplete = timeToComplete
        private set

    var finished = finished
        private set

    override fun run() {
        if (timeToComplete <= 0) {
            cancel()

            finished = true

            return
        }

        timeToComplete--
    }

    fun toJson() : String {
        val data = ChefSellRunnableData(uuid, timeToComplete, initialSellPrice, finished)

        return Json.encodeToString(data)
    }
}