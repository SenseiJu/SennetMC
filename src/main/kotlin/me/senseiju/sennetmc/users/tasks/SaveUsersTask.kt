package me.senseiju.sennetmc.users.tasks

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sennetmc.users.UserManager
import org.bukkit.scheduler.BukkitRunnable

class SaveUsersTask(private val plugin: SennetMC, private val userManager: UserManager) : BukkitRunnable() {
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
        userManager.userMap.keys.toTypedArray().forEach { if (!plugin.server.getOfflinePlayer(it).isOnline) userManager.userMap.remove(it) }
    }
}