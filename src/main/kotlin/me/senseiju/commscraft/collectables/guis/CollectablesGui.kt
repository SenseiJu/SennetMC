package me.senseiju.commscraft.collectables.guis

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.BaseGui
import me.mattstudios.mfgui.gui.guis.PaginatedGui
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.Rarity
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*


class CollectablesGui {
    companion object {
        fun showCollectables(plugin: CommsCraft, player: Player, uuid: UUID = player.uniqueId) {
            defaultScope.launch {
                val config = plugin.collectablesFile.config

                val gui = PaginatedGui(6, 45, "&c&lCollectables".color())

                gui.setDefaultClickAction { it.isCancelled = true }

                gui.setItem(6, 4, ItemBuilder.from(Material.PAPER)
                    .setName("&aPrevious".color())
                    .asGuiItem { gui.previous() } )
                gui.setItem(6, 6, ItemBuilder.from(Material.PAPER)
                    .setName("&aNext".color())
                    .asGuiItem { gui.next() } )

                val set = plugin.database.asyncQuery("SELECT `collectable_id` FROM `collectables` WHERE `uuid`=?;",
                        uuid.toString())
                while (set.next()) {
                    val collectableSection = config.getConfigurationSection(set.getString("collectable_id")) ?: continue

                    val material = Material.matchMaterial(collectableSection.getString("material", "BEDROCK")!!)!!
                    val name = collectableSection.getString("name", "NAME NOT FOUND")!!
                    val rarity = collectableSection.getString("rarity", "COMMON")!!
                    val description = collectableSection.getStringList("description")

                    val lore = ArrayList<String>()
                    lore.add("")
                    lore.add("&bRarity: ${Rarity.valueOf(rarity)}")
                    lore.add("")
                    lore.addAll(description)

                    gui.addItem(ItemBuilder.from(material)
                        .setName(name.color())
                        .setLore(lore.color())
                        .asGuiItem())
                }

                plugin.server.scheduler.runTask(plugin, Runnable { gui.open(player) })
            }
        }
    }
}