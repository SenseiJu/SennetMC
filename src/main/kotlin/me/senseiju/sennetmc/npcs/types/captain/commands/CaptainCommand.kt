package me.senseiju.sennetmc.npcs.types.captain.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_NPC_CAPTAIN_SET_SPAWN_POINT
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.extensions.serializeFull
import org.bukkit.entity.Player

@Command("Captain")
class CaptainCommand(plugin: SennetMC) : CommandBase() {
    private val warpsFile = plugin.warpsFile

    @SubCommand("setSpawnPoint")
    @Permission(PERMISSION_NPC_CAPTAIN_SET_SPAWN_POINT)
    fun onSetSpawnPointSubCommand(player: Player, warpName: String) {
        val warps = warpsFile.config.getConfigurationSection("captain-warps") ?: return

        if (!warps.contains(warpName)) {
            player.sendConfigMessage("CAPTAIN-INVALID-WARP-NAME")
            return
        }

        warps.set("$warpName.spawn-point", player.location.serializeFull())
        player.sendConfigMessage("CAPTAIN-SPAWN-POINT-SET")

        warpsFile.save()
    }

}