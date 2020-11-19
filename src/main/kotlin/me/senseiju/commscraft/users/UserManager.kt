package me.senseiju.commscraft.users

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.users.events.PlayerJoinListener
import me.senseiju.commscraft.users.tasks.SaveUsersTask
import me.senseiju.commscraft.utils.defaultScope
import java.util.*
import kotlin.collections.HashMap

class UserManager(private val plugin: CommsCraft) : BaseManager {
    val saveUsersTask = SaveUsersTask(plugin, this)

    val userMap = HashMap<UUID, User>()
    private val configFile = plugin.configFile

    init {
        registerEvents()
    }

    override fun registerCommands(cm: CommandManager) {
    }

    override fun registerEvents() {
        PlayerJoinListener(plugin, this)
    }

    override fun reload() {}

    fun fetchUsers() {
        val userSet = plugin.database.query("SELECT * FROM `users`;")
        while (userSet.next()) {
            val uuid = UUID.fromString(userSet.getString("uuid"))
            val maxFishCapacity = userSet.getInt("fish_capacity_upgrades")
            val speedboatUpgrades = userSet.getInt("speedboat_upgrades")
            val fishCaughtMap = plugin.fishManager.fetchPlayersFishCaught(uuid)

            userMap[uuid] = User(uuid, fishCaughtMap, maxFishCapacity, speedboatUpgrades)
        }
    }

    fun saveUsers() {
        for ((uuid, user) in userMap) {
            plugin.database.updateQuery("UPDATE `users` SET `fish_capacity_upgrades`=?, `speedboat_upgrades`=? WHERE `uuid`=?;",
                    user.fishCapacityUpgrades, user.speedboatUpgrades, uuid.toString())
            plugin.fishManager.updatePlayersFishCaught(uuid, user.fishCaught)
        }
    }

    fun createNewUser(uuid: UUID) {
        if (!userMap.containsKey(uuid)) {
            val user = User(uuid, plugin.fishManager.createEmptyFishCaughtMap())
            userMap[uuid] = user

            defaultScope.launch {
                withContext(Dispatchers.IO) {
                    plugin.database.asyncUpdateQuery("INSERT INTO `users`(`uuid`, `fish_capacity_upgrades`, `speedboat_upgrades`) " +
                            "VALUES(?,?,?);", uuid.toString(), user.fishCapacityUpgrades, user.speedboatUpgrades)
                    plugin.fishManager.insertPlayersFishCaught(uuid)
                }
            }
        }
    }
}