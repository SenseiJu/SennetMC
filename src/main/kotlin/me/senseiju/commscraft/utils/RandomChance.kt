package me.senseiju.commscraft.utils

import kotlin.random.Random

fun percentChance(chance: Double) : Boolean = Random.nextDouble() <= chance

fun <T> probabilityChance(objectProbabilities: Map<T, Int>) : T {
    val random = Random.nextInt(1, objectProbabilities.values.sum() + 1)
    var count = 0

    println(objectProbabilities.keys.first())
    println(random)

    objectProbabilities.forEach { (obj, probability) ->
        count += probability
        println(count)
        if (random <= count) {
            return obj
        }
    }

    return objectProbabilities.keys.last()
}