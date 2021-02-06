package me.senseiju.sennetmc.collectables

import kotlinx.coroutines.launch
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.CompletionHandler
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.Rarity
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.collectables.commands.CollectablesCommand
import me.senseiju.sennetmc.collectables.listeners.PlayerJoinListener
import me.senseiju.sennetmc.datastorage.DataFile
import me.senseiju.sennetmc.datastorage.ReplacementSet
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.utils.ObjectSet
import me.senseiju.sennetmc.utils.defaultScope
import org.bukkit.Material
import org.bukkit.command.CommandSender
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CollectablesManager(private val plugin: SennetMC) : BaseManager {

    private val collectablesFile = DataFile(plugin, "collectables.yml", true)

    var collectables = HashMap<String, Collectable>()
        private set

    init {
        registerCommands(plugin.commandManager)
        registerEvents()

        loadCollectables()
    }

    override fun registerCommands(cm: CommandManager) {
        registerCommandCompletions(cm.completionHandler)

        cm.register(CollectablesCommand(plugin, this))
    }

    private fun registerCommandCompletions(ch: CompletionHandler) {
        ch.register("#collectableId") { collectables.keys.toList() }
    }

    override fun registerEvents() {
        PlayerJoinListener(plugin, this)
    }

    override fun reload() {
        collectablesFile.reload()

        loadCollectables()
    }

    private fun loadCollectables() {
        val newCollectables = HashMap<String, Collectable>()

        collectablesFile.config.getKeys(false).forEach loop@ {
            val section = collectablesFile.config.getConfigurationSection(it)
            if (section == null) {
                println("ERROR: Failed to parse a collectable with id: $it")
                return@loop
            }

            val name = section.getString("name", "NAME NOT FOUND")!!
            val material = Material.matchMaterial(section.getString("material", "BEDROCK")!!)!!
            val glow = section.getBoolean("glow", false)
            val rarity = Rarity.valueOf(section.getString("rarity", "COMMON")!!)
            val description = section.getStringList("description")

            newCollectables[it] = Collectable(it, name, material, glow, rarity, description)
        }

        collectables = newCollectables
    }

    fun addCollectable(targetUUID: UUID, collectableId: String, sender: CommandSender? = null) {
        defaultScope.launch {
            if (!collectables.containsKey(collectableId)) {
                sender?.sendConfigMessage("COLLECTABLES-CANNOT-FIND-COLLECTABLE")
                return@launch
            }

            val user = plugin.userManager.userMap[targetUUID] ?: return@launch
            if (user.collectables.contains(collectableId)) {
                sender?.sendConfigMessage("COLLECTABLES-TARGET-HAS-COLLECTABLE")
                return@launch
            }

            user.collectables.add(collectableId)

            sender?.sendConfigMessage("COLLECTABLES-COLLECTABLE-SET")

            val targetPlayer = plugin.server.getPlayer(user.uuid) ?: return@launch
            targetPlayer.sendConfigMessage("COLLECTABLES-TARGET-COLLECTABLE-SET",
                    ObjectSet("{collectableName}", collectables[collectableId]!!.name))
        }
    }

    fun removeCollectable(targetUUID: UUID, collectableId: String, sender: CommandSender? = null) {
        defaultScope.launch {
            if (!collectables.containsKey(collectableId)) {
                sender?.sendConfigMessage("COLLECTABLES-CANNOT-FIND-COLLECTABLE")
                return@launch
            }

            val user = plugin.userManager.userMap[targetUUID] ?: return@launch
            if (!user.collectables.contains(collectableId)) {
                sender?.sendConfigMessage("COLLECTABLES-TARGET-DOES-NOT-HAVE-COLLECTABLE")
                return@launch
            }

            user.collectables.remove(collectableId)
            sender?.sendConfigMessage("COLLECTABLES-COLLECTABLE-REMOVED")
        }
    }

    suspend fun fetchCollectables(uuid: UUID) : ArrayList<String> {
        val set = plugin.database.asyncQuery("SELECT * FROM `collectables` WHERE `uuid`=?;", uuid.toString())

        val collectables = ArrayList<String>()
        while (set.next()) {
            collectables.add(set.getString("collectable_id"))
        }

        return collectables
    }

    fun updateCollectables(uuid: UUID, collectables: ArrayList<String>) {
        val deleteQuery = "DELETE FROM `collectables` WHERE `uuid`=?;"
        plugin.database.updateQuery(deleteQuery, uuid.toString())

        val insertQuery = "INSERT INTO `collectables`(`uuid`, `collectable_id`) VALUES(?,?);"
        val replacementSets = collectables.map { ReplacementSet(uuid.toString(), it) }.toTypedArray()

        plugin.database.updateBatchQuery(insertQuery, *replacementSets)
    }
}