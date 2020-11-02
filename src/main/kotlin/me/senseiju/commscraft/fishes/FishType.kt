package me.senseiju.commscraft.fishes

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.datastorage.DataFile
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

            val random = Random.nextInt(1, probabilityRange)
            var index = 0
            var probabilityCount = values()[index].probability()
            while (true) {
                if (random <= probabilityCount) {
                    return values()[index]
                }
                probabilityCount += values()[++index].probability()
            }
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