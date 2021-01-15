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
            var probabilityRange = 1.0
            for (type in values()) {
                probabilityRange += type.probability()
            }
            return probabilityChance(values().associateWith { it.probability() })
        }
    }

    fun capacity() : Int {
        return dataFile.config.getInt("${this}.capacity", 1)
    }

    fun probability() : Double {
        return dataFile.config.getDouble("${this}.probability", 1.0)
    }

    private fun lowSellPrice() : Double {
        return dataFile.config.getDouble("${this}.low-sell-price", 1.0)
    }

    private fun maxSellPrice() : Double {
        return dataFile.config.getDouble("${this}.max-sell-price", 2.0)
    }

    fun selectRandomSellPrice() : Double {
        return Random.nextDouble(this.lowSellPrice(), this.maxSellPrice())
    }
}