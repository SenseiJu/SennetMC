package me.senseiju.sennetmc.extensions

inline fun <T> Iterable<T>.sumByDouble(selector: (T) -> Double): Double {
    return map { selector(it) }.sum()
}