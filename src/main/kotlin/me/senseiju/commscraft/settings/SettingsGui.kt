package me.senseiju.commscraft.settings

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.Gui
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.extensions.defaultGuiTemplate
import me.senseiju.commscraft.extensions.string
import me.senseiju.commscraft.npcs.Callback
import me.senseiju.commscraft.users.User
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(CommsCraft::class.java)
private val scheduler = plugin.server.scheduler

fun showSettingsGui(player: Player, user: User) {
    defaultScope.launch {
        val gui = defaultGuiTemplate(6, "&c&lSettings")

        gui.filler.fillBorder(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE)
                .setName(" ")
                .asGuiItem())

        gui.addItem(toggleAutoCombiningCrates(gui, user))

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun toggleAutoCombiningCrates(gui: Gui, user: User) : GuiItem {
    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Toggle whether or not crates should")
    lore.add("&7automatically be combined when fished up")

    return toggleableSettingGuiItem(gui, user, Material.CHEST, "&b&lAuto combining crates", lore,
            Setting.TOGGLE_AUTO_CRATE_COMBINING) { toggleAutoCombiningCrates(gui, user) }
}

private fun toggleableSettingGuiItem(gui: Gui, user: User, material: Material, name: String, lore: List<String>,
                                     setting: Setting, callback: Callback) : GuiItem {
    val loreWithToggle = ArrayList<String>(lore)
    loreWithToggle.add("")
    loreWithToggle.add("&b&oCurrent setting: ${user.settings.getOrDefault(setting, setting.default).string}")

    return ItemBuilder.from(material)
            .setName(name.color())
            .setLore(loreWithToggle.color())
            .asGuiItem { e ->
                user.toggleSetting(setting)

                gui.updateItem(e.slot, callback.invoke())
            }
}