package me.senseiju.sennetmc.speedboat.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.extensions.sendConfigMessage
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

@Command("boat")
class BoatCommand : CommandBase() {

    @Default
    fun onCommand(player: Player) {
        if (!player.isInWater) {
            player.sendConfigMessage("SPEEDBOAT-MUST-BE-IN-WATER")
            return
        } else if (player.isInsideVehicle) {
            return
        }

        spawnBoat(player)
    }

    private fun spawnBoat(player: Player) {
        var currentBlock = player.location.block
        while (true) {
            currentBlock = currentBlock.getRelative(BlockFace.UP)

            if (currentBlock.type.isAir) {
                break
            }
        }

        val boat = player.world.spawnEntity(currentBlock.location, EntityType.BOAT)
        boat.addPassenger(player)
    }

}