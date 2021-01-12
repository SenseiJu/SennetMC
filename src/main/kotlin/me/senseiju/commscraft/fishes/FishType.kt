package me.senseiju.commscraft.fishes

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.utils.probabilityChance
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

enum class FishType {
    SMALL,
    MEDIUM,
    LARGE,
    OMEGA;

    companion object {
        val dataFile = DataFile(JavaPlugin.getPlugin(CommsCraft::class.java), "fishes.yml", true)

        fun selectRandomType() : FishType {
            var probabilityRange = 1
            for (type in values()) {
                probabilityRange += type.probability()
            }
            return probabilityChance(values().map { it to it.probability() }.toMap())
        }
    }

    fun capacity() : Int {
        return dataFile.config.getInt("${this}.capacity", 1)
    }

    fun probability() : Int {
        return dataFile.config.getInt("${this}.probability", 1)
    }

    fun lowSellPrice() : Double {
        return dataFile.config.getDouble("${this}.low-sell-price", 1.0)
    }

    fun maxSellPrice() : Double {
        return dataFile.config.getDouble("${this}.max-sell-price", 2.0)
    }

    fun selectRandomSellPrice() : Double {
        return Random.nextDouble(this.lowSellPrice(), this.maxSellPrice())
    }
}