package me.senseiju.commscraft.users

import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.users.listeners.PlayerJoinListener
import me.senseiju.commscraft.users.listeners.PlayerPreLoginListener
import me.senseiju.commscraft.users.tasks.SaveUsersTask
import java.util.*
import kotlin.collections.HashMap

class UserManager(private val plugin: CommsCraft) : BaseManager {
    val saveUsersTask = SaveUsersTask(plugin, this)

    val userMap = HashMap<UUID, User>()

    init {
        registerEvents()
    }

    override fun registerCommands(cm: CommandManager) {
    }

    override fun registerEvents() {
        PlayerPreLoginListener(plugin, this)
        PlayerJoinListener(plugin, this)
    }

    override fun reload() {}

    suspend fun doesUserExist(uuid: UUID) : Boolean {
        val q = "SELECT * FROM `users` WHERE `uuid`=?;"

        val set = plugin.database.asyncQuery(q, uuid.toString())

        return set.next()
    }

    suspend fun fetchUser(uuid: UUID) {
        userMap[uuid] = User(uuid,
                plugin.collectablesManager.fetchCollectables(uuid),
                plugin.fishManager.fetchFishCaught(uuid),
                plugin.upgradesManager.fetchUpgrades(uuid),
                plugin.settingsManager.fetchSettings(uuid),
                plugin.modelsManager.fetchModels(uuid),
                plugin.modelsManager.fetchActiveModels(uuid))
    }

    fun saveUsers() {
        for ((uuid, user) in userMap) {
            plugin.fishManager.updateFishCaught(uuid, user.fishCaught)
            plugin.collectablesManager.updateCollectables(uuid, user.collectables)
            plugin.upgradesManager.updateUpgrades(uuid, user.upgrades)
            plugin.settingsManager.updateSettings(uuid, user.settings)
            plugin.modelsManager.updateModels(uuid, user.models)
            plugin.modelsManager.updateActiveModels(uuid, user.activeModels)
        }
    }

    suspend fun createNewUser(uuid: UUID) {
        fetchUser(uuid)
    }
}