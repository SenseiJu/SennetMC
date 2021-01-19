package me.senseiju.commscraft.npcs.types.designer

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.defaultGuiTemplate
import me.senseiju.commscraft.extensions.defaultPaginatedGuiTemplate
import me.senseiju.commscraft.models.ModelType
import me.senseiju.commscraft.npcs.types.NpcType
import me.senseiju.commscraft.users.User
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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

        gui.setItem(2, 5, ItemBuilder.from(Material.LEATHER_HELMET)
                .asGuiItem { showModelTypeGui(player, user, ModelType.HELMET) })

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun showModelTypeGui(player: Player, user: User, modelType: ModelType) {
    defaultScope.launch {
        val gui = defaultPaginatedGuiTemplate(6, 45, NPC_TYPE.npcName)

        val models = modelsManager.models[modelType] ?: return@launch
        val userModels = user.models.getOrDefault(modelType, ArrayList())
        userModels.forEach {
            gui.addItem(GuiItem(models.getOrDefault(it, ItemStack(Material.AIR))))
        }

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}