package me.senseiju.sennetmc.speedboat.listeners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import me.senseiju.sennetmc.PERMISSION_SPEEDBOAT_USE
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.extensions.driver
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.speedboat.SpeedboatManager
import me.senseiju.sennetmc.upgrades.Upgrade
import org.bukkit.entity.Boat
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.util.Vector
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.cos
import kotlin.math.sin

class SpeedboatListener(private val plugin: SennetMC, speedboatManager: SpeedboatManager) : Listener {
    private val protocolManager = ProtocolLibrary.getProtocolManager()

    private val playerSpeedboatToggle = speedboatManager.playerSpeedboatToggle
    private val playerSpeedboatCurrentVector = HashMap<UUID, Vector>()

    private val userManager = plugin.userManager
    private var speedIncrement = plugin.configFile.config.getDouble("speedboat-speed-upgrade-increment", 0.01)

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)

        registerSpeedboatMovePacketListener()
    }

    private fun registerSpeedboatMovePacketListener() {
        protocolManager.addPacketListener(object : PacketAdapter(
                plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.STEER_VEHICLE
        ) {
            override fun onPacketReceiving(e: PacketEvent) {
                if (e.player.vehicle !is Boat || !e.player.hasPermission(PERMISSION_SPEEDBOAT_USE)) {
                    return
                }

                val boatEntity = e.player.vehicle as Boat
                if (boatEntity.driver?.uniqueId != e.player.uniqueId) {
                    return
                }

                if (!playerSpeedboatToggle.containsKey(e.player.uniqueId)) {
                    playerSpeedboatToggle[e.player.uniqueId] = false
                }

                if (!playerSpeedboatToggle[e.player.uniqueId]!!) {
                    return
                }

                if (e.packet.float.read(1) > 0) {
                    val user = userManager.userMap[e.player.uniqueId] ?: return

                    val pitch: Double = (boatEntity.location.pitch + 90) * Math.PI / 180
                    val yaw: Double = (boatEntity.location.yaw + 90) * Math.PI / 180
                    val x = sin(pitch) * cos(yaw)
                    val y = sin(pitch) * sin(yaw)

                    val vector = Vector(x, 0.0, y)

                    boatEntity.velocity = vector.multiply(0.3 + (user.getUpgrade(Upgrade.SPEEDBOAT_SPEED) * speedIncrement))

                    playerSpeedboatCurrentVector[e.player.uniqueId] = vector

                    return
                }

                if (playerSpeedboatCurrentVector.getOrPut(e.player.uniqueId, { Vector() }).length() < 0.01) {
                    playerSpeedboatCurrentVector[e.player.uniqueId] = Vector()
                    boatEntity.velocity = Vector()
                    return
                }

                playerSpeedboatCurrentVector[e.player.uniqueId] = playerSpeedboatCurrentVector[e.player.uniqueId]!!.multiply(0.9)

                boatEntity.velocity = playerSpeedboatCurrentVector[e.player.uniqueId]!!
            }
        })
    }

    @EventHandler
    private fun onBoatExit(e: VehicleExitEvent) {
        if (e.exited.type != EntityType.PLAYER || e.vehicle.type != EntityType.BOAT) {
            return
        }

        if (e.vehicle.passengers.size == 1) {
            e.vehicle.remove()
        }
    }

    fun reload() {
        speedIncrement = plugin.configFile.config.getDouble("speedboat-speed-upgrade-increment", 0.01)
    }
}