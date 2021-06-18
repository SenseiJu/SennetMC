package me.senseiju.sennetmc.utils

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

abstract class CountdownBukkitRunnable : BukkitRunnable() {
    abstract var timeToComplete: Long
    abstract var finished: Boolean

    override fun run() {
        if (timeToComplete <= 0) {
            finished = true

            onComplete()
            cancel()

            return
        }

        timeToComplete--
    }

    fun start(plugin: JavaPlugin) {
        runTaskTimer(plugin, 20L, 20L)
    }

    abstract fun onComplete()
}