package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

private val NPC_TYPE = NpcType.CHEF

class ChefSellRunnable(val uuid: UUID,
                       val initialSellPrice: Double,
                       timeToComplete: Long,
                       finished: Boolean = false) : BukkitRunnable() {

    var timeToComplete = timeToComplete
        private set

    var finished = finished
        private set

    override fun run() {
        if (timeToComplete <= 0) {
            cancel()

            finished = true

            Bukkit.getPlayer(uuid)?.sendConfigMessage("CHEF-FINISHED-RUNNING", false,
                PlaceholderSet("{chefName}", NPC_TYPE.npcName))

            return
        }

        timeToComplete--
    }

    fun toJson() : String {
        val data = SerializableChefSellRunnable(uuid, timeToComplete, initialSellPrice, finished)

        return Json.encodeToString(data)
    }
}