package me.senseiju.commscraft.npcs.types

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.extensions.color
import net.citizensnpcs.api.npc.NPC
import org.bukkit.plugin.java.JavaPlugin

enum class NpcType(path: String, npcName: String) {
    FISHMONGER("npc/fishmonger.yml", "&3&lFishmonger Freddy"),
    MERCHANT("npc/merchant.yml", "&6&lMerchant Manny");

    val dataFile = DataFile(JavaPlugin.getPlugin(CommsCraft::class.java), path, true)
    val npcName = npcName.color()

    companion object {
        fun isNpc(npc: NPC): Boolean {
            values().forEach {
                if (npc.name == it.npcName) return true
            }
            return false
        }
    }
}