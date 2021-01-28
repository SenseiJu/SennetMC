package me.senseiju.sennetmc.models.packetwrappers

import com.comphenix.protocol.ProtocolLibrary
import me.senseiju.sennetmc.SennetMC
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
    val mountPacket = WrapperPlayServerMount()
    mountPacket.entityID = player.entityId
    mountPacket.setPassengers(listOf(modelArmorStand))
    mountPacket.broadcastPacket()
}

fun mountOtherModeArmorStand(player: Player, otherPlayerEntityId: Int, modelArmorStand: ArmorStand) {
    val othersMountPacket = WrapperPlayServerMount()
    othersMountPacket.entityID = otherPlayerEntityId
    othersMountPacket.setPassengers(listOf(modelArmorStand))
    othersMountPacket.sendPacket(player)
}

fun rotateModelArmorStand(player: Player, modelArmorStand: ArmorStand) {
    val rotateArmorStandModelPacket = WrapperPlayServerEntityHeadRotation()
    rotateArmorStandModelPacket.entityID = modelArmorStand.entityId
    rotateArmorStandModelPacket.headYaw = (player.eyeLocation.yaw * 256.0f / 360.0f).toInt().toByte()
    rotateArmorStandModelPacket.broadcastPacket()
}

fun destroyModelArmorStand(modelArmorStand: ArmorStand) {
    val destroyArmorStandModelPacket = WrapperPlayServerEntityDestroy()
    destroyArmorStandModelPacket.setEntityIds(IntArray(1) { modelArmorStand.entityId })
    destroyArmorStandModelPacket.broadcastPacket()
}