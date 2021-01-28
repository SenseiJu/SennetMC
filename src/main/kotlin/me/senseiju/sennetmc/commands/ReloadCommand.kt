package me.senseiju.sennetmc.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_RELOAD
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.extensions.sendConfigMessage
import org.bukkit.command.CommandSender

@Command("SennetMC")
class ReloadCommand(private val plugin: SennetMC) : CommandBase() {

    @SubCommand("Reload")
    @Permission(PERMISSION_RELOAD)
    fun onCommand(sender: CommandSender) {
        plugin.reload()

        sender.sendConfigMessage("RELOAD-COMPLETE")
    }
}