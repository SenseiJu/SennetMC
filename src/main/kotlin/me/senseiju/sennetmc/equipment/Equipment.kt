package me.senseiju.sennetmc.equipment

import me.senseiju.sentils.extensions.primitives.color
import org.bukkit.Material

enum class Equipment(eqName: String, val material: Material) {
    FISHING_NET("&e&lFishing net", Material.SNOWBALL);

    val eqName = eqName.color()
}