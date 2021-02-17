package me.senseiju.sennetmc.npcs.types.designer

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.models.*
import me.senseiju.sennetmc.models.listeners.playerPassengers
import me.senseiju.sennetmc.models.packetwrappers.updateModelArmorStand
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.defaultScope
import me.senseiju.sennetmc.utils.extensions.color
import me.senseiju.sennetmc.utils.extensions.defaultGuiTemplate
import me.senseiju.sennetmc.utils.extensions.defaultPaginatedGuiTemplate
import me.senseiju.sennetmc.utils.extensions.setCustomModelData
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

private val npcType = NpcType.DESIGNER
private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val scheduler = plugin.server.scheduler
private val modelsManager = plugin.modelsManager

fun showDesignerGui(player: Player) {
    defaultScope.launch {
        val user = plugin.userManager.userMap[player.uniqueId] ?: return@launch
        val gui = defaultGuiTemplate(3, npcType.npcName)

        gui.setItem(2, 2, createModelGuiItem(player, user, ModelType.HAT, "&b&lHats"))
        gui.setItem(2, 4, createModelGuiItem(player, user, ModelType.BACKPACK, "&b&lBackpacks"))
        gui.setItem(2, 6, createModelGuiItem(player, user, ModelType.SLEEVE, "&b&lSleeves"))
        gui.setItem(2, 8, createModelGuiItem(player, user, ModelType.FISHING_ROD, "&b&lFishing rods"))

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun showModelTypeGui(player: Player, user: User, modelType: ModelType) {
    defaultScope.launch {
        val gui = defaultPaginatedGuiTemplate(6, 45, npcType.npcName)

        gui.setCloseGuiAction { showDesignerGui(player) }

        gui.setItem(6, 9, ItemBuilder.from(Material.BARRIER)
            .setName("&c&lClear current model".color())
            .asGuiItem { clearModelSelection(player, user, modelType) })

        val models = modelsManager.models.computeIfAbsent(modelType) { HashMap() }
        val userModels = user.models.computeIfAbsent(modelType) { ArrayList() }
        userModels.forEach {
            if (models.containsKey(it)) {
                val model = models.getValue(it)
                gui.addItem(ItemBuilder.from(model.itemStack.clone())
                        .setLore("&7Model data: $it".color(), "", "&7Click to select".color())
                        .asGuiItem {
                            when (modelType) {
                                ModelType.HAT -> equipHelmetModel(player, user, model)
                                ModelType.BACKPACK -> equipBackpackModel(player, user, model)
                                ModelType.SLEEVE -> equipSleeveModel(player, user, model)
                                ModelType.FISHING_ROD -> equipFishingRodModel(player, user, model)
                            }
                        })
            }
        }

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun createModelGuiItem(player: Player, user: User, modelType: ModelType, name: String) : GuiItem {
    val activeModel = modelsManager.models[modelType]?.get(user.activeModels[modelType])
    return ItemBuilder.from(activeModel?.itemStack?.clone() ?: ItemStack(Material.BARRIER))
        .setName(name.color())
        .setLore("", "&7Click to select a different model".color())
        .asGuiItem { showModelTypeGui(player, user, modelType) }
}

private fun clearModelSelection(player: Player, user: User, modelType: ModelType) {
    user.activeModels[modelType] = -1

    when (modelType) {
        ModelType.HAT -> removeHatModel(player)
        ModelType.BACKPACK -> removeBackpackModel(player)
        ModelType.SLEEVE -> removeSleeveModel(player)
        ModelType.FISHING_ROD -> removeFishingRodModel(player)
    }
}

private fun equipFishingRodModel(player: Player, user: User, model: Model) {
    user.activeModels[model.modelType] = model.modelData

    @Suppress("UselessCallOnCollection")
    player.inventory.contents.filterNotNull().forEach {
        if (it.type == Material.FISHING_ROD) {
            it.setCustomModelData(model.modelData)
        }
    }
}

private fun equipBackpackModel(player: Player, user: User, model: Model) {
    user.activeModels[model.modelType] = model.modelData

    val modelArmorStand = playerPassengers[player.uniqueId] ?: return
    modelArmorStand.equipment?.helmet = model.itemStack

    updateModelArmorStand(player, modelArmorStand)
}

private fun equipHelmetModel(player: Player, user: User, model: Model) {
    user.activeModels[model.modelType] = model.modelData

    applyHatModel(player, user)

    player.playSound(player.location, Sound.BLOCK_CHAIN_PLACE, 1.0f, 1.0f)
}

private fun equipSleeveModel(player: Player, user: User, model: Model) {
    user.activeModels[model.modelType] = model.modelData

    applySleeveModel(player, user)

    player.playSound(player.location, Sound.BLOCK_CHAIN_PLACE, 1.0f, 1.0f)
}