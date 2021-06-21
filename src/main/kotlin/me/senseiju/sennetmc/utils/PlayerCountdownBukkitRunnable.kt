package me.senseiju.sennetmc.utils

import java.util.*

abstract class PlayerCountdownBukkitRunnable : CountdownBukkitRunnable() {
    abstract val uuid: UUID
}