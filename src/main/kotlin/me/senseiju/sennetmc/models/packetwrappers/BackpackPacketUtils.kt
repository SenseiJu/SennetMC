package me.senseiju.sennetmc.models.packetwrappers

import com.comphenix.protocol.ProtocolLibrary
import kotlinx.coroutines.launch
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.defaultScope
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)

fun updateModelArmorStand(player: Player, modelArmorStand: ArmorStand) {
    modelArmorStand.teleport(player.location.add(0.0, 1.2, 0.0))

    ProtocolLibrary.getProtocolManager().updateEntity(modelArmorStand, plugin.server.onlinePlayers.toMutableList())

    mountModelArmorStand(player, modelArmorStand)
}

fun mountModelArmorStand(player: Player, modelArmorStand: ArmorStand) {
    defaultScope.launch {
        val mountPacket = WrapperPlayServerMount()
        mountPacket.entityID = player.entityId
        mountPacket.setPassengers(listOf(modelArmorStand))
        mountPacket.broadcastPacket()
    }
}

fun mountOtherModeArmorStand(player: Player, otherPlayerEntityId: Int, modelArmorStand: ArmorStand) {
    defaultScope.launch {
        val othersMountPacket = WrapperPlayServerMount()
        othersMountPacket.entityID = otherPlayerEntityId
        othersMountPacket.setPassengers(listOf(modelArmorStand))
        othersMountPacket.sendPacket(player)
    }
}

fun rotateModelArmorStand(player: Player, modelArmorStand: ArmorStand) {
    defaultScope.launch {
        val rotateArmorStandModelPacket = WrapperPlayServerEntityHeadRotation()
        rotateArmorStandModelPacket.entityID = modelArmorStand.entityId
        rotateArmorStandModelPacket.headYaw = (player.eyeLocation.yaw * 256.0f / 360.0f).toInt().toByte()
        rotateArmorStandModelPacket.broadcastPacket()
    }
}

fun destroyModelArmorStand(modelArmorStand: ArmorStand) {
    defaultScope.launch {
        val destroyArmorStandModelPacket = WrapperPlayServerEntityDestroy()
        destroyArmorStandModelPacket.setEntityIds(IntArray(1) { modelArmorStand.entityId })
        destroyArmorStandModelPacket.broadcastPacket()
    }
}