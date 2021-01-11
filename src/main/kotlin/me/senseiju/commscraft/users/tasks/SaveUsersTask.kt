package me.senseiju.commscraft.users.tasks

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.users.UserManager
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.HashSet

class SaveUsersTask(private val plugin: CommsCraft, private val userManager: UserManager) : BukkitRunnable() {
    private val interval = plugin.configFile.config.getLong("user-save-interval", 120) * 20

    init {
        this.runTaskTimerAsynchronously(plugin, interval, interval)
    }

    override fun run() {
        save()

        cleanupUsers()
    }

    override fun cancel() {
        super.cancel()

        save()
    }

    private fun save() {
        plugin.server.consoleSender.sendConfigMessage("USER-SAVING-STARTED")

        userManager.saveUsers()

        plugin.server.consoleSender.sendConfigMessage("USER-SAVING-FINISHED")
    }

    private fun cleanupUsers() {
        val onlineUUIDs = HashSet<UUID>()
        plugin.server.onlinePlayers.forEach { onlineUUIDs.add(it.uniqueId) }

        userManager.userMap.keys.forEach { if (!onlineUUIDs.contains(it)) userManager.userMap.remove(it) }
    }
}