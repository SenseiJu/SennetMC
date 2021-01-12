package me.senseiju.commscraft.collectables

import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.defaultPaginatedGuiTemplate
import me.senseiju.commscraft.users.User
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(CommsCraft::class.java)
private val collectablesManager = plugin.collectablesManager

fun showCollectablesGui(player: Player, user: User) {
    val gui = defaultPaginatedGuiTemplate(6, 45, "&c&lCollectables")

    user.collectables.forEach {
        val collectable = collectablesManager.collectables[it] ?: return@forEach

        gui.addItem(GuiItem(collectable.item))
    }

    gui.open(player)
}

fun showCollectablesListGui(player: Player) {
    val gui = defaultPaginatedGuiTemplate(6, 45, "&c&lCollectables")

    collectablesManager.collectables.values.forEach {
        gui.addItem(GuiItem(it.item))
    }

    gui.open(player)
}