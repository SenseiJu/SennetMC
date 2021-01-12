package me.senseiju.commscraft.crates.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Optional
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.PERMISSION_CRATES_GIVE
import me.senseiju.commscraft.crates.CratesManager
import me.senseiju.commscraft.extensions.sendConfigMessage
import org.bukkit.command.CommandSender

@Command("CratesGive")
class CratesGiveCommand(private val plugin: CommsCraft, private val cratesManager: CratesManager) : CommandBase(){

    @Default
    @Permission(PERMISSION_CRATES_GIVE)
    fun onCommand(sender: CommandSender, targetName: String, crateId: String, @Optional amount: Int?) {
        if (amount != null && amount <= 0) {
            sender.sendConfigMessage("CRATES-INVALID-AMOUNT")
            return
        }

        val targetPlayer = plugin.server.getPlayer(targetName)
        if (targetPlayer == null) {
            sender.sendConfigMessage("CANNOT-FIND-TARGET")
            return
        }

        if (!cratesManager.cratesMap.containsKey(crateId)) {
            sender.sendConfigMessage("CRATES-CANNOT-FIND-CRATE")
            return
        }

        if (amount != null) cratesManager.cratesMap[crateId]?.giveCrate(targetPlayer, amount)
        else cratesManager.cratesMap[crateId]?.giveCrate(targetPlayer)
    }
}