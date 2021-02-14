package me.senseiju.sennetmc.models

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.extensions.setCustomModelData
import me.senseiju.sennetmc.models.listeners.playerPassengers
import me.senseiju.sennetmc.models.packetwrappers.updateModelArmorStand
import me.senseiju.sennetmc.users.User
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val models = plugin.modelsManager.models

private val backpack = ModelType.BACKPACK
private val hat = ModelType.HAT
private val sleeve = ModelType.SLEEVE

fun applyBackpackModelArmorStand(player: Player, user: User) {
    val stand = player.location.world.spawnEntity(player.location, EntityType.ARMOR_STAND) as ArmorStand

    stand.isInvisible = true
    stand.isInvulnerable = true
    stand.isVisible = false
    stand.isMarker = true
    stand.setMetadata("model_type", FixedMetadataValue(plugin, backpack.name))
    stand.equipment?.helmet = models[backpack]?.get(user.activeModels[backpack])?.itemStack

    player.addPassenger(stand)
}

fun removeBackpackModelArmorStand(player: Player) {
    player.passengers.forEach {
        if (isPassengerBackpackModel(it)) {
            it.remove()
            return
        }
    }
}

fun removeBackpackModel(player: Player) {
    val modelArmorStand = playerPassengers[player.uniqueId] ?: return
    modelArmorStand.equipment?.helmet = null

    updateModelArmorStand(player, modelArmorStand)
}

fun isPassengerBackpackModel(entity: Entity) : Boolean {
    if (entity is ArmorStand && entity.hasMetadata("model_type")) {
        entity.getMetadata("model_type").forEach {
            if (it.value() is String && it.asString() == backpack.name) {
                return true
            }
        }
    }
    return false
}

fun applyHatModel(player: Player, user: User) {
    player.inventory.helmet = models[hat]?.get(user.activeModels[hat])?.itemStack
}

fun removeHatModel(player: Player) {
    player.inventory.helmet = null
}

fun applySleeveModel(player: Player, user: User) {
    player.inventory.setItemInOffHand(models[sleeve]?.get(user.activeModels[sleeve])?.itemStack)
}

fun removeSleeveModel(player: Player) {
    player.inventory.setItemInOffHand(null)
}

fun removeFishingRodModel(player: Player) {
    @Suppress("UselessCallOnCollection")
    player.inventory.contents.filterNotNull().forEach {
        if (it.type == Material.FISHING_ROD) {
            it.setCustomModelData(0)
        }
    }
}
