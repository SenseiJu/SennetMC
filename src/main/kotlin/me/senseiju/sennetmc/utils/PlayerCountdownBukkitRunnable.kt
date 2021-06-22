package me.senseiju.sennetmc.utils

import org.bukkit.Bukkit
import java.util.*

abstract class PlayerCountdownBukkitRunnable : CountdownBukkitRunnable() {
    abstract val uuid: UUID

    val player = run {
        return@run Bukkit.getPlayer(uuid)
    }
}