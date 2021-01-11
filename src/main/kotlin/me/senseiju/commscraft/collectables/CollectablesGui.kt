package me.senseiju.commscraft.collectables

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.Rarity
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.extensions.defaultPaginatedGuiTemplate
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

private val plugin = JavaPlugin.getPlugin(CommsCraft::class.java)

fun showCollectablesGui(player: Player, uuid: UUID = player.uniqueId) {
    defaultScope.launch {
        val collectablesConfig = plugin.collectablesManager.collectablesFile.config

        val gui = defaultPaginatedGuiTemplate(6, 45, "&c&lCollectables")

        val set = plugin.database.asyncQuery("SELECT `collectable_id` FROM `collectables` WHERE `uuid`=?;",
                uuid.toString())
        while (set.next()) {
            val collectableSection = collectablesConfig.getConfigurationSection(set.getString("collectable_id")) ?: continue

            gui.addItem(createCollectableGuiItem(collectableSection))
        }

        plugin.server.scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

fun showCollectablesListGui(player: Player) {
    defaultScope.launch {
        val collectablesConfig = plugin.collectablesManager.collectablesFile.config

        val gui = defaultPaginatedGuiTemplate(6, 45, "&c&lCollectables")

        for (collectableId in collectablesConfig.getKeys(false)) {
            val collectableSection = collectablesConfig.getConfigurationSection(collectableId) ?: continue

            gui.addItem(createCollectableGuiItem(collectableSection))
        }

        plugin.server.scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun createCollectableGuiItem(collectableSection: ConfigurationSection) : GuiItem {
    val material = Material.matchMaterial(collectableSection.getString("material", "BEDROCK")!!)!!
    val glow = collectableSection.getBoolean("glow", false)!!
    val name = collectableSection.getString("name", "NAME NOT FOUND")!!
    val rarity = Rarity.valueOf(collectableSection.getString("rarity", "COMMON")!!)
    val description = collectableSection.getStringList("description")

    val lore = ArrayList<String>()
    lore.add("&bID: &d${collectableSection.name}")
    lore.add("")
    lore.add("&bRarity: $rarity")
    lore.add("")
    lore.addAll(description)

    return ItemBuilder.from(material)
            .setName(name.color())
            .setLore(lore.color())
            .glow(glow)
            .asGuiItem()
}