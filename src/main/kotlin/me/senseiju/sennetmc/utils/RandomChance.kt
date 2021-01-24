package me.senseiju.sennetmc.utils

import kotlin.random.Random

fun percentChance(chance: Double) : Boolean = Random.nextDouble() <= chance

fun <T> probabilityChance(objectProbabilities: Map<T, Double>) : T {
    val random = Random.nextDouble(1.0, objectProbabilities.values.sum() + 1)
    var count = 0.0

    objectProbabilities.forEach { (obj, probability) ->
        count += probability
        if (random <= count) {
            return obj
        }
    }

    return objectProbabilities.keys.first()
}