package me.senseiju.commscraft.npcs.types.designer

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.extensions.defaultGuiTemplate
import me.senseiju.commscraft.extensions.defaultPaginatedGuiTemplate
import me.senseiju.commscraft.models.Model
import me.senseiju.commscraft.models.ModelType
import me.senseiju.commscraft.models.isPassengerModelArmorStand
import me.senseiju.commscraft.npcs.types.NpcType
import me.senseiju.commscraft.users.User
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin

private val NPC_TYPE = NpcType.DESIGNER
private val NPC_CONFIG = NPC_TYPE.dataFile.config

private val plugin = JavaPlugin.getPlugin(CommsCraft::class.java)
private val scheduler = plugin.server.scheduler
private val modelsManager = plugin.modelsManager

fun showDesignerGui(player: Player) {
    defaultScope.launch {
        val user = plugin.userManager.userMap[player.uniqueId] ?: return@launch
        val gui = defaultGuiTemplate(3, NPC_TYPE.npcName)

        gui.setItem(2, 4, ItemBuilder.from(Material.LEATHER_HELMET)
                .setName("&b&lHelmets".color())
                .asGuiItem { showModelTypeGui(player, user, ModelType.HELMET) })

        gui.setItem(2, 6, ItemBuilder.from(Material.LEATHER_CHESTPLATE)
                .setName("&b&lBackpacks".color())
                .asGuiItem { showModelTypeGui(player, user, ModelType.BACKPACK) })

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun showModelTypeGui(player: Player, user: User, modelType: ModelType) {
    defaultScope.launch {
        val gui = defaultPaginatedGuiTemplate(6, 45, NPC_TYPE.npcName)

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
        if (isPassengerModelArmorStand(it, ModelType.BACKPACK)) {
            it as ArmorStand
            it.equipment?.helmet = model.itemStack
            player.playSound(player.location, Sound.BLOCK_CHAIN_PLACE, 1.0f, 1.0f)
            return
        }
    }
}

private fun equipHelmetModel(player: Player, user: User, model: Model) {
    user.activeModels[model.modelType] = model.modelData

    player.inventory.setItem(EquipmentSlot.HEAD, model.itemStack)
    player.playSound(player.location, Sound.BLOCK_CHAIN_PLACE, 1.0f, 1.0f)
}