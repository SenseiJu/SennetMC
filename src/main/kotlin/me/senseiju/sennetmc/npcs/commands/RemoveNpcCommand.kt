package me.senseiju.sennetmc.npcs.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_NPC_REMOVE
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sennetmc.npcs.types.NpcType
import net.citizensnpcs.api.CitizensAPI
import org.bukkit.entity.Player

@Command("RemoveNpc")
class RemoveNpcCommand : CommandBase() {

    @Default
    @Permission(PERMISSION_NPC_REMOVE)
    fun onCommand(player: Player) {
        val targetEntity = player.getTargetEntity(3, true)
        if (targetEntity == null) {
            player.sendConfigMessage("NPC-NO-TARGET-FOUND")
            return
        }

        val targetNpc = CitizensAPI.getNPCRegistry().getNPC(targetEntity)
        if (targetNpc == null) {
            player.sendConfigMessage("NPC-INVALID-TARGET")
            return
        }

        if (!NpcType.isNpc(targetNpc)) {
            player.sendConfigMessage("NPC-NOT-SENNETMC")
            return
        }

        targetNpc.destroy()
        player.sendConfigMessage("NPC-REMOVE-SUCCESS")
    }
}