package me.senseiju.sennetmc.utils

fun applyPlaceholders(string: String, vararg replacements: ObjectSet = emptyArray()) : String {
    var replacedString = string

    replacements.forEach {
        replacedString = replacedString.replace(it.any1.toString(), it.any2.toString())
    }

    return replacedString
}

fun applyPlaceholders(strings: List<String>, vararg replacements: ObjectSet = emptyArray()) : List<String> {
    val replacedStrings = ArrayList<String>()

    strings.forEach string@{ string ->

        replacements.forEach { replacement ->
            if (!string.contains(replacement.any1.toString())) {
                return@forEach
            }

            if (replacement.any2 is List<*>) {
                replacedStrings.addAll(replacement.any2.map { it.toString() })
                return@string
            }

            replacedStrings.add(string.replace(replacement.any1.toString(), replacement.any2.toString()))
            return@string
        }

        replacedStrings.add(string)
    }

    return replacedStrings
}