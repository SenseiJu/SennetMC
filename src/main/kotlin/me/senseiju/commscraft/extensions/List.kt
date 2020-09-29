package me.senseiju.commscraft.extensions

fun List<String>.color() : List<String> {
    return this.map(String::color)
}