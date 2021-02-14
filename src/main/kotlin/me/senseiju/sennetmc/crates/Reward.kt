package me.senseiju.sennetmc.crates

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.extensions.dispatchCommands
import me.senseiju.sennetmc.utils.PlaceholderSet
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)

class Reward(val name: String, val probability: Double, private val commands: List<String> = emptyList()) {

    fun executeCommands(player: Player) {
        plugin.server.dispatchCommands(commands, PlaceholderSet("{player}", player.name))
    }
}