package me.senseiju.sennetmc.npcs.types

import me.senseiju.sentils.extensions.primitives.color
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.trait.LookClose
import org.bukkit.entity.EntityType

private val REGISTRY = CitizensAPI.getNPCRegistry()

enum class NpcType(npcName: String) {
    FISHMONGER("&3&lFishmonger Freddy"),
    MERCHANT("&6&lMerchant Manny"),
    SAILOR("&b&lSailor Sally"),
    LOOTER("&d&lLooter Lincoln"),
    CAPTAIN("&#3a3ae8&lCaptain Crunch"),
    CHEF("&#424242&lChef Ramsay"),
    SCRAPPER("&#9c7c52&lScrapper Alex");

    val npcName = npcName.color()

    companion object {
        fun isNpc(npc: NPC): Boolean {
            if (!npc.data().has("npc-type")) return false

            return values().contains(valueOf(npc.data().get("npc-type")))
        }
    }

    fun createBasicNpc(): NPC {
        val npc = REGISTRY.createNPC(EntityType.PLAYER, npcName)
        npc.data().setPersistent("npc-type", name)
        npc.name = npcName
        npc.isProtected = true
        npc.getOrAddTrait(LookClose::class.java).lookClose(true)

        return npc
    }
}