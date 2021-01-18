package me.senseiju.commscraft.npcs

import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.Location


interface BaseNpc {

    fun spawnNpc(location: Location)

    fun onNpcRightClick(e: NPCRightClickEvent)
}
