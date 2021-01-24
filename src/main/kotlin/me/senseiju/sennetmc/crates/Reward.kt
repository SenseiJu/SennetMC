package me.senseiju.sennetmc.crates

import me.senseiju.sennetmc.SennetMC
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)

class Reward(val name: String, val probability: Double, private val commands: List<String> = emptyList()) {

    fun executeCommands(player: Player) {
        for (command in commands) {
            plugin.server.dispatchCommand(plugin.server.consoleSender, applyPlaceholders(command, player))
        }
    }

    private fun applyPlaceholders(string: String, player: Player) : String {
        return string.replace("{player}", player.name)
    }
}