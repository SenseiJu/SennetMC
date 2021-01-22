package me.senseiju.commscraft.models

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.users.User
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(CommsCraft::class.java)
private val modelsManager = plugin.modelsManager

fun applyModelArmorStandPassenger(player: Player, user: User, modelType: ModelType) {
    val stand = player.location.world.spawnEntity(player.location, EntityType.ARMOR_STAND) as ArmorStand

    stand.isInvisible = true
    stand.isInvulnerable = true
    stand.isVisible = false
    stand.isMarker = true
    stand.setMetadata("model_type", FixedMetadataValue(plugin, modelType.name))
    stand.equipment?.helmet = modelsManager.models[modelType]?.get(user.activeModels[modelType])?.itemStack

    player.addPassenger(stand)
}

fun removeModelArmorStandPassenger(player: Player, modelType: ModelType) {
    player.passengers.forEach {
        if (isPassengerModelArmorStand(it, modelType)) {
            it.remove()
            return
        }
    }
}

fun isPassengerModelArmorStand(entity: Entity, modelType: ModelType) : Boolean {
    if (entity is ArmorStand && entity.hasMetadata("model_type")) {
        entity.getMetadata("model_type").forEach {
            if (it.value() is String && it.asString() == modelType.name) {
                return true
            }
        }
    }
    return false
}