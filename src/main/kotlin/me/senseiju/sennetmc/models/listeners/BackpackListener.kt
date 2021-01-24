package me.senseiju.sennetmc.models.listeners

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.models.*
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Pose
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityPoseChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.spigotmc.event.entity.EntityDismountEvent
import java.util.*
import kotlin.collections.HashMap

class BackpackListener(private val plugin: SennetMC) : Listener {

    private val users = plugin.userManager.userMap

    val map = HashMap<UUID, ArmorStand>()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        val user = users[e.player.uniqueId] ?: return

        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            if (!e.player.isOnline) {
                return@Runnable
            }

            applyBackpackModelArmorStand(e.player, user)
        }, 10L)
    }

    @EventHandler
    private fun onPlayerDeath(e: PlayerDeathEvent) {
        e.entity.passengers.forEach {
            if (isPassengerBackpackModel(it)) {
                it.remove()
                return
            }
        }
    }

    @EventHandler
    private fun onPlayerSpawn(e: PlayerPostRespawnEvent) {
        val user = users[e.player.uniqueId] ?: return

        applyBackpackModelArmorStand(e.player, user)
    }

    @EventHandler
    private fun onPlayerQuit(e: PlayerQuitEvent) {
        removeBackpackModelArmorStand(e.player)
    }

    @EventHandler
    private fun onPlayerMove(e: PlayerMoveEvent) {
        e.player.passengers.forEach {
            if (isPassengerBackpackModel(it)) {
                it.setRotation(e.player.eyeLocation.yaw, e.player.eyeLocation.pitch)
            }
        }
    }

    @EventHandler
    private fun onModelArmorStandDismount(e: EntityDismountEvent) {
        if (e.dismounted is Player && (e.dismounted.pose == Pose.STANDING || e.dismounted.pose == Pose.SNEAKING)
                && isPassengerBackpackModel(e.entity)) {
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
                    if (isPassengerBackpackModel(it)) {
                        return
                    }
                }

                val user = users[player.uniqueId] ?: return

                applyBackpackModelArmorStand(player, user)
            }

            else -> removeBackpackModelArmorStand(player)
        }
    }

    @EventHandler
    private fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND && e.action == Action.RIGHT_CLICK_BLOCK) {
            if (e.item != null && ModelType.isItemModel(e.item!!)) {
                e.isCancelled = true
            }
        }
    }
}