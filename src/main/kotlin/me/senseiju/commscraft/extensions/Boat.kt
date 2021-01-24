package me.senseiju.commscraft.extensions

import org.bukkit.entity.Boat
import org.bukkit.entity.Entity

val Boat.driver : Entity?
    get() = this.passengers.firstOrNull()
