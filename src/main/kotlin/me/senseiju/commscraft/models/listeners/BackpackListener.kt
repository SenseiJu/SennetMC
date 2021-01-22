package me.senseiju.commscraft.models.listeners

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.models.*
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Pose
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPoseChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.spigotmc.event.entity.EntityDismountEvent
import java.util.*
import kotlin.collections.HashMap

class BackpackListener(private val plugin: CommsCraft, private val modelsManager: ModelsManager) : Listener {

    private val userManager = plugin.userManager

    val map = HashMap<UUID, ArmorStand>()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        val user = userManager.userMap[e.player.uniqueId] ?: return

        applyModelArmorStandPassenger(e.player, user, ModelType.BACKPACK)
    }

    @EventHandler
    private fun onPlayerDeath(e: PlayerDeathEvent) {
        e.entity.passengers.forEach {
            if (isPassengerModelArmorStand(it, ModelType.BACKPACK)) {
                it.remove()
                return
            }
        }
    }

    @EventHandler
    private fun onPlayerSpawn(e: PlayerPostRespawnEvent) {
        val user = userManager.userMap[e.player.uniqueId] ?: return

        applyModelArmorStandPassenger(e.player, user, ModelType.BACKPACK)
    }

    @EventHandler
    private fun onPlayerQuit(e: PlayerQuitEvent) {
        removeModelArmorStandPassenger(e.player, ModelType.BACKPACK)
    }

    @EventHandler
    private fun onPlayerMove(e: PlayerMoveEvent) {
        e.player.passengers.forEach {
            if (isPassengerModelArmorStand(it, ModelType.BACKPACK)) {
                it.setRotation(e.player.eyeLocation.yaw, e.player.eyeLocation.pitch)
            }
        }
    }

    @EventHandler
    private fun onModelArmorStandDismount(e: EntityDismountEvent) {
        if (e.dismounted is Player && (e.dismounted.pose == Pose.STANDING || e.dismounted.pose == Pose.SNEAKING)
                && isPassengerModelArmorStand(e.entity, ModelType.BACKPACK)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    private fun onPlayerChangePose(e: EntityPoseChangeEvent) {
        if (e.entityType != EntityType.PLAYER) return

        val player = e.entity as Player
        when (e.pose) {
            Pose.STANDING, Pose.SNEAKING -> {
                player.passengers.forEach {
                    if (isPassengerModelArmorStand(it, ModelType.BACKPACK)) {
                        return
                    }
                }

                val user = userManager.userMap[player.uniqueId] ?: return

                applyModelArmorStandPassenger(player, user, ModelType.BACKPACK)
            }

            else -> removeModelArmorStandPassenger(player, ModelType.BACKPACK)
        }
    }
}