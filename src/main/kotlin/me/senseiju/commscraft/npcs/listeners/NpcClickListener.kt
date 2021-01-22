package me.senseiju.commscraft.npcs.listeners

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.npcs.NpcManager
import me.senseiju.commscraft.npcs.types.NpcType
import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class NpcClickListener(plugin: CommsCraft, private val npcManager: NpcManager) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onNpcRightClick(e: NPCRightClickEvent) {
        if (!e.npc.data().has("npc-type")) return

        npcManager.npcMap[NpcType.valueOf(e.npc.data().get("npc-type"))]?.onNpcRightClick(e)
    }
}