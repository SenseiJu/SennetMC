package me.senseiju.commscraft.collectables.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Optional
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.collectables.guis.CollectablesGui
import org.bukkit.entity.Player

@Command("CollectablesList")
class CollectablesListCommand(private val plugin: CommsCraft) : CommandBase() {

    @Default
    fun onCommand(player: Player) {
        CollectablesGui.showCollectablesList(plugin, player)
    }
}