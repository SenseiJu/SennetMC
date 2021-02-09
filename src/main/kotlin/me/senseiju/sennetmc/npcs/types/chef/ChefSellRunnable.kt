package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class ChefSellRunnable(val uuid: UUID, private var timeRemaining: Int, private val initialSellPrice: Double) : BukkitRunnable() {

    override fun run() {
        if (timeRemaining <= 0) {
            cancel()

            return
        }

        timeRemaining--
    }

    fun toJson() : String {
        val data = ChefSellRunnableData(uuid, timeRemaining, initialSellPrice)

        return Json.encodeToString(data)
    }
}