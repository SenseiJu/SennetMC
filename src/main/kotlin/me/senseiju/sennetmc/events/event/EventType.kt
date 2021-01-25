package me.senseiju.sennetmc.events.event

import me.senseiju.sennetmc.extensions.color

enum class EventType(title: String) {
    FISH_RACE("&b&lFish race");

    val title = title.color()
}