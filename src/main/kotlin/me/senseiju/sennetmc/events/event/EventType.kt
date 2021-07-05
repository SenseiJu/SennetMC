package me.senseiju.sennetmc.events.event

import me.senseiju.sentils.extensions.primitives.color
import kotlin.random.Random

enum class EventType(title: String) {
    FISH_RACE("&b&lFish race"),
    SHIPWRECK("&#825432&lShipwreck"),
    FISHY_COLLAB("&#FEC000&lFishy Collab"),
    RECYCLE("&c&lRecycle");

    val title = title.color()

    companion object {
        fun selectRandom(): EventType = values()[Random.nextInt(values().size)]
    }
}