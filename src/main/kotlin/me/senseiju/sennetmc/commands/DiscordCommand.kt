package me.senseiju.sennetmc.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.extensions.sendConfigMessage
import org.bukkit.entity.Player

@Command("Discord")
class DiscordCommand : CommandBase() {

    @Default
    fun onCommand(player: Player) {
        player.sendConfigMessage("DISCORD-LINK")
    }
}