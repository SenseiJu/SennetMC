package me.senseiju.commscraft.npcs.types

import me.senseiju.commscraft.extensions.color
import net.citizensnpcs.api.npc.NPC

enum class NpcType(npcName: String) {
    FISHMONGER("&3&lFishmonger Freddy"),
    MERCHANT("&6&lMerchant Manny"),
    SAILOR("&b&lSailor Sally"),
    LOOTER("&d&lLooter Lincoln"),
    DESIGNER("&e&lDesigner Darren");

    val npcName = npcName.color()

    companion object {
        fun isNpc(npc: NPC): Boolean {
            if (!npc.data().has("npc-type")) return false

            return values().contains(valueOf(npc.data().get("npc-type")))
        }
    }
}