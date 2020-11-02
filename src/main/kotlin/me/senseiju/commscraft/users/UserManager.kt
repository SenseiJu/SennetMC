package me.senseiju.commscraft.users

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.ParameterHandler
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.fishes.FishCaughtTableName
import me.senseiju.commscraft.users.events.PlayerJoinListener
import me.senseiju.commscraft.users.placeholders.UserPlaceholderExpansion
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

        UserPlaceholderExpansion(plugin, this).register()
    }

    override fun registerCommands(cm: CommandManager) {}
    override fun registerEvents() {
        PlayerJoinListener(plugin, this)
    }

    override fun reload() {}

    fun fetchUsers() {
        val userSet = plugin.database.query("SELECT * FROM `users`;")
        while (userSet.next()) {
            val uuid = UUID.fromString(userSet.getString("uuid"))
            val maxFishCapacity = userSet.getInt("max_fish_capacity")
            val fishCaughtMap = plugin.fishManager.fetchPlayersFishCaught(FishCaughtTableName.FISH_CAUGHT, uuid)
            val totalFishCaughtMap = plugin.fishManager.fetchPlayersFishCaught(FishCaughtTableName.TOTAL_FISH_CAUGHT, uuid)

            userMap[uuid] = User(uuid, maxFishCapacity, fishCaughtMap, totalFishCaughtMap)
        }
    }

    fun saveUsers() {
        for ((uuid, user) in userMap) {
            plugin.database.updateQuery("UPDATE `users` SET `max_fish_capacity`=? WHERE `uuid`=?",
                    user.maxFishCapacity, uuid.toString())
            plugin.fishManager.updatePlayersFishCaught(FishCaughtTableName.FISH_CAUGHT, uuid, user.currentFishCaught)
            plugin.fishManager.updatePlayersFishCaught(FishCaughtTableName.TOTAL_FISH_CAUGHT, uuid, user.totalFishCaught)
        }
    }

    fun createNewUser(uuid: UUID) {
        if (!userMap.containsKey(uuid)) {
            val user = User(uuid, configFile.config.getInt("starting-fish-capacity", 30),
                    plugin.fishManager.createEmptyFishCaughtMap(), plugin.fishManager.createEmptyFishCaughtMap())
            userMap[uuid] = user

            defaultScope.launch {
                withContext(Dispatchers.IO) {
                    plugin.database.asyncUpdateQuery("INSERT INTO `users`(`uuid`, `max_fish_capacity`) VALUES(?,?);",
                            uuid.toString(), user.maxFishCapacity)
                    plugin.fishManager.insertPlayersFishCaught(uuid);
                }
            }
        }
    }
}