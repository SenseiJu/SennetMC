package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class ChefSellRunnable(val uuid: UUID, private var timeToComplete: Int, val initialSellPrice: Double,
                       claimable: Boolean = false) : BukkitRunnable() {

    var claimable = claimable
        private set

    override fun run() {
        if (timeToComplete <= 0) {
            cancel()

            claimable = true

            return
        }

        timeToComplete--
    }

    fun toJson() : String {
        val data = ChefSellRunnableData(uuid, timeToComplete, initialSellPrice, claimable)

        return Json.encodeToString(data)
    }
}