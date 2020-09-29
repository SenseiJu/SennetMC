package me.senseiju.commscraft.extensions

import org.bukkit.ChatColor

fun String.color() : String {
    return ChatColor.translateAlternateColorCodes('&', this)
}