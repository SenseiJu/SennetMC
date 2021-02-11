package me.senseiju.sennetmc.npcs.types

import me.senseiju.sennetmc.extensions.color
import net.citizensnpcs.api.npc.NPC

enum class NpcType(npcName: String) {
    FISHMONGER("&3&lFishmonger Freddy"),
    MERCHANT("&6&lMerchant Manny"),
    SAILOR("&b&lSailor Sally"),
    LOOTER("&d&lLooter Lincoln"),
    DESIGNER("&e&lDesigner Darren"),
    CAPTAIN("#3a3ae8&lCaptain Crunch"),
    CHEF("#424242&lChef Ramsay");

    val npcName = npcName.color()

    companion object {
        fun isNpc(npc: NPC): Boolean {
            if (!npc.data().has("npc-type")) return false

            return values().contains(valueOf(npc.data().get("npc-type")))
        }
    }
}