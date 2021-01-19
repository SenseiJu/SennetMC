package me.senseiju.commscraft.npcs

import me.senseiju.commscraft.npcs.types.NpcType
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import org.bukkit.entity.EntityType
import kotlin.math.pow

private val REGISTRY = CitizensAPI.getNPCRegistry()

fun createBasicNpc(npcType: NpcType) : NPC {
    val npc = REGISTRY.createNPC(EntityType.PLAYER, npcType.npcName)
    npc.data().setPersistent("npc-type", npcType.name)
    npc.name = npcType.npcName
    npc.isProtected = true

    return npc
}

fun calculateNextUpgradeCost(baseCost: Double, currentUpgrades: Int, growthRate: Double = 1.15) : Double {
    return "%.2f".format(baseCost * growthRate.pow(currentUpgrades)).toDouble()
}