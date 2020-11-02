package me.senseiju.commscraft.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.PERMISSION_RELOAD
import me.senseiju.commscraft.extensions.sendConfigMessage
import org.bukkit.command.CommandSender

@Command("CommsCraft")
class ReloadCommand(private val plugin: CommsCraft) : CommandBase() {

    @SubCommand("Reload")
    @Permission(PERMISSION_RELOAD)
    fun onCommand(sender: CommandSender) {
        plugin.reload()

        sender.sendConfigMessage("RELOAD-COMPLETE")
    }
}