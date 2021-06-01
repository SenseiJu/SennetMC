package me.senseiju.sennetmc.utils

import kotlin.random.Random

fun percentChance(chance: Double): Boolean = Random.nextDouble() <= chance

fun <T> probabilityChance(objectProbabilities: Map<T, Double>): T {
    val random = Random.nextDouble(1.0,
        objectProbabilities.values.sumByDouble { it.coerceAtLeast(0.0) } + 1)
    var count = 0.0

    objectProbabilities.forEach { (obj, probability) ->
        count += probability.coerceAtLeast(0.0)
        if (random <= count) {
            return obj
        }
    }

    return objectProbabilities.keys.first()
}