package me.senseiju.commscraft.models.commands

import kotlinx.coroutines.launch
import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.models.ModelType
import me.senseiju.commscraft.models.ModelsManager
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Command("Model")
class ModelCommand(private val plugin: CommsCraft, private val modelsManager: ModelsManager) : CommandBase() {

    @Default
    fun onCommand(player: Player, modelData: Int?) {
        if (modelData == null) return

        val item = ItemStack(Material.BEDROCK)
        val meta = item.itemMeta
        meta.setCustomModelData(modelData)
        item.itemMeta = meta

        player.inventory.addItem(item)
    }

    @SubCommand("a")
    fun onA(player: Player) {
        val entity = player.getTargetEntity(4) ?: return
        if (entity.type != EntityType.ARMOR_STAND) return

        val armorStand = entity as ArmorStand
        armorStand.equipment?.helmet = player.inventory.itemInMainHand
    }

    @SubCommand("set")
    fun onSetSubCommand(sender: CommandSender, targetPlayer: Player?, modelType: ModelType?, modelData: Int?) {
        defaultScope.launch {
            if (targetPlayer == null) {
                sender.sendConfigMessage("CANNOT-FIND-TARGET")
                return@launch
            }
        }
    }

    @SubCommand("remove")
    fun onRemoveSubCommand(sender: CommandSender, targetPlayer: Player?, modelType: ModelType?, modelData: Int?) {
        defaultScope.launch {
            if (targetPlayer == null) {
                sender.sendConfigMessage("CANNOT-FIND-TARGET")
                return@launch
            }
        }
    }

    @CompleteFor("set")
    fun completionForSetSubCommand(args: List<String>, sender: CommandSender) : List<String> {
        return when(args.size) {
            0 -> plugin.server.onlinePlayers.map { it.name }
            1 -> plugin.server.onlinePlayers.map { it.name }.filter { it.startsWith(args[0], true) }
            2 -> ModelType.values().map { it.name }.toList()
            3 -> {
                val player = plugin.server.getPlayer(args[0]) ?: return emptyList()
                val user = plugin.userManager.userMap[player.uniqueId] ?: return emptyList()
                val models = modelsManager.models[ModelType.valueOf(args[1])] ?: return emptyList()
                val userModels = user.models.computeIfAbsent(ModelType.valueOf(args[1])) { ArrayList() } ?: return emptyList()

                models.keys.minus(userModels).map { it.toString() }
            }
            else -> emptyList()
        }
    }

    @CompleteFor("remove")
    fun completionForRemoveSubCommand(args: List<String>, sender: CommandSender) : List<String> {
        return when(args.size) {
            0 -> plugin.server.onlinePlayers.map { it.name }
            1 -> plugin.server.onlinePlayers.map { it.name }.filter { it.startsWith(args[0], true) }
            2 -> ModelType.values().map { it.name }.toList()
            3 -> {
                val player = plugin.server.getPlayer(args[0]) ?: return emptyList()
                val user = plugin.userManager.userMap[player.uniqueId] ?: return emptyList()
                val userModels = user.models.computeIfAbsent(ModelType.valueOf(args[1])) { ArrayList() } ?: return emptyList()

                userModels.map { it.toString() }
            }
            else -> emptyList()
        }
    }
}