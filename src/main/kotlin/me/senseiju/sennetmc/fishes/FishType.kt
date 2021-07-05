package me.senseiju.sennetmc.fishes

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.probabilityChance
import me.senseiju.sentils.storage.ConfigFile
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

enum class FishType {
    SMALL,
    MEDIUM,
    LARGE,
    OMEGA;

    companion object {
        val dataFile = ConfigFile(
            JavaPlugin.getPlugin(SennetMC::class.java),
            "fishes.yml",
            true
        )

        fun selectRandomType(increasedProbability: Double = 0.0): FishType {
            return probabilityChance(values().associate { it to (it.probability() + increasedProbability) })
        }
    }

    fun capacity(): Int {
        return dataFile.getInt("${this}.capacity", 1)
    }

    fun probability(): Double {
        return dataFile.getDouble("${this}.probability", 1.0)
    }

    private fun lowSellPrice(): Double {
        return dataFile.getDouble("${this}.low-sell-price", 1.0)
    }

    private fun maxSellPrice(): Double {
        return dataFile.getDouble("${this}.max-sell-price", 2.0)
    }

    fun selectRandomSellPrice(): Double {
        return Random.nextDouble(this.lowSellPrice(), this.maxSellPrice())
    }
}