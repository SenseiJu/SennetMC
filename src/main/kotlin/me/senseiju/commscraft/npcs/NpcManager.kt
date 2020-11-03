package me.senseiju.commscraft.npcs

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.ParameterHandler
import me.mattstudios.mf.base.components.ParameterResolver
import me.mattstudios.mf.base.components.TypeResult
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.npcs.commands.RemoveNpcCommand
import me.senseiju.commscraft.npcs.commands.SpawnNpcCommand
import me.senseiju.commscraft.npcs.types.NpcType
import me.senseiju.commscraft.npcs.types.NpcType.FISHMONGER
import me.senseiju.commscraft.npcs.types.NpcType.MERCHANT
import me.senseiju.commscraft.npcs.types.fishmonger.Fishmonger
import me.senseiju.commscraft.npcs.types.merchant.Merchant

class NpcManager(private val plugin: CommsCraft) : BaseManager {
    val npcMap = HashMap<NpcType, BaseNpc>()

    init {
        npcMap[FISHMONGER] = Fishmonger(plugin)
        npcMap[MERCHANT] = Merchant(plugin)

        registerCommandParameters(plugin.commandManager.parameterHandler)
        registerCommands(plugin.commandManager)
    }

    override fun registerCommands(cm: CommandManager) {
        registerCommandParameters(cm.parameterHandler)

        cm.register(SpawnNpcCommand(plugin, this))
        cm.register(RemoveNpcCommand(plugin, this))
    }

    private fun registerCommandParameters(ph: ParameterHandler) {
        ph.register(NpcType::class.java, ParameterResolver { argument ->
            try {
                return@ParameterResolver TypeResult(NpcType.valueOf(argument.toString().toUpperCase()), argument)
            } catch (ex: IllegalArgumentException) {
                return@ParameterResolver TypeResult(argument)
            }
        })
    }

    override fun registerEvents() {}
    override fun reload() {
        NpcType.values().forEach { it.dataFile.reload() }
    }
}