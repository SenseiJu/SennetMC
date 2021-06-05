package me.senseiju.sennetmc.equipment.fishing_net

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.equipment.Equipment
import me.senseiju.sennetmc.equipment.EquipmentManager
import me.senseiju.sennetmc.utils.CooldownManager
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.message
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

class FishingNetListener(private val plugin: SennetMC, equipmentManager: EquipmentManager) : Listener {
    private val users = plugin.userManager.userMap
    private val cooldowns = CooldownManager<UUID>(
        equipmentManager.equipmentFile.config.getInt("${Equipment.FISHING_NET}.cooldown", 300) * 1000L
    )

    @EventHandler
    private fun onNetThrow(e: PlayerLaunchProjectileEvent) {
        if (!isItemFishingNet(e.itemStack)) {
            return
        }

        if (e.player.isInWater) {
            e.player.message("Cannot throw in water")
            e.isCancelled = true
            return
        }

        val user = users[e.player.uniqueId] ?: return

        if (!cooldowns.isOnCooldown(e.player.uniqueId)) {
            cooldowns.start(e.player.uniqueId)

            FishingNetRunnable(plugin, e.player, user, e.projectile)
            return
        }

        e.player.sendConfigMessage(
            "EQUIPMENT-ON-COOLDOWN",
            PlaceholderSet("{equipment}", "${Equipment.FISHING_NET}"),
            PlaceholderSet("{cooldown}", cooldowns.timeRemaining(e.player.uniqueId))
        )
        e.isCancelled = true
    }
}