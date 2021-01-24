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
private val models = plugin.modelsManager.models
private val backpack = ModelType.BACKPACK

fun applyBackpackModel(player: Player, user: User) {
    val stand = player.location.world.spawnEntity(player.location, EntityType.ARMOR_STAND) as ArmorStand

    stand.isInvisible = true
    stand.isInvulnerable = true
    stand.isVisible = false
    stand.isMarker = true
    stand.setMetadata("model_type", FixedMetadataValue(plugin, backpack.name))
    stand.equipment?.helmet = models[backpack]?.get(user.activeModels[backpack])?.itemStack

    player.addPassenger(stand)
}

fun removeBackpackModel(player: Player) {
    player.passengers.forEach {
        if (isPassengerBackpackModel(it)) {
            it.remove()
            return
        }
    }
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