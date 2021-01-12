package me.senseiju.commscraft.crates.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.crates.CratesManager
import org.bukkit.entity.Player

@Command("CombineCrates")
class CombineCratesCommand(private val cratesManager: CratesManager) : CommandBase() {

    @Default
    fun onCommand(player: Player) {
        cratesManager.combineCrates(player)
    }
}