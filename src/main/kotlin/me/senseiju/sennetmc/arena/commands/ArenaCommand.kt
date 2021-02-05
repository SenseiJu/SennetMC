package me.senseiju.sennetmc.arena.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_ARENA_SET_LOCATION
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.extensions.serializeFull
import org.bukkit.entity.Player

@Command("Arena")
class ArenaCommand(plugin: SennetMC) : CommandBase() {

    private val configFile = plugin.configFile

    @SubCommand("SetLocation")
    @Permission(PERMISSION_ARENA_SET_LOCATION)
    fun onSetLocationSubCommand(sender: Player, locationName: String) {
        val arena = configFile.config.getConfigurationSection("arena") ?: return

        if (!arena.contains(locationName)) {
            sender.sendConfigMessage("ARENA-INVALID-LOCATION")
            return
        }

        arena.set("arena.$locationName", sender.location.serializeFull())
        sender.sendConfigMessage("ARENA-LOCATION-SET")

        configFile.save()
    }
}