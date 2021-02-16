package me.senseiju.sennetmc.events.event

import me.senseiju.sennetmc.utils.extensions.color
import kotlin.random.Random

enum class EventType(title: String) {
    FISH_RACE("&b&lFish race"),
    SHIPWRECK("&#825432&lShipwreck");

    val title = title.color()

    companion object {
        fun selectRandom() : EventType = values()[Random.nextInt(values().size)]
    }
}