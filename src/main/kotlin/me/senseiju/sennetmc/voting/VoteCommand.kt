package me.senseiju.sennetmc.voting

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import org.bukkit.entity.Player

@Command("vote")
class VoteCommand : CommandBase() {

    @Default
    fun onDefault(sender: Player) {
        sender.sendConfigMessage("VOTE-LINKS")
    }
}