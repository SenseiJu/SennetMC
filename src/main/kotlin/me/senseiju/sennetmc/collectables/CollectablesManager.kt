package me.senseiju.sennetmc.collectables

import kotlinx.coroutines.launch
import me.mattstudios.mf.base.CommandManager
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.Rarity
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.collectables.commands.CollectablesCommand
import me.senseiju.sennetmc.collectables.listeners.PlayerJoinListener
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.datastorage.Replacements
import me.senseiju.sennetmc.utils.defaultScope
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sentils.registerEvents
import me.senseiju.sentils.storage.ConfigFile
import org.bukkit.Material
import org.bukkit.command.CommandSender
import java.util.*

class CollectablesManager(private val plugin: SennetMC) : BaseManager() {

    private val collectablesFile = ConfigFile(plugin, "collectables.yml", true)

    var collectables = HashMap<String, Collectable>()
        private set

    init {
        registerCommands(plugin.commandManager)
        registerEvents(plugin)

        loadCollectables()
    }

    override fun registerCommands(cm: CommandManager) {
        cm.completionHandler.register("#collectableId") { collectables.keys.toList() }

        cm.register(CollectablesCommand(plugin, this))
    }

    override fun registerEvents(plugin: SennetMC) {
        plugin.registerEvents(PlayerJoinListener(this))
    }

    override fun reload() {
        collectablesFile.reload()

        loadCollectables()
    }

    private fun loadCollectables() {
        val newCollectables = HashMap<String, Collectable>()

        collectablesFile.getKeys(false).forEach loop@{
            val section = collectablesFile.getConfigurationSection(it)
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
            targetPlayer.sendConfigMessage(
                "COLLECTABLES-TARGET-COLLECTABLE-SET",
                PlaceholderSet("{collectableName}", collectables[collectableId]!!.name)
            )
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

    suspend fun fetchCollectables(uuid: UUID): ArrayList<String> {
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
        val replacementSets = collectables.map { Replacements(uuid.toString(), it) }.toTypedArray()

        plugin.database.updateBatchQuery(insertQuery, *replacementSets)
    }
}