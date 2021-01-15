package me.senseiju.commscraft.users

import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.users.listeners.PlayerJoinEvent
import me.senseiju.commscraft.users.listeners.PlayerPreLoginEvent
import me.senseiju.commscraft.users.tasks.SaveUsersTask
import java.util.*
import kotlin.collections.ArrayList
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
                Upgrade.mapFromSet(set))
    }

    fun saveUsers() {
        val q = "INSERT INTO `users`(`uuid`, `fish_capacity_upgrades`, `speedboat_speed_upgrades`, `treasure_finder_upgrades`, " +
                "`discovery_upgrades`, `crate_master_upgrades`, `negotiate_upgrades`, `player_speed_upgrades`) VALUES(?,?,?,?,?,?,?,?)" +
                "ON DUPLICATE KEY UPDATE `fish_capacity_upgrades`=?, `speedboat_speed_upgrades`=?, `treasure_finder_upgrades`=?," +
                "`discovery_upgrades`=?, `crate_master_upgrades`=?, `negotiate_upgrades`=?, `player_speed_upgrades`=?;"
        for ((uuid, user) in userMap) {
            val upgrades = ArrayList<Int>()
            upgrades.add(user.upgrades.getOrDefault(Upgrade.FISH_CAPACITY, 0))
            upgrades.add(user.upgrades.getOrDefault(Upgrade.SPEEDBOAT_SPEED, 0))
            upgrades.add(user.upgrades.getOrDefault(Upgrade.TREASURE_FINDER, 0))
            upgrades.add(user.upgrades.getOrDefault(Upgrade.DISCOVERY, 0))
            upgrades.add(user.upgrades.getOrDefault(Upgrade.CRATE_MASTER, 0))
            upgrades.add(user.upgrades.getOrDefault(Upgrade.NEGOTIATE, 0))
            upgrades.add(user.upgrades.getOrDefault(Upgrade.PLAYER_SPEED, 0))

            plugin.database.updateQuery(q, uuid.toString(), *upgrades.toTypedArray(), *upgrades.toTypedArray())

            plugin.fishManager.updateFishCaught(uuid, user.fishCaught)
            plugin.collectablesManager.updateCollectables(uuid, user.collectables)
        }
    }

    suspend fun createNewUser(uuid: UUID) {
        userMap[uuid] = User(uuid,
            plugin.collectablesManager.fetchCollectables(uuid))
    }
}