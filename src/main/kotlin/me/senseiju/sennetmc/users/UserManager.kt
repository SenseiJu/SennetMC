package me.senseiju.sennetmc.users

import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.users.listeners.PlayerJoinListener
import me.senseiju.sennetmc.users.listeners.PlayerPreLoginListener
import me.senseiju.sennetmc.users.tasks.SaveUsersTask
import me.senseiju.sentils.registerEvents
import java.time.Instant
import java.util.*

class UserManager(private val plugin: SennetMC) : BaseManager() {
    val saveUsersTask = SaveUsersTask(plugin, this)

    val userMap = HashMap<UUID, User>()

    init {
        registerEvents(plugin)
    }

    override fun registerEvents(plugin: SennetMC) {
        plugin.registerEvents(
            PlayerPreLoginListener(this),
            PlayerJoinListener(this)
        )
    }

    suspend fun doesUserExist(uuid: UUID): Boolean {
        val q = "SELECT * FROM `users` WHERE `uuid`=?;"

        val set = plugin.database.asyncQuery(q, uuid.toString())

        return set.next()
    }

    suspend fun fetchUser(uuid: UUID) {
        val q = "SELECT * FROM `users` WHERE `uuid`=?;"
        val set = plugin.database.asyncQuery(q, uuid.toString())

        set.next()

        val dailyRewardLastClaimed =
            Instant.ofEpochMilli(set.getLong(UserTable.DAILY_REWARD_LAST_CLAIMED_TIME.databaseField))

        userMap[uuid] = User(
            uuid,
            plugin.collectablesManager.fetchCollectables(uuid),
            plugin.fishManager.fetchFishCaught(uuid),
            plugin.upgradesManager.fetchUpgrades(uuid),
            plugin.settingsManager.fetchSettings(uuid),
            dailyRewardLastClaimed
        )
    }

    fun saveUsers() {
        for ((uuid, user) in userMap) {
            plugin.fishManager.updateFishCaught(uuid, user.fishCaught)
            plugin.collectablesManager.updateCollectables(uuid, user.collectables)
            plugin.upgradesManager.updateUpgrades(uuid, user.upgrades)
            plugin.settingsManager.updateSettings(uuid, user.settings)
            updateUser(user)
        }
    }

    suspend fun createNewUser(uuid: UUID) {
        userMap[uuid] = User(
            uuid,
            plugin.collectablesManager.fetchCollectables(uuid),
            plugin.fishManager.fetchFishCaught(uuid),
            plugin.upgradesManager.fetchUpgrades(uuid),
            plugin.settingsManager.fetchSettings(uuid),
        )
    }

    private fun updateUser(user: User) {
        val dailyRewardLastClaimed = user.dailyRewardLastClaimed.toEpochMilli()

        plugin.database.updateQuery(
            UserTable.buildUpdateTableQuery(), user.uuid.toString(),
            dailyRewardLastClaimed,
            dailyRewardLastClaimed
        )
    }
}