package me.senseiju.commscraft.speedboat.events

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.PERMISSION_SPEEDBOAT
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.speedboat.SpeedboatManager
import me.senseiju.commscraft.utils.ObjectSet
import org.bukkit.entity.Boat
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.Vector
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.cos
import kotlin.math.sin


class SpeedboatListener(private val plugin: CommsCraft, private val speedboatManager: SpeedboatManager) : Listener {

    private val protocolManager = ProtocolLibrary.getProtocolManager()

    private var playerSpeedboatToggle = speedboatManager.playerSpeedboatToggle
    private val speedMultiplier = plugin.configFile.config.getDouble("speedboat-speed-multiplier", 1.02)

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
                if (e.player.vehicle !is Boat || !e.player.hasPermission(PERMISSION_SPEEDBOAT)) return

                if (!playerSpeedboatToggle.containsKey(e.player.uniqueId)) {
                    playerSpeedboatToggle[e.player.uniqueId] = false
                }

                if (!playerSpeedboatToggle[e.player.uniqueId]!!) return

                if (e.packet.float.read(1) > 0) {
                    val boatEntity = e.player.vehicle as Boat

                    val pitch: Double = (boatEntity.location.pitch + 90) * Math.PI / 180
                    val yaw: Double = (boatEntity.location.yaw + 90) * Math.PI / 180
                    val x = sin(pitch) * cos(yaw)
                    val y = sin(pitch) * sin(yaw)

                    val vector = Vector(x, 0.0, y)

                    boatEntity.velocity = vector.multiply(speedMultiplier)
                }
            }
        })
    }

    @EventHandler
    fun onSpeedboatToggle(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND
            || e.player.vehicle !is Boat
            || !e.player.hasPermission(PERMISSION_SPEEDBOAT)
        ) return
        
        if (!playerSpeedboatToggle.containsKey(e.player.uniqueId)) {
            playerSpeedboatToggle[e.player.uniqueId] = false
        }

        playerSpeedboatToggle[e.player.uniqueId] = !playerSpeedboatToggle[e.player.uniqueId]!!

        val toggle = if (playerSpeedboatToggle[e.player.uniqueId]!!) "&a&lTrue" else "&c&lFalse"
        e.player.sendConfigMessage("SPEEDBOAT-TOGGLE", ObjectSet("{toggle}", toggle))
    }
}