package me.senseiju.sennetmc.users.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Optional
import me.mattstudios.mf.base.CommandBase
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.users.UserManager
import me.senseiju.sennetmc.utils.extensions.defaultGuiTemplate
import me.senseiju.sentils.extensions.color
import me.senseiju.sentils.extensions.primitives.color
import org.bukkit.Material
import org.bukkit.entity.Player

@Command("stats")
class StatsCommand(private val userManager: UserManager) : CommandBase() {

    @Default
    fun onDefault(sender: Player) {
        userManager.userMap[sender.uniqueId]?.let {
            sender.openStatsGui(sender, it)
        }
    }
}

private fun Player.openStatsGui(player: Player, user: User) {
    val gui = defaultGuiTemplate(1, "&e&l${player.name}'s stats")

    gui.setItem(4, fishingStatsGuiItem(user))

    gui.open(this)
}

private fun fishingStatsGuiItem(user: User): GuiItem {
    val lore = arrayListOf("")
    lore.add("&f&lFish caught")
    user.fishCaught.forEach { (type, data) ->
        lore.add("&7$type: &e${data.total}")
    }
    lore.add("")
    lore.add("&7Total: &e${user.totalFishCaught}")

    return ItemBuilder.from(Material.FISHING_ROD)
        .setName("&3&lFishing".color())
        .setLore(lore.color())
        .asGuiItem()
}