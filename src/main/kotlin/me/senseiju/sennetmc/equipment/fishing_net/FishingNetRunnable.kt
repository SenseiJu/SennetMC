package me.senseiju.sennetmc.equipment.fishing_net

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.extensions.message
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.scheduler.BukkitRunnable

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
            thrower.message("Net didn't catch shit")
            cancel()
            return
        }

        if (net.isInWater) {
            thrower.message("Fish caught")
            thrower.world.playSound(thrower.location, Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1f, 0f)
            net.remove()
            cancel()
            return
        }
    }
}