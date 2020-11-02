package me.senseiju.commscraft.collectables.guis

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
import java.util.*


class CollectablesGui {
    companion object {
        fun showCollectables(plugin: CommsCraft, player: Player, uuid: UUID = player.uniqueId) {
            defaultScope.launch {
                val config = plugin.collectablesManager.collectablesFile.config

                val gui = defaultPaginatedGuiTemplate(6, 45, "&c&lCollectables")

                val set = plugin.database.asyncQuery("SELECT `collectable_id` FROM `collectables` WHERE `uuid`=?;",
                        uuid.toString())
                while (set.next()) {
                    val collectableSection = config.getConfigurationSection(set.getString("collectable_id")) ?: continue

                    gui.addItem(createCollectableGuiItem(collectableSection))
                }

                plugin.server.scheduler.runTask(plugin, Runnable { gui.open(player) })
            }
        }

        fun showCollectablesList(plugin: CommsCraft, player: Player) {
            defaultScope.launch {
                val config = plugin.collectablesManager.collectablesFile.config

                val gui = defaultPaginatedGuiTemplate(6, 45, "&c&lCollectables")

                for (collectableId in config.getKeys(false)) {
                    val collectableSection = config.getConfigurationSection(collectableId) ?: continue

                    gui.addItem(createCollectableGuiItem(collectableSection, true))
                }

                plugin.server.scheduler.runTask(plugin, Runnable { gui.open(player) })
            }
        }

        private fun createCollectableGuiItem(collectableSection: ConfigurationSection, includeId: Boolean = false) : GuiItem {
            val material = Material.matchMaterial(collectableSection.getString("material", "BEDROCK")!!)!!
            val name = collectableSection.getString("name", "NAME NOT FOUND")!!
            val rarity = Rarity.valueOf(collectableSection.getString("rarity", "COMMON")!!)
            val description = collectableSection.getStringList("description")

            val lore = ArrayList<String>()
            if (includeId) lore.add("&bID: &d${collectableSection.name}")
            lore.add("")
            lore.add("&bRarity: $rarity")
            lore.add("")
            lore.addAll(description)

            return ItemBuilder.from(material)
                    .setName(name.color())
                    .setLore(lore.color())
                    .asGuiItem()
        }
    }
}