package me.senseiju.sennetmc.extensions

fun List<String>.color() : List<String> {
    return this.map(String::color)
}