package me.senseiju.commscraft.collectables

import kotlinx.coroutines.launch
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.entity.Player
import java.util.*

class CollectablesManager(private val plugin: CommsCraft) {

    fun addCollectable(player: Player, targetUUID: UUID, collectableId: String) {
        defaultScope.launch {
            val set = plugin.database.asyncQuery("SELECT COUNT(*) AS `count` FROM `collectables` WHERE `uuid`=? AND `collectable_id`=?;",
                    targetUUID.toString(), collectableId)
            set.next()

            if (set.getInt("count") > 0) {
                plugin.sendMessage(player, "COLLECTABLES-TARGET-HAS-COLLECTABLE")
                return@launch
            }

            plugin.database.asyncUpdateQuery("INSERT INTO `collectables` VALUES(?,?);",
                    targetUUID.toString(), collectableId)
            plugin.sendMessage(player, "COLLECTABLES-COLLECTABLE-SET")
        }
    }

    fun removeCollectable(player: Player, targetUUID: UUID, collectableId: String) {
        defaultScope.launch {
            val set = plugin.database.asyncQuery("SELECT COUNT(*) AS `count` FROM `collectables` WHERE `uuid`=? AND `collectable_id`=?;",
                    targetUUID.toString(), collectableId)
            set.next()

            if (set.getInt("count") == 0) {
                plugin.sendMessage(player, "COLLECTABLES-TARGET-DOES-NOT-HAVE-COLLECTABLE")
                return@launch
            }

            plugin.database.asyncUpdateQuery("DELETE FROM `collectables` WHERE `uuid`=? AND `collectable_id`=?;",
                    targetUUID.toString(), collectableId)
            plugin.sendMessage(player, "COLLECTABLES-COLLECTABLE-REMOVED")
        }
    }
}