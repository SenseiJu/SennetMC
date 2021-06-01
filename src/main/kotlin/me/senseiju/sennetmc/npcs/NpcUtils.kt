package me.senseiju.sennetmc.npcs

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.types.NpcType
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.trait.LookClose
import org.bukkit.entity.EntityType
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.pow

private val REGISTRY = CitizensAPI.getNPCRegistry()
private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val upgradesFile = plugin.upgradesManager.upgradesFile

fun createBasicNpc(npcType: NpcType): NPC {
    val npc = REGISTRY.createNPC(EntityType.PLAYER, npcType.npcName)
    npc.data().setPersistent("npc-type", npcType.name)
    npc.name = npcType.npcName
    npc.isProtected = true
    npc.getOrAddTrait(LookClose::class.java).lookClose(true)

    return npc
}

fun calculateNextUpgradeCost(
    baseCost: Double, currentUpgrades: Int,
    growthRate: Double = upgradesFile.config.getDouble("growth-rate", 1.3)
): Double {
    return "%.2f".format(baseCost * growthRate.pow(currentUpgrades)).toDouble()
}