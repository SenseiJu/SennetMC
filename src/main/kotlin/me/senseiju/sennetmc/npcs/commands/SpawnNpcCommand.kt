package me.senseiju.sennetmc.npcs.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_NPC_SPAWN
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sennetmc.npcs.NpcManager
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.utils.PlaceholderSet
import org.bukkit.entity.Player

@Command("SpawnNpc")
class SpawnNpcCommand(private val plugin: SennetMC, private val npcManager: NpcManager) : CommandBase() {

    @Default
    @Permission(PERMISSION_NPC_SPAWN)
    fun onCommand(player: Player, @Completion("#enum") npcType: NpcType?) {
        if (npcType == null) {
            player.sendConfigMessage("NPC-CANNOT-FIND-NPC-TYPE")
            return
        }

        npcManager.npcMap[npcType]?.spawnNpc(player.location)
        player.sendConfigMessage("NPC-SPAWN-SUCCESS", PlaceholderSet("{npcType}", npcType.toString()))
    }
}