package me.senseiju.sennetmc.speedboat.commands

import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_SPEEDBOAT_USE
import me.senseiju.sennetmc.extensions.driver
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.extensions.string
import me.senseiju.sennetmc.speedboat.SpeedboatManager
import me.senseiju.sennetmc.utils.ObjectSet
import org.bukkit.entity.Boat
import org.bukkit.entity.Player

@Command("togglespeedboat")
@Alias("tsb")
class ToggleSpeedboatCommand(speedboatManager: SpeedboatManager) : CommandBase() {

    private var playerSpeedboatToggle = speedboatManager.playerSpeedboatToggle

    @Default
    @Permission(PERMISSION_SPEEDBOAT_USE)
    fun onCommand(player: Player) {
        if (player.vehicle !is Boat || (player.vehicle as Boat).driver?.uniqueId != player.uniqueId) {
            return
        }

        if (!playerSpeedboatToggle.containsKey(player.uniqueId)) {
            playerSpeedboatToggle[player.uniqueId] = false
        }

        playerSpeedboatToggle[player.uniqueId] = !playerSpeedboatToggle[player.uniqueId]!!

        player.sendConfigMessage("SPEEDBOAT-TOGGLE",
                ObjectSet("{toggle}", playerSpeedboatToggle[player.uniqueId]!!.string))
    }
}