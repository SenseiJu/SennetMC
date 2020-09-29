package me.senseiju.commscraft.extensions

import org.bukkit.entity.Player

fun Player.message(s: String) {
    player?.sendMessage(s.color())
}