package me.senseiju.sennetmc.crates.commands

import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_CRATES_GIVE
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.crates.CratesManager
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.utils.ObjectSet
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command("Crates")
class CratesCommand(private val plugin: SennetMC, private val cratesManager: CratesManager) : CommandBase() {

    @Default
    @Permission(PERMISSION_CRATES_GIVE)
    fun onCommand(sender: CommandSender) {
        sender.sendMessage("Missing args")
    }

    @SubCommand("give")
    @Permission(PERMISSION_CRATES_GIVE)
    fun onGiveSubCommand(sender: CommandSender, @Completion("#players") player: Player?,
                         @Completion("#crateId") crateId: String, @Optional amount: Int?) {
        if (player == null) {
            sender.sendConfigMessage("CANNOT-FIND-TARGET")
            return
        }

        if (amount != null && amount <= 0) {
            sender.sendConfigMessage("CRATES-INVALID-AMOUNT")
            return
        }

        if (!cratesManager.cratesMap.containsKey(crateId)) {
            sender.sendConfigMessage("CRATES-CANNOT-FIND-CRATE")
            return
        }

        if (amount != null) cratesManager.cratesMap[crateId]?.giveCrate(player, amount)
        else cratesManager.cratesMap[crateId]?.giveCrate(player)

        sender.sendConfigMessage("CRATES-GIVE-SUCCESS", ObjectSet("{player}", player.name))
    }
}