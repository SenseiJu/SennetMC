package me.senseiju.sennetmc.voting

import com.vexsoftware.votifier.model.VotifierEvent
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.dispatchCommands
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sentils.functions.getOnlinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class VotifierListener(private val plugin: SennetMC) : Listener {
    private val config = plugin.configFile

    @EventHandler
    private fun onVote(e: VotifierEvent) {
        val vote = e.vote
        val player = getOnlinePlayer(vote.username) ?: return

        player.sendConfigMessage(
            "VOTE-RECEIVED",
            PlaceholderSet("{serviceName}", vote.serviceName)
        )

        plugin.server.dispatchCommands(
            config.getStringList("vote-rewards"),
            PlaceholderSet("{player}", player.name)
        )
    }
}