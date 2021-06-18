package me.senseiju.sennetmc.utils.extensions

import org.bukkit.Sound
import org.bukkit.entity.Player

fun Player.playSound(sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f) {
    playSound(location, sound, volume, pitch)
}