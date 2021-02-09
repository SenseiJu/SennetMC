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
import me.senseiju.sennetmc.npcs.types.captain.Captain
import me.senseiju.sennetmc.npcs.types.chef.Chef
import me.senseiju.sennetmc.npcs.types.designer.Designer
import me.senseiju.sennetmc.npcs.types.fishmonger.Fishmonger
import me.senseiju.sennetmc.npcs.types.looter.Looter
import me.senseiju.sennetmc.npcs.types.merchant.Merchant
import me.senseiju.sennetmc.npcs.types.sailor.Sailor

class NpcManager(private val plugin: SennetMC) : BaseManager {
    val npcMap = HashMap<NpcType, BaseNpc>()

    init {
        registerCommands(plugin.commandManager)
        registerEvents()

        npcMap[FISHMONGER] = Fishmonger(plugin)
        npcMap[MERCHANT] = Merchant(plugin)
        npcMap[SAILOR] = Sailor()
        npcMap[LOOTER] = Looter(plugin)
        npcMap[DESIGNER] = Designer(plugin)
        npcMap[CAPTAIN] = Captain(plugin)
        npcMap[CHEF] = Chef(plugin)
    }

    override fun registerCommands(cm: CommandManager) {
        cm.parameterHandler.register(NpcType::class.java, ParameterResolver { argument ->
            try {
                return@ParameterResolver TypeResult(NpcType.valueOf(argument.toString().toUpperCase()), argument)
            } catch (ex: IllegalArgumentException) {
                return@ParameterResolver TypeResult(argument)
            }
        })

        cm.register(SpawnNpcCommand(plugin, this))
        cm.register(RemoveNpcCommand())
    }

    override fun registerEvents() {
        NpcClickListener(plugin, this)
    }

    override fun reload() {
    }
}