package me.senseiju.sennetmc.fishes

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.datastorage.DataFile
import me.senseiju.sennetmc.utils.probabilityChance
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

enum class FishType {
    SMALL,
    MEDIUM,
    LARGE,
    OMEGA;

    companion object {
        val dataFile = DataFile(JavaPlugin.getPlugin(SennetMC::class.java), "fishes.yml", true)

        fun selectRandomType(increasedProbability: Double = 0.0): FishType {
            return probabilityChance(values().associate { it to (it.probability() + increasedProbability) })
        }
    }

    fun capacity(): Int {
        return dataFile.config.getInt("${this}.capacity", 1)
    }

    fun probability(): Double {
        return dataFile.config.getDouble("${this}.probability", 1.0)
    }

    private fun lowSellPrice(): Double {
        return dataFile.config.getDouble("${this}.low-sell-price", 1.0)
    }

    private fun maxSellPrice(): Double {
        return dataFile.config.getDouble("${this}.max-sell-price", 2.0)
    }

    fun selectRandomSellPrice(): Double {
        return Random.nextDouble(this.lowSellPrice(), this.maxSellPrice())
    }
}