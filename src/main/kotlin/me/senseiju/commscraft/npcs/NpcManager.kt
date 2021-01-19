package me.senseiju.commscraft.npcs

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.ParameterHandler
import me.mattstudios.mf.base.components.ParameterResolver
import me.mattstudios.mf.base.components.TypeResult
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.npcs.commands.RemoveNpcCommand
import me.senseiju.commscraft.npcs.commands.SpawnNpcCommand
import me.senseiju.commscraft.npcs.events.NpcClickEvent
import me.senseiju.commscraft.npcs.types.NpcType
import me.senseiju.commscraft.npcs.types.NpcType.*
import me.senseiju.commscraft.npcs.types.designer.Designer
import me.senseiju.commscraft.npcs.types.fishmonger.Fishmonger
import me.senseiju.commscraft.npcs.types.looter.Looter
import me.senseiju.commscraft.npcs.types.merchant.Merchant
import me.senseiju.commscraft.npcs.types.sailor.Sailor
import kotlin.math.pow

class NpcManager(private val plugin: CommsCraft) : BaseManager {
    val npcMap = HashMap<NpcType, BaseNpc>()

    init {
        npcMap[FISHMONGER] = Fishmonger(plugin)
        npcMap[MERCHANT] = Merchant()
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
        NpcClickEvent(plugin, this)
    }

    override fun reload() {
        NpcType.values().forEach { it.dataFile.reload() }
    }
}