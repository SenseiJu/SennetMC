package me.senseiju.sennetmc

import me.senseiju.sennetmc.utils.extensions.color

enum class Rarity(private val string: String) {
    COMMON("&8Common"),
    UNCOMMON("&aUncommon"),
    RARE("&dRare"),
    LEGENDARY("&6&lLegendary"),
    UNIQUE("&5&lUnique");

    override fun toString(): String = string.color()
}