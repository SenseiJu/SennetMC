package me.senseiju.sennetmc.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.extensions.dispatchCommands
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.utils.ObjectSet
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.time.Instant
import java.util.concurrent.TimeUnit

@Command("Daily")
class DailyCommand(private val plugin: SennetMC) : CommandBase() {
    private val configFile = plugin.configFile
    private val users = plugin.userManager.userMap

    @Default
    fun onCommand(player: Player) {
        val user = users[player.uniqueId] ?: return
        val timePassed = Instant.now().minusMillis(user.dailyRewardLastClaimed.toEpochMilli())

        if (TimeUnit.HOURS.convert(timePassed.toEpochMilli(), TimeUnit.MILLISECONDS) < 23) {
            player.sendConfigMessage("DAILY-NOT-READY")
            return
        }

        user.dailyRewardLastClaimed = Instant.now()

        giveDailyRewards(player)
    }

    private fun giveDailyRewards(player: Player) {
        val section = configFile.config.getConfigurationSection("daily-rewards") ?: return

        section.getKeys(false).forEach { key ->
            if (player.hasPermission("group.$key")) {
                plugin.server.dispatchCommands(section.getStringList(key), ObjectSet("{player}", player.name))
            }
        }

        player.sendConfigMessage("DAILY-CLAIMED")
        player.playSound(player.location, Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F)
    }
}