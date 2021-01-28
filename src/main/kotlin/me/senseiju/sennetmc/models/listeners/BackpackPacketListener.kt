package me.senseiju.sennetmc.models.listeners

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.models.*
import me.senseiju.sennetmc.models.packetwrappers.*
import me.senseiju.sennetmc.users.User
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Pose
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPoseChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import org.bukkit.metadata.FixedMetadataValue
import java.util.*
import kotlin.collections.HashMap

val playerPassengers = HashMap<UUID, ArmorStand>()

class BackpackPacketListener(private val plugin: SennetMC, modelsManager: ModelsManager) : Listener {
    private val users = plugin.userManager.userMap
    private val models = modelsManager.models

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

            val joinerModelArmorStand = createNewArmorStand(e.player, user)
            mountModelArmorStand(e.player, joinerModelArmorStand)

            plugin.server.onlinePlayers.forEach {
                val othersModelArmorStand = playerPassengers[it.uniqueId] ?: return@forEach
                mountOtherModeArmorStand(e.player, it.entityId, othersModelArmorStand)
            }

            playerPassengers[e.player.uniqueId] = joinerModelArmorStand
        }, 5L)
    }

    @EventHandler
    private fun onPlayerDeath(e: PlayerDeathEvent) {
        val modelArmorStand = playerPassengers[e.entity.uniqueId] ?: return
        destroyModelArmorStand(modelArmorStand)
    }

    @EventHandler
    private fun onPlayerTeleport(e: PlayerTeleportEvent) {
        if (e.isCancelled) {
            return
        }

        val modelArmorStand = playerPassengers[e.player.uniqueId] ?: return
        modelArmorStand.teleport(e.to)
        mountModelArmorStand(e.player, modelArmorStand)

        plugin.server.onlinePlayers.forEach {
            val othersModelArmorStand = playerPassengers[it.uniqueId] ?: return@forEach
            mountOtherModeArmorStand(e.player, it.entityId, othersModelArmorStand)
        }
    }

    @EventHandler
    private fun onPlayerSpawn(e: PlayerPostRespawnEvent) {
        val modelArmorStand = playerPassengers[e.player.uniqueId] ?: return
        updateModelArmorStand(e.player, modelArmorStand)

        plugin.server.onlinePlayers.forEach {
            val othersModelArmorStand = playerPassengers[it.uniqueId] ?: return@forEach
            mountOtherModeArmorStand(e.player, it.entityId, othersModelArmorStand)
        }
    }

    @EventHandler
    private fun onPlayerQuit(e: PlayerQuitEvent) {
        playerPassengers.remove(e.player.uniqueId)?.remove()
    }


    @EventHandler
    private fun onPlayerMove(e: PlayerMoveEvent) {
        val modelArmorStand = playerPassengers[e.player.uniqueId] ?: return

        if (e.from.chunk.x != e.to.chunk.x || e.from.chunk.z != e.to.chunk.z) {
            modelArmorStand.teleport(e.player)
        }

        mountModelArmorStand(e.player, modelArmorStand)
        rotateModelArmorStand(e.player, modelArmorStand)
    }

    @EventHandler
    private fun onPlayerChangePose(e: EntityPoseChangeEvent) {
        if (e.entityType != EntityType.PLAYER) return

        val player = e.entity as Player
        val modelArmorStand = playerPassengers[player.uniqueId] ?: return

        when (e.pose) {
            Pose.STANDING, Pose.SNEAKING -> updateModelArmorStand(player, modelArmorStand)
            else -> destroyModelArmorStand(modelArmorStand)
        }
    }


    private fun createNewArmorStand(player: Player, user: User) : ArmorStand {
        val stand = player.location.world.spawnEntity(player.location, EntityType.ARMOR_STAND) as ArmorStand

        stand.isInvisible = true
        stand.isInvulnerable = true
        stand.isVisible = false
        stand.isMarker = true
        stand.setMetadata("model_type", FixedMetadataValue(plugin, ModelType.BACKPACK.name))
        stand.equipment?.helmet = models[ModelType.BACKPACK]?.get(user.activeModels[ModelType.BACKPACK])?.itemStack

        return stand
    }
}