package me.senseiju.sennetmc.upgrades.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.upgrades.openUpgradeGui
import org.bukkit.entity.Player

@Command("upgrades")
class UpgradesCommand : CommandBase() {

    @Default
    fun default(sender: Player) {
        openUpgradeGui(sender)
    }
}