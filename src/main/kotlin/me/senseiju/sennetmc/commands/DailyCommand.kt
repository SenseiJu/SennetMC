package me.senseiju.sennetmc.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.extensions.dispatchCommands
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.secondsToTimeFormat
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.time.Instant
import java.util.concurrent.TimeUnit

const val COOLDOWN_IN_SECONDS = (23 * 60 * 60) + (30 * 60)

@Command("Daily")
class DailyCommand(private val plugin: SennetMC) : CommandBase() {
    private val configFile = plugin.configFile
    private val users = plugin.userManager.userMap

    @Default
    fun onCommand(player: Player) {
        val user = users[player.uniqueId] ?: return
        val timePassedInSeconds = TimeUnit.SECONDS.convert(
            Instant.now().minusMillis(user.dailyRewardLastClaimed.toEpochMilli()).toEpochMilli(), TimeUnit.MILLISECONDS)

        if (timePassedInSeconds < COOLDOWN_IN_SECONDS) {
            player.sendConfigMessage("DAILY-NOT-READY",
                PlaceholderSet("{time}", secondsToTimeFormat(COOLDOWN_IN_SECONDS - timePassedInSeconds)))
            return
        }

        user.dailyRewardLastClaimed = Instant.now()

        giveDailyRewards(player)
    }

    private fun giveDailyRewards(player: Player) {
        val section = configFile.config.getConfigurationSection("daily-rewards") ?: return

        section.getKeys(false).forEach { key ->
            if (player.hasPermission("group.$key")) {
                plugin.server.dispatchCommands(section.getStringList(key), PlaceholderSet("{player}", player.name))
            }
        }

        player.sendConfigMessage("DAILY-CLAIMED")
        player.playSound(player.location, Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F)
    }
}