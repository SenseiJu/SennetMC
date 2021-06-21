package me.senseiju.sennetmc.npcs.types.scrapper

import me.senseiju.sennetmc.utils.PlayerCountdownBukkitRunnable

abstract class Craftable : PlayerCountdownBukkitRunnable() {
    abstract val scrapCost: Long
    abstract val moneyCost: Long
}