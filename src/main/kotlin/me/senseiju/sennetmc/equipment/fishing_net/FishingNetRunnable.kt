package me.senseiju.sennetmc.equipment.fishing_net

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.equipment.Equipment
import me.senseiju.sennetmc.fishes.FishType
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.message
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sentils.extensions.entity.playSound
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.random.Random

class FishingNetRunnable(
    plugin: SennetMC,
    private val thrower: Player,
    private val user: User,
    private val net: Projectile
) : BukkitRunnable() {

    init {
        runTaskTimer(plugin, 0, 1)
    }

    override fun run() {
        if (net.isDead) {
            thrower.sendConfigMessage(
                "EQUIPMENT-FAILED",
                PlaceholderSet("{equipment}", Equipment.FISHING_NET.eqName)
            )
            cancel()
            return
        }

        if (net.isInWater) {
            thrower.sendConfigMessage(
                "EQUIPMENT-SUCCESS",
                PlaceholderSet("{equipment}", Equipment.FISHING_NET.eqName)
            )
            thrower.playSound(Sound.ENTITY_FISHING_BOBBER_RETRIEVE)
            net.remove()
            givePlayerFish()
            cancel()
            return
        }
    }

    private fun givePlayerFish() {
        for (i in 0..Random.nextInt(3, 6)) {
            user.addToCurrentFish(FishType.selectRandomType())
        }
    }
}