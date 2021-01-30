package me.senseiju.sennetmc.npcs.types.captain

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.createBasicNpc
import me.senseiju.sennetmc.npcs.types.BaseNpc
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.npcs.types.captain.commands.CaptainCommand
import me.senseiju.sennetmc.npcs.types.designer.showDesignerGui
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.trait.SkinTrait
import org.bukkit.Location

private const val SKIN_TEXTURE = ""
private const val SKIN_SIGNATURE = ""

private val NPC_TYPE = NpcType.CAPTAIN

class Captain(plugin: SennetMC) : BaseNpc {

    init {
        plugin.commandManager.register(CaptainCommand(plugin))
    }

    override fun spawnNpc(location: Location) {
        val npc = createBasicNpc(NPC_TYPE)

        npc.getOrAddTrait(SkinTrait::class.java).setSkinPersistent(NPC_TYPE.name, SKIN_SIGNATURE, SKIN_TEXTURE)

        npc.spawn(location)
    }

    override fun onNpcRightClick(e: NPCRightClickEvent) {
        showCaptainGui(e.clicker)
    }
}