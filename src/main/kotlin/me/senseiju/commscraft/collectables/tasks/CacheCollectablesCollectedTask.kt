package me.senseiju.commscraft.collectables.tasks

import kotlinx.coroutines.launch
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.collectables.CollectablesManager
import me.senseiju.commscraft.utils.defaultScope
import java.util.*
import kotlin.collections.HashMap

class CacheCollectablesCollectedTask(private val plugin: CommsCraft, private val collectablesManager: CollectablesManager) : Runnable {

    private var inProgress = false

    init {
        plugin.server.scheduler.runTaskTimerAsynchronously(plugin, this, 0, 20)
    }

    override fun run() {
        if (inProgress) return

        inProgress = true

        defaultScope.launch {
            val collectablesCollectedTempMap = HashMap<UUID, Int>()

            plugin.server.onlinePlayers.forEach {
                val q = "SELECT COUNT(*) AS `count` FROM `collectables` WHERE `uuid`=?;"
                val set = plugin.database.asyncQuery(q, it.uniqueId.toString())

                val collectablesCollected = if (!set.next()) 0 else set.getInt("count")

                collectablesCollectedTempMap[it.uniqueId] = collectablesCollected
            }

            collectablesManager.collectablesCollected = collectablesCollectedTempMap

            inProgress = false
        }
    }
}