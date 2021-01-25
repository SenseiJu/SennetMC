package me.senseiju.sennetmc.utils

import org.bukkit.entity.Player

fun applyPlaceholders(string: String, vararg replacements: ObjectSet = emptyArray()) : String {
    var replacedString = string

    replacements.forEach {
        replacedString = replacedString.replace(it.any1.toString(), it.any2.toString())
    }

    return replacedString
}