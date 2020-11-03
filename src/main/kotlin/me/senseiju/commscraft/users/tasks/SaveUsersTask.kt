package me.senseiju.commscraft.users.tasks

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.users.UserManager
import org.bukkit.scheduler.BukkitRunnable

class SaveUsersTask(private val plugin: CommsCraft, private val userManager: UserManager) : BukkitRunnable() {
    private val interval = plugin.configFile.config.getLong("user-save-interval", 120) * 20

    init {
        this.runTaskTimerAsynchronously(plugin, interval, interval)
    }

    override fun run() {
        save()
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
}