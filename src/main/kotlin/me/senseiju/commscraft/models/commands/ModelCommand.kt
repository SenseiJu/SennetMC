package me.senseiju.commscraft.models.commands

import kotlinx.coroutines.launch
import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.*
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.models.ModelType
import me.senseiju.commscraft.models.ModelsManager
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Command("Model")
class ModelCommand(private val plugin: CommsCraft, private val modelsManager: ModelsManager) : CommandBase() {

    @Default
    @Permission(PERMISSION_MODELS_TEMP)
    fun onCommand(player: Player, modelData: Int?) {
        if (modelData == null) return

        val item = ItemStack(Material.BEDROCK)
        val meta = item.itemMeta
        meta.setCustomModelData(modelData)
        item.itemMeta = meta

        player.inventory.addItem(item)
    }

    @SubCommand("b")
    @Permission(PERMISSION_MODELS_TEMP)
    fun onBSubCommand(player: Player, modelData: Int?) {
        if (modelData == null) return

        val item = ItemStack(Material.OBSIDIAN)
        val meta = item.itemMeta
        meta.setCustomModelData(modelData)
        item.itemMeta = meta

        player.inventory.addItem(item)
    }

    @SubCommand("set")
    @Permission(PERMISSION_MODELS_SET)
    fun onSetSubCommand(sender: CommandSender, targetPlayer: Player?, modelType: ModelType?, modelData: Int?) {
        defaultScope.launch {
            if (targetPlayer == null) {
                sender.sendConfigMessage("CANNOT-FIND-TARGET")
                return@launch
            }

            modelsManager.addModel(targetPlayer.uniqueId, modelType, modelData, sender)
        }
    }

    @SubCommand("remove")
    @Permission(PERMISSION_MODELS_REMOVE)
    fun onRemoveSubCommand(sender: CommandSender, targetPlayer: Player?, modelType: ModelType?, modelData: Int?) {
        defaultScope.launch {
            if (targetPlayer == null) {
                sender.sendConfigMessage("CANNOT-FIND-TARGET")
                return@launch
            }

            modelsManager.removeModel(targetPlayer.uniqueId, modelType, modelData, sender)
        }
    }

    @CompleteFor("set")
    fun completionForSetSubCommand(args: List<String>, sender: CommandSender) : List<String> {
        return when(args.size) {
            0 -> plugin.server.onlinePlayers.map { it.name }
            1 -> plugin.server.onlinePlayers.map { it.name }.filter { it.startsWith(args[0], true) }
            2 -> ModelType.values().map { it.name }.toList()
            3 -> {
                if (!ModelType.values().map { it.name }.contains(args[1].toUpperCase())) return emptyList()

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
                if (!ModelType.values().map { it.name }.contains(args[1].toUpperCase())) return emptyList()

                val player = plugin.server.getPlayer(args[0]) ?: return emptyList()
                val user = plugin.userManager.userMap[player.uniqueId] ?: return emptyList()
                val userModels = user.models.computeIfAbsent(ModelType.valueOf(args[1])) { ArrayList() } ?: return emptyList()

                userModels.map { it.toString() }
            }
            else -> emptyList()
        }
    }
}