package me.senseiju.commscraft.npcs.types

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.datastorage.DataFile
import org.bukkit.plugin.java.JavaPlugin

enum class NpcType(path: String) {
    FISHMONGER("npc/fishmonger.yml");

    val dataFile = DataFile(JavaPlugin.getPlugin(CommsCraft::class.java), path, true)
}