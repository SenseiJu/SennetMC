package me.senseiju.sennetmc.npcs.types.designer

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.extensions.color
import me.senseiju.sennetmc.extensions.defaultGuiTemplate
import me.senseiju.sennetmc.extensions.defaultPaginatedGuiTemplate
import me.senseiju.sennetmc.models.Model
import me.senseiju.sennetmc.models.ModelType
import me.senseiju.sennetmc.models.isPassengerBackpackModel
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.defaultScope
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val npcType = NpcType.DESIGNER
private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val scheduler = plugin.server.scheduler
private val modelsManager = plugin.modelsManager

fun showDesignerGui(player: Player) {
    defaultScope.launch {
        val user = plugin.userManager.userMap[player.uniqueId] ?: return@launch
        val gui = defaultGuiTemplate(3, npcType.npcName)

        gui.setItem(2, 3, ItemBuilder.from(Material.LEATHER_HELMET)
                .setName("&b&lHelmets".color())
                .asGuiItem { showModelTypeGui(player, user, ModelType.HELMET) })

        gui.setItem(2, 5, ItemBuilder.from(Material.LEATHER_CHESTPLATE)
                .setName("&b&lBackpacks".color())
                .asGuiItem { showModelTypeGui(player, user, ModelType.BACKPACK) })

        gui.setItem(2, 7, ItemBuilder.from(Material.WOODEN_SWORD)
                .setName("&b&lSleeves".color())
                .asGuiItem { showModelTypeGui(player, user, ModelType.SLEEVE) })

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun showModelTypeGui(player: Player, user: User, modelType: ModelType) {
    defaultScope.launch {
        val gui = defaultPaginatedGuiTemplate(6, 45, npcType.npcName)

        val models = modelsManager.models.computeIfAbsent(modelType) { HashMap() }
        val userModels = user.models.computeIfAbsent(modelType) { ArrayList() }
        userModels.forEach {
            if (models.containsKey(it)) {
                val model = models.getValue(it)
                gui.addItem(ItemBuilder.from(model.itemStack.clone())
                        .setLore("", "&7Click to equip".color())
                        .asGuiItem {
                            when (modelType) {
                                ModelType.HELMET -> equipHelmetModel(player, user, model)
                                ModelType.BACKPACK -> equipBackpackModel(player, user, model)
                                ModelType.SLEEVE -> equipSleeveModel(player, user, model)
                            }
                        })
            }
        }

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun equipBackpackModel(player: Player, user: User, model: Model) {
    user.activeModels[model.modelType] = model.modelData

    player.passengers.forEach {
        if (isPassengerBackpackModel(it)) {
            it as ArmorStand
            it.equipment?.helmet = model.itemStack
            player.playSound(player.location, Sound.BLOCK_CHAIN_PLACE, 1.0f, 1.0f)
            return
        }
    }
}

private fun equipHelmetModel(player: Player, user: User, model: Model) {
    user.activeModels[model.modelType] = model.modelData

    player.inventory.helmet = model.itemStack
    player.playSound(player.location, Sound.BLOCK_CHAIN_PLACE, 1.0f, 1.0f)
}

private fun equipSleeveModel(player: Player, user: User, model: Model) {
    user.activeModels[model.modelType] = model.modelData

    player.inventory.setItemInOffHand(model.itemStack)
    player.playSound(player.location, Sound.BLOCK_CHAIN_PLACE, 1.0f, 1.0f)
}