package me.senseiju.sennetmc.equipment

import me.senseiju.sennetmc.utils.extensions.color

enum class Equipment(eqName: String) {
    FISHING_NET("&e&lFishing net");

    val eqName = eqName.color()
}