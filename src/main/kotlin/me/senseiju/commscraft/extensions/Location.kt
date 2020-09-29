package me.senseiju.commscraft.extensions

import org.bukkit.Bukkit
import org.bukkit.Location

fun Location.serialize() : String {
    return "${world.name}:$x:$y:$z"
}

fun Location.deserialize(s: String) : Location {
    val location = s.split(":")
    return Location(Bukkit.getWorld(location[0]), location[1].toDouble(), location[2].toDouble(), location[3].toDouble())
}