package me.senseiju.commscraft.npcs.types

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.extensions.color
import net.citizensnpcs.api.npc.NPC
import org.bukkit.plugin.java.JavaPlugin

enum class NpcType(fileName: String, npcName: String) {
    FISHMONGER("fishmonger.yml", "&3&lFishmonger Freddy"),
    MERCHANT("merchant.yml", "&6&lMerchant Manny"),
    SAILOR("sailor.yml", "&b&lSailor Sally"),
    LOOTER("looter.yml", "&d&lLooter Lincoln");

    val dataFile = DataFile(JavaPlugin.getPlugin(CommsCraft::class.java), "npc/$fileName", true)
    val npcName = npcName.color()

    companion object {
        fun isNpc(npc: NPC): Boolean {
            if (!npc.data().has("npc-type")) return false

            return values().contains(npc.data().get("npc-type"))
        }
    }
}