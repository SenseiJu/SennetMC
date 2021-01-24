package me.senseiju.sennetmc.npcs.listeners

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.NpcManager
import me.senseiju.sennetmc.npcs.types.NpcType
import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class NpcClickListener(plugin: SennetMC, private val npcManager: NpcManager) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onNpcRightClick(e: NPCRightClickEvent) {
        if (!e.npc.data().has("npc-type")) return

        npcManager.npcMap[NpcType.valueOf(e.npc.data().get("npc-type"))]?.onNpcRightClick(e)
    }
}