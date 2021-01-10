package me.senseiju.commscraft.collectables

import kotlinx.coroutines.launch
import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.collectables.commands.CollectablesCommand
import me.senseiju.commscraft.collectables.commands.CollectablesListCommand
import me.senseiju.commscraft.collectables.commands.CollectablesRemoveCommand
import me.senseiju.commscraft.collectables.commands.CollectablesSetCommand
import me.senseiju.commscraft.collectables.listeners.PlayerJoinListener
import me.senseiju.commscraft.collectables.tasks.CacheCollectablesCollectedTask
import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.utils.ObjectSet
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.entity.Player
import java.util.*

class CollectablesManager(private val plugin: CommsCraft) : BaseManager {

    val collectablesFile = DataFile(plugin, "collectables.yml", true)

    var collectablesCollected = HashMap<UUID, Int>()

    init {
        registerCommands(plugin.commandManager)
        registerEvents()

        CacheCollectablesCollectedTask(plugin, this)
    }

    override fun registerCommands(cm: CommandManager) {
        cm.register(CollectablesCommand(plugin))
        cm.register(CollectablesListCommand(plugin))
        cm.register(CollectablesSetCommand(plugin, this))
        cm.register(CollectablesRemoveCommand(plugin, this))
    }

    override fun registerEvents() {
        PlayerJoinListener(plugin, this)
    }

    override fun reload() {
        collectablesFile.reload()
    }

    fun addCollectable(targetUUID: UUID, collectableId: String, player: Player? = null) {
        defaultScope.launch {
            val set = plugin.database.asyncQuery("SELECT COUNT(*) AS `count` FROM `collectables` WHERE `uuid`=? AND `collectable_id`=?;",
                    targetUUID.toString(), collectableId)
            set.next()

            if (set.getInt("count") > 0) {
                player?.sendConfigMessage("COLLECTABLES-TARGET-HAS-COLLECTABLE")
                return@launch
            }

            plugin.database.asyncUpdateQuery("INSERT INTO `collectables` VALUES(?,?);",
                    targetUUID.toString(), collectableId)

            player?.sendConfigMessage("COLLECTABLES-COLLECTABLE-SET")
            plugin.server.getPlayer(targetUUID)?.sendConfigMessage("COLLECTABLES-TARGET-COLLECTABLE-SET",
                ObjectSet("{collectableName}", collectablesFile.config.getString("${collectableId}.name")!!))

            return@launch
        }
    }

    fun removeCollectable(targetUUID: UUID, collectableId: String, player: Player? = null) {
        defaultScope.launch {
            val set = plugin.database.asyncQuery("SELECT COUNT(*) AS `count` FROM `collectables` WHERE `uuid`=? AND `collectable_id`=?;",
                    targetUUID.toString(), collectableId)
            set.next()

            if (set.getInt("count") == 0) {
                player?.sendConfigMessage("COLLECTABLES-TARGET-DOES-NOT-HAVE-COLLECTABLE")
                return@launch
            }

            plugin.database.asyncUpdateQuery("DELETE FROM `collectables` WHERE `uuid`=? AND `collectable_id`=?;",
                    targetUUID.toString(), collectableId)
            player?.sendConfigMessage("COLLECTABLES-COLLECTABLE-REMOVED")
        }
    }
}