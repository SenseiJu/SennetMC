package me.senseiju.sennetmc.settings.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.settings.showSettingsGui
import org.bukkit.entity.Player

@Command("Settings")
class SettingsCommand(private val plugin: SennetMC) : CommandBase() {

    @Default
    fun onCommand(player: Player) {
        val user = plugin.userManager.userMap[player.uniqueId] ?: return

        showSettingsGui(player, user)
    }
}