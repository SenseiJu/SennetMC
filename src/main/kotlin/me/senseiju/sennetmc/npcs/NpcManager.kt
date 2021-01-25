package me.senseiju.sennetmc.npcs

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.ParameterHandler
import me.mattstudios.mf.base.components.ParameterResolver
import me.mattstudios.mf.base.components.TypeResult
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.commands.RemoveNpcCommand
import me.senseiju.sennetmc.npcs.commands.SpawnNpcCommand
import me.senseiju.sennetmc.npcs.listeners.NpcClickListener
import me.senseiju.sennetmc.npcs.types.BaseNpc
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.npcs.types.NpcType.*
import me.senseiju.sennetmc.npcs.types.designer.Designer
import me.senseiju.sennetmc.npcs.types.fishmonger.Fishmonger
import me.senseiju.sennetmc.npcs.types.looter.Looter
import me.senseiju.sennetmc.npcs.types.merchant.Merchant
import me.senseiju.sennetmc.npcs.types.sailor.Sailor

class NpcManager(private val plugin: SennetMC) : BaseManager {
    val npcMap = HashMap<NpcType, BaseNpc>()

    init {
        npcMap[FISHMONGER] = Fishmonger()
        npcMap[MERCHANT] = Merchant(plugin)
        npcMap[SAILOR] = Sailor()
        npcMap[LOOTER] = Looter()
        npcMap[DESIGNER] = Designer()

        registerEvents()
        registerCommandParameters(plugin.commandManager.parameterHandler)
        registerCommands(plugin.commandManager)
    }

    override fun registerCommands(cm: CommandManager) {
        registerCommandParameters(cm.parameterHandler)

        cm.register(SpawnNpcCommand(plugin, this))
        cm.register(RemoveNpcCommand())
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

    override fun registerEvents() {
        NpcClickListener(plugin, this)
    }

    override fun reload() {
    }
}