package me.senseiju.commscraft.npcs.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.PERMISSION_NPC_SPAWN
import me.senseiju.commscraft.collectables.CollectablesManager
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.npcs.NpcManager
import me.senseiju.commscraft.npcs.types.NpcType
import me.senseiju.commscraft.utils.ObjectSet
import org.bukkit.entity.Player

@Command("SpawnNpc")
class SpawnNpcCommand(private val plugin: CommsCraft, private val npcManager: NpcManager) : CommandBase() {

    @Default
    @Permission(PERMISSION_NPC_SPAWN)
    fun onCommand(player: Player, @Completion("#enum") npcType: NpcType?) {
        if (npcType == null) {
            player.sendConfigMessage("NPC-CANNOT-FIND-NPC-TYPE")
            return
        }

        npcManager.npcMap[npcType]?.spawnNpc(player.location)
        player.sendConfigMessage("NPC-SPAWN-SUCCESS", ObjectSet("{npcType}", npcType.toString()))
    }
}