package me.senseiju.commscraft.npcs.types

import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.Location
import org.bukkit.event.Listener


interface BaseNpc : Listener {

    fun spawnNpc(location: Location)

    fun onNpcRightClick(e: NPCRightClickEvent)

}
