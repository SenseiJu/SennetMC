package me.senseiju.commscraft.users

import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.users.listeners.PlayerJoinEvent
import me.senseiju.commscraft.users.listeners.PlayerPreLoginEvent
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
        PlayerPreLoginEvent(plugin, this)
        PlayerJoinEvent(plugin, this)
    }

    override fun reload() {}

    suspend fun doesUserExist(uuid: UUID) : Boolean {
        val q = "SELECT * FROM `users` WHERE `uuid`=?;"

        val set = plugin.database.asyncQuery(q, uuid.toString())

        return set.next()
    }

    suspend fun fetchUser(uuid: UUID) {
        val q = "SELECT * FROM `users` WHERE `uuid`=?;"
        val set = plugin.database.asyncQuery(q, uuid.toString())

        if (!set.next()) return

        userMap[uuid] = User(uuid,
                plugin.collectablesManager.fetchCollectables(uuid),
                plugin.fishManager.fetchFishCaught(uuid),
                set.getInt("fish_capacity_upgrades"),
                set.getInt("speedboat_upgrades"))
    }

    fun saveUsers() {
        val q = "INSERT INTO `users`(`uuid`, `fish_capacity_upgrades`, `speedboat_upgrades`) VALUES(?,?,?)" +
                "ON DUPLICATE KEY UPDATE `fish_capacity_upgrades`=?, `speedboat_upgrades`=?;"
        for ((uuid, user) in userMap) {
            plugin.database.updateQuery(q,
                uuid.toString(), user.fishCapacityUpgrades, user.speedboatUpgrades,
                user.fishCapacityUpgrades, user.speedboatUpgrades)

            plugin.fishManager.updateFishCaught(uuid, user.fishCaught)
            plugin.collectablesManager.updateCollectables(uuid, user.collectables)
        }
    }

    fun createNewUser(uuid: UUID) { userMap[uuid] = User(uuid) }
}